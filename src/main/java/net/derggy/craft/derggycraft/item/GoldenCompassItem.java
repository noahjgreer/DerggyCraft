package net.derggy.craft.derggycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.item.StationItemStack;
import net.modificationstation.stationapi.api.item.UseOnEntityFirst;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Golden Compass: right-click an entity to lock and continuously track it.
 * Server sends coordinates to the client via network messages.
 */
public class GoldenCompassItem extends TemplateItem implements UseOnEntityFirst, CustomTooltipProvider {

    public GoldenCompassItem(Identifier id) {
        super(id);
        this.setMaxCount(1);
    }

    @Override
    public boolean onUseOnEntityFirst(ItemStack stack, PlayerEntity player, World world, Entity entity) {
        NbtCompound nbt = ((StationItemStack) (Object) stack).getStationNbt();

        // Don't allow re-locking if already locked
        if (nbt.contains("locked") && nbt.getBoolean("locked")) {
            return false;
        }

        nbt.putBoolean("locked", true);
        nbt.putInt("targetId", entity.id);

        // Store display name
        if (entity instanceof PlayerEntity) {
            nbt.putString("targetName", ((PlayerEntity) entity).name);
            nbt.putString("targetType", "player");
        } else {
            String typeName = entity.getClass().getSimpleName().replace("Entity", "");
            nbt.putString("targetName", typeName);
            nbt.putString("targetType", "mob");
        }

        return true;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        NbtCompound nbt = ((StationItemStack) (Object) stack).getStationNbt();

        if (!nbt.contains("locked") || !nbt.getBoolean("locked")) {
            return new String[]{originalTooltip};
        }

        String name = nbt.getString("targetName");
        return new String[]{
                originalTooltip,
                "\u00a77" + name
        };
    }
}
