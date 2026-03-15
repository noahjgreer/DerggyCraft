package net.noahsarch.derggycraft.world;

import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public final class TorchExtinguishTracker {
    private static final Map<BlockPosKey, Long> DUE_TICKS = new HashMap<>();

    private TorchExtinguishTracker() {
    }

    public static void schedule(World world, int x, int y, int z, long delayTicks) {
        if (world == null || world.isRemote) {
            return;
        }

        long dueTick = world.getTime() + Math.max(1L, delayTicks);
        DUE_TICKS.put(new BlockPosKey(world.dimension.id, x, y, z), dueTick);
    }

    public static void clear(World world, int x, int y, int z) {
        if (world == null) {
            return;
        }

        DUE_TICKS.remove(new BlockPosKey(world.dimension.id, x, y, z));
    }

    public static boolean isDue(World world, int x, int y, int z) {
        if (world == null) {
            return false;
        }

        BlockPosKey key = new BlockPosKey(world.dimension.id, x, y, z);
        Long dueTick = DUE_TICKS.get(key);
        if (dueTick == null) {
            return false;
        }

        if (dueTick > world.getTime()) {
            return false;
        }

        DUE_TICKS.remove(key);
        return true;
    }

    private record BlockPosKey(int dimensionId, int x, int y, int z) {
    }
}