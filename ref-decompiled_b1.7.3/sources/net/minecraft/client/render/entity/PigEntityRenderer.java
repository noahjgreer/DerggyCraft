/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.PigEntity;

@Environment(value=EnvType.CLIENT)
public class PigEntityRenderer
extends LivingEntityRenderer {
    public PigEntityRenderer(EntityModel model, EntityModel saddleModel, float shadowRadius) {
        super(model, shadowRadius);
        this.setDecorationModel(saddleModel);
    }

    protected boolean bindTexture(PigEntity pigEntity, int i, float f) {
        this.bindTexture("/mob/saddle.png");
        return i == 0 && pigEntity.isSaddled();
    }
}

