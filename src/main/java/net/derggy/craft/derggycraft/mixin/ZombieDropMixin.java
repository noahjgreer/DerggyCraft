package net.derggy.craft.derggycraft.mixin;

import net.derggy.craft.derggycraft.events.init.ItemInit;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces zombie feather drops with rotten flesh.
 */
@Mixin(ZombieEntity.class)
public class ZombieDropMixin {

    @Inject(method = "getDroppedItemId", at = @At("HEAD"), cancellable = true)
    private void replaceDropWithRottenFlesh(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ItemInit.ROTTEN_FLESH.id);
    }
}
