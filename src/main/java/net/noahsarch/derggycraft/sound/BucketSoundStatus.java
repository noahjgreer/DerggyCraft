package net.noahsarch.derggycraft.sound;

public final class BucketSoundStatus {
    public static final byte BUCKET_FILL_WATER = 30;
    public static final byte BUCKET_FILL_LAVA = 31;
    public static final byte BUCKET_EMPTY_WATER = 32;
    public static final byte BUCKET_EMPTY_LAVA = 33;

    private BucketSoundStatus() {
    }

    public static byte toStatus(String playbackId) {
        if (BackportedVanillaSounds.BUCKET_FILL_PLAYBACK_ID.equals(playbackId)) {
            return BUCKET_FILL_WATER;
        }
        if (BackportedVanillaSounds.BUCKET_FILL_LAVA_PLAYBACK_ID.equals(playbackId)) {
            return BUCKET_FILL_LAVA;
        }
        if (BackportedVanillaSounds.BUCKET_EMPTY_PLAYBACK_ID.equals(playbackId)) {
            return BUCKET_EMPTY_WATER;
        }
        if (BackportedVanillaSounds.BUCKET_EMPTY_LAVA_PLAYBACK_ID.equals(playbackId)) {
            return BUCKET_EMPTY_LAVA;
        }
        return 0;
    }

    public static String toPlaybackId(byte status) {
        if (status == BUCKET_FILL_WATER) {
            return BackportedVanillaSounds.BUCKET_FILL_PLAYBACK_ID;
        }
        if (status == BUCKET_FILL_LAVA) {
            return BackportedVanillaSounds.BUCKET_FILL_LAVA_PLAYBACK_ID;
        }
        if (status == BUCKET_EMPTY_WATER) {
            return BackportedVanillaSounds.BUCKET_EMPTY_PLAYBACK_ID;
        }
        if (status == BUCKET_EMPTY_LAVA) {
            return BackportedVanillaSounds.BUCKET_EMPTY_LAVA_PLAYBACK_ID;
        }
        return null;
    }
}
