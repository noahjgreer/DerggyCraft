package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.noahsarch.derggycraft.network.server.ServerMusicSync;
import net.noahsarch.derggycraft.sound.VanillaMusicTracks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerChatMixin {
    @Shadow
    private MinecraftServer server;

    @Shadow
    private ServerPlayerEntity player;

    @Shadow
    public abstract void sendPacket(Packet packet);

    @ModifyConstant(method = "onChatMessage", constant = @Constant(intValue = 100), require = 0)
    private int derggycraft$expandServerChatLengthLimit(int original) {
        return 32767;
    }

    @Inject(method = "handleCommand", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleMusicSyncCommand(String message, CallbackInfo ci) {
        String trimmed = message == null ? "" : message.trim();
        String[] args = trimmed.split("\\s+");
        if (args.length == 0 || !"/dcmusic".equalsIgnoreCase(args[0])) {
            return;
        }

        if (this.server == null || this.server.playerManager == null || this.player == null) {
            this.sendPacket(new ChatMessagePacket("\u00a7cMusic sync command unavailable right now."));
            ci.cancel();
            return;
        }

        if (!this.server.playerManager.isOperator(this.player.name)) {
            this.sendPacket(new ChatMessagePacket("\u00a7cYou don't have permission to use /dcmusic."));
            ci.cancel();
            return;
        }

        long leadMillis = 1200L;
        if (args.length >= 2) {
            try {
                leadMillis = Long.parseLong(args[1]);
            } catch (NumberFormatException ignored) {
                this.sendPacket(new ChatMessagePacket("\u00a7cUsage: /dcmusic [leadMs]"));
                ci.cancel();
                return;
            }
        }

        int recipients = ServerMusicSync.broadcastSynchronizedPlayback(
                this.player,
                leadMillis,
            true,
                1.0F,
                1.0F,
            VanillaMusicTracks.TRACK_KEYS
        );

        if (recipients > 0) {
            this.sendPacket(new ChatMessagePacket("\u00a7aTriggered synchronized local track playback for " + recipients + " player(s)."));
        } else {
            this.sendPacket(new ChatMessagePacket("\u00a7cNo recipients available for /dcmusic."));
        }

        ci.cancel();
    }
}
