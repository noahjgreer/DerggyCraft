/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.network.packet.play.UpdateSignPacket;
import net.minecraft.util.CharacterUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends Screen {
    protected String title = "Edit sign message:";
    private SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow = 0;
    private static final String VALID_CHARACTERS = CharacterUtils.VALID_CHARACTERS;

    public SignEditScreen(SignBlockEntity sign) {
        this.sign = sign;
    }

    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents((boolean)true);
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
    }

    public void removed() {
        Keyboard.enableRepeatEvents((boolean)false);
        if (this.minecraft.world.isRemote) {
            this.minecraft.getNetworkHandler().sendPacket(new UpdateSignPacket(this.sign.x, this.sign.y, this.sign.z, this.sign.texts));
        }
    }

    public void tick() {
        ++this.ticksSinceOpened;
    }

    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }
        if (button.id == 0) {
            this.sign.markDirty();
            this.minecraft.setScreen(null);
        }
    }

    protected void keyPressed(char character, int keyCode) {
        if (keyCode == 200) {
            this.currentRow = this.currentRow - 1 & 3;
        }
        if (keyCode == 208 || keyCode == 28) {
            this.currentRow = this.currentRow + 1 & 3;
        }
        if (keyCode == 14 && this.sign.texts[this.currentRow].length() > 0) {
            this.sign.texts[this.currentRow] = this.sign.texts[this.currentRow].substring(0, this.sign.texts[this.currentRow].length() - 1);
        }
        if (VALID_CHARACTERS.indexOf(character) >= 0 && this.sign.texts[this.currentRow].length() < 15) {
            int n = this.currentRow;
            this.sign.texts[n] = this.sign.texts[n] + character;
        }
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), (float)0.0f, (float)50.0f);
        float f = 93.75f;
        GL11.glScalef((float)(-f), (float)(-f), (float)(-f));
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        Block block = this.sign.getBlock();
        if (block == Block.SIGN) {
            float f2 = (float)(this.sign.getPushedBlockData() * 360) / 16.0f;
            GL11.glRotatef((float)f2, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)0.0f, (float)-1.0625f, (float)0.0f);
        } else {
            int n = this.sign.getPushedBlockData();
            float f3 = 0.0f;
            if (n == 2) {
                f3 = 180.0f;
            }
            if (n == 4) {
                f3 = 90.0f;
            }
            if (n == 5) {
                f3 = -90.0f;
            }
            GL11.glRotatef((float)f3, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)0.0f, (float)-1.0625f, (float)0.0f);
        }
        if (this.ticksSinceOpened / 6 % 2 == 0) {
            this.sign.currentRow = this.currentRow;
        }
        BlockEntityRenderDispatcher.INSTANCE.render(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.currentRow = -1;
        GL11.glPopMatrix();
        super.render(mouseX, mouseY, delta);
    }
}

