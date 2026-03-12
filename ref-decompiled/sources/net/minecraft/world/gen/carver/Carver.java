/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.carver;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;

public class Carver {
    protected int range = 8;
    protected Random random = new Random();

    public void carve(ChunkSource source, World world, int chunkX, int chunkZ, byte[] blocks) {
        int n = this.range;
        this.random.setSeed(world.getSeed());
        long l = this.random.nextLong() / 2L * 2L + 1L;
        long l2 = this.random.nextLong() / 2L * 2L + 1L;
        for (int i = chunkX - n; i <= chunkX + n; ++i) {
            for (int j = chunkZ - n; j <= chunkZ + n; ++j) {
                this.random.setSeed((long)i * l + (long)j * l2 ^ world.getSeed());
                this.carve(world, i, j, chunkX, chunkZ, blocks);
            }
        }
    }

    protected void carve(World world, int startChunkX, int startChunkZ, int chunkX, int chunkZ, byte[] blocks) {
    }
}

