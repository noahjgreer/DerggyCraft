package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.tom.cpm.client.SinglePlayerCommands", remap = false)
public abstract class CPMSinglePlayerCommandSafetyMixin {
    @Inject(method = "executeCommand(Lnet/minecraft/client/Minecraft;Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void derggycraft$guardMalformedGiveCommand(Minecraft mc, String command, CallbackInfo ci) {
        if (command == null) {
            return;
        }

        String trimmed = command.trim();
        if (!trimmed.startsWith("/give")) {
            return;
        }

        String[] tokens = trimmed.split("\\s+");
        if (tokens.length >= 2) {
            return;
        }

        if (mc != null && mc.inGameHud != null) {
            mc.inGameHud.addChatMessage("Usage: /give <id> [amount] [meta]");
        }
        ci.cancel();
    }
}
