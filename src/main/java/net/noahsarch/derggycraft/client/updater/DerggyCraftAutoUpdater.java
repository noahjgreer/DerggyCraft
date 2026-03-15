package net.noahsarch.derggycraft.client.updater;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.noahsarch.derggycraft.DerggyCraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DerggyCraftAutoUpdater {
    public static final String MOD_ID = "derggycraft";
    public static final String RELEASES_PAGE_URL = "https://github.com/noahjgreer/DerggyCraft/releases/";

    private static final String LATEST_RELEASE_API_URL = "https://api.github.com/repos/noahjgreer/DerggyCraft/releases/latest";
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\\\"tag_name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile("\\\"browser_download_url\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private DerggyCraftAutoUpdater() {
    }

    public static UpdateCheckResult checkForUpdate() throws IOException {
        ModContainer modContainer = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .orElseThrow(() -> new IOException("Unable to locate mod container for " + MOD_ID));

        String installedVersion = normalizeVersion(modContainer.getMetadata().getVersion().getFriendlyString());
        String responseJson = fetchUtf8(LATEST_RELEASE_API_URL);

        String latestTag = extractMatch(TAG_NAME_PATTERN, responseJson);
        if (latestTag == null || latestTag.isBlank()) {
            throw new IOException("GitHub latest release does not include tag_name.");
        }

        String latestVersion = normalizeVersion(latestTag);
        String downloadUrl = findPrimaryJarAssetUrl(responseJson);
        if (downloadUrl == null) {
            throw new IOException("No downloadable jar asset found in the latest release.");
        }

        String assetFileName = determineAssetFileName(downloadUrl);
        boolean updateAvailable = !installedVersion.equalsIgnoreCase(latestVersion);

        return new UpdateCheckResult(installedVersion, latestVersion, latestTag, updateAvailable, downloadUrl, assetFileName);
    }

    public static Path resolveInstalledModJarPath() throws IOException {
        ModContainer modContainer = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .orElseThrow(() -> new IOException("Unable to locate mod container for " + MOD_ID));

        List<Path> originPaths = modContainer.getOrigin().getPaths();
        for (Path originPath : originPaths) {
            if (originPath == null) {
                continue;
            }
            Path absolutePath = originPath.toAbsolutePath().normalize();
            if (Files.isRegularFile(absolutePath) && absolutePath.toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                return absolutePath;
            }
        }

        throw new IOException("Automatic update requires the mod to be loaded from a jar in the mods folder.");
    }

    public static Path downloadUpdate(Path installedJarPath, UpdateCheckResult update, ProgressListener progressListener) throws IOException {
        if (update == null || !update.updateAvailable()) {
            throw new IOException("No update is available to download.");
        }

        Path updaterDirectory = installedJarPath.getParent().resolve(".derggycraft-updater");
        Files.createDirectories(updaterDirectory);

        String safeName = sanitizeFileName(update.assetFileName());
        Path targetFile = updaterDirectory.resolve(safeName);
        Path partialFile = updaterDirectory.resolve(safeName + ".part");

        Files.deleteIfExists(partialFile);

        HttpURLConnection connection = openConnection(update.downloadUrl());
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("GitHub download failed with HTTP " + responseCode);
        }

        long totalBytes = connection.getContentLengthLong();
        long downloadedBytes = 0L;
        if (progressListener != null) {
            progressListener.onProgress(0L, totalBytes);
        }

        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = Files.newOutputStream(partialFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, read);
                downloadedBytes += read;
                if (progressListener != null) {
                    progressListener.onProgress(downloadedBytes, totalBytes);
                }
            }
        } finally {
            connection.disconnect();
        }

        Files.move(partialFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        return targetFile;
    }

    public static void scheduleInstallAndRestart(Path installedJarPath, Path downloadedJarPath) throws IOException {
        if (!isWindows()) {
            throw new IOException("Automatic update currently supports Windows only.");
        }

        long currentPid = ProcessHandle.current().pid();
        String relaunchCommand = resolveRelaunchCommand();
        if (relaunchCommand == null || relaunchCommand.isBlank()) {
            throw new IOException("Unable to determine relaunch command for this client.");
        }

        Path updaterDirectory = downloadedJarPath.getParent();
        Files.createDirectories(updaterDirectory);
        Path scriptPath = updaterDirectory.resolve("derggycraft-apply-update.ps1");
        Path logPath = updaterDirectory.resolve("derggycraft-updater.log");
        Path backupPath = Paths.get(installedJarPath.toString() + ".old");

        String script = buildPowerShellScript(
                currentPid,
                installedJarPath,
                downloadedJarPath,
                backupPath,
                relaunchCommand,
                logPath
        );

        Files.writeString(scriptPath, script, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        new ProcessBuilder(
                "powershell",
                "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-WindowStyle", "Hidden",
                "-File", scriptPath.toString()
        ).start();
    }

    private static HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(20_000);
        connection.setRequestProperty("User-Agent", "DerggyCraft-Updater");
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        return connection;
    }

    private static String fetchUtf8(String urlString) throws IOException {
        HttpURLConnection connection = openConnection(urlString);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("GitHub API request failed with HTTP " + responseCode);
        }

        try (InputStream inputStream = connection.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } finally {
            connection.disconnect();
        }
    }

    private static String findPrimaryJarAssetUrl(String releaseJson) {
        Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(releaseJson);
        while (matcher.find()) {
            String candidate = unescapeJsonString(matcher.group(1));
            String lower = candidate.toLowerCase(Locale.ROOT);
            if (!lower.endsWith(".jar")) {
                continue;
            }
            if (lower.contains("-sources") || lower.contains("-source") || lower.contains("-javadoc")) {
                continue;
            }
            return candidate;
        }

        return null;
    }

    private static String extractMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return unescapeJsonString(matcher.group(1));
        }
        return null;
    }

    private static String determineAssetFileName(String downloadUrl) {
        try {
            URI uri = URI.create(downloadUrl);
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                return "derggycraft-update.jar";
            }
            int slash = path.lastIndexOf('/');
            if (slash >= 0 && slash + 1 < path.length()) {
                return path.substring(slash + 1);
            }
            return "derggycraft-update.jar";
        } catch (Exception ignored) {
            return "derggycraft-update.jar";
        }
    }

    private static String normalizeVersion(String rawVersion) {
        if (rawVersion == null) {
            return "";
        }

        String normalized = rawVersion.trim();
        while (normalized.startsWith("v") || normalized.startsWith("V")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private static String sanitizeFileName(String fileName) {
        String sanitized = fileName == null ? "derggycraft-update.jar" : fileName.trim();
        if (sanitized.isEmpty()) {
            return "derggycraft-update.jar";
        }

        sanitized = sanitized.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (!sanitized.toLowerCase(Locale.ROOT).endsWith(".jar")) {
            sanitized = sanitized + ".jar";
        }
        return sanitized;
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return osName.contains("win");
    }

    private static String unescapeJsonString(String value) {
        return value
                .replace("\\/", "/")
                .replace("\\u0026", "&")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static String buildPowerShellScript(long pid, Path installedJarPath, Path downloadedJarPath, Path backupPath, String relaunchCommand, Path logPath) {
        return """
                $ErrorActionPreference = 'Stop'
                $PidToWait = %d
                $TargetJar = '%s'
                $DownloadedJar = '%s'
                $BackupJar = '%s'
                $RelaunchCommand = '%s'
                $LogFile = '%s'

                function Write-UpdateLog([string]$message) {
                    Add-Content -Path $LogFile -Value ("[{0}] {1}" -f (Get-Date -Format s), $message)
                }

                Write-UpdateLog 'Updater helper started.'
                while (Get-Process -Id $PidToWait -ErrorAction SilentlyContinue) {
                    Start-Sleep -Milliseconds 400
                }

                try {
                    if (Test-Path $BackupJar) {
                        Remove-Item -Path $BackupJar -Force -ErrorAction SilentlyContinue
                    }

                    if (Test-Path $TargetJar) {
                        Move-Item -Path $TargetJar -Destination $BackupJar -Force
                    }

                    Move-Item -Path $DownloadedJar -Destination $TargetJar -Force

                    if (Test-Path $BackupJar) {
                        Remove-Item -Path $BackupJar -Force -ErrorAction SilentlyContinue
                    }

                    Write-UpdateLog 'Jar replacement completed.'
                    Start-Process -FilePath 'cmd.exe' -ArgumentList '/c', $RelaunchCommand
                    Write-UpdateLog 'Relaunch command launched.'
                } catch {
                    Write-UpdateLog ('Updater failed: ' + $_.Exception.Message)
                }
                """.formatted(
                pid,
                escapePowerShellSingleQuoted(installedJarPath.toString()),
                escapePowerShellSingleQuoted(downloadedJarPath.toString()),
                escapePowerShellSingleQuoted(backupPath.toString()),
                escapePowerShellSingleQuoted(relaunchCommand),
                escapePowerShellSingleQuoted(logPath.toString())
        );
    }

    private static String escapePowerShellSingleQuoted(String value) {
        return value.replace("'", "''");
    }

    private static String resolveRelaunchCommand() {
        Optional<String> processCommandLine = ProcessHandle.current().info().commandLine();
        if (processCommandLine.isPresent() && !processCommandLine.get().isBlank()) {
            return processCommandLine.get();
        }

        String javaHome = System.getProperty("java.home", "");
        Path javaExecutable = Paths.get(javaHome, "bin", "javaw.exe");
        if (!Files.exists(javaExecutable)) {
            javaExecutable = Paths.get(javaHome, "bin", "java.exe");
        }

        String classPath = System.getProperty("java.class.path", "");
        String sunCommand = System.getProperty("sun.java.command", "");
        if (sunCommand.isBlank() || classPath.isBlank()) {
            return "";
        }

        List<String> args = new ArrayList<>();
        args.add(javaExecutable.toString());
        for (String vmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (vmArg != null && !vmArg.isBlank()) {
                args.add(vmArg);
            }
        }
        args.add("-cp");
        args.add(classPath);
        args.add(sunCommand);

        return buildWindowsCommandLine(args);
    }

    private static String buildWindowsCommandLine(List<String> args) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.size(); ++i) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(quoteForCommand(args.get(i)));
        }
        return builder.toString();
    }

    private static String quoteForCommand(String value) {
        if (value == null) {
            return "\"\"";
        }

        boolean requiresQuote = value.isEmpty() || value.indexOf(' ') >= 0 || value.indexOf('\t') >= 0 || value.indexOf('"') >= 0;
        if (!requiresQuote) {
            return value;
        }

        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    public interface ProgressListener {
        void onProgress(long downloadedBytes, long totalBytes);
    }

    public record UpdateCheckResult(
            String installedVersion,
            String latestVersion,
            String latestTag,
            boolean updateAvailable,
            String downloadUrl,
            String assetFileName
    ) {
    }
}
