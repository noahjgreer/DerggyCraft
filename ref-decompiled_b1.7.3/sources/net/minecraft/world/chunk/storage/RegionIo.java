/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.chunk.storage.RegionFile;

public class RegionIo {
    private static final Map REGION_FILES = new HashMap();

    private RegionIo() {
    }

    public static synchronized RegionFile getRegionFile(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile;
        File file = new File(worldDir, "region");
        File file2 = new File(file, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mcr");
        Reference reference = (Reference)REGION_FILES.get(file2);
        if (reference != null && (regionFile = (RegionFile)reference.get()) != null) {
            return regionFile;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        if (REGION_FILES.size() >= 256) {
            RegionIo.flush();
        }
        regionFile = new RegionFile(file2);
        REGION_FILES.put(file2, new SoftReference<RegionFile>(regionFile));
        return regionFile;
    }

    public static synchronized void flush() {
        for (Reference reference : REGION_FILES.values()) {
            try {
                RegionFile regionFile = (RegionFile)reference.get();
                if (regionFile == null) continue;
                regionFile.close();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        REGION_FILES.clear();
    }

    public static int getChunkSize(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile = RegionIo.getRegionFile(worldDir, chunkX, chunkZ);
        return regionFile.resetBytesWritten();
    }

    public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile = RegionIo.getRegionFile(worldDir, chunkX, chunkZ);
        return regionFile.getChunkInputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }

    public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile = RegionIo.getRegionFile(worldDir, chunkX, chunkZ);
        return regionFile.getChunkOutputStream(chunkX & 0x1F, chunkZ & 0x1F);
    }
}

