package net.derggy.craft.derggycraft.mixin.client;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends chat input limit and adds visual line wrapping for long messages.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow
    protected String text;

    /**
     * Extend the 100-char typing limit in keyPressed to 32768.
     */
    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = 100, ordinal = 0))
    private int extendTypingLimit(int original) {
        return 32768;
    }

    /**
     * Extend the 100-char limit in mouseClicked (name insertion).
     */
    @ModifyConstant(method = "mouseClicked", constant = @Constant(intValue = 100))
    private int extendClickLimit(int original) {
        return 32768;
    }

    /**
     * Override rendering to wrap long text into multiple lines.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderWrappedChat(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int focusedTicks;
        try {
            java.lang.reflect.Field f = ChatScreen.class.getDeclaredField("focusedTicks");
            f.setAccessible(true);
            focusedTicks = f.getInt(this);
        } catch (Exception e) {
            focusedTicks = 0;
        }

        String displayText = "> " + this.text + (focusedTicks / 6 % 2 == 0 ? "_" : "");
        int maxWidth = this.width - 8;

        // Calculate wrapped lines
        List<String> lines = new ArrayList<>();
        if (this.textRenderer != null && maxWidth > 0) {
            int lineStart = 0;
            while (lineStart < displayText.length()) {
                String remaining = displayText.substring(lineStart);
                int w = this.textRenderer.getWidth(remaining);
                if (w <= maxWidth) {
                    lines.add(remaining);
                    break;
                }
                // Binary search for the right cutoff
                int lo = 1, hi = remaining.length();
                while (lo < hi) {
                    int mid = (lo + hi + 1) / 2;
                    if (this.textRenderer.getWidth(remaining.substring(0, mid)) <= maxWidth) {
                        lo = mid;
                    } else {
                        hi = mid - 1;
                    }
                }
                lines.add(remaining.substring(0, lo));
                lineStart += lo;
            }
        } else {
            lines.add(displayText);
        }

        if (lines.isEmpty()) {
            lines.add(displayText);
        }

        int lineHeight = 12;
        int totalHeight = lines.size() * lineHeight + 2;
        int baseY = this.height - totalHeight;

        // Draw background
        this.fill(2, baseY, this.width - 2, this.height - 2, Integer.MIN_VALUE);

        // Draw each line
        for (int i = 0; i < lines.size(); i++) {
            this.drawTextWithShadow(this.textRenderer, lines.get(i), 4, baseY + 1 + i * lineHeight, 0xE0E0E0);
        }

        ci.cancel();
    }
}
