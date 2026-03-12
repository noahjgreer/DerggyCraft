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
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class GhastEntityRenderer
extends LivingEntityRenderer {
    public GhastEntityRenderer() {
        super(new GhastEntityModel(), 0.5f);
    }

    protected void applyScale(GhastEntity ghastEntity, float f) {
        GhastEntity ghastEntity2 = ghastEntity;
        float f2 = ((float)ghastEntity2.lastChargeTime + (float)(ghastEntity2.chargeTime - ghastEntity2.lastChargeTime) * f) / 20.0f;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        f2 = 1.0f / (f2 * f2 * f2 * f2 * f2 * 2.0f + 1.0f);
        float f3 = (8.0f + f2) / 2.0f;
        float f4 = (8.0f + 1.0f / f2) / 2.0f;
        GL11.glScalef((float)f4, (float)f3, (float)f4);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

