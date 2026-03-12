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
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class ProjectileEntityRenderer
extends EntityRenderer {
    private int itemTextureId;

    public ProjectileEntityRenderer(int itemTextureId) {
        this.itemTextureId = itemTextureId;
    }

    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x), (float)((float)y), (float)((float)z));
        GL11.glEnable((int)32826);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        this.bindTexture("/gui/items.png");
        Tessellator tessellator = Tessellator.INSTANCE;
        float f = (float)(this.itemTextureId % 16 * 16 + 0) / 256.0f;
        float f2 = (float)(this.itemTextureId % 16 * 16 + 16) / 256.0f;
        float f3 = (float)(this.itemTextureId / 16 * 16 + 0) / 256.0f;
        float f4 = (float)(this.itemTextureId / 16 * 16 + 16) / 256.0f;
        float f5 = 1.0f;
        float f6 = 0.5f;
        float f7 = 0.25f;
        GL11.glRotatef((float)(180.0f - this.dispatcher.yaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.dispatcher.pitch), (float)1.0f, (float)0.0f, (float)0.0f);
        tessellator.startQuads();
        tessellator.normal(0.0f, 1.0f, 0.0f);
        tessellator.vertex(0.0f - f6, 0.0f - f7, 0.0, f, f4);
        tessellator.vertex(f5 - f6, 0.0f - f7, 0.0, f2, f4);
        tessellator.vertex(f5 - f6, 1.0f - f7, 0.0, f2, f3);
        tessellator.vertex(0.0f - f6, 1.0f - f7, 0.0, f, f3);
        tessellator.draw();
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
    }
}

