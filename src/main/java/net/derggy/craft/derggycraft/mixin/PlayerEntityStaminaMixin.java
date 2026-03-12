package net.derggy.craft.derggycraft.mixin;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.derggy.craft.derggycraft.stamina.StaminaAccessor;
import net.derggy.craft.derggycraft.stamina.StaminaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects stamina data storage and sprinting logic into PlayerEntity.
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityStaminaMixin implements StaminaAccessor {

    @Unique
    private float derggycraft_stamina = 20.0f;

    @Unique
    private float derggycraft_prevStamina = 20.0f;

    @Unique
    private int derggycraft_regenCooldown = 0;

    @Unique
    private boolean derggycraft_sprinting = false;

    // StaminaAccessor implementation
    @Override
    public float derggycraft_getStamina() {
        return this.derggycraft_stamina;
    }

    @Override
    public void derggycraft_setStamina(float stamina) {
        this.derggycraft_stamina = stamina;
    }

    @Override
    public int derggycraft_getRegenCooldown() {
        return this.derggycraft_regenCooldown;
    }

    @Override
    public void derggycraft_setRegenCooldown(int cooldown) {
        this.derggycraft_regenCooldown = cooldown;
    }

    @Override
    public boolean derggycraft_isSprinting() {
        return this.derggycraft_sprinting;
    }

    @Override
    public void derggycraft_setSprinting(boolean sprinting) {
        this.derggycraft_sprinting = sprinting;
    }

    @Override
    public float derggycraft_getPrevStamina() {
        return this.derggycraft_prevStamina;
    }

    @Override
    public void derggycraft_setPrevStamina(float prevStamina) {
        this.derggycraft_prevStamina = prevStamina;
    }

    /**
     * Initialize stamina when player is created.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, CallbackInfo ci) {
        this.derggycraft_stamina = DerggyCraftConfig.CONFIG.stamina.maxStamina;
        this.derggycraft_prevStamina = this.derggycraft_stamina;
    }

    /**
     * Handle stamina drain and regeneration each tick.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Store previous stamina for smooth interpolation
        this.derggycraft_prevStamina = this.derggycraft_stamina;

        // Handle sprinting drain
        if (this.derggycraft_sprinting && player.velocityX * player.velocityX + player.velocityZ * player.velocityZ > 0.001) {
            StaminaManager.forceDrainStamina(player, DerggyCraftConfig.CONFIG.stamina.sprintDrainRate);
            
            // Stop sprinting if out of stamina
            if (this.derggycraft_stamina <= 0) {
                this.derggycraft_sprinting = false;
            }
        } else {
            // Regenerate stamina when not sprinting
            StaminaManager.tickRegen(player);
        }
    }
}
