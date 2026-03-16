package net.noahsarch.derggycraft.network.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.noahsarch.derggycraft.network.MusicSyncNetworking;

import java.util.concurrent.atomic.AtomicLong;

public final class ServerMusicSync {
    private static final AtomicLong NEXT_TRIGGER_ID = new AtomicLong(1L);

    private ServerMusicSync() {
    }

    public static int broadcastSynchronizedPlayback(
            ServerPlayerEntity source,
            long leadMillis,
            boolean streaming,
            float volume,
            float pitch,
            String[] candidateTrackIds
    ) {
        if (source == null) {
            return 0;
        }

        return broadcastSynchronizedPlayback(
                source.server,
                leadMillis,
                streaming,
                volume,
                pitch,
                candidateTrackIds
        );
    }

    public static int broadcastSynchronizedPlayback(
            MinecraftServer server,
            long leadMillis,
            boolean streaming,
            float volume,
            float pitch,
            String[] candidateTrackIds
    ) {
        if (server == null || server.playerManager == null || candidateTrackIds == null || candidateTrackIds.length == 0) {
            return 0;
        }

        long startAtMillis = System.currentTimeMillis() + Math.max(0L, leadMillis);
        long triggerId = NEXT_TRIGGER_ID.getAndIncrement();
        MessagePacket packet = MusicSyncNetworking.createStartPacket(
                triggerId,
                startAtMillis,
                streaming,
                volume,
                pitch,
                candidateTrackIds
        );

        int sent = 0;
        for (Object playerObj : server.playerManager.players) {
            if (!(playerObj instanceof ServerPlayerEntity player)) {
                continue;
            }

            PacketHelper.sendTo(player, packet);
            ++sent;
        }

        return sent;
    }
}