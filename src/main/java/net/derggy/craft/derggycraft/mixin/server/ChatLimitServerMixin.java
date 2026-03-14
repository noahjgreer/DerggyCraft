package net.derggy.craft.derggycraft.mixin.server;

import net.derggy.craft.derggycraft.gamerule.GameruleManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends the server-side chat message length check and intercepts /gamerule commands.
 */
@Mixin(ServerPlayNetworkHandler.class)
public class ChatLimitServerMixin {

    @Shadow
    private MinecraftServer server;

    @Shadow
    public ServerPlayerEntity player;

    @ModifyConstant(method = "onChatMessage", constant = @Constant(intValue = 100))
    private int extendServerChatLimit(int original) {
        return 32768;
    }

    /**
     * Intercept /gamerule commands before they are forwarded to handleCommand.
     */
    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void interceptGameruleCommand(ChatMessagePacket packet, CallbackInfo ci) {
        String message = packet.chatMessage;
        if (message == null) return;

        String trimmed = message.trim();
        if (!trimmed.toLowerCase().startsWith("/gamerule")) return;

        ServerPlayNetworkHandler self = (ServerPlayNetworkHandler) (Object) this;

        // Check if player is an operator
        if (!this.server.playerManager.isOperator(this.player.name)) {
            self.sendPacket(new ChatMessagePacket("\u00a7cYou do not have permission to use this command."));
            ci.cancel();
            return;
        }

        String[] parts = trimmed.split("\\s+");

        if (parts.length == 1) {
            // /gamerule - list all gamerules
            self.sendPacket(new ChatMessagePacket("Available gamerules: keepInventory, extinguishTorches"));
            ci.cancel();
            return;
        }

        if (parts.length == 2) {
            // /gamerule <name> - query value
            String value = GameruleManager.getGamerule(parts[1]);
            if (value != null) {
                self.sendPacket(new ChatMessagePacket(parts[1] + " = " + value));
            } else {
                self.sendPacket(new ChatMessagePacket("\u00a7cUnknown gamerule: " + parts[1]));
            }
            ci.cancel();
            return;
        }

        if (parts.length == 3) {
            // /gamerule <name> <value> - set value
            if (GameruleManager.setGamerule(parts[1], parts[2])) {
                String value = GameruleManager.getGamerule(parts[1]);
                this.server.playerManager.sendToAll(
                        new ChatMessagePacket("Gamerule " + parts[1] + " set to " + value)
                );
            } else {
                self.sendPacket(new ChatMessagePacket("\u00a7cUnknown gamerule: " + parts[1]));
            }
            ci.cancel();
            return;
        }

        self.sendPacket(new ChatMessagePacket("\u00a7cUsage: /gamerule [name] [value]"));
        ci.cancel();
    }
}
