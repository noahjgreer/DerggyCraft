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
import net.minecraft.entity.passive.CowEntity;

@Environment(value=EnvType.CLIENT)
public class CowEntityRenderer
extends LivingEntityRenderer {
    public CowEntityRenderer(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    public void render(CowEntity cowEntity, double d, double e, double f, float g, float h) {
        super.render(cowEntity, d, e, f, g, h);
    }
}

