package net.derggy.craft.derggycraft.events.init;

import net.derggy.craft.derggycraft.client.GoldenCompassSprite;
import net.derggy.craft.derggycraft.client.IronCompassSprite;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

import java.lang.invoke.MethodHandles;

public class TextureInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    @EventListener
    private static void registerTextures(TextureRegisterEvent event) {
        // Item textures
        ItemInit.ROTTEN_FLESH.setTexture(NAMESPACE.id("item/rotten_flesh"));
        ItemInit.LEATHER_SCRAP.setTexture(NAMESPACE.id("item/leather_scrap"));
        ItemInit.IRON_COMPASS.setTexture(NAMESPACE.id("item/player_compass"));
        ItemInit.GOLDEN_COMPASS.setTexture(NAMESPACE.id("item/player_compass"));

        // Register compass texture binders (arsenic-compatible animated sprites)
        ExpandableAtlas guiItems = Atlases.getGuiItems();
        guiItems.addTextureBinder(NAMESPACE.id("item/player_compass"), IronCompassSprite::new);
        guiItems.addTextureBinder(NAMESPACE.id("item/player_compass"), GoldenCompassSprite::new);

        // Block textures
        ExpandableAtlas terrain = Atlases.getTerrain();
        BlockInit.EXTINGUISHED_TORCH.textureId = terrain.addTexture(NAMESPACE.id("block/torch_extinguished")).index;
    }
}
