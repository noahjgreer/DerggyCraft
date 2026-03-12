/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtFloat
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtFloat;

public class NbtFloat
extends NbtElement
implements StationNbtFloat {
    public float value;

    public NbtFloat() {
    }

    public NbtFloat(float value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeFloat(this.value);
    }

    void read(DataInput input) {
        this.value = input.readFloat();
    }

    public byte getType() {
        return 5;
    }

    public String toString() {
        return "" + this.value;
    }
}

