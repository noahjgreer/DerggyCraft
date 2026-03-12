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
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.SquidEntity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer
extends LivingEntityRenderer {
    public SquidEntityRenderer(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    public void render(SquidEntity squidEntity, double d, double e, double f, float g, float h) {
        super.render(squidEntity, d, e, f, g, h);
    }

    protected void applyHandSwingRotation(SquidEntity squidEntity, float f, float g, float h) {
        float f2 = squidEntity.lastTiltAngle + (squidEntity.tiltAngle - squidEntity.lastTiltAngle) * h;
        float f3 = squidEntity.lastRollAngle + (squidEntity.rollAngle - squidEntity.lastRollAngle) * h;
        GL11.glTranslatef((float)0.0f, (float)0.5f, (float)0.0f);
        GL11.glRotatef((float)(180.0f - g), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)f2, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)f3, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)0.0f, (float)-1.2f, (float)0.0f);
    }

    protected void applyScale(SquidEntity squidEntity, float f) {
    }

    protected float getHeadBob(SquidEntity squidEntity, float f) {
        float f2 = squidEntity.lastTentacleAngle + (squidEntity.tentacleAngle - squidEntity.lastTentacleAngle) * f;
        return f2;
    }
}

