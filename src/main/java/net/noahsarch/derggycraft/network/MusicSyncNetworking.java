package net.noahsarch.derggycraft.network;

import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.util.Identifier;

public final class MusicSyncNetworking {
    public static final Identifier START_SYNC_MESSAGE_ID = Identifier.of("derggycraft:music_sync/start");

    private MusicSyncNetworking() {
    }

    public static MessagePacket createStartPacket(
            long triggerId,
            long startAtMillis,
            boolean streaming,
            float volume,
            float pitch,
            String[] candidateTrackIds
    ) {
        MessagePacket packet = new MessagePacket(START_SYNC_MESSAGE_ID);
        // StationAPI 2.0.0-alpha.5.4 MessagePacket writes the long count from ints.length.
        // Keep ints length aligned with longs length so downstream packet decoding stays synchronized.
        packet.longs = new long[]{triggerId, startAtMillis};
        packet.ints = new int[packet.longs.length];
        packet.booleans = new boolean[]{streaming};
        packet.floats = new float[]{volume, pitch};
        packet.strings = candidateTrackIds;
        return packet;
    }
}