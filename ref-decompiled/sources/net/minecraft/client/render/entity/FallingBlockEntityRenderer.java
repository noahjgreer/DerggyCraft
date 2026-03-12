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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer {
    private BlockRenderManager blockRenderManager = new BlockRenderManager();

    public FallingBlockEntityRenderer() {
        this.shadowRadius = 0.5f;
    }

    public void render(FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)d), (float)((float)e), (float)((float)f));
        this.bindTexture("/terrain.png");
        Block block = Block.BLOCKS[fallingBlockEntity.blockId];
        World world = fallingBlockEntity.getWorld();
        GL11.glDisable((int)2896);
        this.blockRenderManager.renderFallingBlockEntity(block, world, MathHelper.floor(fallingBlockEntity.x), MathHelper.floor(fallingBlockEntity.y), MathHelper.floor(fallingBlockEntity.z));
        GL11.glEnable((int)2896);
        GL11.glPopMatrix();
    }
}

