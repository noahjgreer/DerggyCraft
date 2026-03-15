package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.noahsarch.derggycraft.client.chat.ChatInputLayoutState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    private static final int DERGGYCRAFT_MAX_CHAT_LENGTH = 32767;

    @Shadow
    protected String text;

    @Shadow
    private int focusedTicks;

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 100), require = 0)
    private int derggycraft$expandClientTypingLimit(int original) {
        return DERGGYCRAFT_MAX_CHAT_LENGTH;
    }

    @ModifyConstant(method = "mouseClicked", constant = @Constant(intValue = 100), require = 0)
    private int derggycraft$expandClientNameInsertLimit(int original) {
        return DERGGYCRAFT_MAX_CHAT_LENGTH;
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void derggycraft$resetLayoutOnInit(CallbackInfo ci) {
        ChatInputLayoutState.reset();
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void derggycraft$resetLayoutOnClose(CallbackInfo ci) {
        ChatInputLayoutState.reset();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void derggycraft$renderWrappedInput(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.textRenderer == null) {
            return;
        }

        String cursor = this.focusedTicks / 6 % 2 == 0 ? "_" : "";
        String displayText = "> " + (this.text == null ? "" : this.text) + cursor;
        int maxTextWidth = this.width - 8;
        int wrappedHeight = this.textRenderer.splitAndGetHeight(displayText, maxTextWidth);

        int boxBottom = this.height - 2;
        int boxTop = boxBottom - wrappedHeight - 2;
        this.fill(2, boxTop, this.width - 2, boxBottom, Integer.MIN_VALUE);
        this.textRenderer.drawSplit(displayText, 4, boxTop + 1, maxTextWidth, 0xE0E0E0);

        ChatInputLayoutState.setChatOffset(Math.max(0, wrappedHeight - 8) + 4);

        super.render(mouseX, mouseY, delta);
        ci.cancel();
    }
}
