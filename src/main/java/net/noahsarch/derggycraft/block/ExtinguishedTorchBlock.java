package net.noahsarch.derggycraft.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateTorchBlock;

import java.util.Random;

public class ExtinguishedTorchBlock extends TemplateTorchBlock {
    public ExtinguishedTorchBlock(net.modificationstation.stationapi.api.util.Identifier identifier, int textureId) {
        super(identifier, textureId);
    }

    @Override
    public int getDroppedItemId(int blockMeta, Random random) {
        return Block.TORCH.id;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        // Intentionally empty: extinguished torches should not emit flame/smoke particles.
    }
}