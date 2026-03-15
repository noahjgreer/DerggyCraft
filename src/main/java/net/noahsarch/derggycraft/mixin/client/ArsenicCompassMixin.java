package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.binder.ArsenicCompass;
import net.noahsarch.derggycraft.item.IronCompassTracking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArsenicCompass.class)
public abstract class ArsenicCompassMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int[] compassTexture;
    @Shadow
    private double currentRotation;
    @Shadow
    private double rotationDelay;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$overrideIronCompassBehavior(CallbackInfo ci) {
        DynamicTextureAccessor dynamicTexture = (DynamicTextureAccessor) this;
        byte[] pixels = dynamicTexture.derggycraft$getPixels();
        boolean anaglyph = dynamicTexture.derggycraft$isAnaglyph();
        if (this.compassTexture == null || this.compassTexture.length == 0 || pixels == null) {
            return;
        }

        int pixelCount = this.compassTexture.length;
        int width = (int) Math.sqrt(pixelCount);
        if (width <= 0) {
            return;
        }
        int height = pixelCount / width;
        if (width * height != pixelCount || pixels.length < pixelCount * 4) {
            return;
        }

        for (int i = 0; i < pixelCount; ++i) {
            int a = this.compassTexture[i] >> 24 & 255;
            int r = this.compassTexture[i] >> 16 & 255;
            int g = this.compassTexture[i] >> 8 & 255;
            int b = this.compassTexture[i] & 255;
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

        updateRotation(resolveTargetRotation());
        double sin = Math.sin(this.currentRotation);
        double cos = Math.cos(this.currentRotation);

        for (int i = -height / 4; i <= height / 4; ++i) {
            int x = (int) (0.53125D * width + cos * (double) i * 0.3D);
            int y = (int) (0.46875D * height - sin * (double) i * 0.3D * 0.5D);
            if (x < 0 || x >= width || y < 0 || y >= height) {
                continue;
            }
            int index = y * width + x;
            setDialPixel(pixels, anaglyph, index, 100, 100, 100, 255);
        }

        for (int i = -width / 2; i <= width; ++i) {
            int x = (int) (0.53125D * width + sin * (double) i * 0.3D);
            int y = (int) (0.46875D * height + cos * (double) i * 0.3D * 0.5D);
            if (x < 0 || x >= width || y < 0 || y >= height) {
                continue;
            }
            int index = y * width + x;
            int r = i >= 0 ? 255 : 100;
            int g = i >= 0 ? 20 : 100;
            int b = i >= 0 ? 20 : 100;
            setDialPixel(pixels, anaglyph, index, r, g, b, 255);
        }

        ci.cancel();
    }

    private double resolveTargetRotation() {
        if (this.minecraft == null || this.minecraft.world == null || this.minecraft.player == null) {
            return this.currentRotation + (Math.random() - 0.5D) * 0.2D;
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

        return this.currentRotation + (Math.random() - 0.5D) * 0.2D;
    }

    private void updateRotation(double targetRotation) {
        double delta = targetRotation - this.currentRotation;
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

        this.rotationDelay += delta * 0.1D;
        this.rotationDelay *= 0.8D;
        this.currentRotation += this.rotationDelay;
    }

    private static void setDialPixel(byte[] pixels, boolean anaglyph, int index, int r, int g, int b, int a) {
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