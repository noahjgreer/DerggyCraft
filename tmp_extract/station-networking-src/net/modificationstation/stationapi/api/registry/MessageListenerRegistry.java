package net.modificationstation.stationapi.api.registry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.class_54;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;

import java.util.function.BiConsumer;

import static net.modificationstation.stationapi.api.StationAPI.NAMESPACE;

/**
 * Registry that holds {@link MessagePacket} listeners.
 *
 * <p>A message listener must have the same identifier as the message it listens for.
 *
 * @author mine_diver
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageListenerRegistry {
    public static final RegistryKey<Registry<BiConsumer<class_54, MessagePacket>>> KEY = RegistryKey.ofRegistry(NAMESPACE.id("message_listeners"));
    public static final Registry<BiConsumer<class_54, MessagePacket>> INSTANCE = Registries.create(KEY);
}
