/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtShort
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtShort;

public class NbtShort
extends NbtElement
implements StationNbtShort {
    public short value;

    public NbtShort() {
    }

    public NbtShort(short value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeShort(this.value);
    }

    void read(DataInput input) {
        this.value = input.readShort();
    }

    public byte getType() {
        return 2;
    }

    public String toString() {
        return "" + this.value;
    }
}

