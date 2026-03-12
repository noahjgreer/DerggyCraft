/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.material;

import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;

public class AirMaterial
extends Material {
    public AirMaterial(MapColor mapColor) {
        super(mapColor);
        this.setReplaceable();
    }

    public boolean isSolid() {
        return false;
    }

    public boolean blocksVision() {
        return false;
    }

    public boolean blocksMovement() {
        return false;
    }
}

