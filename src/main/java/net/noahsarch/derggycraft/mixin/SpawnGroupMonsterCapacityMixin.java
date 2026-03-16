package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMonsterCapacityMixin {
    @Shadow
    @Final
    private int capacity;

    @Inject(method = "getCapacity", at = @At("HEAD"), cancellable = true)
    private void derggycraft$doubleMonsterCapacity(CallbackInfoReturnable<Integer> cir) {
        if ((SpawnGroup) (Object) this == SpawnGroup.MONSTER) {
            cir.setReturnValue(this.capacity * 2);
        }
    }
}