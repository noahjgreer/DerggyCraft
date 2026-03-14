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

/**
 * Custom compass sprite for the Iron Compass using arsenic-compatible StationTextureBinder.
 * Spins randomly when unlocked, points to locked target when set.
 */
@Environment(EnvType.CLIENT)
public class IronCompassSprite extends StationTextureBinder {

    @SuppressWarnings("deprecation")
    private final Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
    private int[] compassPixels;
    private int texWidth;
    private int texHeight;
    private double angle;
    private double angleDelta;

    public IronCompassSprite(Atlas.Sprite staticReference) {
        super(staticReference);
    }

    @Override
    public void reloadFromTexturePack(TexturePack newTexturePack) {
        Atlas.Sprite ref = getStaticReference();
        this.texWidth = ref.getWidth();
        this.texHeight = ref.getHeight();
        int square = this.texWidth * this.texHeight;
        this.compassPixels = ref.getSprite().getContents().getBaseFrame().makePixelArray();
        this.pixels = new byte[square * 4];
    }

    @Override
    public void tick() {
        if (this.compassPixels == null || this.pixels == null) return;

        int square = this.texWidth * this.texHeight;

        // Copy base pixels (makePixelArray returns ABGR: R in low byte)
        for (int i = 0; i < square; ++i) {
            int r = this.compassPixels[i] & 0xFF;
            int g = this.compassPixels[i] >> 8 & 0xFF;
            int b = this.compassPixels[i] >> 16 & 0xFF;
            int a = this.compassPixels[i] >> 24 & 0xFF;
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

            boolean hasTarget = false;
            int targetX = 0;
            int targetZ = 0;

            if (held != null && held.getItem() == ItemInit.IRON_COMPASS) {
                NbtCompound nbt = ((StationItemStack) (Object) held).getStationNbt();
                if (nbt.contains("locked") && nbt.getBoolean("locked")) {
                    targetX = nbt.getInt("targetX");
                    targetZ = nbt.getInt("targetZ");
                    hasTarget = true;
                }
            }

            if (hasTarget) {
                double dx = (double) targetX - this.minecraft.player.x;
                double dz = (double) targetZ - this.minecraft.player.z;
                targetAngle = (double) (this.minecraft.player.yaw - 90.0f) * Math.PI / 180.0 - Math.atan2(dz, dx);
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
