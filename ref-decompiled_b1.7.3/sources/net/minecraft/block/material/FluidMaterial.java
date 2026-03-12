/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.material;

import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;

public class FluidMaterial
extends Material {
    public FluidMaterial(MapColor mapColor) {
        super(mapColor);
        this.setReplaceable();
        this.setDestroyPistonBehavior();
    }

    public boolean isFluid() {
        return true;
    }

    public boolean blocksMovement() {
        return false;
    }

    public boolean isSolid() {
        return false;
    }
}

