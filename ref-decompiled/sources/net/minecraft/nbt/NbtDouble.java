/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtDouble
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtDouble;

public class NbtDouble
extends NbtElement
implements StationNbtDouble {
    public double value;

    public NbtDouble() {
    }

    public NbtDouble(double value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeDouble(this.value);
    }

    void read(DataInput input) {
        this.value = input.readDouble();
    }

    public byte getType() {
        return 6;
    }

    public String toString() {
        return "" + this.value;
    }
}

