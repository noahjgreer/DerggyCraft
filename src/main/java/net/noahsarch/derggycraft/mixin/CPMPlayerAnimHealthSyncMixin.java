package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(targets = "com.tom.cpm.common.PlayerAnimUpdater", remap = false)
public abstract class CPMPlayerAnimHealthSyncMixin {
    private static Field derggycraft$cpmHealthField;

    @Inject(method = "accept(Lnet/minecraft/entity/player/PlayerEntity;Lcom/tom/cpm/shared/animation/ServerAnimationState;)V", at = @At("TAIL"), remap = false, require = 0)
    private void derggycraft$normalizeHealthState(PlayerEntity player, Object state, CallbackInfo ci) {
        if (player == null || state == null) {
            return;
        }

        float maxHealth = Math.max(1.0F, player.maxHealth);
        float normalizedHealth = Math.max(0.0F, Math.min(1.0F, player.health / maxHealth));
        this.derggycraft$setHealth(state, normalizedHealth);
    }

    private void derggycraft$setHealth(Object state, float value) {
        try {
            if (derggycraft$cpmHealthField == null || derggycraft$cpmHealthField.getDeclaringClass() != state.getClass()) {
                Field healthField = state.getClass().getField("health");
                healthField.setAccessible(true);
                derggycraft$cpmHealthField = healthField;
            }
            derggycraft$cpmHealthField.setFloat(state, value);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
