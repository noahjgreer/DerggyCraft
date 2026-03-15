package net.noahsarch.derggycraft;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.CompassSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.noahsarch.derggycraft.client.render.texture.NewCompassSprite;
import net.noahsarch.derggycraft.item.CollarItem;
import net.noahsarch.derggycraft.item.LeatherScrapItem;
import net.noahsarch.derggycraft.item.RottenFleshItem;
import net.noahsarch.derggycraft.item.GoldenCompass;

public class DerggyCraft {
    TextureManager textureManager;

    // Namespace Utility Field
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    // Item Fields
    public static Item COLLAR_ITEM;
    public static Item ROTTEN_FLESH_ITEM;
    public static Item LEATHER_SCRAP_ITEM;
    public static Item GOLDEN_COMPASS_ITEM;

    // Item Registry
    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        COLLAR_ITEM = new CollarItem(NAMESPACE.id("collar"))
                .setTranslationKey(NAMESPACE, "collar");
        ROTTEN_FLESH_ITEM = new RottenFleshItem(NAMESPACE.id("rotten_flesh"))
                .setTranslationKey(NAMESPACE, "rotten_flesh");
        LEATHER_SCRAP_ITEM = new LeatherScrapItem(NAMESPACE.id("leather_scrap"))
                .setTranslationKey(NAMESPACE, "leather_scrap");
        GOLDEN_COMPASS_ITEM = new GoldenCompass(NAMESPACE.id("golden_compass"))
                .setTranslationKey(NAMESPACE, "golden_compass");
    }

    // Texture Registry
    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        DerggyCraft.COLLAR_ITEM.setTexture(NAMESPACE.id("item/collar"));
        DerggyCraft.ROTTEN_FLESH_ITEM.setTexture(NAMESPACE.id("item/rotten_flesh"));
        DerggyCraft.LEATHER_SCRAP_ITEM.setTexture(NAMESPACE.id("item/leather_scrap"));
    }
}
