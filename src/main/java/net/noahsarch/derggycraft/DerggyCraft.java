package net.noahsarch.derggycraft;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.noahsarch.derggycraft.item.CollarItem;
import net.noahsarch.derggycraft.item.LeatherScrapItem;
import net.noahsarch.derggycraft.item.RottenFleshItem;

public class DerggyCraft {
    // Namespace Utility Field
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    // Item Fields
    public static Item COLLAR_ITEM;
    public static Item ROTTEN_FLESH_ITEM;
    public static Item LEATHER_SCRAP_ITEM;

    // Item Registry
    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        COLLAR_ITEM = new CollarItem(NAMESPACE.id("collar"))
                .setTranslationKey(NAMESPACE, "collar");
        ROTTEN_FLESH_ITEM = new RottenFleshItem(NAMESPACE.id("rotten_flesh"))
                .setTranslationKey(NAMESPACE, "rotten_flesh");
        LEATHER_SCRAP_ITEM = new LeatherScrapItem(NAMESPACE.id("leather_scrap"))
                .setTranslationKey(NAMESPACE, "leather_scrap");
    }

    // Texture Registry
    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        DerggyCraft.COLLAR_ITEM.setTexture(NAMESPACE.id("item/collar"));
        DerggyCraft.ROTTEN_FLESH_ITEM.setTexture(NAMESPACE.id("item/rotten_flesh"));
        DerggyCraft.LEATHER_SCRAP_ITEM.setTexture(NAMESPACE.id("item/leather_scrap"));
    }
}
