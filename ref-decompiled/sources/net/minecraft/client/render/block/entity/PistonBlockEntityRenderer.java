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
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderer
extends BlockEntityRenderer {
    private BlockRenderManager blockRenderManager;

    public void render(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g) {
        Block block = Block.BLOCKS[pistonBlockEntity.getPushedBlockId()];
        if (block != null && pistonBlockEntity.getProgress(g) < 1.0f) {
            Tessellator tessellator = Tessellator.INSTANCE;
            this.bindTexture("/terrain.png");
            Lighting.turnOff();
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glEnable((int)3042);
            GL11.glDisable((int)2884);
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GL11.glShadeModel((int)7425);
            } else {
                GL11.glShadeModel((int)7424);
            }
            tessellator.startQuads();
            tessellator.setOffset((float)d - (float)pistonBlockEntity.x + pistonBlockEntity.getRenderOffsetX(g), (float)e - (float)pistonBlockEntity.y + pistonBlockEntity.getRenderOffsetY(g), (float)f - (float)pistonBlockEntity.z + pistonBlockEntity.getRenderOffsetZ(g));
            tessellator.color(1, 1, 1);
            if (block == Block.PISTON_HEAD && pistonBlockEntity.getProgress(g) < 0.5f) {
                this.blockRenderManager.renderPistonHeadWithoutCulling(block, pistonBlockEntity.x, pistonBlockEntity.y, pistonBlockEntity.z, false);
            } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
                Block.PISTON_HEAD.setSprite(((PistonBlock)block).getTopTexture());
                this.blockRenderManager.renderPistonHeadWithoutCulling(Block.PISTON_HEAD, pistonBlockEntity.x, pistonBlockEntity.y, pistonBlockEntity.z, pistonBlockEntity.getProgress(g) < 0.5f);
                Block.PISTON_HEAD.clearSprite();
                tessellator.setOffset((float)d - (float)pistonBlockEntity.x, (float)e - (float)pistonBlockEntity.y, (float)f - (float)pistonBlockEntity.z);
                this.blockRenderManager.renderExtendedPiston(block, pistonBlockEntity.x, pistonBlockEntity.y, pistonBlockEntity.z);
            } else {
                this.blockRenderManager.renderWithoutCulling(block, pistonBlockEntity.x, pistonBlockEntity.y, pistonBlockEntity.z);
            }
            tessellator.setOffset(0.0, 0.0, 0.0);
            tessellator.draw();
            Lighting.turnOn();
        }
    }

    public void setWorld(World world) {
        this.blockRenderManager = new BlockRenderManager(world);
    }
}

