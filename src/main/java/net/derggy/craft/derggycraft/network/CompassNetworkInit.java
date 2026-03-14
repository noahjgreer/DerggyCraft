package net.derggy.craft.derggycraft.network;

import net.derggy.craft.derggycraft.client.CompassTargetCache;
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
 * Registers network message listeners for compass coordinate sync.
 */
public class CompassNetworkInit {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    @EventListener
    private static void registerMessageListeners(MessageListenerRegistryEvent event) {
        // Client receives golden compass target coordinates from server
        Registry.register(MessageListenerRegistry.INSTANCE, NAMESPACE.id("golden_compass_update"),
                (BiConsumer) (player, message) -> {
                    MessagePacket msg = (MessagePacket) message;
                    if (msg.booleans != null && msg.booleans.length > 0 && !msg.booleans[0]) {
                        // No target found
                        CompassTargetCache.clearTarget();
                    } else if (msg.doubles != null && msg.doubles.length >= 2) {
                        boolean self = msg.booleans != null && msg.booleans.length > 1 && msg.booleans[1];
                        CompassTargetCache.setTarget(msg.doubles[0], msg.doubles[1], self);
                    }
                });
    }
}
