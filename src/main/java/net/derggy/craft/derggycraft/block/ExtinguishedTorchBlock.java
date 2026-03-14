package net.derggy.craft.derggycraft.block;

import net.modificationstation.stationapi.api.template.block.TemplateTorchBlock;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

/**
 * An extinguished torch block - looks like a torch but emits no light.
 * Does not produce fire/smoke particles.
 */
public class ExtinguishedTorchBlock extends TemplateTorchBlock {

    public ExtinguishedTorchBlock(Identifier identifier, int textureId) {
        super(identifier, textureId);
        this.setLuminance(0.0f);
        this.setHardness(0.0f);
    }

    @Override
    public void randomDisplayTick(net.minecraft.world.World world, int x, int y, int z, Random random) {
        // No particles - torch is extinguished
    }
}
