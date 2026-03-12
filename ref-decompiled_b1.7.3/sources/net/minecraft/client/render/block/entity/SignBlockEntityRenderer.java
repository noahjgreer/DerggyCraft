/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignModel;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends BlockEntityRenderer {
    private SignModel model = new SignModel();

    public void render(SignBlockEntity signBlockEntity, double d, double e, double f, float g) {
        float f2;
        Block block = signBlockEntity.getBlock();
        GL11.glPushMatrix();
        float f3 = 0.6666667f;
        if (block == Block.SIGN) {
            GL11.glTranslatef((float)((float)d + 0.5f), (float)((float)e + 0.75f * f3), (float)((float)f + 0.5f));
            float f4 = (float)(signBlockEntity.getPushedBlockData() * 360) / 16.0f;
            GL11.glRotatef((float)(-f4), (float)0.0f, (float)1.0f, (float)0.0f);
            this.model.stick.visible = true;
        } else {
            int n = signBlockEntity.getPushedBlockData();
            f2 = 0.0f;
            if (n == 2) {
                f2 = 180.0f;
            }
            if (n == 4) {
                f2 = 90.0f;
            }
            if (n == 5) {
                f2 = -90.0f;
            }
            GL11.glTranslatef((float)((float)d + 0.5f), (float)((float)e + 0.75f * f3), (float)((float)f + 0.5f));
            GL11.glRotatef((float)(-f2), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)0.0f, (float)-0.3125f, (float)-0.4375f);
            this.model.stick.visible = false;
        }
        this.bindTexture("/item/sign.png");
        GL11.glPushMatrix();
        GL11.glScalef((float)f3, (float)(-f3), (float)(-f3));
        this.model.render();
        GL11.glPopMatrix();
        TextRenderer textRenderer = this.getTextRenderer();
        f2 = 0.016666668f * f3;
        GL11.glTranslatef((float)0.0f, (float)(0.5f * f3), (float)(0.07f * f3));
        GL11.glScalef((float)f2, (float)(-f2), (float)f2);
        GL11.glNormal3f((float)0.0f, (float)0.0f, (float)(-1.0f * f2));
        GL11.glDepthMask((boolean)false);
        int n = 0;
        for (int i = 0; i < signBlockEntity.texts.length; ++i) {
            String string = signBlockEntity.texts[i];
            if (i == signBlockEntity.currentRow) {
                string = "> " + string + " <";
                textRenderer.draw(string, -textRenderer.getWidth(string) / 2, i * 10 - signBlockEntity.texts.length * 5, n);
                continue;
            }
            textRenderer.draw(string, -textRenderer.getWidth(string) / 2, i * 10 - signBlockEntity.texts.length * 5, n);
        }
        GL11.glDepthMask((boolean)true);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }
}

