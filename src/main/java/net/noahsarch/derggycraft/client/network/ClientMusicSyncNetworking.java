package net.noahsarch.derggycraft.client.network;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.noahsarch.derggycraft.client.music.ClientMusicSyncManager;
import net.noahsarch.derggycraft.network.MusicSyncNetworking;

public class ClientMusicSyncNetworking {
    @EventListener
    public void registerMessageListeners(MessageListenerRegistryEvent event) {
        event.register(MusicSyncNetworking.START_SYNC_MESSAGE_ID, (player, packet) -> ClientMusicSyncManager.queueFromPacket(packet));
    }
}