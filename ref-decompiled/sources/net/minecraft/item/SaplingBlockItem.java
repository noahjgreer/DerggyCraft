/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class SaplingBlockItem
extends BlockItem {
    public SaplingBlockItem(int i) {
        super(i);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getPlacementMetadata(int meta) {
        return meta;
    }

    @Environment(value=EnvType.CLIENT)
    public int getTextureId(int damage) {
        return Block.SAPLING.getTexture(0, damage);
    }
}

