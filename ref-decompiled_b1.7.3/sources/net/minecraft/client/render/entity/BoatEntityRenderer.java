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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends EntityRenderer {
    protected EntityModel model;

    public BoatEntityRenderer() {
        this.shadowRadius = 0.5f;
        this.model = new BoatEntityModel();
    }

    public void render(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)d), (float)((float)e), (float)((float)f));
        GL11.glRotatef((float)(180.0f - g), (float)0.0f, (float)1.0f, (float)0.0f);
        float f2 = (float)boatEntity.damageWobbleTicks - h;
        float f3 = (float)boatEntity.damageWobbleStrength - h;
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        if (f2 > 0.0f) {
            GL11.glRotatef((float)(MathHelper.sin(f2) * f2 * f3 / 10.0f * (float)boatEntity.damageWobbleSide), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        this.bindTexture("/terrain.png");
        float f4 = 0.75f;
        GL11.glScalef((float)f4, (float)f4, (float)f4);
        GL11.glScalef((float)(1.0f / f4), (float)(1.0f / f4), (float)(1.0f / f4));
        this.bindTexture("/item/boat.png");
        GL11.glScalef((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.model.render(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
}

