/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtLong
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtLong;

public class NbtLong
extends NbtElement
implements StationNbtLong {
    public long value;

    public NbtLong() {
    }

    public NbtLong(long value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeLong(this.value);
    }

    void read(DataInput input) {
        this.value = input.readLong();
    }

    public byte getType() {
        return 4;
    }

    public String toString() {
        return "" + this.value;
    }
}

