/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class GlowstoneBlock
extends Block {
    public GlowstoneBlock(int i, int j, Material material) {
        super(i, j, material);
    }

    public int getDroppedItemCount(Random random) {
        return 2 + random.nextInt(3);
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return Item.GLOWSTONE_DUST.id;
    }
}

