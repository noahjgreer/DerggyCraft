package net.derggy.craft.derggycraft.events.init;

import net.derggy.craft.derggycraft.block.ExtinguishedTorchBlock;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

import java.lang.invoke.MethodHandles;

public class BlockInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static Block EXTINGUISHED_TORCH;

    @EventListener
    private static void registerBlocks(BlockRegistryEvent event) {
        EXTINGUISHED_TORCH = new ExtinguishedTorchBlock(NAMESPACE.id("extinguished_torch"), 0);
        EXTINGUISHED_TORCH.setSoundGroup(Block.WOOD_SOUND_GROUP);
        EXTINGUISHED_TORCH.setTranslationKey(NAMESPACE, "extinguishedTorch");
    }
}
