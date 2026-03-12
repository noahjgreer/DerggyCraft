package net.derggy.craft.derggycraft.mixin.client;

import net.derggy.craft.derggycraft.stamina.StaminaAccessor;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles sprint speed modification for client player.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {

    public ClientPlayerEntityMixin() {
        super(null);
    }

    /**
     * Apply sprint speed multiplier when sprinting.
     */
    @Inject(method = "tickLiving", at = @At("TAIL"))
    private void onTickLiving(CallbackInfo ci) {
        StaminaAccessor accessor = (StaminaAccessor) this;
        
        if (accessor.derggycraft_isSprinting() && this.forwardSpeed > 0) {
            // Increase forward speed by 30% when sprinting
            this.forwardSpeed *= 1.3f;
        }
    }
}
