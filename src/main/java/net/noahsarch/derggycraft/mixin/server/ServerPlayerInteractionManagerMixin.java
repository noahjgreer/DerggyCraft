package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Inject(
            method = "interactBlock(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)Z",
            at = @At("HEAD")
    )
    private void derggycraft$trackIronCompassBlockServer(
            PlayerEntity player,
            World world,
            ItemStack stack,
            int x,
            int y,
            int z,
            int side,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (stack != null && stack.itemId == Item.COMPASS.id) {
            IronCompassTracking.setTrackedBlock(stack, world, x, y, z);
        }
    }
}