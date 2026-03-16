package net.noahsarch.derggycraft;

import org.apache.logging.log4j.Logger;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.noahsarch.derggycraft.block.ExtinguishedTorchBlock;
import net.noahsarch.derggycraft.item.CollarItem;
import net.noahsarch.derggycraft.item.LeatherScrapItem;
import net.noahsarch.derggycraft.item.RottenFleshItem;
import net.noahsarch.derggycraft.item.GoldenCompassItem;

public class DerggyCraft {
    // Namespace Utility Field
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;
    @Entrypoint.Logger
    public static Logger LOGGER;

    // Item Fields
    public static Item COLLAR_ITEM;
    public static Item ROTTEN_FLESH_ITEM;
    public static Item LEATHER_SCRAP_ITEM;
    public static Item GOLDEN_COMPASS_ITEM;

        // Block Fields
        public static Block EXTINGUISHED_TORCH_BLOCK;

        // Block Registry
        @EventListener
        public void registerBlocks(BlockRegistryEvent event) {
                EXTINGUISHED_TORCH_BLOCK = new ExtinguishedTorchBlock(NAMESPACE.id("torch_extinguished"), Block.TORCH.textureId)
                                .setHardness(0.0F)
                                .setSoundGroup(Block.WOOD_SOUND_GROUP)
                                .setTranslationKey(NAMESPACE, "torch_extinguished")
                                .ignoreMetaUpdates();
        }

    // Item Registry
    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        // Empty buckets are stackable up to 16.
        Item.BUCKET.setMaxCount(16);

        COLLAR_ITEM = new CollarItem(NAMESPACE.id("collar"))
                .setTranslationKey(NAMESPACE, "collar");
        ROTTEN_FLESH_ITEM = new RottenFleshItem(NAMESPACE.id("rotten_flesh"))
                .setTranslationKey(NAMESPACE, "rotten_flesh");
        LEATHER_SCRAP_ITEM = new LeatherScrapItem(NAMESPACE.id("leather_scrap"))
                .setTranslationKey(NAMESPACE, "leather_scrap");
        GOLDEN_COMPASS_ITEM = new GoldenCompassItem(NAMESPACE.id("golden_compass"))
                .setTranslationKey(NAMESPACE, "golden_compass");
    }
}
