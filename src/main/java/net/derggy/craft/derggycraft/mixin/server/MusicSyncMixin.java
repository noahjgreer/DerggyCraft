package net.derggy.craft.derggycraft.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Namespace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Server-side music sync: maintains a timer and broadcasts "play music" packets
 * to all players simultaneously.
 */
@Mixin(MinecraftServer.class)
public class MusicSyncMixin {

    @Unique
    private static final Random musicRandom = new Random();

    @Unique
    private static int timeUntilNextSong = musicRandom.nextInt(12000);

    @SuppressWarnings("UnstableApiUsage")
    @Unique
    private static final Namespace NAMESPACE = Namespace.resolve();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onServerTickMusic(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;

        if (server.playerManager == null || server.playerManager.players.isEmpty()) return;

        if (timeUntilNextSong > 0) {
            timeUntilNextSong--;
            return;
        }

        // Time to trigger music for all players
        timeUntilNextSong = musicRandom.nextInt(12000) + 12000;

        MessagePacket msg = new MessagePacket(NAMESPACE.id("music_sync"));
        for (Object obj : server.playerManager.players) {
            PacketHelper.sendTo((net.minecraft.entity.player.PlayerEntity) obj, msg);
        }
    }
}
