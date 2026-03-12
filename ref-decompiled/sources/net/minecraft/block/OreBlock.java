/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class OreBlock
extends Block {
    public OreBlock(int id, int textureId) {
        super(id, textureId, Material.STONE);
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        if (this.id == Block.COAL_ORE.id) {
            return Item.COAL.id;
        }
        if (this.id == Block.DIAMOND_ORE.id) {
            return Item.DIAMOND.id;
        }
        if (this.id == Block.LAPIS_ORE.id) {
            return Item.DYE.id;
        }
        return this.id;
    }

    public int getDroppedItemCount(Random random) {
        if (this.id == Block.LAPIS_ORE.id) {
            return 4 + random.nextInt(5);
        }
        return 1;
    }

    protected int getDroppedItemMeta(int blockMeta) {
        if (this.id == Block.LAPIS_ORE.id) {
            return 4;
        }
        return 0;
    }
}

