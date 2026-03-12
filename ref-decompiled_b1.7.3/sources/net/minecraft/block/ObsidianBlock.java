/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.StoneBlock;

public class ObsidianBlock
extends StoneBlock {
    public ObsidianBlock(int i, int j) {
        super(i, j);
    }

    public int getDroppedItemCount(Random random) {
        return 1;
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return Block.OBSIDIAN.id;
    }
}

