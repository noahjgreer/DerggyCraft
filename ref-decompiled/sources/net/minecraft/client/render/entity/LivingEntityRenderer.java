/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class LivingEntityRenderer
extends EntityRenderer {
    protected EntityModel model;
    protected EntityModel decorationModel;

    public LivingEntityRenderer(EntityModel entityModel, float shadowRadius) {
        this.model = entityModel;
        this.shadowRadius = shadowRadius;
    }

    public void setDecorationModel(EntityModel model) {
        this.decorationModel = model;
    }

    public void render(LivingEntity livingEntity, double d, double e, double f, float g, float h) {
        GL11.glPushMatrix();
        GL11.glDisable((int)2884);
        this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, h);
        if (this.decorationModel != null) {
            this.decorationModel.handSwingProgress = this.model.handSwingProgress;
        }
        this.model.riding = livingEntity.hasVehicle();
        if (this.decorationModel != null) {
            this.decorationModel.riding = this.model.riding;
        }
        try {
            float f2 = livingEntity.lastBodyYaw + (livingEntity.bodyYaw - livingEntity.lastBodyYaw) * h;
            float f3 = livingEntity.prevYaw + (livingEntity.yaw - livingEntity.prevYaw) * h;
            float f4 = livingEntity.prevPitch + (livingEntity.pitch - livingEntity.prevPitch) * h;
            this.applyTranslation(livingEntity, d, e, f);
            float f5 = this.getHeadBob(livingEntity, h);
            this.applyHandSwingRotation(livingEntity, f5, f2, h);
            float f6 = 0.0625f;
            GL11.glEnable((int)32826);
            GL11.glScalef((float)-1.0f, (float)-1.0f, (float)1.0f);
            this.applyScale(livingEntity, h);
            GL11.glTranslatef((float)0.0f, (float)(-24.0f * f6 - 0.0078125f), (float)0.0f);
            float f7 = livingEntity.lastWalkAnimationSpeed + (livingEntity.walkAnimationSpeed - livingEntity.lastWalkAnimationSpeed) * h;
            float f8 = livingEntity.walkAnimationProgress - livingEntity.walkAnimationSpeed * (1.0f - h);
            if (f7 > 1.0f) {
                f7 = 1.0f;
            }
            this.bindDownloadedTexture(livingEntity.skinUrl, livingEntity.getTexture());
            GL11.glEnable((int)3008);
            this.model.animateModel(livingEntity, f8, f7, h);
            this.model.render(f8, f7, f5, f3 - f2, f4, f6);
            for (int i = 0; i < 4; ++i) {
                if (!this.bindTexture(livingEntity, i, h)) continue;
                this.decorationModel.render(f8, f7, f5, f3 - f2, f4, f6);
                GL11.glDisable((int)3042);
                GL11.glEnable((int)3008);
            }
            this.renderMore(livingEntity, h);
            float f9 = livingEntity.getBrightnessAtEyes(h);
            int n = this.getOverlayColor(livingEntity, f9, h);
            if ((n >> 24 & 0xFF) > 0 || livingEntity.hurtTime > 0 || livingEntity.deathTime > 0) {
                GL11.glDisable((int)3553);
                GL11.glDisable((int)3008);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glDepthFunc((int)514);
                if (livingEntity.hurtTime > 0 || livingEntity.deathTime > 0) {
                    GL11.glColor4f((float)f9, (float)0.0f, (float)0.0f, (float)0.4f);
                    this.model.render(f8, f7, f5, f3 - f2, f4, f6);
                    for (int i = 0; i < 4; ++i) {
                        if (!this.bindDecorationTexture(livingEntity, i, h)) continue;
                        GL11.glColor4f((float)f9, (float)0.0f, (float)0.0f, (float)0.4f);
                        this.decorationModel.render(f8, f7, f5, f3 - f2, f4, f6);
                    }
                }
                if ((n >> 24 & 0xFF) > 0) {
                    float f10 = (float)(n >> 16 & 0xFF) / 255.0f;
                    float f11 = (float)(n >> 8 & 0xFF) / 255.0f;
                    float f12 = (float)(n & 0xFF) / 255.0f;
                    float f13 = (float)(n >> 24 & 0xFF) / 255.0f;
                    GL11.glColor4f((float)f10, (float)f11, (float)f12, (float)f13);
                    this.model.render(f8, f7, f5, f3 - f2, f4, f6);
                    for (int i = 0; i < 4; ++i) {
                        if (!this.bindDecorationTexture(livingEntity, i, h)) continue;
                        GL11.glColor4f((float)f10, (float)f11, (float)f12, (float)f13);
                        this.decorationModel.render(f8, f7, f5, f3 - f2, f4, f6);
                    }
                }
                GL11.glDepthFunc((int)515);
                GL11.glDisable((int)3042);
                GL11.glEnable((int)3008);
                GL11.glEnable((int)3553);
            }
            GL11.glDisable((int)32826);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        GL11.glEnable((int)2884);
        GL11.glPopMatrix();
        this.renderNameTag(livingEntity, d, e, f);
    }

    protected void applyTranslation(LivingEntity entity, double headBob, double bodyYaw, double tickDelta) {
        GL11.glTranslatef((float)((float)headBob), (float)((float)bodyYaw), (float)((float)tickDelta));
    }

    protected void applyHandSwingRotation(LivingEntity entity, float dx, float dy, float dz) {
        GL11.glRotatef((float)(180.0f - dy), (float)0.0f, (float)1.0f, (float)0.0f);
        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + dz - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            GL11.glRotatef((float)(f * this.getDeathYaw(entity)), (float)0.0f, (float)0.0f, (float)1.0f);
        }
    }

    protected float getHandSwingProgress(LivingEntity entity, float tickDelta) {
        return entity.getHandSwingProgress(tickDelta);
    }

    protected float getHeadBob(LivingEntity entity, float tickDelta) {
        return (float)entity.age + tickDelta;
    }

    protected void renderMore(LivingEntity entity, float tickDelta) {
    }

    protected boolean bindDecorationTexture(LivingEntity entity, int i, float f) {
        return this.bindTexture(entity, i, f);
    }

    protected boolean bindTexture(LivingEntity mob, int layer, float tickDelta) {
        return false;
    }

    protected float getDeathYaw(LivingEntity entity) {
        return 90.0f;
    }

    protected int getOverlayColor(LivingEntity entity, float brightness, float timeDelta) {
        return 0;
    }

    protected void applyScale(LivingEntity entity, float scale) {
    }

    protected void renderNameTag(LivingEntity entity, double dx, double dy, double dz) {
        if (Minecraft.isDebugProfilerEnabled()) {
            this.renderNameTag(entity, Integer.toString(entity.id), dx, dy, dz, 64);
        }
    }

    protected void renderNameTag(LivingEntity entity, String name, double dx, double dy, double dz, int range) {
        float f = entity.getDistance(this.dispatcher.cameraEntity);
        if (f > (float)range) {
            return;
        }
        TextRenderer textRenderer = this.getTextRenderer();
        float f2 = 1.6f;
        float f3 = 0.016666668f * f2;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)dx + 0.0f), (float)((float)dy + 2.3f), (float)((float)dz));
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.dispatcher.yaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)this.dispatcher.pitch, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)(-f3), (float)(-f3), (float)f3);
        GL11.glDisable((int)2896);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        Tessellator tessellator = Tessellator.INSTANCE;
        int n = 0;
        if (name.equals("deadmau5")) {
            n = -10;
        }
        GL11.glDisable((int)3553);
        tessellator.startQuads();
        int n2 = textRenderer.getWidth(name) / 2;
        tessellator.color(0.0f, 0.0f, 0.0f, 0.25f);
        tessellator.vertex(-n2 - 1, -1 + n, 0.0);
        tessellator.vertex(-n2 - 1, 8 + n, 0.0);
        tessellator.vertex(n2 + 1, 8 + n, 0.0);
        tessellator.vertex(n2 + 1, -1 + n, 0.0);
        tessellator.draw();
        GL11.glEnable((int)3553);
        textRenderer.draw(name, -textRenderer.getWidth(name) / 2, n, 0x20FFFFFF);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        textRenderer.draw(name, -textRenderer.getWidth(name) / 2, n, -1);
        GL11.glEnable((int)2896);
        GL11.glDisable((int)3042);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }
}

