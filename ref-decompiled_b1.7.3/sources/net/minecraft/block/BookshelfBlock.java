/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BookshelfBlock
extends Block {
    public BookshelfBlock(int id, int textureId) {
        super(id, textureId, Material.WOOD);
    }

    public int getTexture(int side) {
        if (side <= 1) {
            return 4;
        }
        return this.textureId;
    }

    public int getDroppedItemCount(Random random) {
        return 0;
    }
}

