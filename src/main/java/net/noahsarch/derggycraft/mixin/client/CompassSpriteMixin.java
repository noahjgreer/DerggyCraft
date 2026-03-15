package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.CompassSprite;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassSprite.class)
public abstract class CompassSpriteMixin {
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private int[] compass;
    @Shadow
    private double angle;
    @Shadow
    private double angleDelta;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$trackClickedBlock(CallbackInfo ci) {
        DynamicTextureAccessor dynamicTexture = (DynamicTextureAccessor) (DynamicTexture) (Object) this;
        byte[] pixels = dynamicTexture.derggycraft$getPixels();
        boolean anaglyph = dynamicTexture.derggycraft$isAnaglyph();

        for (int i = 0; i < 256; ++i) {
            int a = this.compass[i] >> 24 & 255;
            int r = this.compass[i] >> 16 & 255;
            int g = this.compass[i] >> 8 & 255;
            int b = this.compass[i] & 255;

            if (anaglyph) {
                int r2 = (r * 30 + g * 59 + b * 11) / 100;
                int g2 = (r * 30 + g * 70) / 100;
                int b2 = (r * 30 + b * 70) / 100;
                r = r2;
                g = g2;
                b = b2;
            }

            pixels[i * 4] = (byte) r;
            pixels[i * 4 + 1] = (byte) g;
            pixels[i * 4 + 2] = (byte) b;
            pixels[i * 4 + 3] = (byte) a;
        }

        double targetRotation = resolveTargetRotation();
        double delta = targetRotation - this.angle;
        while (delta < -Math.PI) {
            delta += Math.PI * 2.0D;
        }

        while (delta >= Math.PI) {
            delta -= Math.PI * 2.0D;
        }

        if (delta < -1.0D) {
            delta = -1.0D;
        }

        if (delta > 1.0D) {
            delta = 1.0D;
        }

        this.angleDelta += delta * 0.1D;
        this.angleDelta *= 0.8D;
        this.angle += this.angleDelta;

        double sin = Math.sin(this.angle);
        double cos = Math.cos(this.angle);

        for (int i = -4; i <= 4; ++i) {
            int x = (int) (8.5D + cos * (double) i * 0.3D);
            int y = (int) (7.5D - sin * (double) i * 0.3D * 0.5D);
            int index = y * 16 + x;
            setPixel(pixels, anaglyph, index, 100, 100, 100, 255);
        }

        for (int i = -8; i <= 16; ++i) {
            int x = (int) (8.5D + sin * (double) i * 0.3D);
            int y = (int) (7.5D + cos * (double) i * 0.3D * 0.5D);
            int index = y * 16 + x;
            int r = i >= 0 ? 255 : 100;
            int g = i >= 0 ? 20 : 100;
            int b = i >= 0 ? 20 : 100;
            setPixel(pixels, anaglyph, index, r, g, b, 255);
        }

        ci.cancel();
    }

    private double resolveTargetRotation() {
        if (this.minecraft == null || this.minecraft.world == null || this.minecraft.player == null) {
            return this.angle + (Math.random() - 0.5D) * 0.2D;
        }

        ItemStack selected = this.minecraft.player.inventory.getSelectedItem();
        if (selected != null && selected.itemId == Item.COMPASS.id
                && IronCompassTracking.hasTrackedBlock(selected)
                && IronCompassTracking.isTrackedInWorld(selected, this.minecraft.world)) {
            double dx = IronCompassTracking.getTrackedCenterX(selected) - this.minecraft.player.x;
            double dz = IronCompassTracking.getTrackedCenterZ(selected) - this.minecraft.player.z;
            return (double) (this.minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        }

        if (IronCompassTracking.hasGlobalTrackedBlock(this.minecraft.world)) {
            double dx = IronCompassTracking.getGlobalTrackedCenterX(this.minecraft.world) - this.minecraft.player.x;
            double dz = IronCompassTracking.getGlobalTrackedCenterZ(this.minecraft.world) - this.minecraft.player.z;
            return (double) (this.minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        }

        // Behave like the nether/random fallback but keep the same smooth inertia.
        return this.angle + (Math.random() - 0.5D) * 0.2D;
    }

    private void setPixel(byte[] pixels, boolean anaglyph, int index, int r, int g, int b, int a) {
        if (anaglyph) {
            int r2 = (r * 30 + g * 59 + b * 11) / 100;
            int g2 = (r * 30 + g * 70) / 100;
            int b2 = (r * 30 + b * 70) / 100;
            r = r2;
            g = g2;
            b = b2;
        }

        pixels[index * 4] = (byte) r;
        pixels[index * 4 + 1] = (byte) g;
        pixels[index * 4 + 2] = (byte) b;
        pixels[index * 4 + 3] = (byte) a;
    }
}