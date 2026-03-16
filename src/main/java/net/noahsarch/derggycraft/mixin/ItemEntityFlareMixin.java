package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.flare.FlareRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityFlareMixin {
    @org.spongepowered.asm.mixin.Unique
    private boolean derggycraft$flareOrientationInitialized;
    @org.spongepowered.asm.mixin.Unique
    private float derggycraft$flareYawVelocity;
    @org.spongepowered.asm.mixin.Unique
    private float derggycraft$flarePitchVelocity;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;markDead()V"))
    private void derggycraft$handleFlareCustomLifetime(ItemEntity self) {
        if (FlareRuntime.isFlareStack(self.stack)) {
            return;
        }

        self.markDead();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void derggycraft$tickFlareBehavior(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        FlareRuntime.tick(self);

        // Keep movement/server authority vanilla; tumble/topple is client-side visual simulation.
        if (!FlareRuntime.isFlareStack(self.stack) || self.world == null || !self.world.isRemote) {
            return;
        }

        if (!this.derggycraft$flareOrientationInitialized) {
            this.derggycraft$flareOrientationInitialized = true;
            this.derggycraft$flareYawVelocity = (self.world.random.nextFloat() - 0.5F) * 20.0F;
            this.derggycraft$flarePitchVelocity = (self.world.random.nextFloat() - 0.5F) * 26.0F;
        }

        self.prevYaw = self.yaw;
        self.prevPitch = self.pitch;

        double horizontalSpeed = Math.sqrt(self.velocityX * self.velocityX + self.velocityZ * self.velocityZ);
        if (!self.onGround) {
            this.derggycraft$flareYawVelocity += (float) (horizontalSpeed * 18.0D);
            this.derggycraft$flarePitchVelocity += (float) ((Math.abs(self.velocityY) + horizontalSpeed) * 26.0D);
        } else {
            this.derggycraft$flareYawVelocity *= 0.85F;
            this.derggycraft$flarePitchVelocity *= 0.72F;

            if (horizontalSpeed < 0.035D && Math.abs(self.velocityY) < 0.03D) {
                float targetPitch = 88.0F;
                self.pitch += (targetPitch - self.pitch) * 0.2F;
            }
        }

        self.yaw += this.derggycraft$flareYawVelocity;
        self.pitch += this.derggycraft$flarePitchVelocity;
        if (self.pitch > 120.0F) {
            self.pitch = 120.0F;
        } else if (self.pitch < -120.0F) {
            self.pitch = -120.0F;
        }
    }

    @Inject(method = "onPlayerInteraction", at = @At("HEAD"), cancellable = true)
    private void derggycraft$preventFlarePickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!FlareRuntime.isFlareStack(self.stack)) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "markDead", at = @At("HEAD"), require = 0)
    private void derggycraft$clearFlareLightOnRemove(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        FlareRuntime.clearLight(self);
    }
}
