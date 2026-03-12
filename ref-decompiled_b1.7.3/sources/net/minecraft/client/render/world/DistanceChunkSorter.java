/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.world;

import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class DistanceChunkSorter
implements Comparator {
    private double cameraX;
    private double cameraY;
    private double cameraZ;

    public DistanceChunkSorter(Entity camera) {
        this.cameraX = -camera.x;
        this.cameraY = -camera.y;
        this.cameraZ = -camera.z;
    }

    public int compare(ChunkBuilder chunkBuilder, ChunkBuilder chunkBuilder2) {
        double d = (double)chunkBuilder.centerX + this.cameraX;
        double d2 = (double)chunkBuilder.centerY + this.cameraY;
        double d3 = (double)chunkBuilder.centerZ + this.cameraZ;
        double d4 = (double)chunkBuilder2.centerX + this.cameraX;
        double d5 = (double)chunkBuilder2.centerY + this.cameraY;
        double d6 = (double)chunkBuilder2.centerZ + this.cameraZ;
        return (int)((d * d + d2 * d2 + d3 * d3 - (d4 * d4 + d5 * d5 + d6 * d6)) * 1024.0);
    }
}

