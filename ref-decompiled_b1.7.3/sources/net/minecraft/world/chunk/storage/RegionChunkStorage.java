/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.chunk.storage.RegionIo;

public class RegionChunkStorage
implements ChunkStorage {
    private final File dir;

    public RegionChunkStorage(File dir) {
        this.dir = dir;
    }

    public Chunk loadChunk(World world, int chunkX, int chunkZ) {
        DataInputStream dataInputStream = RegionIo.getChunkInputStream(this.dir, chunkX, chunkZ);
        if (dataInputStream == null) {
            return null;
        }
        NbtCompound nbtCompound = NbtIo.read(dataInputStream);
        if (!nbtCompound.contains("Level")) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
            return null;
        }
        if (!nbtCompound.getCompound("Level").contains("Blocks")) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
            return null;
        }
        Chunk chunk = AlphaChunkStorage.loadChunkFromNbt(world, nbtCompound.getCompound("Level"));
        if (!chunk.chunkPosEquals(chunkX, chunkZ)) {
            System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is in the wrong location; relocating. (Expected " + chunkX + ", " + chunkZ + ", got " + chunk.x + ", " + chunk.z + ")");
            nbtCompound.putInt("xPos", chunkX);
            nbtCompound.putInt("zPos", chunkZ);
            chunk = AlphaChunkStorage.loadChunkFromNbt(world, nbtCompound.getCompound("Level"));
        }
        chunk.fill();
        return chunk;
    }

    public void saveChunk(World world, Chunk chunk) {
        world.checkSessionLock();
        try {
            DataOutputStream dataOutputStream = RegionIo.getChunkOutputStream(this.dir, chunk.x, chunk.z);
            NbtCompound nbtCompound = new NbtCompound();
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound.put("Level", (NbtElement)nbtCompound2);
            AlphaChunkStorage.saveChunkToNbt(chunk, world, nbtCompound2);
            NbtIo.write(nbtCompound, dataOutputStream);
            dataOutputStream.close();
            WorldProperties worldProperties = world.getProperties();
            worldProperties.setSizeOnDisk(worldProperties.getSizeOnDisk() + (long)RegionIo.getChunkSize(this.dir, chunk.x, chunk.z));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveEntities(World world, Chunk chunk) {
    }

    public void tick() {
    }

    public void flush() {
    }
}

