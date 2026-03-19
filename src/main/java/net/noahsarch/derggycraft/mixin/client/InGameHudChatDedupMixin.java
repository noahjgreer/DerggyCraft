package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudChatDedupMixin {
    private static final int DERGGYCRAFT_MAX_DEDUP_AGE_TICKS = 40;
    private static final int DERGGYCRAFT_RECENT_CHAT_SCAN_LIMIT = 8;

    @Shadow
    private Minecraft minecraft;

    @Shadow
    private List messages;

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    private void derggycraft$dedupeImmediateSelfChatEcho(String message, CallbackInfo ci) {
        if (message == null || this.minecraft == null || this.minecraft.player == null || !this.minecraft.isWorldRemote()) {
            return;
        }

        String normalizedIncoming = derggycraft$normalizeChatText(message);
        String selfPrefix = "<" + this.minecraft.player.name + "> ";
        if (!normalizedIncoming.startsWith(selfPrefix) || this.messages.isEmpty()) {
            return;
        }

        int scanned = 0;
        for (Object entry : this.messages) {
            if (scanned++ >= DERGGYCRAFT_RECENT_CHAT_SCAN_LIMIT) {
                break;
            }

            if (!(entry instanceof ChatHudLine line)) {
                continue;
            }

            if (line.age > DERGGYCRAFT_MAX_DEDUP_AGE_TICKS) {
                continue;
            }

            String normalizedExisting = derggycraft$normalizeChatText(line.text);
            // CPM can render a local self-message before the server relay arrives; collapse the second copy.
            if (normalizedIncoming.equals(normalizedExisting)) {
                ci.cancel();
                return;
            }
        }
    }

    private static String derggycraft$normalizeChatText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '\u00A7' && i + 1 < text.length()) {
                i++;
                continue;
            }
            builder.append(current);
        }
        return builder.toString().trim();
    }
}
