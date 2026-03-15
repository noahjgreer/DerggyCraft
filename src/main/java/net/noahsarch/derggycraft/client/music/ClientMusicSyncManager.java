package net.noahsarch.derggycraft.client.music;

import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.noahsarch.derggycraft.client.sound.MusicSyncSoundController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class ClientMusicSyncManager {
    private static final Object LOCK = new Object();
    private static final List<PendingTrigger> PENDING = new ArrayList<>();
    private static final LinkedHashSet<Long> RECENT_TRIGGER_IDS = new LinkedHashSet<>();
    private static final int MAX_RECENT_TRIGGER_IDS = 256;
    private static final long MAX_FUTURE_SCHEDULE_MS = 30_000L;

    private ClientMusicSyncManager() {
    }

    public static void queueFromPacket(MessagePacket packet) {
        if (packet == null || packet.longs == null || packet.longs.length < 2 || packet.strings == null || packet.strings.length == 0) {
            return;
        }

        long triggerId = packet.longs[0];
        long requestedStartAtMillis = packet.longs[1];

        boolean streaming = packet.booleans != null && packet.booleans.length > 0 && packet.booleans[0];
        float volume = packet.floats != null && packet.floats.length > 0 ? clamp(packet.floats[0], 0.0F, 4.0F) : 1.0F;
        float pitch = packet.floats != null && packet.floats.length > 1 ? clamp(packet.floats[1], 0.5F, 2.0F) : 1.0F;

        String[] candidateIds = sanitizeIds(packet.strings);
        if (candidateIds.length == 0) {
            return;
        }

        long now = System.currentTimeMillis();
        long startAtMillis = Math.max(now, requestedStartAtMillis);
        if (startAtMillis - now > MAX_FUTURE_SCHEDULE_MS) {
            startAtMillis = now + MAX_FUTURE_SCHEDULE_MS;
        }

        String selectedTrackId = candidateIds[ThreadLocalRandom.current().nextInt(candidateIds.length)];
        PendingTrigger trigger = new PendingTrigger(triggerId, startAtMillis, selectedTrackId, streaming, volume, pitch);

        synchronized (LOCK) {
            if (!RECENT_TRIGGER_IDS.add(triggerId)) {
                return;
            }
            trimRecentTriggerIds();
            PENDING.add(trigger);
        }
    }

    public static void tick(Minecraft minecraft) {
        if (minecraft == null || minecraft.soundManager == null) {
            return;
        }

        long now = System.currentTimeMillis();
        List<PendingTrigger> readyToPlay = new ArrayList<>();

        synchronized (LOCK) {
            Iterator<PendingTrigger> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingTrigger trigger = iterator.next();
                if (trigger.startAtMillis > now) {
                    continue;
                }

                readyToPlay.add(trigger);
                iterator.remove();
            }
        }

        for (PendingTrigger trigger : readyToPlay) {
            if (trigger.streaming) {
                if (minecraft.soundManager instanceof MusicSyncSoundController musicController) {
                    musicController.derggycraft$playSynchronizedMusic(trigger.trackId, trigger.volume);
                }
                continue;
            } else {
                minecraft.soundManager.playSound(trigger.trackId, trigger.volume, trigger.pitch);
            }
        }
    }

    private static String[] sanitizeIds(String[] rawIds) {
        List<String> ids = new ArrayList<>();
        for (String rawId : rawIds) {
            if (rawId == null) {
                continue;
            }

            String id = rawId.trim();
            if (id.isEmpty()) {
                continue;
            }

            ids.add(id);
        }

        return ids.toArray(new String[0]);
    }

    private static void trimRecentTriggerIds() {
        while (RECENT_TRIGGER_IDS.size() > MAX_RECENT_TRIGGER_IDS) {
            Iterator<Long> iterator = RECENT_TRIGGER_IDS.iterator();
            if (!iterator.hasNext()) {
                return;
            }

            iterator.next();
            iterator.remove();
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private record PendingTrigger(long triggerId, long startAtMillis, String trackId, boolean streaming, float volume, float pitch) {
    }
}