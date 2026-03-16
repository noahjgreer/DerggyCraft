package net.noahsarch.derggycraft.client;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.client.render.texture.GoldenCompassTextureBinder;

public class TextureRegistry {
    private static boolean goldenCompassBinderRegistered = false;

    // Texture Registry
    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        DerggyCraft.COLLAR_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/collar"));
        DerggyCraft.ROTTEN_FLESH_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/rotten_flesh"));
        DerggyCraft.LEATHER_SCRAP_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/leather_scrap"));
        DerggyCraft.GOLDEN_COMPASS_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/golden_compass"));
        DerggyCraft.FLARE_1M_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/flare0"));
        DerggyCraft.FLARE_10M_ITEM.setTexture(DerggyCraft.NAMESPACE.id("item/flare1"));
        if (DerggyCraft.EXTINGUISHED_TORCH_BLOCK != null) {
            DerggyCraft.EXTINGUISHED_TORCH_BLOCK.textureId = Atlases.getTerrain()
                    .addTexture(DerggyCraft.NAMESPACE.id("block/torch_extinguished"))
                    .index;
        }

        if (!goldenCompassBinderRegistered) {
            ExpandableAtlas guiItems = Atlases.getGuiItems();
            guiItems.addTextureBinder(
                    guiItems.getTexture(DerggyCraft.GOLDEN_COMPASS_ITEM.getTextureId(0)),
                    GoldenCompassTextureBinder::new
            );
            goldenCompassBinderRegistered = true;
        }
    }
}
