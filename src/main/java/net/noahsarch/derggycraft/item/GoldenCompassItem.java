package net.noahsarch.derggycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

    public GoldenCompassItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public void useOnEntity(ItemStack stack, LivingEntity target) {
        NbtCompound stationNbt = stack.getStationNbt();
        stationNbt.putInt(TRACKED_ENTITY_ID_KEY, target.id);
        stationNbt.putString(TRACKED_ENTITY_NAME_KEY, getEntityDisplayName(target));
        writeLastKnownPosition(stationNbt, target);

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

        return new String[]{
                originalTooltip,
                "\u00a77Tracking: " + stationNbt.getString(TRACKED_ENTITY_NAME_KEY)
        };
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

    public double getTrackedLastZ(ItemStack stack) {
        return stack.getStationNbt().getDouble(TRACKED_LAST_Z_KEY);
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
}