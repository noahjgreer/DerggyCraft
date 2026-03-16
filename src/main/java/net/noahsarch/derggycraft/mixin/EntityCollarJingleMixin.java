package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.sound.CollarJingleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityCollarJingleMixin {
    @Inject(
            method = "move(DDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onSteppedOn(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void derggycraft$playCollarJingleOnStep(double dx, double dy, double dz, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        if (!CollarJingleHelper.hasCollarEquipped(player)) {
            return;
        }

        CollarJingleHelper.playRandomNearbyJingle(entity, 0.45F, 0.65F, 0.92F, 1.08F);
    }
}