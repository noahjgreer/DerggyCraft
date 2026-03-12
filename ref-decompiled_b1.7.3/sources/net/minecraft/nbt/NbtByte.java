/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtByte
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtByte;

public class NbtByte
extends NbtElement
implements StationNbtByte {
    public byte value;

    public NbtByte() {
    }

    public NbtByte(byte value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeByte(this.value);
    }

    void read(DataInput input) {
        this.value = input.readByte();
    }

    public byte getType() {
        return 1;
    }

    public String toString() {
        return "" + this.value;
    }
}

