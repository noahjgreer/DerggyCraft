/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;

public class SpawnerBlock
extends BlockWithEntity {
    public SpawnerBlock(int id, int textureId) {
        super(id, textureId, Material.STONE);
    }

    protected BlockEntity createBlockEntity() {
        return new MobSpawnerBlockEntity();
    }

    public int getDroppedItemId(int blockMeta, Random random) {
        return 0;
    }

    public int getDroppedItemCount(Random random) {
        return 0;
    }

    public boolean isOpaque() {
        return false;
    }
}

