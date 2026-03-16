package net.noahsarch.derggycraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

public class FlareLightBlock extends TemplateBlock {
    public FlareLightBlock(Identifier identifier, float luminance) {
        super(identifier, Material.AIR);
        this.setHardness(0.0F)
                .setLuminance(luminance)
                .setTickRandomly(true)
                .ignoreMetaUpdates();
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean hasCollision() {
        return false;
    }

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int getDroppedItemId(int blockMeta, Random random) {
        return -1;
    }
}
