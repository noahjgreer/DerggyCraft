package net.derggy.craft.derggycraft.gamerule;

/**
 * Manages custom gamerule values for the server.
 */
public class GameruleManager {

    private static boolean keepInventory = false;
    private static boolean extinguishTorches = false;

    public static boolean getKeepInventory() {
        return keepInventory;
    }

    public static void setKeepInventory(boolean value) {
        keepInventory = value;
    }

    public static boolean getExtinguishTorches() {
        return extinguishTorches;
    }

    public static void setExtinguishTorches(boolean value) {
        extinguishTorches = value;
    }

    /**
     * Try to set a gamerule by name.
     * @return true if the gamerule was recognized and set
     */
    public static boolean setGamerule(String name, String value) {
        switch (name.toLowerCase()) {
            case "keepinventory":
                keepInventory = parseBoolean(value);
                return true;
            case "extinguishtorches":
                extinguishTorches = parseBoolean(value);
                return true;
            default:
                return false;
        }
    }

    /**
     * Get a gamerule value string by name.
     * @return the value string, or null if unknown
     */
    public static String getGamerule(String name) {
        switch (name.toLowerCase()) {
            case "keepinventory":
                return String.valueOf(keepInventory);
            case "extinguishtorches":
                return String.valueOf(extinguishTorches);
            default:
                return null;
        }
    }

    private static boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }
}
