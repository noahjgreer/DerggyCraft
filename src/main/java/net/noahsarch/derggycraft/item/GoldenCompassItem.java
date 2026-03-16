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
    private static final String TRAIL_ACTIVE_KEY = "TrailActive";
    private static final String TRAIL_START_X_KEY = "TrailStartX";
    private static final String TRAIL_START_Y_KEY = "TrailStartY";
    private static final String TRAIL_START_Z_KEY = "TrailStartZ";
    private static final String TRAIL_DEST_X_KEY = "TrailDestX";
    private static final String TRAIL_DEST_Y_KEY = "TrailDestY";
    private static final String TRAIL_DEST_Z_KEY = "TrailDestZ";
    private static final String TRAIL_STEP_KEY = "TrailStep";
    private static final String TRAIL_STEP_COUNT_KEY = "TrailStepCount";
    private static final int MAX_TRAIL_STEPS = 96;
    private static final double TRAIL_STEP_DISTANCE = 16.0D;
    private static final double TRAIL_REACHED_DISTANCE_SQ = 9.0D;

    public GoldenCompassItem(Identifier identifier) {
        super(identifier);
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
        stationNbt.putBoolean(TRAIL_ACTIVE_KEY, false);

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
        if (isTrailActive(stack)) {
            return new String[]{
                    originalTooltip,
                    "\u00a77Tracking: " + stationNbt.getString(TRACKED_ENTITY_NAME_KEY),
                    "\u00a78Trail mode: active",
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
        double targetX;
        double targetY;
        double targetZ;
        if (trackedEntity != null) {
            targetX = trackedEntity.x;
            targetY = trackedEntity.y;
            targetZ = trackedEntity.z;
            writeLastKnownPosition(stationNbt, trackedEntity);
        } else if (hasTrackedLastPosition(stack) && isTrackedLastPositionInWorld(stack, world)) {
            targetX = getTrackedLastX(stack);
            targetY = getTrackedLastY(stack);
            targetZ = getTrackedLastZ(stack);
        } else {
            return stack;
        }

        startTrail(stationNbt, user, targetX, targetY, targetZ);
        updateTrailWaypoint(stationNbt, world, user);
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

            if (stationNbt.getBoolean(TRAIL_ACTIVE_KEY)) {
                double dx = trackedEntity.x - stationNbt.getDouble(TRAIL_DEST_X_KEY);
                double dz = trackedEntity.z - stationNbt.getDouble(TRAIL_DEST_Z_KEY);
                if (dx * dx + dz * dz > 36.0D) {
                    startTrail(stationNbt, player, trackedEntity.x, trackedEntity.y, trackedEntity.z);
                } else {
                    stationNbt.putDouble(TRAIL_DEST_X_KEY, trackedEntity.x);
                    stationNbt.putDouble(TRAIL_DEST_Y_KEY, trackedEntity.y);
                    stationNbt.putDouble(TRAIL_DEST_Z_KEY, trackedEntity.z);
                }
            }
        }

        if (selected && stationNbt.getBoolean(TRAIL_ACTIVE_KEY) && isTrackedLastPositionInWorld(stack, world)) {
            updateTrailWaypoint(stationNbt, world, player);
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
        if (stack == null) {
            return false;
        }

        return stack.getStationNbt().getBoolean(TRAIL_ACTIVE_KEY);
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

    private static void writeLastKnownPosition(NbtCompound stationNbt, int dimensionId, double x, double y, double z) {
        stationNbt.putDouble(TRACKED_LAST_X_KEY, x);
        stationNbt.putDouble(TRACKED_LAST_Y_KEY, y);
        stationNbt.putDouble(TRACKED_LAST_Z_KEY, z);
        stationNbt.putInt(TRACKED_LAST_DIMENSION_KEY, dimensionId);
    }

    private static boolean isLocked(NbtCompound stationNbt) {
        return stationNbt.contains(TRACKED_ENTITY_ID_KEY)
                || stationNbt.contains(TRACKED_ENTITY_PERSISTENT_ID_KEY)
                || stationNbt.contains(TRACKED_ENTITY_NAME_KEY);
    }

    private static void startTrail(NbtCompound stationNbt, PlayerEntity user, double targetX, double targetY, double targetZ) {
        stationNbt.putBoolean(TRAIL_ACTIVE_KEY, true);
        stationNbt.putDouble(TRAIL_START_X_KEY, user.x);
        stationNbt.putDouble(TRAIL_START_Y_KEY, user.y);
        stationNbt.putDouble(TRAIL_START_Z_KEY, user.z);
        stationNbt.putDouble(TRAIL_DEST_X_KEY, targetX);
        stationNbt.putDouble(TRAIL_DEST_Y_KEY, targetY);
        stationNbt.putDouble(TRAIL_DEST_Z_KEY, targetZ);
        stationNbt.putInt(TRAIL_STEP_KEY, 1);

        int stepCount = resolveTrailStepCount(user.x, user.z, targetX, targetZ);
        stationNbt.putInt(TRAIL_STEP_COUNT_KEY, stepCount);
    }

    private static int resolveTrailStepCount(double startX, double startZ, double endX, double endZ) {
        double dx = endX - startX;
        double dz = endZ - startZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        int steps = (int) Math.ceil(horizontalDistance / TRAIL_STEP_DISTANCE);
        if (steps < 1) {
            return 1;
        }
        return Math.min(MAX_TRAIL_STEPS, steps);
    }

    private static void updateTrailWaypoint(NbtCompound stationNbt, World world, PlayerEntity user) {
        if (!stationNbt.getBoolean(TRAIL_ACTIVE_KEY)) {
            return;
        }

        int stepCount = stationNbt.getInt(TRAIL_STEP_COUNT_KEY);
        if (stepCount <= 0) {
            stationNbt.putBoolean(TRAIL_ACTIVE_KEY, false);
            return;
        }

        int step = stationNbt.getInt(TRAIL_STEP_KEY);
        if (step < 1) {
            step = 1;
        }
        if (step > stepCount) {
            step = stepCount;
        }

        double startX = stationNbt.getDouble(TRAIL_START_X_KEY);
        double startY = stationNbt.getDouble(TRAIL_START_Y_KEY);
        double startZ = stationNbt.getDouble(TRAIL_START_Z_KEY);
        double destX = stationNbt.getDouble(TRAIL_DEST_X_KEY);
        double destY = stationNbt.getDouble(TRAIL_DEST_Y_KEY);
        double destZ = stationNbt.getDouble(TRAIL_DEST_Z_KEY);

        double t = (double) step / (double) stepCount;
        double waypointX = startX + (destX - startX) * t;
        double waypointY = startY + (destY - startY) * t;
        double waypointZ = startZ + (destZ - startZ) * t;

        writeLastKnownPosition(stationNbt, world.dimension.id, waypointX, waypointY, waypointZ);

        double waypointDx = waypointX - user.x;
        double waypointDz = waypointZ - user.z;
        if (waypointDx * waypointDx + waypointDz * waypointDz <= TRAIL_REACHED_DISTANCE_SQ) {
            if (step < stepCount) {
                stationNbt.putInt(TRAIL_STEP_KEY, step + 1);
            } else {
                writeLastKnownPosition(stationNbt, world.dimension.id, destX, destY, destZ);
                stationNbt.putBoolean(TRAIL_ACTIVE_KEY, false);
            }
        }

        double targetDx = destX - user.x;
        double targetDz = destZ - user.z;
        if (targetDx * targetDx + targetDz * targetDz <= TRAIL_REACHED_DISTANCE_SQ) {
            writeLastKnownPosition(stationNbt, world.dimension.id, destX, destY, destZ);
            stationNbt.putBoolean(TRAIL_ACTIVE_KEY, false);
        }
    }
}