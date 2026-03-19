package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.sound.CollarJingleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityCollarJingleMixin {
    @Unique
    private boolean derggycraft$wasOnGround = true;

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

    @Inject(method = "move(DDD)V", at = @At("TAIL"))
    private void derggycraft$broadcastJumpJingleFallback(double dx, double dy, double dz, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        boolean justJumped = this.derggycraft$wasOnGround && !entity.onGround && dy > 0.2D;
        this.derggycraft$wasOnGround = entity.onGround;

        if (!justJumped || entity.world == null || entity.world.isRemote) {
            return;
        }

        if (!(entity instanceof PlayerEntity player) || !CollarJingleHelper.hasCollarEquipped(player)) {
            return;
        }

        CollarJingleHelper.playRandomNearbyJingle(entity, 0.65F, 0.85F, 0.95F, 1.12F);
    }
}