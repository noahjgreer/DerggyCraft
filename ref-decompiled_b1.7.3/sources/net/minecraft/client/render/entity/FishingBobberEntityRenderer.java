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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer {
    public void render(FishingBobberEntity fishingBobberEntity, double d, double e, double f, float g, float h) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)d), (float)((float)e), (float)((float)f));
        GL11.glEnable((int)32826);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        int n = 1;
        int n2 = 2;
        this.bindTexture("/particles.png");
        Tessellator tessellator = Tessellator.INSTANCE;
        float f2 = (float)(n * 8 + 0) / 128.0f;
        float f3 = (float)(n * 8 + 8) / 128.0f;
        float f4 = (float)(n2 * 8 + 0) / 128.0f;
        float f5 = (float)(n2 * 8 + 8) / 128.0f;
        float f6 = 1.0f;
        float f7 = 0.5f;
        float f8 = 0.5f;
        GL11.glRotatef((float)(180.0f - this.dispatcher.yaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.dispatcher.pitch), (float)1.0f, (float)0.0f, (float)0.0f);
        tessellator.startQuads();
        tessellator.normal(0.0f, 1.0f, 0.0f);
        tessellator.vertex(0.0f - f7, 0.0f - f8, 0.0, f2, f5);
        tessellator.vertex(f6 - f7, 0.0f - f8, 0.0, f3, f5);
        tessellator.vertex(f6 - f7, 1.0f - f8, 0.0, f3, f4);
        tessellator.vertex(0.0f - f7, 1.0f - f8, 0.0, f2, f4);
        tessellator.draw();
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
        if (fishingBobberEntity.owner != null) {
            float f9 = (fishingBobberEntity.owner.prevYaw + (fishingBobberEntity.owner.yaw - fishingBobberEntity.owner.prevYaw) * h) * (float)Math.PI / 180.0f;
            double d2 = MathHelper.sin(f9);
            double d3 = MathHelper.cos(f9);
            float f10 = fishingBobberEntity.owner.getHandSwingProgress(h);
            float f11 = MathHelper.sin(MathHelper.sqrt(f10) * (float)Math.PI);
            Vec3d vec3d = Vec3d.createCached(-0.5, 0.03, 0.8);
            vec3d.rotateX(-(fishingBobberEntity.owner.prevPitch + (fishingBobberEntity.owner.pitch - fishingBobberEntity.owner.prevPitch) * h) * (float)Math.PI / 180.0f);
            vec3d.rotateY(-(fishingBobberEntity.owner.prevYaw + (fishingBobberEntity.owner.yaw - fishingBobberEntity.owner.prevYaw) * h) * (float)Math.PI / 180.0f);
            vec3d.rotateY(f11 * 0.5f);
            vec3d.rotateX(-f11 * 0.7f);
            double d4 = fishingBobberEntity.owner.prevX + (fishingBobberEntity.owner.x - fishingBobberEntity.owner.prevX) * (double)h + vec3d.x;
            double d5 = fishingBobberEntity.owner.prevY + (fishingBobberEntity.owner.y - fishingBobberEntity.owner.prevY) * (double)h + vec3d.y;
            double d6 = fishingBobberEntity.owner.prevZ + (fishingBobberEntity.owner.z - fishingBobberEntity.owner.prevZ) * (double)h + vec3d.z;
            if (this.dispatcher.options.thirdPerson) {
                f9 = (fishingBobberEntity.owner.lastBodyYaw + (fishingBobberEntity.owner.bodyYaw - fishingBobberEntity.owner.lastBodyYaw) * h) * (float)Math.PI / 180.0f;
                d2 = MathHelper.sin(f9);
                d3 = MathHelper.cos(f9);
                d4 = fishingBobberEntity.owner.prevX + (fishingBobberEntity.owner.x - fishingBobberEntity.owner.prevX) * (double)h - d3 * 0.35 - d2 * 0.85;
                d5 = fishingBobberEntity.owner.prevY + (fishingBobberEntity.owner.y - fishingBobberEntity.owner.prevY) * (double)h - 0.45;
                d6 = fishingBobberEntity.owner.prevZ + (fishingBobberEntity.owner.z - fishingBobberEntity.owner.prevZ) * (double)h - d2 * 0.35 + d3 * 0.85;
            }
            double d7 = fishingBobberEntity.prevX + (fishingBobberEntity.x - fishingBobberEntity.prevX) * (double)h;
            double d8 = fishingBobberEntity.prevY + (fishingBobberEntity.y - fishingBobberEntity.prevY) * (double)h + 0.25;
            double d9 = fishingBobberEntity.prevZ + (fishingBobberEntity.z - fishingBobberEntity.prevZ) * (double)h;
            double d10 = (float)(d4 - d7);
            double d11 = (float)(d5 - d8);
            double d12 = (float)(d6 - d9);
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2896);
            tessellator.start(3);
            tessellator.color(0);
            int n3 = 16;
            for (int i = 0; i <= n3; ++i) {
                float f12 = (float)i / (float)n3;
                tessellator.vertex(d + d10 * (double)f12, e + d11 * (double)(f12 * f12 + f12) * 0.5 + 0.25, f + d12 * (double)f12);
            }
            tessellator.draw();
            GL11.glEnable((int)2896);
            GL11.glEnable((int)3553);
        }
    }
}

