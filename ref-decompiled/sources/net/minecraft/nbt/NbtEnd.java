/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtEnd
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtEnd;

public class NbtEnd
extends NbtElement
implements StationNbtEnd {
    void read(DataInput input) {
    }

    void write(DataOutput output) {
    }

    public byte getType() {
        return 0;
    }

    public String toString() {
        return "END";
    }
}

