package net.derggy.craft.derggycraft.mixin.client;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.derggy.craft.derggycraft.events.init.InitListener;
import net.derggy.craft.derggycraft.stamina.StaminaAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects stamina bar rendering into the HUD.
 * The stamina bar is rendered below the health bar, which is shifted up.
 */
@Mixin(value = InGameHud.class, priority = 999)
public abstract class InGameHudMixin extends DrawContext {

    @Shadow
    private Minecraft minecraft;

    @Unique
    private static final int STAMINA_BAR_WIDTH = 81; // 9 icons * 9 pixels each

    @Unique
    private static final int STAMINA_ICON_SIZE = 9;

    @Unique
    private static final int STAMINA_ICONS = 10;

    /**
     * Render the stamina bar after the main HUD rendering.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderStaminaBar(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft.player == null) return;
        if (!this.minecraft.interactionManager.canBeRendered()) return;

        PlayerEntity player = this.minecraft.player;
        StaminaAccessor accessor = (StaminaAccessor) player;

        ScreenScaler scaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        int screenWidth = scaler.getScaledWidth();
        int screenHeight = scaler.getScaledHeight();

        // Calculate interpolated stamina for smooth rendering
        float prevStamina = accessor.derggycraft_getPrevStamina();
        float currentStamina = accessor.derggycraft_getStamina();
        float displayStamina = prevStamina + (currentStamina - prevStamina) * tickDelta;
        float maxStamina = DerggyCraftConfig.CONFIG.stamina.maxStamina;

        // Position: below health bar (health is at screenHeight - 32, we go 10 pixels lower)
        int baseY = screenHeight - 42;
        int baseX = screenWidth / 2 - 91;

        // Bind stamina texture - use stationapi resource path
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.getTextureId(
            "/assets/derggycraft/gui/stamina.png"));

        // Enable blending for overlay effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Draw 10 stamina icons
        for (int i = 0; i < STAMINA_ICONS; ++i) {
            int x = baseX + i * 8;
            int y = baseY;

            // Draw empty bar background (first row, first 9x9 section)
            this.derggycraft_drawStaminaIcon(x, y, 0, 0);

            // Calculate fill amount for this icon
            float staminaPerIcon = maxStamina / STAMINA_ICONS;
            float iconStamina = displayStamina - (i * staminaPerIcon);
            
            if (iconStamina >= staminaPerIcon) {
                // Full icon - draw full stamina overlay (second row, first 9x9)
                this.derggycraft_drawStaminaIcon(x, y, 0, STAMINA_ICON_SIZE);
            } else if (iconStamina > 0) {
                // Partial fill - calculate pixel progress
                float fillRatio = iconStamina / staminaPerIcon;
                
                // Draw half icon if > 50%
                if (fillRatio > 0.5f) {
                    // Draw half stamina icon (second row, second 9x9)
                    this.derggycraft_drawStaminaIcon(x, y, STAMINA_ICON_SIZE, STAMINA_ICON_SIZE);
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        
        // Reset texture to default icons
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.getTextureId("/gui/icons.png"));
    }

    @Unique
    private void derggycraft_drawStaminaIcon(int x, int y, int u, int v) {
        float texU1 = u / 27.0f;
        float texV1 = v / 18.0f;
        float texU2 = (u + STAMINA_ICON_SIZE) / 27.0f;
        float texV2 = (v + STAMINA_ICON_SIZE) / 18.0f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(texU1, texV2);
        GL11.glVertex2f(x, y + STAMINA_ICON_SIZE);
        GL11.glTexCoord2f(texU2, texV2);
        GL11.glVertex2f(x + STAMINA_ICON_SIZE, y + STAMINA_ICON_SIZE);
        GL11.glTexCoord2f(texU2, texV1);
        GL11.glVertex2f(x + STAMINA_ICON_SIZE, y);
        GL11.glTexCoord2f(texU1, texV1);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }
}
