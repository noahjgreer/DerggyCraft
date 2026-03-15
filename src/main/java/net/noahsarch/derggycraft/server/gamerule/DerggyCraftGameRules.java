package net.noahsarch.derggycraft.server.gamerule;

import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Locale;

public final class DerggyCraftGameRules {
    private static final String STATE_ID = "derggycraft_gamerules";

    private static boolean sendDeathMessages = true;
    private static boolean keepInventory = false;
    private static boolean extinguishTorches = false;
    private static DerggyCraftGameRulesState persistentState;

    private DerggyCraftGameRules() {
    }

    public static Rule parseRule(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return null;
        }

        return switch (rawName.toLowerCase(Locale.ROOT)) {
            case "senddeathmessages" -> Rule.SEND_DEATH_MESSAGES;
            case "keepinventory" -> Rule.KEEP_INVENTORY;
            case "extinguishtorches" -> Rule.EXTINGUISH_TORCHES;
            default -> null;
        };
    }

    public static Boolean parseBoolean(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        return switch (rawValue.toLowerCase(Locale.ROOT)) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> null;
        };
    }

    public static synchronized void ensureLoaded(World world) {
        if (world == null || world.isRemote || persistentState != null) {
            return;
        }

        PersistentState existing = world.getOrCreateState(DerggyCraftGameRulesState.class, STATE_ID);
        if (existing instanceof DerggyCraftGameRulesState loadedState) {
            persistentState = loadedState;
            syncFromState();
            return;
        }

        persistentState = new DerggyCraftGameRulesState(STATE_ID);
        syncToState();
        world.setState(STATE_ID, persistentState);
        persistentState.markDirty();
    }

    public static boolean get(World world, Rule rule) {
        ensureLoaded(world);
        return get(rule);
    }

    public static boolean get(Rule rule) {
        if (rule == null) {
            return false;
        }

        return switch (rule) {
            case SEND_DEATH_MESSAGES -> sendDeathMessages;
            case KEEP_INVENTORY -> keepInventory;
            case EXTINGUISH_TORCHES -> extinguishTorches;
        };
    }

    public static void set(Rule rule, boolean value) {
        set(null, rule, value);
    }

    public static synchronized void set(World world, Rule rule, boolean value) {
        ensureLoaded(world);

        if (rule == null) {
            return;
        }

        switch (rule) {
            case SEND_DEATH_MESSAGES -> sendDeathMessages = value;
            case KEEP_INVENTORY -> keepInventory = value;
            case EXTINGUISH_TORCHES -> extinguishTorches = value;
        }

        syncToState();
        if (persistentState != null) {
            persistentState.markDirty();
        }
    }

    public static String formatRuleName(Rule rule) {
        if (rule == null) {
            return "unknown";
        }

        return switch (rule) {
            case SEND_DEATH_MESSAGES -> "sendDeathMessages";
            case KEEP_INVENTORY -> "keepInventory";
            case EXTINGUISH_TORCHES -> "extinguishTorches";
        };
    }

    public static String formatAllRules() {
        StringBuilder builder = new StringBuilder();
        for (Rule rule : Rule.values()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }

            builder.append(formatRuleName(rule)).append('=').append(get(rule));
        }
        return builder.toString();
    }

    private static void syncFromState() {
        if (persistentState == null) {
            return;
        }

        sendDeathMessages = persistentState.sendDeathMessages;
        keepInventory = persistentState.keepInventory;
        extinguishTorches = persistentState.extinguishTorches;
    }

    private static void syncToState() {
        if (persistentState == null) {
            return;
        }

        persistentState.sendDeathMessages = sendDeathMessages;
        persistentState.keepInventory = keepInventory;
        persistentState.extinguishTorches = extinguishTorches;
    }

    public enum Rule {
        SEND_DEATH_MESSAGES,
        KEEP_INVENTORY,
        EXTINGUISH_TORCHES
    }
}