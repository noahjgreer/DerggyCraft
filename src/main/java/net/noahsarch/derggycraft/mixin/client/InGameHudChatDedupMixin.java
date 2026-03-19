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
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private List messages;

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    private void derggycraft$dedupeImmediateSelfChatEcho(String message, CallbackInfo ci) {
        if (message == null || this.minecraft == null || this.minecraft.player == null || !this.minecraft.isWorldRemote()) {
            return;
        }

        String selfPrefix = "<" + this.minecraft.player.name + "> ";
        if (!message.startsWith(selfPrefix) || this.messages.isEmpty()) {
            return;
        }

        Object first = this.messages.get(0);
        if (!(first instanceof ChatHudLine firstLine)) {
            return;
        }

        // CPM adds a local echo before the server message arrives; swallow only the immediate duplicate.
        if (message.equals(firstLine.text) && firstLine.age <= 2) {
            ci.cancel();
        }
    }
}
