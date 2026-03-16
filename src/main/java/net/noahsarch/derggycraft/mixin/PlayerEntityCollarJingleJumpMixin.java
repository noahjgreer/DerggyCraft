package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.sound.CollarJingleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityCollarJingleJumpMixin {
    @Inject(method = "jump", at = @At("TAIL"))
    private void derggycraft$playCollarJingleOnJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!CollarJingleHelper.hasCollarEquipped(player)) {
            return;
        }

        CollarJingleHelper.playRandomNearbyJingle(player, 0.65F, 0.85F, 0.95F, 1.12F);
    }
}