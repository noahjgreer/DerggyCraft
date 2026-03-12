/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;

public class AxeItem
extends ToolItem {
    private static Block[] axeEffectiveBlocks = new Block[]{Block.PLANKS, Block.BOOKSHELF, Block.LOG, Block.CHEST};

    public AxeItem(int id, ToolMaterial toolMaterial) {
        super(id, 3, toolMaterial, axeEffectiveBlocks);
    }
}

