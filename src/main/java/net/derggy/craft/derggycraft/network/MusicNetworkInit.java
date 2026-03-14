package net.derggy.craft.derggycraft.network;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.registry.MessageListenerRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;

import java.lang.invoke.MethodHandles;
import java.util.function.BiConsumer;

/**
 * Registers network message listener for server-synced music playback.
 */
public class MusicNetworkInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    /** Set to true when the server triggers music. Client checks and resets this. */
    public static volatile boolean shouldPlayMusic = false;

    @EventListener
    private static void registerMessageListeners(MessageListenerRegistryEvent event) {
        Registry.register(MessageListenerRegistry.INSTANCE, NAMESPACE.id("music_sync"),
                (BiConsumer) (player, message) -> {
                    // Server told us to play music
                    shouldPlayMusic = true;
                });
    }
}
