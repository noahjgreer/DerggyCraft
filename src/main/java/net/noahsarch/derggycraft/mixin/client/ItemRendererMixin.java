package net.noahsarch.derggycraft.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.client.render.texture.GoldenCompassRenderState;
import net.noahsarch.derggycraft.item.GoldenCompassItem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    private static final String GOLDEN_COMPASS_TEXTURE_PATH = "/assets/derggycraft/stationapi/textures/item/golden_compass.png";
    private static final String CALI_COMPASS_TEXTURE_PATH = "/assets/derggycraft/stationapi/textures/item/cali_compass.png";
    private static final String FINNI_COMPASS_TEXTURE_PATH = "/assets/derggycraft/stationapi/textures/item/finni_compass.png";

    private static final int PRIMARY_DIAL_COLOR = 0x00F3F6;
    private static final int SECONDARY_DIAL_COLOR = 0x125D5E;
    private static final int MAX_ROTATION_STATES = 2048;
    private static final Map<Integer, double[]> ROTATION_STATE_BY_KEY = new HashMap<>();

    @Inject(
            method = "renderGuiItem(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void derggycraft$renderGoldenCompassPerStack(TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y, CallbackInfo ci) {
        if (stack == null || DerggyCraft.GOLDEN_COMPASS_ITEM == null || stack.itemId != DerggyCraft.GOLDEN_COMPASS_ITEM.id) {
            return;
        }
        if (!(DerggyCraft.GOLDEN_COMPASS_ITEM instanceof GoldenCompassItem goldenCompassItem)) {
            return;
        }

        String trackedEntityName = goldenCompassItem.getTrackedEntityName(stack);
        String texturePath = "NotANaN".equals(trackedEntityName) ? CALI_COMPASS_TEXTURE_PATH : "FinniTheFox".equals(trackedEntityName) ? FINNI_COMPASS_TEXTURE_PATH : GOLDEN_COMPASS_TEXTURE_PATH;
        

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureManager.bindTexture(textureManager.getTextureId(texturePath));
        drawBoundTexture(x, y, 16, 16);

        double[] rotationState = getRotationState(stack);
        double angle = resolveTargetRotation(stack, goldenCompassItem, rotationState);
        renderDialOverlay(x, y, angle);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        ci.cancel();
    }

    @SuppressWarnings("deprecation")
    private static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }

    private static double resolveTargetRotation(ItemStack stack, GoldenCompassItem goldenCompassItem, double[] rotationState) {
        if (GoldenCompassRenderState.matchesMainhandSnapshot(stack)) {
            rotationState[0] = GoldenCompassRenderState.getMainhandRotation();
            rotationState[1] = 0.0D;
            return rotationState[0];
        }

        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.world == null || minecraft.player == null) {
            double targetRotation = Math.random() * Math.PI * 2.0D;
            updateRotation(rotationState, targetRotation);
            return rotationState[0];
        }

        LivingEntity trackedEntity = goldenCompassItem.getTrackedEntity(stack, minecraft.world);
        double targetRotation;
        if (trackedEntity != null && trackedEntity.world == minecraft.world) {
            double dx = trackedEntity.x - minecraft.player.x;
            double dz = trackedEntity.z - minecraft.player.z;
            targetRotation = (double) (minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        } else if (goldenCompassItem.hasTrackedLastPosition(stack)
                && goldenCompassItem.isTrackedLastPositionInWorld(stack, minecraft.world)) {
            double dx = goldenCompassItem.getTrackedLastX(stack) - minecraft.player.x;
            double dz = goldenCompassItem.getTrackedLastZ(stack) - minecraft.player.z;
            targetRotation = (double) (minecraft.player.yaw - 90.0F) * Math.PI / 180.0D - Math.atan2(dz, dx);
        } else {
            targetRotation = Math.random() * Math.PI * 2.0D;
        }

        updateRotation(rotationState, targetRotation);
        return rotationState[0];
    }

    private static double[] getRotationState(ItemStack stack) {
        if (ROTATION_STATE_BY_KEY.size() > MAX_ROTATION_STATES) {
            ROTATION_STATE_BY_KEY.clear();
        }

        int key = getRotationStateKey(stack);
        double[] state = ROTATION_STATE_BY_KEY.get(key);
        if (state == null) {
            state = new double[]{0.0D, 0.0D};
            ROTATION_STATE_BY_KEY.put(key, state);
        }
        return state;
    }

    private static int getRotationStateKey(ItemStack stack) {
        return GoldenCompassRenderState.getStackRenderKey(stack);
    }

    private static void updateRotation(double[] state, double targetRotation) {
        double delta = targetRotation - state[0];
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

        state[1] += delta * 0.1D;
        state[1] *= 0.8D;
        state[0] += state[1];
    }

    private static void drawBoundTexture(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + height, 0.0, 0.0, 1.0);
        tessellator.vertex(x + width, y + height, 0.0, 1.0, 1.0);
        tessellator.vertex(x + width, y, 0.0, 1.0, 0.0);
        tessellator.vertex(x, y, 0.0, 0.0, 0.0);
        tessellator.draw();
    }

    private static void renderDialOverlay(int x, int y, double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        for (int i = -4; i <= 4; ++i) {
            int px = x + (int) (8.5D + cos * (double) i * 0.3D);
            int py = y + (int) (7.5D - sin * (double) i * 0.3D * 0.5D);
            drawPixel(px, py, SECONDARY_DIAL_COLOR);
        }

        for (int i = -8; i <= 16; ++i) {
            int px = x + (int) (8.5D + sin * (double) i * 0.3D);
            int py = y + (int) (7.5D + cos * (double) i * 0.3D * 0.5D);
            int color = i >= 0 ? PRIMARY_DIAL_COLOR : SECONDARY_DIAL_COLOR;
            drawPixel(px, py, color);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void drawPixel(int x, int y, int rgbColor) {
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.color(rgbColor);
        tessellator.vertex(x, y + 1, 0.0);
        tessellator.vertex(x + 1, y + 1, 0.0);
        tessellator.vertex(x + 1, y, 0.0);
        tessellator.vertex(x, y, 0.0);
        tessellator.draw();
    }
}
