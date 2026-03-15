package net.noahsarch.derggycraft.client.render.texture;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.pack.TexturePack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.binder.StationTextureBinder;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.item.GoldenCompassItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GoldenCompassTextureBinder extends StationTextureBinder {
    private static final int VANILLA_PRIMARY_DIAL_COLOR = 0xFF1414;
    private static final int VANILLA_SECONDARY_DIAL_COLOR = 0x646464;

    // Change these two hex values to recolor the dial.
    private static final int PRIMARY_DIAL_COLOR = 0x00F3F6;
    private static final int SECONDARY_DIAL_COLOR = 0x125D5E;
    private static final String CALI_COMPASS_TEXTURE_PATH = "/assets/derggycraft/stationapi/textures/item/cali_compass.png";
    private static final String FINNI_COMPASS_TEXTURE_PATH = "/assets/derggycraft/stationapi/textures/item/finni_compass.png";

    private int[] defaultBaseTexture;
    private int[] caliBaseTexture;
    private int[] finniBaseTexture;
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

        defaultBaseTexture = staticReference.getSprite().getContents().getBaseFrame().makePixelArray();
        caliBaseTexture = loadTextureAsAbgr(CALI_COMPASS_TEXTURE_PATH, width, height);
        finniBaseTexture = loadTextureAsAbgr(FINNI_COMPASS_TEXTURE_PATH, width, height);
        pixels = new byte[pixelCount * 4];
    }

    @Override
    public void tick() {
        if (this.defaultBaseTexture == null || this.pixels == null) {
            return;
        }

        Atlas.Sprite staticReference = getStaticReference();
        int width = staticReference.getWidth();
        int height = staticReference.getHeight();
        int[] activeBaseTexture = getActiveBaseTexture();
        for (int i = 0; i < width * height; ++i) {
            int r = activeBaseTexture[i] & 255;
            int g = activeBaseTexture[i] >> 8 & 255;
            int b = activeBaseTexture[i] >> 16 & 255;
            int a = activeBaseTexture[i] >> 24 & 255;
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
        publishMainhandSnapshot();
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

    @SuppressWarnings("deprecation")
    private Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }

    private double resolveTargetRotation() {
        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.world == null || minecraft.player == null) {
            return this.currentRotation + (Math.random() - 0.5D) * 0.2D;
        }

        ItemStack selected = minecraft.player.inventory.getSelectedItem();
        if (selected == null || DerggyCraft.GOLDEN_COMPASS_ITEM == null
                || selected.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id
                || !(DerggyCraft.GOLDEN_COMPASS_ITEM instanceof GoldenCompassItem goldenCompassItem)) {
            return Math.random() * Math.PI * 2.0D;
        }

        LivingEntity trackedEntity = goldenCompassItem.getTrackedEntity(selected, minecraft.world);
        if (trackedEntity != null && trackedEntity.world == minecraft.world) {
            double dx = trackedEntity.x - minecraft.player.x;
            double dz = trackedEntity.z - minecraft.player.z;
            return (double) (minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        }

        if (goldenCompassItem.hasTrackedLastPosition(selected)
                && goldenCompassItem.isTrackedLastPositionInWorld(selected, minecraft.world)) {
            double dx = goldenCompassItem.getTrackedLastX(selected) - minecraft.player.x;
            double dz = goldenCompassItem.getTrackedLastZ(selected) - minecraft.player.z;
            return (double) (minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        }

        return Math.random() * Math.PI * 2.0D;
    }

    private void publishMainhandSnapshot() {
        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.player == null || DerggyCraft.GOLDEN_COMPASS_ITEM == null) {
            GoldenCompassRenderState.clearMainhandSnapshot();
            return;
        }

        ItemStack selected = minecraft.player.inventory.getSelectedItem();
        if (selected == null || selected.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id) {
            GoldenCompassRenderState.clearMainhandSnapshot();
            return;
        }

        GoldenCompassRenderState.updateMainhandSnapshot(selected, this.currentRotation);
    }

    private int[] getActiveBaseTexture() {
        if (this.caliBaseTexture == null) {
            return this.defaultBaseTexture;
        }

        if (this.finniBaseTexture == null) {
            return this.defaultBaseTexture;
        }

        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.player == null || DerggyCraft.GOLDEN_COMPASS_ITEM == null
                || !(DerggyCraft.GOLDEN_COMPASS_ITEM instanceof GoldenCompassItem goldenCompassItem)) {
            return this.defaultBaseTexture;
        }

        ItemStack selected = minecraft.player.inventory.getSelectedItem();
        if (selected == null || selected.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id) {
            return this.defaultBaseTexture;
        }

        String trackedEntityName = goldenCompassItem.getTrackedEntityName(selected);
        if ("NotANaN".equals(trackedEntityName)) {
            return this.caliBaseTexture;
        }
        if ("FinniTheFox".equals(trackedEntityName)) {
            return this.finniBaseTexture;
        }

        return this.defaultBaseTexture;
    }

    private static int[] loadTextureAsAbgr(String resourcePath, int expectedWidth, int expectedHeight) {
        try {
            BufferedImage image = ImageIO.read(GoldenCompassTextureBinder.class.getResource(resourcePath));
            if (image == null || image.getWidth() != expectedWidth || image.getHeight() != expectedHeight) {
                return null;
            }

            int[] argb = new int[expectedWidth * expectedHeight];
            image.getRGB(0, 0, expectedWidth, expectedHeight, argb, 0, expectedWidth);
            int[] abgr = new int[argb.length];
            for (int i = 0; i < argb.length; ++i) {
                int color = argb[i];
                int a = (color >>> 24) & 255;
                int r = (color >>> 16) & 255;
                int g = (color >>> 8) & 255;
                int b = color & 255;
                abgr[i] = (a << 24) | (b << 16) | (g << 8) | r;
            }

            return abgr;
        } catch (IOException | IllegalArgumentException ignored) {
            return null;
        }
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
