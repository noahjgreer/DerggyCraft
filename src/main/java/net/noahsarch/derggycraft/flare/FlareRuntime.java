package net.noahsarch.derggycraft.flare;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.item.ThrowableFlareItem;
import net.noahsarch.derggycraft.sound.FlareLoopSound;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class FlareRuntime {
    private static final int FLARE_FIRST_LOOP_DELAY_TICKS = 16;
    private static final int FLARE_LOOP_INTERVAL_TICKS = 20 * 30;
    private static final float FLARE_LOOP_VOLUME = 0.65F;
    private static final int SMOKE_INTERVAL_TICKS = 2;

    private static final int[][] BASE_ANCHOR_SEARCH_OFFSETS = new int[][]{
        {0, 0, 0},
        {0, 1, 0},
        {0, -1, 0},
        {1, 0, 0},
        {-1, 0, 0},
        {0, 0, 1},
        {0, 0, -1}
    };

    private static final int[][] NORMAL_LIGHT_OFFSETS = new int[][]{
        {0, 0, 0}
    };

    private static final int[][] STRONG_LIGHT_OFFSETS = new int[][]{
        {0, 0, 0},
        {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
        {2, 0, 0}, {-2, 0, 0}, {0, 0, 2}, {0, 0, -2},
        {0, 1, 0},
        {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}
    };

    private static final Map<Integer, LightState> ACTIVE_LIGHTS = new HashMap<>();

    private FlareRuntime() {
    }

    public static boolean isFlareStack(ItemStack stack) {
        if (stack == null || stack.itemId <= 0 || stack.itemId >= net.minecraft.item.Item.ITEMS.length) {
            return false;
        }

        return net.minecraft.item.Item.ITEMS[stack.itemId] instanceof ThrowableFlareItem;
    }

    public static int getLifetimeTicks(ItemStack stack) {
        if (!isFlareStack(stack)) {
            return -1;
        }

        ThrowableFlareItem item = (ThrowableFlareItem) net.minecraft.item.Item.ITEMS[stack.itemId];
        return item.getLifetimeTicks();
    }

    public static boolean isFlareItemId(int itemId) {
        if (itemId <= 0 || itemId >= net.minecraft.item.Item.ITEMS.length) {
            return false;
        }

        return net.minecraft.item.Item.ITEMS[itemId] instanceof ThrowableFlareItem;
    }

    public static boolean isStrongFlareStack(ItemStack stack) {
        return isFlareStack(stack)
                && DerggyCraft.FLARE_1M_ITEM != null
                && stack.itemId == DerggyCraft.FLARE_1M_ITEM.id;
    }

    public static void tick(ItemEntity entity) {
        if (entity == null) {
            return;
        }

        ItemStack stack = entity.stack;
        if (!isFlareStack(stack)) {
            clearLight(entity);
            return;
        }

        if (entity.world == null) {
            return;
        }

        if (entity.world.isRemote) {
            if (shouldPlayLoopSound(entity.itemAge)) {
                entity.world.playSound(entity, FlareLoopSound.PLAYBACK_ID, FLARE_LOOP_VOLUME, 1.0F);
            }
            return;
        }

        int lifetime = getLifetimeTicks(stack);
        if (lifetime > 0 && entity.itemAge >= lifetime) {
            clearLight(entity);
            entity.markDead();
            return;
        }

        if (entity.itemAge % SMOKE_INTERVAL_TICKS == 0) {
            entity.world.addParticle(
                    "smoke",
                    entity.x,
                    entity.y + 0.24D,
                    entity.z,
                    entity.velocityX * 0.05D,
                    0.02D + entity.world.random.nextDouble() * 0.015D,
                    entity.velocityZ * 0.05D
            );
        }

        updateLight(entity);
    }

    public static void clearLight(ItemEntity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote) {
            return;
        }

        LightState existing = ACTIVE_LIGHTS.remove(entity.id);
        if (existing == null || existing.dimensionId != entity.world.dimension.id) {
            return;
        }

        for (LightAnchor anchor : existing.anchors) {
            clearLightAt(entity.world, anchor.x, anchor.y, anchor.z);
        }
    }

    private static void updateLight(ItemEntity entity) {
        World world = entity.world;
        ItemStack stack = entity.stack;
        int lightBlockId = getLightBlockIdForStack(stack);
        LightState next = resolveLightState(world, entity, lightBlockId, isStrongFlareStack(stack));
        LightState previous = ACTIVE_LIGHTS.get(entity.id);

        if (next == null) {
            if (previous != null && previous.dimensionId == world.dimension.id) {
                for (LightAnchor anchor : previous.anchors) {
                    clearLightAt(world, anchor.x, anchor.y, anchor.z);
                }
            }
            ACTIVE_LIGHTS.remove(entity.id);
            return;
        }

        if (previous != null && previous.equals(next)) {
            return;
        }

        if (previous != null && previous.dimensionId == world.dimension.id) {
            for (LightAnchor anchor : previous.anchors) {
                if (!next.anchors.contains(anchor)) {
                    clearLightAt(world, anchor.x, anchor.y, anchor.z);
                }
            }
        }

        for (LightAnchor anchor : next.anchors) {
            if (previous == null || !previous.anchors.contains(anchor) || previous.lightBlockId != next.lightBlockId) {
                setLightAt(world, anchor.x, anchor.y, anchor.z, next.lightBlockId);
            }
        }

        ACTIVE_LIGHTS.put(entity.id, next);
    }

    private static LightState resolveLightState(World world, ItemEntity entity, int lightBlockId, boolean strong) {
        if (lightBlockId <= 0) {
            return null;
        }

        LightAnchor primaryAnchor = resolvePrimaryAnchor(world, entity);
        if (primaryAnchor == null) {
            return null;
        }

        int[][] offsets = strong ? STRONG_LIGHT_OFFSETS : NORMAL_LIGHT_OFFSETS;
        Set<LightAnchor> anchors = new HashSet<>();

        for (int[] offset : offsets) {
            int x = primaryAnchor.x + offset[0];
            int y = primaryAnchor.y + offset[1];
            int z = primaryAnchor.z + offset[2];
            if (!canPlaceAnchor(world, x, y, z)) {
                continue;
            }

            anchors.add(new LightAnchor(x, y, z));
        }

        if (anchors.isEmpty()) {
            anchors.add(primaryAnchor);
        }

        return new LightState(world.dimension.id, lightBlockId, anchors);
    }

    private static LightAnchor resolvePrimaryAnchor(World world, ItemEntity entity) {
        int baseX = MathHelper.floor(entity.x);
        int baseY = MathHelper.floor(entity.y + 0.05D);
        int baseZ = MathHelper.floor(entity.z);

        for (int[] offset : BASE_ANCHOR_SEARCH_OFFSETS) {
            int x = baseX + offset[0];
            int y = baseY + offset[1];
            int z = baseZ + offset[2];
            if (!canPlaceAnchor(world, x, y, z)) {
                continue;
            }

            return new LightAnchor(x, y, z);
        }

        return null;
    }

    private static int getLightBlockIdForStack(ItemStack stack) {
        if (isStrongFlareStack(stack) && DerggyCraft.FLARE_LIGHT_STRONG_BLOCK != null) {
            return DerggyCraft.FLARE_LIGHT_STRONG_BLOCK.id;
        }

        return DerggyCraft.FLARE_LIGHT_BLOCK != null ? DerggyCraft.FLARE_LIGHT_BLOCK.id : -1;
    }

    private static boolean canPlaceAnchor(World world, int x, int y, int z) {
        if (world == null || y < 0 || y >= 128) {
            return false;
        }

        int blockId = world.getBlockId(x, y, z);
        return blockId == 0 || isLightBlockId(blockId);
    }

    private static boolean isLightBlockId(int blockId) {
        if (DerggyCraft.FLARE_LIGHT_BLOCK != null && blockId == DerggyCraft.FLARE_LIGHT_BLOCK.id) {
            return true;
        }

        return DerggyCraft.FLARE_LIGHT_STRONG_BLOCK != null && blockId == DerggyCraft.FLARE_LIGHT_STRONG_BLOCK.id;
    }

    private static void setLightAt(World world, int x, int y, int z, int lightBlockId) {
        if (lightBlockId <= 0) {
            return;
        }

        if (world.getBlockId(x, y, z) != lightBlockId) {
            world.setBlock(x, y, z, lightBlockId);
        }
    }

    private static void clearLightAt(World world, int x, int y, int z) {
        if (isLightBlockId(world.getBlockId(x, y, z))) {
            world.setBlock(x, y, z, 0);
        }
    }

    private static boolean shouldPlayLoopSound(int itemAge) {
        return itemAge >= FLARE_FIRST_LOOP_DELAY_TICKS
                && (itemAge - FLARE_FIRST_LOOP_DELAY_TICKS) % FLARE_LOOP_INTERVAL_TICKS == 0;
    }

    private record LightState(int dimensionId, int lightBlockId, Set<LightAnchor> anchors) {
        private LightState {
            anchors = Set.copyOf(anchors);
        }
    }

    private record LightAnchor(int x, int y, int z) {
    }
}
