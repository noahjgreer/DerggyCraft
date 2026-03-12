/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class StoneBlock
extends Block {
    public StoneBlock(int id, int textureId) {
        super(id, textureId, Material.STONE);
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return Block.COBBLESTONE.id;
    }
}

