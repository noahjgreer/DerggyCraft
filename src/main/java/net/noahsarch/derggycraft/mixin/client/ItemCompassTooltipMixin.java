package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemCompassTooltipMixin implements CustomTooltipProvider {
    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        if ((Object) this != Item.COMPASS || !IronCompassTracking.hasTrackedBlock(stack)) {
            return new String[]{originalTooltip};
        }

        return new String[]{
                originalTooltip,
                "\u00a77Tracking: "
                        + IronCompassTracking.getTrackedX(stack) + ", "
                        + IronCompassTracking.getTrackedY(stack) + ", "
                        + IronCompassTracking.getTrackedZ(stack)
        };
    }
}