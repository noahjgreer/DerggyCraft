package net.noahsarch.derggycraft.sound;

public final class BackportedVanillaSounds {
    public static final String CAVE_PLAYBACK_ID = "ambient.cave.cave";
    public static final String BUCKET_FILL_PLAYBACK_ID = "item.bucket.fill";
    public static final String BUCKET_FILL_LAVA_PLAYBACK_ID = "item.bucket.fill_lava";
    public static final String BUCKET_EMPTY_PLAYBACK_ID = "item.bucket.empty";
    public static final String BUCKET_EMPTY_LAVA_PLAYBACK_ID = "item.bucket.empty_lava";

    public static final String[] CAVE_SOUND_FILES = buildSeries("ambient/cave/cave", 1, 23);
    public static final String[] BUCKET_FILL_SOUND_FILES = buildSeries("item/bucket/fill", 1, 3);
    public static final String[] BUCKET_FILL_LAVA_SOUND_FILES = buildSeries("item/bucket/fill_lava", 1, 3);
    public static final String[] BUCKET_EMPTY_SOUND_FILES = buildSeries("item/bucket/empty", 1, 3);
    public static final String[] BUCKET_EMPTY_LAVA_SOUND_FILES = buildSeries("item/bucket/empty_lava", 1, 3);

    private BackportedVanillaSounds() {
    }

    private static String[] buildSeries(String prefix, int startInclusive, int endInclusive) {
        int size = endInclusive - startInclusive + 1;
        String[] files = new String[size];
        for (int i = 0; i < size; ++i) {
            files[i] = prefix + (startInclusive + i) + ".ogg";
        }
        return files;
    }
}