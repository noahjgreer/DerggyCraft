package net.derggy.craft.derggycraft.mixin.client;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.derggy.craft.derggycraft.stamina.StaminaAccessor;
import net.derggy.craft.derggycraft.stamina.StaminaManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds hand sway effect when stamina is low and drops hand when empty.
 */
@Mixin(value = HeldItemRenderer.class, priority = 999)
public class HeldItemRendererMixin {

    @Shadow
    private Minecraft minecraft;

    @Unique
    private float derggycraft_swayTime = 0;

    /**
     * Apply hand sway and lowered hand effects based on stamina.
     * Inject at HEAD to apply transformations before any rendering.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void applyStaminaEffects(float tickDelta, CallbackInfo ci) {
        if (this.minecraft.player == null) return;

        PlayerEntity player = this.minecraft.player;
        StaminaAccessor accessor = (StaminaAccessor) player;
        
        float staminaRatio = StaminaManager.getStaminaRatio(player);
        float lowThreshold = DerggyCraftConfig.CONFIG.stamina.lowStaminaThreshold;

        // Increment sway time
        this.derggycraft_swayTime += tickDelta * 0.05f;

        if (staminaRatio <= 0) {
            // Empty stamina - lower the hand significantly
            GL11.glTranslatef(0.0f, 0.3f, 0.0f);
            GL11.glRotatef(-15.0f, 1.0f, 0.0f, 0.0f);
            
            // Add heavy sway
            float swayX = (float) Math.sin(this.derggycraft_swayTime * 2.0f) * 0.08f;
            float swayY = (float) Math.cos(this.derggycraft_swayTime * 1.5f) * 0.06f;
            GL11.glTranslatef(swayX, swayY, 0.0f);
            
        } else if (staminaRatio < lowThreshold) {
            // Low stamina - gradual sway effect
            float fatigueFactor = 1.0f - (staminaRatio / lowThreshold);
            
            // Sway increases as stamina gets lower
            float swayIntensity = fatigueFactor * 0.04f;
            float swayX = (float) Math.sin(this.derggycraft_swayTime * 3.0f) * swayIntensity;
            float swayY = (float) Math.cos(this.derggycraft_swayTime * 2.3f) * swayIntensity * 0.7f;
            
            GL11.glTranslatef(swayX, swayY, 0.0f);
            
            // Slight downward tilt
            float tiltAmount = fatigueFactor * 5.0f;
            GL11.glRotatef(-tiltAmount, 1.0f, 0.0f, 0.0f);
            
            // Slight lowering
            GL11.glTranslatef(0.0f, fatigueFactor * 0.1f, 0.0f);
        }
    }
}
