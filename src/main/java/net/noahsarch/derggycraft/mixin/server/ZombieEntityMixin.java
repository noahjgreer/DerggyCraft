package net.noahsarch.derggycraft.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.noahsarch.derggycraft.DerggyCraft;

@Mixin(targets = "net.minecraft.entity.mob.ZombieEntity")
public class ZombieEntityMixin {
    @Inject(method = "getDroppedItemId()I", at = @At("HEAD"), cancellable = true)
    private void derggycraft$dropRottenFlesh(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(DerggyCraft.ROTTEN_FLESH_ITEM.id);
    }
}
