package net.noahsarch.derggycraft.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class IronCompassTracking {
    private static final String TARGET_X_KEY = "IronCompassTargetX";
    private static final String TARGET_Y_KEY = "IronCompassTargetY";
    private static final String TARGET_Z_KEY = "IronCompassTargetZ";
    private static final String TARGET_DIMENSION_KEY = "IronCompassTargetDimension";
    private static final Map<Integer, int[]> LAST_TRACKED_BLOCK_BY_DIMENSION = new HashMap<>();

    private IronCompassTracking() {
    }

    public static void setTrackedBlock(ItemStack stack, World world, int x, int y, int z) {
        if (stack == null || world == null || world.dimension == null) {
            return;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        stationNbt.putInt(TARGET_X_KEY, x);
        stationNbt.putInt(TARGET_Y_KEY, y);
        stationNbt.putInt(TARGET_Z_KEY, z);
        stationNbt.putInt(TARGET_DIMENSION_KEY, world.dimension.id);
        LAST_TRACKED_BLOCK_BY_DIMENSION.put(world.dimension.id, new int[]{x, y, z});
    }

    public static boolean hasTrackedBlock(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        return stationNbt.contains(TARGET_X_KEY)
                && stationNbt.contains(TARGET_Y_KEY)
                && stationNbt.contains(TARGET_Z_KEY)
                && stationNbt.contains(TARGET_DIMENSION_KEY);
    }

    public static boolean isTrackedInWorld(ItemStack stack, World world) {
        if (!hasTrackedBlock(stack) || world == null || world.dimension == null) {
            return false;
        }

        return stack.getStationNbt().getInt(TARGET_DIMENSION_KEY) == world.dimension.id;
    }

    public static double getTrackedCenterX(ItemStack stack) {
        return stack.getStationNbt().getInt(TARGET_X_KEY) + 0.5D;
    }

    public static double getTrackedCenterZ(ItemStack stack) {
        return stack.getStationNbt().getInt(TARGET_Z_KEY) + 0.5D;
    }

    public static int getTrackedX(ItemStack stack) {
        return stack.getStationNbt().getInt(TARGET_X_KEY);
    }

    public static int getTrackedY(ItemStack stack) {
        return stack.getStationNbt().getInt(TARGET_Y_KEY);
    }

    public static int getTrackedZ(ItemStack stack) {
        return stack.getStationNbt().getInt(TARGET_Z_KEY);
    }

    public static boolean hasGlobalTrackedBlock(World world) {
        return world != null && world.dimension != null
                && LAST_TRACKED_BLOCK_BY_DIMENSION.containsKey(world.dimension.id);
    }

    public static double getGlobalTrackedCenterX(World world) {
        int[] pos = LAST_TRACKED_BLOCK_BY_DIMENSION.get(world.dimension.id);
        return (double) pos[0] + 0.5D;
    }

    public static double getGlobalTrackedCenterZ(World world) {
        int[] pos = LAST_TRACKED_BLOCK_BY_DIMENSION.get(world.dimension.id);
        return (double) pos[2] + 0.5D;
    }
}