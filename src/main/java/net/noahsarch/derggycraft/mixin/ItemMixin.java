package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(
            method = "useOnBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;IIII)Z",
            at = @At("HEAD")
    )
    private void derggycraft$trackBlockForIronCompass(
            ItemStack stack,
            PlayerEntity user,
            World world,
            int x,
            int y,
            int z,
            int side,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if ((Object) this != Item.COMPASS || stack == null || world == null) {
            return;
        }

        IronCompassTracking.setTrackedBlock(stack, world, x, y, z);
    }
}