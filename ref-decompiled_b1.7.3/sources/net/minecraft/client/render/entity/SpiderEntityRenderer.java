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
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.mob.SpiderEntity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityRenderer
extends LivingEntityRenderer {
    public SpiderEntityRenderer() {
        super(new SpiderEntityModel(), 1.0f);
        this.setDecorationModel(new SpiderEntityModel());
    }

    protected float getDeathYaw(SpiderEntity spiderEntity) {
        return 180.0f;
    }

    protected boolean bindTexture(SpiderEntity spiderEntity, int i, float f) {
        if (i != 0) {
            return false;
        }
        if (i != 0) {
            return false;
        }
        this.bindTexture("/mob/spider_eyes.png");
        float f2 = (1.0f - spiderEntity.getBrightnessAtEyes(1.0f)) * 0.5f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3008);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)f2);
        return true;
    }
}

