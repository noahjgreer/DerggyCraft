/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel
extends EntityModel {
    public ModelPart head;
    public ModelPart torso;
    public ModelPart rightHindLeg;
    public ModelPart leftHindLeg;
    public ModelPart rightFrontLeg;
    public ModelPart leftFrontLeg;
    ModelPart field_2095;
    ModelPart field_2096;
    ModelPart field_2097;
    ModelPart tail;
    ModelPart neck;

    public WolfEntityModel() {
        float f = 0.0f;
        float f2 = 13.5f;
        this.head = new ModelPart(0, 0);
        this.head.addCuboid(-3.0f, -3.0f, -2.0f, 6, 6, 4, f);
        this.head.setPivot(-1.0f, f2, -7.0f);
        this.torso = new ModelPart(18, 14);
        this.torso.addCuboid(-4.0f, -2.0f, -3.0f, 6, 9, 6, f);
        this.torso.setPivot(0.0f, 14.0f, 2.0f);
        this.neck = new ModelPart(21, 0);
        this.neck.addCuboid(-4.0f, -3.0f, -3.0f, 8, 6, 7, f);
        this.neck.setPivot(-1.0f, 14.0f, 2.0f);
        this.rightHindLeg = new ModelPart(0, 18);
        this.rightHindLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 8, 2, f);
        this.rightHindLeg.setPivot(-2.5f, 16.0f, 7.0f);
        this.leftHindLeg = new ModelPart(0, 18);
        this.leftHindLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 8, 2, f);
        this.leftHindLeg.setPivot(0.5f, 16.0f, 7.0f);
        this.rightFrontLeg = new ModelPart(0, 18);
        this.rightFrontLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 8, 2, f);
        this.rightFrontLeg.setPivot(-2.5f, 16.0f, -4.0f);
        this.leftFrontLeg = new ModelPart(0, 18);
        this.leftFrontLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 8, 2, f);
        this.leftFrontLeg.setPivot(0.5f, 16.0f, -4.0f);
        this.tail = new ModelPart(9, 18);
        this.tail.addCuboid(-1.0f, 0.0f, -1.0f, 2, 8, 2, f);
        this.tail.setPivot(-1.0f, 12.0f, 8.0f);
        this.field_2095 = new ModelPart(16, 14);
        this.field_2095.addCuboid(-3.0f, -5.0f, 0.0f, 2, 2, 1, f);
        this.field_2095.setPivot(-1.0f, f2, -7.0f);
        this.field_2096 = new ModelPart(16, 14);
        this.field_2096.addCuboid(1.0f, -5.0f, 0.0f, 2, 2, 1, f);
        this.field_2096.setPivot(-1.0f, f2, -7.0f);
        this.field_2097 = new ModelPart(0, 10);
        this.field_2097.addCuboid(-2.0f, 0.0f, -5.0f, 3, 3, 4, f);
        this.field_2097.setPivot(-0.5f, f2, -7.0f);
    }

    public void render(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.render(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.setAngles(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.head.renderForceTransform(scale);
        this.torso.render(scale);
        this.rightHindLeg.render(scale);
        this.leftHindLeg.render(scale);
        this.rightFrontLeg.render(scale);
        this.leftFrontLeg.render(scale);
        this.field_2095.renderForceTransform(scale);
        this.field_2096.renderForceTransform(scale);
        this.field_2097.renderForceTransform(scale);
        this.tail.renderForceTransform(scale);
        this.neck.render(scale);
    }

    public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        float f;
        WolfEntity wolfEntity = (WolfEntity)entity;
        this.tail.yaw = wolfEntity.isAngry() ? 0.0f : MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        if (wolfEntity.isInSittingPose()) {
            this.neck.setPivot(-1.0f, 16.0f, -3.0f);
            this.neck.pitch = 1.2566371f;
            this.neck.yaw = 0.0f;
            this.torso.setPivot(0.0f, 18.0f, 0.0f);
            this.torso.pitch = 0.7853982f;
            this.tail.setPivot(-1.0f, 21.0f, 6.0f);
            this.rightHindLeg.setPivot(-2.5f, 22.0f, 2.0f);
            this.rightHindLeg.pitch = 4.712389f;
            this.leftHindLeg.setPivot(0.5f, 22.0f, 2.0f);
            this.leftHindLeg.pitch = 4.712389f;
            this.rightFrontLeg.pitch = 5.811947f;
            this.rightFrontLeg.setPivot(-2.49f, 17.0f, -4.0f);
            this.leftFrontLeg.pitch = 5.811947f;
            this.leftFrontLeg.setPivot(0.51f, 17.0f, -4.0f);
        } else {
            this.torso.setPivot(0.0f, 14.0f, 2.0f);
            this.torso.pitch = 1.5707964f;
            this.neck.setPivot(-1.0f, 14.0f, -3.0f);
            this.neck.pitch = this.torso.pitch;
            this.tail.setPivot(-1.0f, 12.0f, 8.0f);
            this.rightHindLeg.setPivot(-2.5f, 16.0f, 7.0f);
            this.leftHindLeg.setPivot(0.5f, 16.0f, 7.0f);
            this.rightFrontLeg.setPivot(-2.5f, 16.0f, -4.0f);
            this.leftFrontLeg.setPivot(0.5f, 16.0f, -4.0f);
            this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
            this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
            this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
            this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        }
        this.head.roll = f = wolfEntity.getBegAnimationProgress(tickDelta) + wolfEntity.getShakeAnimationProgress(tickDelta, 0.0f);
        this.field_2095.roll = f;
        this.field_2096.roll = f;
        this.field_2097.roll = f;
        this.neck.roll = wolfEntity.getShakeAnimationProgress(tickDelta, -0.08f);
        this.torso.roll = wolfEntity.getShakeAnimationProgress(tickDelta, -0.16f);
        this.tail.roll = wolfEntity.getShakeAnimationProgress(tickDelta, -0.2f);
        if (wolfEntity.isFurWet()) {
            float f2 = wolfEntity.getBrightnessAtEyes(tickDelta) * wolfEntity.getFurBrightnessMultiplier(tickDelta);
            GL11.glColor3f((float)f2, (float)f2, (float)f2);
        }
    }

    public void setAngles(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.setAngles(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.head.pitch = headPitch / 57.295776f;
        this.field_2095.yaw = this.head.yaw = headYaw / 57.295776f;
        this.field_2095.pitch = this.head.pitch;
        this.field_2096.yaw = this.head.yaw;
        this.field_2096.pitch = this.head.pitch;
        this.field_2097.yaw = this.head.yaw;
        this.field_2097.pitch = this.head.pitch;
        this.tail.pitch = animationProgress;
    }
}

