/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.util.math.StationBlockPos
 */
package net.minecraft.util.math;

import net.modificationstation.stationapi.api.util.math.StationBlockPos;

public class BlockPos
implements StationBlockPos {
    public final int x;
    public final int y;
    public final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object o) {
        if (o instanceof BlockPos) {
            BlockPos blockPos = (BlockPos)o;
            return blockPos.x == this.x && blockPos.y == this.y && blockPos.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }
}

