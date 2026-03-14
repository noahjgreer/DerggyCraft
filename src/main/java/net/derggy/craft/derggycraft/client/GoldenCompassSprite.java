package net.derggy.craft.derggycraft.client;

import net.derggy.craft.derggycraft.events.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.pack.TexturePack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.binder.StationTextureBinder;
import net.modificationstation.stationapi.api.item.StationItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Custom compass sprite for the Golden Compass using arsenic-compatible StationTextureBinder.
 * Reads target from CompassTargetCache (populated by server network messages).
 * Uses custom base textures for specific tracked players.
 */
@Environment(EnvType.CLIENT)
public class GoldenCompassSprite extends StationTextureBinder {

    @SuppressWarnings("deprecation")
    private final Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
    private int[] defaultPixels;
    private int[] finniPixels;
    private int[] caliPixels;
    private int texWidth;
    private int texHeight;
    private double angle;
    private double angleDelta;

    public GoldenCompassSprite(Atlas.Sprite staticReference) {
        super(staticReference);
    }

    @Override
    public void reloadFromTexturePack(TexturePack newTexturePack) {
        Atlas.Sprite ref = getStaticReference();
        this.texWidth = ref.getWidth();
        this.texHeight = ref.getHeight();
        int square = this.texWidth * this.texHeight;

        // Load default compass base from atlas sprite (ABGR format)
        this.defaultPixels = ref.getSprite().getContents().getBaseFrame().makePixelArray();
        this.pixels = new byte[square * 4];

        // Load custom textures from mod resources (convert ARGB→ABGR)
        this.finniPixels = loadCustomTexture("/assets/derggycraft/stationapi/textures/item/finni_compass.png");
        this.caliPixels = loadCustomTexture("/assets/derggycraft/stationapi/textures/item/cali_compass.png");
    }

    private int[] loadCustomTexture(String path) {
        try {
            InputStream stream = GoldenCompassSprite.class.getResourceAsStream(path);
            if (stream != null) {
                BufferedImage img = ImageIO.read(stream);
                int w = Math.min(img.getWidth(), this.texWidth);
                int h = Math.min(img.getHeight(), this.texHeight);
                int[] argbPixels = new int[this.texWidth * this.texHeight];
                img.getRGB(0, 0, w, h, argbPixels, 0, this.texWidth);
                stream.close();
                // Convert ARGB to ABGR to match makePixelArray() format
                int[] abgrPixels = new int[argbPixels.length];
                for (int i = 0; i < argbPixels.length; i++) {
                    int a = (argbPixels[i] >> 24) & 0xFF;
                    int r = (argbPixels[i] >> 16) & 0xFF;
                    int g = (argbPixels[i] >> 8) & 0xFF;
                    int b = argbPixels[i] & 0xFF;
                    abgrPixels[i] = (a << 24) | (b << 16) | (g << 8) | r;
                }
                return abgrPixels;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Fallback to default
        if (this.defaultPixels != null) {
            int[] copy = new int[this.defaultPixels.length];
            System.arraycopy(this.defaultPixels, 0, copy, 0, this.defaultPixels.length);
            return copy;
        }
        return new int[this.texWidth * this.texHeight];
    }

    @Override
    public void tick() {
        if (this.defaultPixels == null || this.pixels == null) return;

        int square = this.texWidth * this.texHeight;

        // Choose base texture based on tracked target name
        int[] basePixels = this.defaultPixels;

        if (this.minecraft.player != null) {
            ItemStack held = this.minecraft.player.inventory.getSelectedItem();
            if (held != null && held.getItem() == ItemInit.GOLDEN_COMPASS) {
                NbtCompound nbt = ((StationItemStack) (Object) held).getStationNbt();
                if (nbt.contains("targetName")) {
                    String name = nbt.getString("targetName");
                    if ("FinniTheFox".equals(name) && this.finniPixels != null) {
                        basePixels = this.finniPixels;
                    } else if ("NotANaN".equals(name) && this.caliPixels != null) {
                        basePixels = this.caliPixels;
                    }
                }
            }
        }

        // Copy base pixels (ABGR format: R in low byte)
        for (int i = 0; i < square; ++i) {
            int r = basePixels[i] & 0xFF;
            int g = basePixels[i] >> 8 & 0xFF;
            int b = basePixels[i] >> 16 & 0xFF;
            int a = basePixels[i] >> 24 & 0xFF;
            if (this.anaglyph) {
                int rr = (r * 30 + g * 59 + b * 11) / 100;
                int gg = (r * 30 + g * 70) / 100;
                int bb = (r * 30 + b * 70) / 100;
                r = rr; g = gg; b = bb;
            }
            this.pixels[i * 4] = (byte) r;
            this.pixels[i * 4 + 1] = (byte) g;
            this.pixels[i * 4 + 2] = (byte) b;
            this.pixels[i * 4 + 3] = (byte) a;
        }

        double targetAngle = 0.0;

        if (this.minecraft.world != null && this.minecraft.player != null) {
            ItemStack held = this.minecraft.player.inventory.getSelectedItem();
            boolean isGoldenCompass = held != null && held.getItem() == ItemInit.GOLDEN_COMPASS;

            if (isGoldenCompass && CompassTargetCache.hasTarget()) {
                if (CompassTargetCache.isTrackingSelf()) {
                    targetAngle = Math.PI;
                } else {
                    double dx = CompassTargetCache.getTargetX() - this.minecraft.player.x;
                    double dz = CompassTargetCache.getTargetZ() - this.minecraft.player.z;
                    targetAngle = (double) (this.minecraft.player.yaw - 90.0f) * Math.PI / 180.0 - Math.atan2(dz, dx);
                }
            } else {
                targetAngle = Math.random() * Math.PI * 2.0;
            }
        }

        // Smooth angle transition
        double d = targetAngle - this.angle;
        while (d < -Math.PI) d += Math.PI * 2;
        while (d >= Math.PI) d -= Math.PI * 2;
        if (d < -1.0) d = -1.0;
        if (d > 1.0) d = 1.0;
        this.angleDelta += d * 0.1;
        this.angleDelta *= 0.8;
        this.angle += this.angleDelta;

        // Draw needle scaled to texture size
        double sinA = Math.sin(this.angle);
        double cosA = Math.cos(this.angle);

        // Horizontal crossbar (gray)
        for (int n = -this.texHeight / 4; n <= this.texHeight / 4; ++n) {
            int px = (int) (0.53125 * this.texWidth + cosA * (double) n * 0.3);
            int py = (int) (0.46875 * this.texHeight - sinA * (double) n * 0.3 * 0.5);
            int idx = py * this.texWidth + px;
            if (idx < 0 || idx >= square) continue;
            setPixel(idx, 100, 100, 100, 255);
        }

        // Vertical needle (south=red, north=gray)
        for (int n = -this.texWidth / 2; n <= this.texWidth; ++n) {
            int px = (int) (0.53125 * this.texHeight + sinA * (double) n * 0.3);
            int py = (int) (0.46875 * this.texWidth + cosA * (double) n * 0.3 * 0.5);
            int idx = py * this.texHeight + px;
            if (idx < 0 || idx >= square) continue;
            int r = n >= 0 ? 255 : 100;
            int g = n >= 0 ? 20 : 100;
            int b = n >= 0 ? 20 : 100;
            setPixel(idx, r, g, b, 255);
        }
    }

    private void setPixel(int idx, int r, int g, int b, int a) {
        if (this.anaglyph) {
            int rr = (r * 30 + g * 59 + b * 11) / 100;
            int gg = (r * 30 + g * 70) / 100;
            int bb = (r * 30 + b * 70) / 100;
            r = rr; g = gg; b = bb;
        }
        this.pixels[idx * 4] = (byte) r;
        this.pixels[idx * 4 + 1] = (byte) g;
        this.pixels[idx * 4 + 2] = (byte) b;
        this.pixels[idx * 4 + 3] = (byte) a;
    }
}
