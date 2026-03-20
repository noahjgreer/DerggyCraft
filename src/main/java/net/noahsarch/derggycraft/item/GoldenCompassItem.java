package net.noahsarch.derggycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.entity.PersistentEntityIds;
import org.jetbrains.annotations.NotNull;

public class GoldenCompassItem extends TemplateItem implements CustomTooltipProvider {
    private static final String TRACKED_ENTITY_ID_KEY = "TrackedEntityRuntimeId";
    private static final String TRACKED_ENTITY_PERSISTENT_ID_KEY = "TrackedEntityPersistentId";
    private static final String TRACKED_ENTITY_NAME_KEY = "TrackedEntityName";
    private static final String TRACKED_LAST_X_KEY = "TrackedEntityLastX";
    private static final String TRACKED_LAST_Y_KEY = "TrackedEntityLastY";
    private static final String TRACKED_LAST_Z_KEY = "TrackedEntityLastZ";
    private static final String TRACKED_LAST_DIMENSION_KEY = "TrackedEntityLastDimension";
    private static final String TARGET_GLOW_ACTIVE_KEY = "TargetGlowActive";
    // Backward-compat key from previous trail mechanic.
    private static final String LEGACY_TRAIL_ACTIVE_KEY = "TrailActive";
    private static final double TARGET_REACHED_DISTANCE_SQ = 25.0D;

    public GoldenCompassItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
        this.setMaxDamage(100);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public void useOnEntity(ItemStack stack, LivingEntity target) {
        if (stack == null || target == null) {
            return;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        if (isLocked(stationNbt)) {
            return;
        }

        stationNbt.putInt(TRACKED_ENTITY_ID_KEY, target.id);
        stationNbt.putString(TRACKED_ENTITY_NAME_KEY, getEntityDisplayName(target));
        writeLastKnownPosition(stationNbt, target);
        setGlowActive(stationNbt, false);

        String persistentId = PersistentEntityIds.getOrCreate(target);
        if (persistentId != null && !persistentId.isEmpty()) {
            stationNbt.putString(TRACKED_ENTITY_PERSISTENT_ID_KEY, persistentId);
        }

        DerggyCraft.LOGGER.info("Golden Compass locked target '{}' (runtime id {}).", getEntityDisplayName(target), target.id);
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        NbtCompound stationNbt = stack.getStationNbt();
        if (!stationNbt.contains(TRACKED_ENTITY_NAME_KEY)) {
            return new String[]{originalTooltip};
        }

        int usesLeft = Math.max(0, stack.getMaxDamage() - stack.getDamage());
        if (isGlowActive(stack)) {
            return new String[]{
                    originalTooltip,
                    "\u00a77Tracking: " + stationNbt.getString(TRACKED_ENTITY_NAME_KEY),
                "\u00a7bTarget glow: active",
                    "\u00a78Uses left: " + usesLeft
            };
        }

        return new String[]{
                originalTooltip,
                "\u00a77Tracking: " + stationNbt.getString(TRACKED_ENTITY_NAME_KEY),
                "\u00a78Uses left: " + usesLeft
        };
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if (stack == null || world == null || user == null || world.isRemote) {
            return stack;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        if (!isLocked(stationNbt)) {
            return stack;
        }

        LivingEntity trackedEntity = getTrackedEntity(stack, world);
        if (trackedEntity != null) {
            writeLastKnownPosition(stationNbt, trackedEntity);
        }

        if (!hasTrackedLastPosition(stack) || !isTrackedLastPositionInWorld(stack, world)) {
            return stack;
        }

        setGlowActive(stationNbt, true);
        stack.damage(1, user);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stack == null || world == null || !(entity instanceof PlayerEntity player)) {
            return;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        LivingEntity trackedEntity = getTrackedEntity(stack, world);
        if (trackedEntity != null) {
            writeLastKnownPosition(stationNbt, trackedEntity);
        }

        if (!isGlowActive(stack)) {
            return;
        }

        if (!hasTrackedLastPosition(stack) || !isTrackedLastPositionInWorld(stack, world)) {
            setGlowActive(stationNbt, false);
            return;
        }

        if (isWithinAcceptableRadius(stack, world, player.x, player.z)) {
            setGlowActive(stationNbt, false);
        }
    }

    public LivingEntity getTrackedEntity(ItemStack stack, World world) {
        if (stack == null || world == null) {
            return null;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        if (stationNbt.contains(TRACKED_ENTITY_PERSISTENT_ID_KEY)) {
            String persistentId = stationNbt.getString(TRACKED_ENTITY_PERSISTENT_ID_KEY);
            Entity resolvedByPersistentId = findEntityByPersistentId(world, persistentId);
            if (resolvedByPersistentId instanceof LivingEntity livingEntity && !livingEntity.dead) {
                stationNbt.putInt(TRACKED_ENTITY_ID_KEY, livingEntity.id);
                writeLastKnownPosition(stationNbt, livingEntity);
                return livingEntity;
            }
        }

        if (stationNbt.contains(TRACKED_ENTITY_ID_KEY)) {
            int runtimeEntityId = stationNbt.getInt(TRACKED_ENTITY_ID_KEY);
            Entity resolvedByRuntimeId = findEntityByRuntimeId(world, runtimeEntityId);
            if (resolvedByRuntimeId instanceof LivingEntity livingEntity && !livingEntity.dead) {
                String persistentId = PersistentEntityIds.getOrCreate(livingEntity);
                if (persistentId != null && !persistentId.isEmpty()) {
                    stationNbt.putString(TRACKED_ENTITY_PERSISTENT_ID_KEY, persistentId);
                }
                writeLastKnownPosition(stationNbt, livingEntity);
                return livingEntity;
            }
        }

        return null;
    }

    public String getTrackedEntityName(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        if (!stationNbt.contains(TRACKED_ENTITY_NAME_KEY)) {
            return null;
        }

        return stationNbt.getString(TRACKED_ENTITY_NAME_KEY);
    }

    public boolean hasTrackedLastPosition(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        return stationNbt.contains(TRACKED_LAST_X_KEY)
                && stationNbt.contains(TRACKED_LAST_Y_KEY)
                && stationNbt.contains(TRACKED_LAST_Z_KEY)
                && stationNbt.contains(TRACKED_LAST_DIMENSION_KEY);
    }

    public boolean isTrackedLastPositionInWorld(ItemStack stack, World world) {
        if (!hasTrackedLastPosition(stack) || world == null || world.dimension == null) {
            return false;
        }

        return stack.getStationNbt().getInt(TRACKED_LAST_DIMENSION_KEY) == world.dimension.id;
    }

    public double getTrackedLastX(ItemStack stack) {
        return stack.getStationNbt().getDouble(TRACKED_LAST_X_KEY);
    }

    public double getTrackedLastY(ItemStack stack) {
        return stack.getStationNbt().getDouble(TRACKED_LAST_Y_KEY);
    }

    public double getTrackedLastZ(ItemStack stack) {
        return stack.getStationNbt().getDouble(TRACKED_LAST_Z_KEY);
    }

    public boolean isTrailActive(ItemStack stack) {
        return isGlowActive(stack);
    }

    public boolean isGlowActive(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        return stationNbt.getBoolean(TARGET_GLOW_ACTIVE_KEY) || stationNbt.getBoolean(LEGACY_TRAIL_ACTIVE_KEY);
    }

    public boolean shouldSpinNearLastPosition(ItemStack stack, World world, double seekerX, double seekerZ) {
        if (!hasTrackedLastPosition(stack) || !isTrackedLastPositionInWorld(stack, world)) {
            return false;
        }

        double dx = getTrackedLastX(stack) - seekerX;
        double dz = getTrackedLastZ(stack) - seekerZ;
        if (dx * dx + dz * dz > 100.0D * 100.0D) {
            return false;
        }

        int lastBlockX = MathHelper.floor(getTrackedLastX(stack));
        int lastBlockY = MathHelper.floor(getTrackedLastY(stack));
        int lastBlockZ = MathHelper.floor(getTrackedLastZ(stack));
        return world.isPosLoaded(lastBlockX, lastBlockY, lastBlockZ);
    }

    public static boolean shouldHighlightEntityForPlayer(PlayerEntity player, LivingEntity candidate) {
        if (player == null || candidate == null || player.world == null || candidate.world != player.world) {
            return false;
        }

        if (!(DerggyCraft.GOLDEN_COMPASS_ITEM instanceof GoldenCompassItem compassItem)) {
            return false;
        }

        if (player.inventory == null || player.inventory.main == null) {
            return false;
        }

        for (ItemStack stack : player.inventory.main) {
            if (stack == null || stack.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id) {
                continue;
            }

            if (!compassItem.isGlowActive(stack)) {
                continue;
            }

            if (!compassItem.isTrackingEntity(stack, candidate)) {
                continue;
            }

            if (compassItem.isWithinAcceptableRadius(stack, player.world, player.x, player.z)) {
                continue;
            }

            return true;
        }

        return false;
    }

    private static Entity findEntityByRuntimeId(World world, int runtimeEntityId) {
        for (Object worldEntity : world.entities) {
            if (worldEntity instanceof Entity entity && entity.id == runtimeEntityId) {
                return entity;
            }
        }

        for (Object worldPlayer : world.players) {
            if (worldPlayer instanceof Entity entity && entity.id == runtimeEntityId) {
                return entity;
            }
        }

        return null;
    }

    private static Entity findEntityByPersistentId(World world, String persistentId) {
        if (persistentId == null || persistentId.isEmpty()) {
            return null;
        }

        for (Object worldEntity : world.entities) {
            if (worldEntity instanceof Entity entity) {
                String entityPersistentId = PersistentEntityIds.getOrCreate(entity);
                if (persistentId.equals(entityPersistentId)) {
                    return entity;
                }
            }
        }

        for (Object worldPlayer : world.players) {
            if (worldPlayer instanceof Entity entity) {
                String entityPersistentId = PersistentEntityIds.getOrCreate(entity);
                if (persistentId.equals(entityPersistentId)) {
                    return entity;
                }
            }
        }

        return null;
    }

    private static String getEntityDisplayName(LivingEntity target) {
        if (target instanceof PlayerEntity player && player.name != null && !player.name.isEmpty()) {
            return player.name;
        }

        String registryName = EntityRegistry.getId(target);
        if (registryName != null && !registryName.isEmpty()) {
            return registryName;
        }

        return target.getClass().getSimpleName();
    }

    private static void writeLastKnownPosition(NbtCompound stationNbt, LivingEntity target) {
        stationNbt.putDouble(TRACKED_LAST_X_KEY, target.x);
        stationNbt.putDouble(TRACKED_LAST_Y_KEY, target.y);
        stationNbt.putDouble(TRACKED_LAST_Z_KEY, target.z);
        if (target.world != null && target.world.dimension != null) {
            stationNbt.putInt(TRACKED_LAST_DIMENSION_KEY, target.world.dimension.id);
        }
    }

    private static boolean isLocked(NbtCompound stationNbt) {
        return stationNbt.contains(TRACKED_ENTITY_ID_KEY)
                || stationNbt.contains(TRACKED_ENTITY_PERSISTENT_ID_KEY)
                || stationNbt.contains(TRACKED_ENTITY_NAME_KEY);
    }

    private static void setGlowActive(NbtCompound stationNbt, boolean active) {
        stationNbt.putBoolean(TARGET_GLOW_ACTIVE_KEY, active);
        stationNbt.putBoolean(LEGACY_TRAIL_ACTIVE_KEY, active);
    }

    private boolean isWithinAcceptableRadius(ItemStack stack, World world, double seekerX, double seekerZ) {
        if (!hasTrackedLastPosition(stack) || !isTrackedLastPositionInWorld(stack, world)) {
            return false;
        }

        double dx = getTrackedLastX(stack) - seekerX;
        double dz = getTrackedLastZ(stack) - seekerZ;
        return dx * dx + dz * dz <= TARGET_REACHED_DISTANCE_SQ;
    }

    private boolean isTrackingEntity(ItemStack stack, LivingEntity candidate) {
        if (stack == null || candidate == null) {
            return false;
        }

        NbtCompound stationNbt = stack.getStationNbt();
        if (stationNbt.contains(TRACKED_ENTITY_PERSISTENT_ID_KEY)) {
            String expectedPersistentId = stationNbt.getString(TRACKED_ENTITY_PERSISTENT_ID_KEY);
            if (!expectedPersistentId.isEmpty()) {
                String candidatePersistentId = PersistentEntityIds.getOrCreate(candidate);
                if (expectedPersistentId.equals(candidatePersistentId)) {
                    return true;
                }
            }
        }

        return stationNbt.contains(TRACKED_ENTITY_ID_KEY)
                && stationNbt.getInt(TRACKED_ENTITY_ID_KEY) == candidate.id;
    }
}