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
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class BoxEntityRenderer
extends EntityRenderer {
    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        BoxEntityRenderer.renderShape(entity.boundingBox, x - entity.lastTickX, y - entity.lastTickY, z - entity.lastTickZ);
        GL11.glPopMatrix();
    }
}

