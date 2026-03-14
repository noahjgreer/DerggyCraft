package net.derggy.craft.derggycraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-side cache for golden compass target coordinates received from the server.
 */
@Environment(EnvType.CLIENT)
public class CompassTargetCache {

    private static boolean hasTarget = false;
    private static double targetX;
    private static double targetZ;
    private static boolean trackingSelf = false;

    public static void setTarget(double x, double z, boolean self) {
        targetX = x;
        targetZ = z;
        hasTarget = true;
        trackingSelf = self;
    }

    public static void clearTarget() {
        hasTarget = false;
        trackingSelf = false;
    }

    public static boolean hasTarget() {
        return hasTarget;
    }

    public static double getTargetX() {
        return targetX;
    }

    public static double getTargetZ() {
        return targetZ;
    }

    public static boolean isTrackingSelf() {
        return trackingSelf;
    }
}
