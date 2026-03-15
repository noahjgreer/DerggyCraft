package net.noahsarch.derggycraft.client.render.texture;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.pack.TexturePack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.binder.StationTextureBinder;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.item.GoldenCompassItem;

public class GoldenCompassTextureBinder extends StationTextureBinder {
    private static final int VANILLA_PRIMARY_DIAL_COLOR = 0xFF1414;
    private static final int VANILLA_SECONDARY_DIAL_COLOR = 0x646464;

    // Change these two hex values to recolor the dial.
    private static final int PRIMARY_DIAL_COLOR = 0x00F3F6;
    private static final int SECONDARY_DIAL_COLOR = 0x125D5E;

    @SuppressWarnings("deprecation")
    private final Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
    private int[] baseTexture;
    private double currentRotation;
    private double rotationDelay;

    public GoldenCompassTextureBinder(Atlas.Sprite staticReference) {
        super(staticReference);
    }

    @Override
    public void reloadFromTexturePack(TexturePack newTexturePack) {
        Atlas.Sprite staticReference = getStaticReference();
        int width = staticReference.getWidth();
        int height = staticReference.getHeight();
        int pixelCount = width * height;

        baseTexture = staticReference.getSprite().getContents().getBaseFrame().makePixelArray();
        pixels = new byte[pixelCount * 4];
    }

    @Override
    public void tick() {
        Atlas.Sprite staticReference = getStaticReference();
        int width = staticReference.getWidth();
        int height = staticReference.getHeight();
        for (int i = 0; i < width * height; ++i) {
            int r = this.baseTexture[i] & 255;
            int g = this.baseTexture[i] >> 8 & 255;
            int b = this.baseTexture[i] >> 16 & 255;
            int a = this.baseTexture[i] >> 24 & 255;
            if (this.anaglyph) {
                int r2 = (r * 30 + g * 59 + b * 11) / 100;
                int g2 = (r * 30 + g * 70) / 100;
                int b2 = (r * 30 + b * 70) / 100;
                r = r2;
                g = g2;
                b = b2;
            }

            this.pixels[i * 4] = (byte) r;
            this.pixels[i * 4 + 1] = (byte) g;
            this.pixels[i * 4 + 2] = (byte) b;
            this.pixels[i * 4 + 3] = (byte) a;
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
            setDialPixel(index, SECONDARY_DIAL_COLOR);
        }

        for (int i = -width / 2; i <= width; ++i) {
            int x = (int) (0.53125D * width + sin * (double) i * 0.3D);
            int y = (int) (0.46875D * height + cos * (double) i * 0.3D * 0.5D);
            if (x < 0 || x >= width || y < 0 || y >= height) {
                continue;
            }
            int index = y * width + x;
            int color = i >= 0 ? PRIMARY_DIAL_COLOR : SECONDARY_DIAL_COLOR;
            setDialPixel(index, color);
        }
    }

    private double resolveTargetRotation() {
        if (this.minecraft.world == null || this.minecraft.player == null) {
            return this.currentRotation + (Math.random() - 0.5D) * 0.2D;
        }

        LivingEntity trackedEntity = getTrackedEntityFromSelectedCompass();
        if (trackedEntity != null && trackedEntity.world == this.minecraft.world) {
            double dx = trackedEntity.x - this.minecraft.player.x;
            double dz = trackedEntity.z - this.minecraft.player.z;
            return (double) (this.minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        }

        Vec3i spawnPos = this.minecraft.world.getSpawnPos();
        double dx = (double) spawnPos.x - this.minecraft.player.x;
        double dz = (double) spawnPos.z - this.minecraft.player.z;
        double targetRotation = (double) (this.minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        if (this.minecraft.world.dimension.isNether) {
            targetRotation = Math.random() * Math.PI * 2.0D;
        }
        return targetRotation;
    }

    private LivingEntity getTrackedEntityFromSelectedCompass() {
        ItemStack selected = this.minecraft.player.inventory.getSelectedItem();
        if (selected == null || DerggyCraft.GOLDEN_COMPASS_ITEM == null || selected.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id) {
            return null;
        }

        if (DerggyCraft.GOLDEN_COMPASS_ITEM instanceof GoldenCompassItem goldenCompassItem) {
            return goldenCompassItem.getTrackedEntity(selected, this.minecraft.world);
        }

        return null;
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

    private void setDialPixel(int index, int rgbColor) {
        int r = (rgbColor >> 16) & 255;
        int g = (rgbColor >> 8) & 255;
        int b = rgbColor & 255;

        if (this.anaglyph) {
            int r2 = (r * 30 + g * 59 + b * 11) / 100;
            int g2 = (r * 30 + g * 70) / 100;
            int b2 = (r * 30 + b * 70) / 100;
            r = r2;
            g = g2;
            b = b2;
        }

        this.pixels[index * 4] = (byte) r;
        this.pixels[index * 4 + 1] = (byte) g;
        this.pixels[index * 4 + 2] = (byte) b;
        this.pixels[index * 4 + 3] = (byte) 255;
    }
}
