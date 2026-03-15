package net.noahsarch.derggycraft.client.render.texture;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class GoldenCompassRenderState {
    private static final String TRACKED_ENTITY_ID_KEY = "TrackedEntityRuntimeId";

    private static volatile int mainhandRenderKey = Integer.MIN_VALUE;
    private static volatile double mainhandRotation;

    private GoldenCompassRenderState() {
    }

    public static int getStackRenderKey(ItemStack stack) {
        if (stack == null) {
            return Integer.MIN_VALUE;
        }

        int key = stack.itemId;
        key = 31 * key + stack.getDamage();

        NbtCompound stationNbt = stack.getStationNbt();
        if (stationNbt.contains(TRACKED_ENTITY_ID_KEY)) {
            key = 31 * key + stationNbt.getInt(TRACKED_ENTITY_ID_KEY);
        }

        return key;
    }

    public static void updateMainhandSnapshot(ItemStack selectedCompassStack, double currentRotation) {
        mainhandRenderKey = getStackRenderKey(selectedCompassStack);
        mainhandRotation = currentRotation;
    }

    public static void clearMainhandSnapshot() {
        mainhandRenderKey = Integer.MIN_VALUE;
    }

    public static boolean matchesMainhandSnapshot(ItemStack stack) {
        return getStackRenderKey(stack) == mainhandRenderKey;
    }

    public static double getMainhandRotation() {
        return mainhandRotation;
    }
}