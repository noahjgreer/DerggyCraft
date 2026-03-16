package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.util.math.MathHelper;
import net.noahsarch.derggycraft.stamina.StaminaAccessor;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudStaminaMixin {
    @Unique
    private static final String DERGGYCRAFT$STAMINA_BAR_TEXTURE = "/assets/derggycraft/stationapi/textures/gui/stamina_bar.png";

    @Unique
    private static final String DERGGYCRAFT$STAMINA_PROGRESS_TEXTURE = "/assets/derggycraft/stationapi/textures/gui/stamina_progress.png";

    @Unique
    private static final int DERGGYCRAFT$BAR_WIDTH = 182;

    @Unique
    private static final int DERGGYCRAFT$BAR_HEIGHT = 9;

    @Unique
    private static final int DERGGYCRAFT$STATUS_ICON_SHIFT = 10;

    @Unique
    private static final float DERGGYCRAFT$BAR_U_SCALE = 1.0F / 27.0F;

    @Unique
    private static final float DERGGYCRAFT$BAR_V_SCALE = 1.0F / 9.0F;

    @Shadow
    private Minecraft minecraft;

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V")
    )
    private void derggycraft$offsetStatusIcons(InGameHud hud, int x, int y, int u, int v, int width, int height) {
        int adjustedY = y;
        if (width == 9 && height == 9 && (v == 0 || v == 9 || v == 18)) {
            adjustedY -= DERGGYCRAFT$STATUS_ICON_SHIFT;
        }

        ((DrawContextInvoker) (Object) hud).derggycraft$invokeDrawTexture(x, adjustedY, u, v, width, height);
    }

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V", ordinal = 1, shift = At.Shift.AFTER),
            require = 0
    )
    private void derggycraft$renderStaminaBar(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.player == null || !(this.minecraft.player instanceof StaminaAccessor stamina)) {
            return;
        }

        ScreenScaler scaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        int scaledWidth = scaler.getScaledWidth();
        int scaledHeight = scaler.getScaledHeight();
        int x = scaledWidth / 2 - 91;
        int y = scaledHeight - 32;

        this.derggycraft$drawThreeSliceBar(DERGGYCRAFT$STAMINA_BAR_TEXTURE, x, y, DERGGYCRAFT$BAR_WIDTH, DERGGYCRAFT$BAR_HEIGHT);

        double max = Math.max(1.0, stamina.derggycraft$getMaxStamina());
        float ratio = (float) (stamina.derggycraft$getDisplayStamina() / max);
        ratio = Math.max(0.0F, Math.min(1.0F, ratio));

        int fillWidth = MathHelper.floor(ratio * DERGGYCRAFT$BAR_WIDTH + 0.5F);
        if (fillWidth <= 0) {
            return;
        }

        int scale = scaler.scaleFactor;
        int scissorX = x * scale;
        int scissorY = this.minecraft.displayHeight - (y + DERGGYCRAFT$BAR_HEIGHT) * scale;
        int scissorWidth = fillWidth * scale;
        int scissorHeight = DERGGYCRAFT$BAR_HEIGHT * scale;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, Math.max(0, scissorY), Math.max(0, scissorWidth), Math.max(0, scissorHeight));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        this.derggycraft$drawThreeSliceBar(DERGGYCRAFT$STAMINA_PROGRESS_TEXTURE, x, y, DERGGYCRAFT$BAR_WIDTH, DERGGYCRAFT$BAR_HEIGHT);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Unique
    private void derggycraft$drawThreeSliceBar(String texturePath, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0 || this.minecraft == null || this.minecraft.textureManager == null) {
            return;
        }

        int textureId = this.minecraft.textureManager.getTextureId(texturePath);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        if (width <= 18) {
            this.derggycraft$drawBarSegment(x, y, width, height, 0, width);
            return;
        }

        this.derggycraft$drawBarSegment(x, y, 9, height, 0, 9);

        int centerX = x + 9;
        int centerWidth = width - 18;
        while (centerWidth > 0) {
            int segmentWidth = Math.min(9, centerWidth);
            this.derggycraft$drawBarSegment(centerX, y, segmentWidth, height, 9, 9 + segmentWidth);
            centerX += segmentWidth;
            centerWidth -= segmentWidth;
        }

        this.derggycraft$drawBarSegment(x + width - 9, y, 9, height, 18, 27);
    }

    @Unique
    private void derggycraft$drawBarSegment(int x, int y, int width, int height, int uStart, int uEnd) {
        float u0 = uStart * DERGGYCRAFT$BAR_U_SCALE;
        float u1 = uEnd * DERGGYCRAFT$BAR_U_SCALE;
        float v0 = 0.0F;
        float v1 = 9.0F * DERGGYCRAFT$BAR_V_SCALE;

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + height, -90.0, u0, v1);
        tessellator.vertex(x + width, y + height, -90.0, u1, v1);
        tessellator.vertex(x + width, y, -90.0, u1, v0);
        tessellator.vertex(x, y, -90.0, u0, v0);
        tessellator.draw();
    }
}