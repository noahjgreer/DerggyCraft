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
import net.minecraft.entity.mob.GiantEntity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends LivingEntityRenderer {
    private float scale;

    public GiantEntityRenderer(EntityModel model, float shadowSize, float scale) {
        super(model, shadowSize * scale);
        this.scale = scale;
    }

    protected void applyScale(GiantEntity giantEntity, float f) {
        GL11.glScalef((float)this.scale, (float)this.scale, (float)this.scale);
    }
}

