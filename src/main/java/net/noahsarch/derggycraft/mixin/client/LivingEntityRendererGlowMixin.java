package net.noahsarch.derggycraft.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.item.GoldenCompassItem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererGlowMixin {
    @Shadow
    protected EntityModel model;

    @Shadow
    protected abstract float getHandSwingProgress(LivingEntity entity, float tickDelta);

    @Shadow
    protected abstract float getHeadBob(LivingEntity entity, float tickDelta);

    @Shadow
    protected abstract void applyTranslation(LivingEntity entity, double headBob, double bodyYaw, double tickDelta);

    @Shadow
    protected abstract void applyHandSwingRotation(LivingEntity entity, float dx, float dy, float dz);

    @Shadow
    protected abstract void applyScale(LivingEntity entity, float scale);

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/entity/LivingEntity;DDD)V"
            ),
            require = 0
    )
    private void derggycraft$renderCompassGlowOutline(LivingEntity entity, double d, double e, double f, float g, float h, CallbackInfo ci) {
        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.player == null || entity == null) {
            return;
        }

        PlayerEntity player = minecraft.player;
        if (entity == player) {
            return;
        }

        if (!GoldenCompassItem.shouldHighlightEntityForPlayer(player, entity)) {
            return;
        }

        float bodyYaw = entity.lastBodyYaw + (entity.bodyYaw - entity.lastBodyYaw) * h;
        float yaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * h;
        float pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * h;
        float headBob = this.getHeadBob(entity, h);
        float scale = 0.0625f;
        float limbAmount = entity.lastWalkAnimationSpeed + (entity.walkAnimationSpeed - entity.lastWalkAnimationSpeed) * h;
        float limbProgress = entity.walkAnimationProgress - entity.walkAnimationSpeed * (1.0f - h);

        if (limbAmount > 1.0f) {
            limbAmount = 1.0f;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        this.model.handSwingProgress = this.getHandSwingProgress(entity, h);
        this.model.riding = entity.hasVehicle();
        this.applyTranslation(entity, d, e, f);
        this.applyHandSwingRotation(entity, headBob, bodyYaw, h);
        GL11.glEnable(32826);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        this.applyScale(entity, h);
        GL11.glTranslatef(0.0f, -24.0f * scale - 0.0078125f, 0.0f);
        this.model.animateModel(entity, limbProgress, limbAmount, h);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.95f);
        GL11.glLineWidth(3.0f);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        this.model.render(limbProgress, limbAmount, headBob, yaw - bodyYaw, pitch, scale);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.12f);
        this.model.render(limbProgress, limbAmount, headBob, yaw - bodyYaw, pitch, scale);

        GL11.glLineWidth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    @SuppressWarnings("deprecation")
    private static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }
}
