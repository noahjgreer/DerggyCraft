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
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class UndeadEntityRenderer
extends LivingEntityRenderer {
    protected BipedEntityModel entityModel;

    public UndeadEntityRenderer(BipedEntityModel model, float shadowSize) {
        super(model, shadowSize);
        this.entityModel = model;
    }

    protected void renderMore(LivingEntity entity, float tickDelta) {
        ItemStack itemStack = entity.getHeldItem();
        if (itemStack != null) {
            GL11.glPushMatrix();
            this.entityModel.rightArm.transform(0.0625f);
            GL11.glTranslatef((float)-0.0625f, (float)0.4375f, (float)0.0625f);
            if (itemStack.itemId < 256 && BlockRenderManager.isSideLit(Block.BLOCKS[itemStack.itemId].getRenderType())) {
                float f = 0.5f;
                GL11.glTranslatef((float)0.0f, (float)0.1875f, (float)-0.3125f);
                GL11.glRotatef((float)20.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glScalef((float)(f *= 0.75f), (float)(-f), (float)f);
            } else if (Item.ITEMS[itemStack.itemId].isHandheld()) {
                float f = 0.625f;
                GL11.glTranslatef((float)0.0f, (float)0.1875f, (float)0.0f);
                GL11.glScalef((float)f, (float)(-f), (float)f);
                GL11.glRotatef((float)-100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            } else {
                float f = 0.375f;
                GL11.glTranslatef((float)0.25f, (float)0.1875f, (float)-0.1875f);
                GL11.glScalef((float)f, (float)f, (float)f);
                GL11.glRotatef((float)60.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            this.dispatcher.heldItemRenderer.renderItem(entity, itemStack);
            GL11.glPopMatrix();
        }
    }
}

