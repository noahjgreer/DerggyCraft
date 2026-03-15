package net.modificationstation.stationapi.api.event.registry;

import net.mine_diver.unsafeevents.event.EventPhases;
import net.minecraft.class_54;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.registry.MessageListenerRegistry;
import net.modificationstation.stationapi.api.registry.Registry;

import java.util.function.BiConsumer;

/**
 * Registry event that fires when {@link MessageListenerRegistry} is ready to register listeners.
 *
 * @author mine_diver
 */
@EventPhases(StationAPI.INTERNAL_PHASE)
public class MessageListenerRegistryEvent extends RegistryEvent.EntryTypeBound<BiConsumer<class_54, MessagePacket>, Registry<BiConsumer<class_54, MessagePacket>>> {
    public MessageListenerRegistryEvent() {
        super(MessageListenerRegistry.INSTANCE);
    }
}
