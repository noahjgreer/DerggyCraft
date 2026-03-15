package net.noahsarch.derggycraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void derggycraft$allowSneakPlacement(
            World world,
            int x,
            int y,
            int z,
            PlayerEntity player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (player != null && player.isSneaking()) {
            // Returning false allows the held block item to handle placement instead of opening the chest.
            cir.setReturnValue(false);
        }
    }

    @Redirect(
            method = "onUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;shouldSuffocate(III)Z")
    )
    private boolean derggycraft$allowStackedChestAccess(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) == Block.CHEST.id) {
            return false;
        }
        return world.shouldSuffocate(x, y, z);
    }
}