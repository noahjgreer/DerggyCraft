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
import org.jetbrains.annotations.NotNull;

public class GoldenCompassItem extends TemplateItem implements CustomTooltipProvider {
    private static final String TRACKED_ENTITY_ID_KEY = "TrackedEntityRuntimeId";
    private static final String TRACKED_ENTITY_NAME_KEY = "TrackedEntityName";

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
        if (!stationNbt.contains(TRACKED_ENTITY_ID_KEY)) {
            return null;
        }

        int runtimeEntityId = stationNbt.getInt(TRACKED_ENTITY_ID_KEY);
        Entity resolved = findEntityByRuntimeId(world, runtimeEntityId);
        if (resolved instanceof LivingEntity livingEntity && !livingEntity.dead) {
            return livingEntity;
        }
        return null;
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
}