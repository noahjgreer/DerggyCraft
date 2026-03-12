/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.render.ProgressRenderError;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.ScreenScaler;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class ProgressRenderer
implements LoadingDisplay {
    private String stage = "";
    private Minecraft minecraft;
    private String title = "";
    private long lastTime = System.currentTimeMillis();
    private boolean noAbort = false;

    public ProgressRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void progressStart(String title) {
        this.noAbort = false;
        this.start(title);
    }

    public void progressStartNoAbort(String title) {
        this.noAbort = true;
        this.start(this.title);
    }

    public void start(String title) {
        if (!this.minecraft.running) {
            if (this.noAbort) {
                return;
            }
            throw new ProgressRenderError();
        }
        this.title = title;
        ScreenScaler screenScaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        GL11.glClear((int)256);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)screenScaler.rawScaledWidth, (double)screenScaler.rawScaledHeight, (double)0.0, (double)100.0, (double)300.0);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-200.0f);
    }

    public void progressStage(String stage) {
        if (!this.minecraft.running) {
            if (this.noAbort) {
                return;
            }
            throw new ProgressRenderError();
        }
        this.lastTime = 0L;
        this.stage = stage;
        this.progressStagePercentage(-1);
        this.lastTime = 0L;
    }

    public void progressStagePercentage(int percentage) {
        if (!this.minecraft.running) {
            if (this.noAbort) {
                return;
            }
            throw new ProgressRenderError();
        }
        long l = System.currentTimeMillis();
        if (l - this.lastTime < 20L) {
            return;
        }
        this.lastTime = l;
        ScreenScaler screenScaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        int n = screenScaler.getScaledWidth();
        int n2 = screenScaler.getScaledHeight();
        GL11.glClear((int)256);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)screenScaler.rawScaledWidth, (double)screenScaler.rawScaledHeight, (double)0.0, (double)100.0, (double)300.0);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-200.0f);
        GL11.glClear((int)16640);
        Tessellator tessellator = Tessellator.INSTANCE;
        int n3 = this.minecraft.textureManager.getTextureId("/gui/background.png");
        GL11.glBindTexture((int)3553, (int)n3);
        float f = 32.0f;
        tessellator.startQuads();
        tessellator.color(0x404040);
        tessellator.vertex(0.0, n2, 0.0, 0.0, (float)n2 / f);
        tessellator.vertex(n, n2, 0.0, (float)n / f, (float)n2 / f);
        tessellator.vertex(n, 0.0, 0.0, (float)n / f, 0.0);
        tessellator.vertex(0.0, 0.0, 0.0, 0.0, 0.0);
        tessellator.draw();
        if (percentage >= 0) {
            int n4 = 100;
            int n5 = 2;
            int n6 = n / 2 - n4 / 2;
            int n7 = n2 / 2 + 16;
            GL11.glDisable((int)3553);
            tessellator.startQuads();
            tessellator.color(0x808080);
            tessellator.vertex(n6, n7, 0.0);
            tessellator.vertex(n6, n7 + n5, 0.0);
            tessellator.vertex(n6 + n4, n7 + n5, 0.0);
            tessellator.vertex(n6 + n4, n7, 0.0);
            tessellator.color(0x80FF80);
            tessellator.vertex(n6, n7, 0.0);
            tessellator.vertex(n6, n7 + n5, 0.0);
            tessellator.vertex(n6 + percentage, n7 + n5, 0.0);
            tessellator.vertex(n6 + percentage, n7, 0.0);
            tessellator.draw();
            GL11.glEnable((int)3553);
        }
        this.minecraft.textRenderer.drawWithShadow(this.title, (n - this.minecraft.textRenderer.getWidth(this.title)) / 2, n2 / 2 - 4 - 16, 0xFFFFFF);
        this.minecraft.textRenderer.drawWithShadow(this.stage, (n - this.minecraft.textRenderer.getWidth(this.stage)) / 2, n2 / 2 - 4 + 8, 0xFFFFFF);
        Display.update();
        try {
            Thread.yield();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

