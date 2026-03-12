/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.BlockItem;

public class PistonBlockItem
extends BlockItem {
    public PistonBlockItem(int i) {
        super(i);
    }

    public int getPlacementMetadata(int meta) {
        return 7;
    }
}

