package net.derggy.craft.derggycraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.item.UseOnBlockFirst;
import net.modificationstation.stationapi.api.item.StationItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Iron Compass: right-click a block to lock target coordinates.
 * Shows coordinates (or sign text) in tooltip.
 */
public class IronCompassItem extends TemplateItem implements UseOnBlockFirst, CustomTooltipProvider {

    public IronCompassItem(Identifier id) {
        super(id);
        this.setMaxCount(1);
    }

    @Override
    public boolean onUseOnBlockFirst(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int sideClicked) {
        NbtCompound nbt = ((StationItemStack) (Object) stack).getStationNbt();

        // Don't allow re-locking if already locked
        if (nbt.contains("locked") && nbt.getBoolean("locked")) {
            return false;
        }

        // Store target coordinates
        nbt.putInt("targetX", x);
        nbt.putInt("targetY", y);
        nbt.putInt("targetZ", z);
        nbt.putBoolean("locked", true);

        // Check if the target block is a sign
        int blockId = world.getBlockId(x, y, z);
        if (blockId == Block.SIGN.id || blockId == Block.WALL_SIGN.id) {
            BlockEntity be = world.getBlockEntity(x, y, z);
            if (be instanceof SignBlockEntity) {
                SignBlockEntity sign = (SignBlockEntity) be;
                StringBuilder signText = new StringBuilder();
                for (int i = 0; i < sign.texts.length; i++) {
                    String line = sign.texts[i];
                    if (line != null && !line.isEmpty()) {
                        if (signText.length() > 0) signText.append(" ");
                        signText.append(line);
                    }
                }
                if (signText.length() > 0) {
                    nbt.putString("signText", signText.toString());
                }
            }
        }

        return true;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        NbtCompound nbt = ((StationItemStack) (Object) stack).getStationNbt();

        if (!nbt.contains("locked") || !nbt.getBoolean("locked")) {
            return new String[]{originalTooltip};
        }

        // If sign text is stored, show it in quotes
        if (nbt.contains("signText")) {
            String signText = nbt.getString("signText");
            return new String[]{
                    originalTooltip,
                    "\u00a77\"" + signText + "\""
            };
        }

        // Otherwise show coordinates
        int tx = nbt.getInt("targetX");
        int ty = nbt.getInt("targetY");
        int tz = nbt.getInt("targetZ");
        return new String[]{
                originalTooltip,
                "\u00a77" + tx + ", " + ty + ", " + tz
        };
    }
}
