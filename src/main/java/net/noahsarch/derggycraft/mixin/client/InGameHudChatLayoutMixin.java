package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.noahsarch.derggycraft.client.chat.ChatInputLayoutState;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudChatLayoutMixin {
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 2),
            require = 0
    )
    private void derggycraft$offsetChatHudForWrappedInput(float x, float y, float z) {
        GL11.glTranslatef(x, y - ChatInputLayoutState.getChatOffset(), z);
    }
}
