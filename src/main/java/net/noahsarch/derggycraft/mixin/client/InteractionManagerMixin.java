package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.InteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionManager.class)
public abstract class InteractionManagerMixin {
    @Inject(
            method = "interactBlock(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)Z",
            at = @At("HEAD")
    )
    private void derggycraft$trackIronCompassBlockClient(
            PlayerEntity player,
            World world,
            ItemStack item,
            int x,
            int y,
            int z,
            int side,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (item != null && item.itemId == Item.COMPASS.id) {
            IronCompassTracking.setTrackedBlock(item, world, x, y, z);
        }
    }
}