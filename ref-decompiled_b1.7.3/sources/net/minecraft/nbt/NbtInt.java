/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtInt
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtInt;

public class NbtInt
extends NbtElement
implements StationNbtInt {
    public int value;

    public NbtInt() {
    }

    public NbtInt(int value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeInt(this.value);
    }

    void read(DataInput input) {
        this.value = input.readInt();
    }

    public byte getType() {
        return 3;
    }

    public String toString() {
        return "" + this.value;
    }
}

