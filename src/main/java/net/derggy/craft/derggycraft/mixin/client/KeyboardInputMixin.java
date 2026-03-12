package net.derggy.craft.derggycraft.mixin.client;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.derggy.craft.derggycraft.stamina.StaminaAccessor;
import net.derggy.craft.derggycraft.stamina.StaminaManager;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles sprint input via double-tap W key detection.
 */
@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    @Shadow
    private boolean[] keys;

    @Shadow
    private GameOptions options;

    @Unique
    private long derggycraft_lastForwardPress = 0;

    @Unique
    private boolean derggycraft_wasForwardPressed = false;

    @Unique
    private static final long DOUBLE_TAP_WINDOW = 300; // milliseconds

    /**
     * Detect double-tap of forward key to toggle sprint.
     */
    @Inject(method = "updateKey", at = @At("TAIL"))
    private void onUpdateKey(int key, boolean keyDown, CallbackInfo ci) {
        if (!DerggyCraftConfig.CONFIG.stamina.doubleTapSprint) return;
        
        // Check if this is the forward key
        if (key == this.options.forwardKey.code) {
            if (keyDown && !this.derggycraft_wasForwardPressed) {
                // Key just pressed
                long now = System.currentTimeMillis();
                if (now - this.derggycraft_lastForwardPress < DOUBLE_TAP_WINDOW) {
                    // Double tap detected - toggle sprint flag
                    // Actual sprint handling is done in update() method through player reference
                }
                this.derggycraft_lastForwardPress = now;
            }
            this.derggycraft_wasForwardPressed = keyDown;
        }
    }

    /**
     * Handle sprint movement speed and toggle on double-tap.
     */
    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(PlayerEntity player, CallbackInfo ci) {
        if (player == null) return;
        
        StaminaAccessor accessor = (StaminaAccessor) player;
        boolean forwardPressed = this.keys[0]; // forward key

        // Handle double-tap sprint toggle
        if (DerggyCraftConfig.CONFIG.stamina.doubleTapSprint) {
            long now = System.currentTimeMillis();
            if (forwardPressed && !this.derggycraft_wasForwardPressed) {
                if (now - this.derggycraft_lastForwardPress < DOUBLE_TAP_WINDOW) {
                    // Toggle sprint if we have stamina
                    if (StaminaManager.canSprint(player)) {
                        accessor.derggycraft_setSprinting(!accessor.derggycraft_isSprinting());
                    }
                }
                this.derggycraft_lastForwardPress = now;
            }
            this.derggycraft_wasForwardPressed = forwardPressed;
        }

        // Stop sprinting if not moving forward or out of stamina
        if (accessor.derggycraft_isSprinting()) {
            if (!forwardPressed || !StaminaManager.canSprint(player)) {
                accessor.derggycraft_setSprinting(false);
            }
        }
    }
}
