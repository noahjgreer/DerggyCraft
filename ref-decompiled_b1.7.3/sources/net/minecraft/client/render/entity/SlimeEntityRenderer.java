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
import net.minecraft.entity.mob.SlimeEntity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends LivingEntityRenderer {
    private EntityModel innerModel;

    public SlimeEntityRenderer(EntityModel model, EntityModel innerModel, float tickDelta) {
        super(model, tickDelta);
        this.innerModel = innerModel;
    }

    protected boolean bindTexture(SlimeEntity slimeEntity, int i, float f) {
        if (i == 0) {
            this.setDecorationModel(this.innerModel);
            GL11.glEnable((int)2977);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            return true;
        }
        if (i == 1) {
            GL11.glDisable((int)3042);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        return false;
    }

    protected void applyScale(SlimeEntity slimeEntity, float f) {
        int n = slimeEntity.getSize();
        float f2 = (slimeEntity.lastStretch + (slimeEntity.stretch - slimeEntity.lastStretch) * f) / ((float)n * 0.5f + 1.0f);
        float f3 = 1.0f / (f2 + 1.0f);
        float f4 = n;
        GL11.glScalef((float)(f3 * f4), (float)(1.0f / f3 * f4), (float)(f3 * f4));
    }
}

