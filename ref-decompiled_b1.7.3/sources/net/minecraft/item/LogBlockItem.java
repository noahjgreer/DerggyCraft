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

public class LogBlockItem
extends BlockItem {
    public LogBlockItem(int i) {
        super(i);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTextureId(int damage) {
        return Block.LOG.getTexture(2, damage);
    }

    public int getPlacementMetadata(int meta) {
        return meta;
    }
}

