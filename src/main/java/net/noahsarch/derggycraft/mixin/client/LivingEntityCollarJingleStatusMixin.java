package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.sound.BucketSoundStatus;
import net.noahsarch.derggycraft.sound.CollarJingleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCollarJingleStatusMixin {
    @Inject(method = "processServerEntityStatus", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleCollarJingleStatus(byte status, CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof PlayerEntity)) {
            return;
        }

        String bucketPlaybackId = BucketSoundStatus.toPlaybackId(status);
        if (bucketPlaybackId != null) {
            float pitch = 0.95F + living.world.random.nextFloat() * 0.1F;
            living.world.playSound(living, bucketPlaybackId, 1.0F, pitch);
            ci.cancel();
            return;
        }

        if (status != CollarJingleHelper.COLLAR_JINGLE_STATUS) {
            return;
        }

        CollarJingleHelper.playRandomNearbyJingleClient(living, 0.5F, 0.8F, 0.93F, 1.1F);
        ci.cancel();
    }
}
