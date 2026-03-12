/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.modificationstation.stationapi.api.nbt.StationNbtByteArray
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.nbt.NbtElement;
import net.modificationstation.stationapi.api.nbt.StationNbtByteArray;

public class NbtByteArray
extends NbtElement
implements StationNbtByteArray {
    public byte[] value;

    public NbtByteArray() {
    }

    public NbtByteArray(byte[] value) {
        this.value = value;
    }

    void write(DataOutput output) {
        output.writeInt(this.value.length);
        output.write(this.value);
    }

    void read(DataInput input) {
        int n = input.readInt();
        this.value = new byte[n];
        input.readFully(this.value);
    }

    public byte getType() {
        return 7;
    }

    public String toString() {
        return "[" + this.value.length + " bytes]";
    }
}

