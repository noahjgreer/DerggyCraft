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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class FireballEntityRenderer
extends EntityRenderer {
    public void render(FireballEntity fireballEntity, double d, double e, double f, float g, float h) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)d), (float)((float)e), (float)((float)f));
        GL11.glEnable((int)32826);
        float f2 = 2.0f;
        GL11.glScalef((float)(f2 / 1.0f), (float)(f2 / 1.0f), (float)(f2 / 1.0f));
        int n = Item.SNOWBALL.getTextureId(0);
        this.bindTexture("/gui/items.png");
        Tessellator tessellator = Tessellator.INSTANCE;
        float f3 = (float)(n % 16 * 16 + 0) / 256.0f;
        float f4 = (float)(n % 16 * 16 + 16) / 256.0f;
        float f5 = (float)(n / 16 * 16 + 0) / 256.0f;
        float f6 = (float)(n / 16 * 16 + 16) / 256.0f;
        float f7 = 1.0f;
        float f8 = 0.5f;
        float f9 = 0.25f;
        GL11.glRotatef((float)(180.0f - this.dispatcher.yaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.dispatcher.pitch), (float)1.0f, (float)0.0f, (float)0.0f);
        tessellator.startQuads();
        tessellator.normal(0.0f, 1.0f, 0.0f);
        tessellator.vertex(0.0f - f8, 0.0f - f9, 0.0, f3, f6);
        tessellator.vertex(f7 - f8, 0.0f - f9, 0.0, f4, f6);
        tessellator.vertex(f7 - f8, 1.0f - f9, 0.0, f4, f5);
        tessellator.vertex(0.0f - f8, 1.0f - f9, 0.0, f3, f5);
        tessellator.draw();
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }
}

