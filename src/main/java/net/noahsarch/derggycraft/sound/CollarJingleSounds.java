package net.noahsarch.derggycraft.sound;

public final class CollarJingleSounds {
    public static final String[] FILE_NAMES = {
            "jingle0.ogg",
            "jingle1.ogg",
            "jingle2.ogg",
            "jingle3.ogg",
            "jingle4.ogg",
            "jingle5.ogg",
            "jingle6.ogg",
            "jingle7.ogg",
            "jingle8.ogg",
            "jingle9.ogg",
            "jingle11.ogg",
            "jingle12.ogg",
            "jingle13.ogg",
            "jingle14.ogg",
            "jingle15.ogg"
    };

    public static final String[] REGISTRATION_IDS = buildRegistrationIds();
    public static final String[] PLAYBACK_IDS = buildPlaybackIds();

    private CollarJingleSounds() {
    }

    private static String[] buildRegistrationIds() {
        String[] ids = new String[FILE_NAMES.length];
        for (int i = 0; i < FILE_NAMES.length; ++i) {
            ids[i] = "derggycraft:collar/jingle/jingle_variant_" + (char) ('a' + i) + ".ogg";
        }
        return ids;
    }

    private static String[] buildPlaybackIds() {
        String[] ids = new String[REGISTRATION_IDS.length];
        for (int i = 0; i < REGISTRATION_IDS.length; ++i) {
            String registrationId = REGISTRATION_IDS[i];
            String noExt = registrationId.substring(0, registrationId.lastIndexOf('.'));
            ids[i] = noExt.replace('/', '.');
        }
        return ids;
    }
}