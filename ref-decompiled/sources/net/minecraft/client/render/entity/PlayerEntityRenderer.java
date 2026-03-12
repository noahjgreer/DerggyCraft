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
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer {
    private BipedEntityModel bipedModel;
    private BipedEntityModel armor1;
    private BipedEntityModel armor2;
    private static final String[] armorTextureNames = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

    public PlayerEntityRenderer() {
        super(new BipedEntityModel(0.0f), 0.5f);
        this.bipedModel = (BipedEntityModel)this.model;
        this.armor1 = new BipedEntityModel(1.0f);
        this.armor2 = new BipedEntityModel(0.5f);
    }

    protected boolean bindTexture(PlayerEntity playerEntity, int i, float f) {
        Item item;
        ItemStack itemStack = playerEntity.inventory.getArmorStack(3 - i);
        if (itemStack != null && (item = itemStack.getItem()) instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            this.bindTexture("/armor/" + armorTextureNames[armorItem.textureIndex] + "_" + (i == 2 ? 2 : 1) + ".png");
            BipedEntityModel bipedEntityModel = i == 2 ? this.armor2 : this.armor1;
            bipedEntityModel.head.visible = i == 0;
            bipedEntityModel.hat.visible = i == 0;
            bipedEntityModel.body.visible = i == 1 || i == 2;
            bipedEntityModel.rightArm.visible = i == 1;
            bipedEntityModel.leftArm.visible = i == 1;
            bipedEntityModel.rightLeg.visible = i == 2 || i == 3;
            bipedEntityModel.leftLeg.visible = i == 2 || i == 3;
            this.setDecorationModel(bipedEntityModel);
            return true;
        }
        return false;
    }

    public void render(PlayerEntity playerEntity, double d, double e, double f, float g, float h) {
        ItemStack itemStack = playerEntity.inventory.getSelectedItem();
        this.bipedModel.rightArmPose = itemStack != null;
        this.armor2.rightArmPose = this.bipedModel.rightArmPose;
        this.armor1.rightArmPose = this.bipedModel.rightArmPose;
        this.armor2.sneaking = this.bipedModel.sneaking = playerEntity.isSneaking();
        this.armor1.sneaking = this.bipedModel.sneaking;
        double d2 = e - (double)playerEntity.standingEyeHeight;
        if (playerEntity.isSneaking() && !(playerEntity instanceof ClientPlayerEntity)) {
            d2 -= 0.125;
        }
        super.render(playerEntity, d, d2, f, g, h);
        this.bipedModel.sneaking = false;
        this.armor2.sneaking = false;
        this.armor1.sneaking = false;
        this.bipedModel.rightArmPose = false;
        this.armor2.rightArmPose = false;
        this.armor1.rightArmPose = false;
    }

    protected void renderNameTag(PlayerEntity playerEntity, double d, double e, double f) {
        if (Minecraft.isDisplayGui() && playerEntity != this.dispatcher.cameraEntity) {
            float f2;
            float f3 = 1.6f;
            float f4 = 0.016666668f * f3;
            float f5 = playerEntity.getDistance(this.dispatcher.cameraEntity);
            float f6 = f2 = playerEntity.isSneaking() ? 32.0f : 64.0f;
            if (f5 < f2) {
                String string = playerEntity.name;
                if (!playerEntity.isSneaking()) {
                    if (playerEntity.isSleeping()) {
                        this.renderNameTag(playerEntity, string, d, e - 1.5, f, 64);
                    } else {
                        this.renderNameTag(playerEntity, string, d, e, f, 64);
                    }
                } else {
                    TextRenderer textRenderer = this.getTextRenderer();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)((float)d + 0.0f), (float)((float)e + 2.3f), (float)((float)f));
                    GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)(-this.dispatcher.yaw), (float)0.0f, (float)1.0f, (float)0.0f);
                    GL11.glRotatef((float)this.dispatcher.pitch, (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glScalef((float)(-f4), (float)(-f4), (float)f4);
                    GL11.glDisable((int)2896);
                    GL11.glTranslatef((float)0.0f, (float)(0.25f / f4), (float)0.0f);
                    GL11.glDepthMask((boolean)false);
                    GL11.glEnable((int)3042);
                    GL11.glBlendFunc((int)770, (int)771);
                    Tessellator tessellator = Tessellator.INSTANCE;
                    GL11.glDisable((int)3553);
                    tessellator.startQuads();
                    int n = textRenderer.getWidth(string) / 2;
                    tessellator.color(0.0f, 0.0f, 0.0f, 0.25f);
                    tessellator.vertex(-n - 1, -1.0, 0.0);
                    tessellator.vertex(-n - 1, 8.0, 0.0);
                    tessellator.vertex(n + 1, 8.0, 0.0);
                    tessellator.vertex(n + 1, -1.0, 0.0);
                    tessellator.draw();
                    GL11.glEnable((int)3553);
                    GL11.glDepthMask((boolean)true);
                    textRenderer.draw(string, -textRenderer.getWidth(string) / 2, 0, 0x20FFFFFF);
                    GL11.glEnable((int)2896);
                    GL11.glDisable((int)3042);
                    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    protected void renderMore(PlayerEntity playerEntity, float f) {
        ItemStack itemStack;
        float f2;
        ItemStack itemStack2 = playerEntity.inventory.getArmorStack(3);
        if (itemStack2 != null && itemStack2.getItem().id < 256) {
            GL11.glPushMatrix();
            this.bipedModel.head.transform(0.0625f);
            if (BlockRenderManager.isSideLit(Block.BLOCKS[itemStack2.itemId].getRenderType())) {
                float f3 = 0.625f;
                GL11.glTranslatef((float)0.0f, (float)-0.25f, (float)0.0f);
                GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glScalef((float)f3, (float)(-f3), (float)f3);
            }
            this.dispatcher.heldItemRenderer.renderItem(playerEntity, itemStack2);
            GL11.glPopMatrix();
        }
        if (playerEntity.name.equals("deadmau5") && this.bindDownloadedTexture(playerEntity.skinUrl, null)) {
            for (int i = 0; i < 2; ++i) {
                f2 = playerEntity.prevYaw + (playerEntity.yaw - playerEntity.prevYaw) * f - (playerEntity.lastBodyYaw + (playerEntity.bodyYaw - playerEntity.lastBodyYaw) * f);
                float f4 = playerEntity.prevPitch + (playerEntity.pitch - playerEntity.prevPitch) * f;
                GL11.glPushMatrix();
                GL11.glRotatef((float)f2, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)f4, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)(0.375f * (float)(i * 2 - 1)), (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)0.0f, (float)-0.375f, (float)0.0f);
                GL11.glRotatef((float)(-f4), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(-f2), (float)0.0f, (float)1.0f, (float)0.0f);
                float f5 = 1.3333334f;
                GL11.glScalef((float)f5, (float)f5, (float)f5);
                this.bipedModel.renderEars(0.0625f);
                GL11.glPopMatrix();
            }
        }
        if (this.bindDownloadedTexture(playerEntity.playerCapeUrl, null)) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.125f);
            double d = playerEntity.prevCapeX + (playerEntity.capeX - playerEntity.prevCapeX) * (double)f - (playerEntity.prevX + (playerEntity.x - playerEntity.prevX) * (double)f);
            double d2 = playerEntity.prevCapeY + (playerEntity.capeY - playerEntity.prevCapeY) * (double)f - (playerEntity.prevY + (playerEntity.y - playerEntity.prevY) * (double)f);
            double d3 = playerEntity.prevCapeZ + (playerEntity.capeZ - playerEntity.prevCapeZ) * (double)f - (playerEntity.prevZ + (playerEntity.z - playerEntity.prevZ) * (double)f);
            float f6 = playerEntity.lastBodyYaw + (playerEntity.bodyYaw - playerEntity.lastBodyYaw) * f;
            double d4 = MathHelper.sin(f6 * (float)Math.PI / 180.0f);
            double d5 = -MathHelper.cos(f6 * (float)Math.PI / 180.0f);
            float f7 = (float)d2 * 10.0f;
            if (f7 < -6.0f) {
                f7 = -6.0f;
            }
            if (f7 > 32.0f) {
                f7 = 32.0f;
            }
            float f8 = (float)(d * d4 + d3 * d5) * 100.0f;
            float f9 = (float)(d * d5 - d3 * d4) * 100.0f;
            if (f8 < 0.0f) {
                f8 = 0.0f;
            }
            float f10 = playerEntity.prevStepBobbingAmount + (playerEntity.stepBobbingAmount - playerEntity.prevStepBobbingAmount) * f;
            f7 += MathHelper.sin((playerEntity.prevHorizontalSpeed + (playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed) * f) * 6.0f) * 32.0f * f10;
            if (playerEntity.isSneaking()) {
                f7 += 25.0f;
            }
            GL11.glRotatef((float)(6.0f + f8 / 2.0f + f7), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)(f9 / 2.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)(-f9 / 2.0f), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            this.bipedModel.renderCape(0.0625f);
            GL11.glPopMatrix();
        }
        if ((itemStack = playerEntity.inventory.getSelectedItem()) != null) {
            GL11.glPushMatrix();
            this.bipedModel.rightArm.transform(0.0625f);
            GL11.glTranslatef((float)-0.0625f, (float)0.4375f, (float)0.0625f);
            if (playerEntity.fishHook != null) {
                itemStack = new ItemStack(Item.STICK);
            }
            if (itemStack.itemId < 256 && BlockRenderManager.isSideLit(Block.BLOCKS[itemStack.itemId].getRenderType())) {
                f2 = 0.5f;
                GL11.glTranslatef((float)0.0f, (float)0.1875f, (float)-0.3125f);
                GL11.glRotatef((float)20.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glScalef((float)(f2 *= 0.75f), (float)(-f2), (float)f2);
            } else if (Item.ITEMS[itemStack.itemId].isHandheld()) {
                f2 = 0.625f;
                if (Item.ITEMS[itemStack.itemId].isHandheldRod()) {
                    GL11.glRotatef((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                    GL11.glTranslatef((float)0.0f, (float)-0.125f, (float)0.0f);
                }
                GL11.glTranslatef((float)0.0f, (float)0.1875f, (float)0.0f);
                GL11.glScalef((float)f2, (float)(-f2), (float)f2);
                GL11.glRotatef((float)-100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            } else {
                f2 = 0.375f;
                GL11.glTranslatef((float)0.25f, (float)0.1875f, (float)-0.1875f);
                GL11.glScalef((float)f2, (float)f2, (float)f2);
                GL11.glRotatef((float)60.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            this.dispatcher.heldItemRenderer.renderItem(playerEntity, itemStack);
            GL11.glPopMatrix();
        }
    }

    protected void applyScale(PlayerEntity playerEntity, float f) {
        float f2 = 0.9375f;
        GL11.glScalef((float)f2, (float)f2, (float)f2);
    }

    public void renderHand() {
        this.bipedModel.handSwingProgress = 0.0f;
        this.bipedModel.setAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        this.bipedModel.rightArm.render(0.0625f);
    }

    protected void applyTranslation(PlayerEntity playerEntity, double d, double e, double f) {
        if (playerEntity.isAlive() && playerEntity.isSleeping()) {
            super.applyTranslation(playerEntity, d + (double)playerEntity.sleepOffsetX, e + (double)playerEntity.sleepOffsetY, f + (double)playerEntity.sleepOffsetZ);
        } else {
            super.applyTranslation(playerEntity, d, e, f);
        }
    }

    protected void applyHandSwingRotation(PlayerEntity playerEntity, float f, float g, float h) {
        if (playerEntity.isAlive() && playerEntity.isSleeping()) {
            GL11.glRotatef((float)playerEntity.getSleepingRotation(), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)this.getDeathYaw(playerEntity), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)270.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        } else {
            super.applyHandSwingRotation(playerEntity, f, g, h);
        }
    }
}

