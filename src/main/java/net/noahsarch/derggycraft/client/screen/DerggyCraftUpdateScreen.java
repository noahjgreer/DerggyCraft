package net.noahsarch.derggycraft.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.client.updater.DerggyCraftAutoUpdater;
import org.lwjgl.input.Keyboard;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DerggyCraftUpdateScreen extends Screen {
    private static final int CONTINUE_BUTTON_ID = 0;
    private static final int UPDATE_BUTTON_ID = 1;

    private final Screen nextScreen;

    private volatile Stage stage = Stage.CHECKING;
    private volatile String headerText = "Checking for updates";
    private volatile String statusText = "Searching GitHub releases...";
    private volatile String installedVersion = "";
    private volatile String latestVersion = "";
    private volatile String errorText = "";
    private volatile String updateAssetFileName = "";

    private volatile long downloadedBytes;
    private volatile long totalBytes = -1L;

    private volatile boolean showContinueButton;
    private volatile boolean showUpdateChoiceButtons;
    private volatile boolean requestShutdown;

    private boolean workerStarted;
    private boolean updateWorkerStarted;
    private boolean continueButtonAdded;
    private boolean updateButtonAdded;
    private boolean shutdownTriggered;

    private volatile DerggyCraftAutoUpdater.UpdateCheckResult pendingUpdate;

    private long autoContinueAtMillis = -1L;

    public DerggyCraftUpdateScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void init() {
        this.buttons.clear();
        this.continueButtonAdded = false;
        this.updateButtonAdded = false;
        this.addButtonsIfNeeded();

        if (!this.workerStarted) {
            this.workerStarted = true;
            Thread workerThread = new Thread(this::runUpdateCheckWorkflow, "DerggyCraft-Updater");
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        this.addButtonsIfNeeded();

        if (this.requestShutdown && !this.shutdownTriggered) {
            this.shutdownTriggered = true;
            this.minecraft.scheduleStop();
        }

        if (this.autoContinueAtMillis > 0L && System.currentTimeMillis() >= this.autoContinueAtMillis) {
            this.minecraft.setScreen(this.nextScreen);
            this.autoContinueAtMillis = -1L;
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if ((this.showContinueButton || this.showUpdateChoiceButtons) && (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)) {
            this.minecraft.setScreen(this.nextScreen);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == UPDATE_BUTTON_ID) {
            this.startUpdateInstallWorkflow();
            return;
        }

        if (button.id == CONTINUE_BUTTON_ID) {
            this.minecraft.setScreen(this.nextScreen);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.drawCenteredTextWithShadow(this.textRenderer, this.headerText, centerX, centerY - 56, 0xFFFFFF);
        this.drawCenteredTextWithShadow(this.textRenderer, this.statusText, centerX, centerY - 40, 0xD0D0D0);

        if (!this.installedVersion.isBlank()) {
            this.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Installed: " + this.installedVersion + (this.latestVersion.isBlank() ? "" : "   Latest: " + this.latestVersion),
                    centerX,
                    centerY - 24,
                    0xA8A8A8
            );
        }

        if (this.stage == Stage.DOWNLOADING || this.stage == Stage.APPLYING || this.stage == Stage.RESTARTING) {
            this.renderProgressBar(centerX, centerY + 8);
        }

        if (this.stage == Stage.ERROR && !this.errorText.isBlank()) {
            int textY = centerY - 6;
            for (String line : wrapText(this.errorText, Math.max(140, this.width - 40))) {
                this.drawCenteredTextWithShadow(this.textRenderer, line, centerX, textY, 0xFF9090);
                textY += 10;
            }
        }

        super.render(mouseX, mouseY, delta);
    }

    private void renderProgressBar(int centerX, int y) {
        int barWidth = 100;
        int barHeight = 2;
        int left = centerX - barWidth / 2;

        this.fill(left, y, left + barWidth, y + barHeight, 0xFF808080);

        int progressPixels;
        if (this.totalBytes > 0L) {
            float progress = clamp01((float) this.downloadedBytes / (float) this.totalBytes);
            progressPixels = Math.round(progress * barWidth);
        } else if (this.stage == Stage.RESTARTING) {
            progressPixels = barWidth;
        } else {
            long phase = (System.currentTimeMillis() / 70L) % barWidth;
            progressPixels = (int) Math.max(2L, phase);
        }

        this.fill(left, y, left + progressPixels, y + barHeight, 0xFF80FF80);

        String progressText;
        if (this.totalBytes > 0L) {
            int pct = (int) Math.round(clamp01((float) this.downloadedBytes / (float) this.totalBytes) * 100.0F);
            progressText = pct + "% (" + formatBytes(this.downloadedBytes) + " / " + formatBytes(this.totalBytes) + ")";
        } else if (this.downloadedBytes > 0L) {
            progressText = formatBytes(this.downloadedBytes) + " downloaded";
        } else {
            progressText = "Preparing download...";
        }

        this.drawCenteredTextWithShadow(this.textRenderer, progressText, centerX, y + 10, 0xD0D0D0);
    }

    private void runUpdateCheckWorkflow() {
        try {
            this.headerText = "Checking for updates";
            this.statusText = "Searching GitHub releases...";
            this.stage = Stage.CHECKING;

            DerggyCraftAutoUpdater.UpdateCheckResult checkResult = DerggyCraftAutoUpdater.checkForUpdate();
            this.installedVersion = checkResult.installedVersion();
            this.latestVersion = checkResult.latestVersion();

            if (!checkResult.updateAvailable()) {
                this.headerText = "DerggyCraft is up to date";
                this.statusText = "No update needed. Launching game...";
                this.stage = Stage.UP_TO_DATE;
                this.autoContinueAtMillis = System.currentTimeMillis() + 900L;
                return;
            }

            this.headerText = "Update found: " + checkResult.latestTag();
            this.statusText = "Choose whether to install now or continue without updating.";
            this.stage = Stage.UPDATE_FOUND;
            this.pendingUpdate = checkResult;
            this.updateAssetFileName = checkResult.assetFileName();
            this.showUpdateChoiceButtons = true;
            this.showContinueButton = true;
        } catch (Exception exception) {
            if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.warn("DerggyCraft updater failed; allowing player to continue.", exception);
            }

            this.headerText = "Update check failed";
            this.statusText = "Could not fetch/apply latest build.";
            String message = exception.getMessage();
            this.errorText = message == null || message.isBlank()
                    ? "Unknown updater error. You can continue and play normally."
                    : message + "  Press Continue to keep playing.";
            this.stage = Stage.ERROR;
            this.showContinueButton = true;
        }
    }

    private void startUpdateInstallWorkflow() {
        if (this.updateWorkerStarted || this.pendingUpdate == null) {
            return;
        }

        this.updateWorkerStarted = true;
        this.showUpdateChoiceButtons = false;
        this.showContinueButton = false;
        this.buttons.clear();
        this.continueButtonAdded = false;
        this.updateButtonAdded = false;

        Thread workerThread = new Thread(this::runUpdateInstallWorkflow, "DerggyCraft-Updater-Install");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void runUpdateInstallWorkflow() {
        try {
            DerggyCraftAutoUpdater.UpdateCheckResult update = this.pendingUpdate;
            if (update == null) {
                throw new IllegalStateException("No update information is available.");
            }

            Path installedJarPath = DerggyCraftAutoUpdater.resolveInstalledModJarPath();

            this.headerText = "Downloading update";
            this.statusText = "Downloading " + this.updateAssetFileName + "...";
            this.stage = Stage.DOWNLOADING;
            this.downloadedBytes = 0L;
            this.totalBytes = -1L;

            Path downloadedJarPath = DerggyCraftAutoUpdater.downloadUpdate(installedJarPath, update, (downloaded, total) -> {
                this.downloadedBytes = downloaded;
                this.totalBytes = total;
            });

            this.headerText = "Applying update";
            this.statusText = "Preparing restart and jar replacement...";
            this.stage = Stage.APPLYING;

            DerggyCraftAutoUpdater.scheduleInstallAndRestart(installedJarPath, downloadedJarPath);

            this.headerText = "Update downloaded";
            this.statusText = "Restarting client to load " + update.latestTag() + "...";
            this.stage = Stage.RESTARTING;
            this.requestShutdown = true;
        } catch (Exception exception) {
            if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.warn("DerggyCraft updater install failed; allowing player to continue.", exception);
            }

            this.headerText = "Update install failed";
            this.statusText = "Could not download/apply latest build.";
            String message = exception.getMessage();
            this.errorText = message == null || message.isBlank()
                    ? "Unknown updater error. You can continue and play normally."
                    : message + "  Press Continue to keep playing.";
            this.stage = Stage.ERROR;
            this.showContinueButton = true;
        }
    }

    private void addButtonsIfNeeded() {
        if (this.showUpdateChoiceButtons && !this.updateButtonAdded) {
            this.buttons.add(new ButtonWidget(UPDATE_BUTTON_ID, this.width / 2 - 100, this.height / 2 + 20, "Update and restart"));
            this.updateButtonAdded = true;
        }

        if (this.showContinueButton && !this.continueButtonAdded) {
            int continueY = this.showUpdateChoiceButtons ? this.height / 2 + 44 : this.height / 2 + 20;
            this.buttons.add(new ButtonWidget(CONTINUE_BUTTON_ID, this.width / 2 - 100, continueY, "Continue without updating"));
            this.continueButtonAdded = true;
        }
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }

        double kb = bytes / 1024.0;
        if (kb < 1024.0) {
            return String.format(Locale.ROOT, "%.1f KB", kb);
        }

        double mb = kb / 1024.0;
        return String.format(Locale.ROOT, "%.2f MB", mb);
    }

    private List<String> wrapText(String text, int maxWidth) {
        if (text == null || text.isBlank() || this.textRenderer == null) {
            return Collections.emptyList();
        }

        String[] words = text.trim().split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (this.textRenderer.getWidth(candidate) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
                continue;
            }

            if (!current.isEmpty()) {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
                continue;
            }

            lines.add(word);
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private enum Stage {
        CHECKING,
        UPDATE_FOUND,
        DOWNLOADING,
        APPLYING,
        UP_TO_DATE,
        RESTARTING,
        ERROR
    }
}
