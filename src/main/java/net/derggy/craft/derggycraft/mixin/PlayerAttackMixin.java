package net.derggy.craft.derggycraft.mixin;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.derggy.craft.derggycraft.stamina.StaminaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Drains stamina when player attacks and prevents attacking when empty.
 */
@Mixin(PlayerEntity.class)
public class PlayerAttackMixin {

    /**
     * Check if player can attack and drain stamina on attack.
     */
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Check if attack should be prevented due to empty stamina
        if (!StaminaManager.canAttack(player)) {
            ci.cancel();
            return;
        }
        
        // Drain stamina for attack
        StaminaManager.forceDrainStamina(player, DerggyCraftConfig.CONFIG.stamina.attackCost);
    }
}
