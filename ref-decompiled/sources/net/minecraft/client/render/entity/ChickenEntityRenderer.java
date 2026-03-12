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
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderer
extends LivingEntityRenderer {
    public ChickenEntityRenderer(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    public void render(ChickenEntity chickenEntity, double d, double e, double f, float g, float h) {
        super.render(chickenEntity, d, e, f, g, h);
    }

    protected float getHeadBob(ChickenEntity chickenEntity, float f) {
        float f2 = chickenEntity.prevFlapProgress + (chickenEntity.flapProgress - chickenEntity.prevFlapProgress) * f;
        float f3 = chickenEntity.prevMaxWingDeviation + (chickenEntity.maxWingDeviation - chickenEntity.prevMaxWingDeviation) * f;
        return (MathHelper.sin(f2) + 1.0f) * f3;
    }
}

