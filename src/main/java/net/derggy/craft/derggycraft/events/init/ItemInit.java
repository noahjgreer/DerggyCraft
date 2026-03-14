package net.derggy.craft.derggycraft.events.init;

import net.derggy.craft.derggycraft.item.GoldenCompassItem;
import net.derggy.craft.derggycraft.item.IronCompassItem;
import net.derggy.craft.derggycraft.item.RottenFleshItem;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

import java.lang.invoke.MethodHandles;

public class ItemInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static Item ROTTEN_FLESH;
    public static Item LEATHER_SCRAP;
    public static Item IRON_COMPASS;
    public static Item GOLDEN_COMPASS;

    @EventListener
    private static void registerItems(ItemRegistryEvent event) {
        ROTTEN_FLESH = new RottenFleshItem(NAMESPACE.id("rotten_flesh"))
                .setTranslationKey(NAMESPACE, "rottenFlesh");

        LEATHER_SCRAP = new TemplateItem(NAMESPACE.id("leather_scrap"))
                .setMaxCount(64)
                .setTranslationKey(NAMESPACE, "leatherScrap");

        IRON_COMPASS = new IronCompassItem(NAMESPACE.id("iron_compass"))
                .setTranslationKey(NAMESPACE, "ironCompass");

        GOLDEN_COMPASS = new GoldenCompassItem(NAMESPACE.id("golden_compass"))
                .setTranslationKey(NAMESPACE, "goldenCompass");
    }
}
