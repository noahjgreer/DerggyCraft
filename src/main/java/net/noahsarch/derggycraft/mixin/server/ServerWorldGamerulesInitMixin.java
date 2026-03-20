package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.noahsarch.derggycraft.network.server.ServerMusicSync;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import net.noahsarch.derggycraft.sound.VanillaMusicTracks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldGamerulesInitMixin {
    @Shadow
    private MinecraftServer server;

    @Unique
    private long derggycraft$nextMusicRollAtMillis = -1L;

    @Unique
    private static final long DERGGYCRAFT_MUSIC_ROLL_INTERVAL_MS = 30_000L;

    @Unique
    private static final int DERGGYCRAFT_MUSIC_ROLL_DENOMINATOR = 30;

    @Unique
    private static final long DERGGYCRAFT_MUSIC_LEAD_MS = 1400L;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void derggycraft$loadPersistentGamerules(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.dimension != null && world.dimension.id == 0) {
            DerggyCraftGameRules.ensureLoaded(world);
        }
    }

    @Inject(method = "updateWeatherCycles()V", at = @At("TAIL"))
    private void derggycraft$scheduleSynchronizedMusic(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.dimension == null || world.dimension.id != 0 || this.server == null || this.server.playerManager == null) {
            return;
        }

        if (this.server.playerManager.players == null || this.server.playerManager.players.isEmpty()) {
            this.derggycraft$nextMusicRollAtMillis = -1L;
            return;
        }

        long now = System.currentTimeMillis();
        if (this.derggycraft$nextMusicRollAtMillis < 0L) {
            this.derggycraft$nextMusicRollAtMillis = now + DERGGYCRAFT_MUSIC_ROLL_INTERVAL_MS;
            return;
        }

        if (now < this.derggycraft$nextMusicRollAtMillis) {
            return;
        }

        this.derggycraft$nextMusicRollAtMillis = now + DERGGYCRAFT_MUSIC_ROLL_INTERVAL_MS;
        if (DERGGYCRAFT_MUSIC_ROLL_DENOMINATOR > 1
                && world.random.nextInt(DERGGYCRAFT_MUSIC_ROLL_DENOMINATOR) != 0) {
            return;
        }

        ServerMusicSync.broadcastSynchronizedPlayback(
            this.server,
            DERGGYCRAFT_MUSIC_LEAD_MS,
            true,
            1.0F,
            1.0F,
            VanillaMusicTracks.TRACK_KEYS
        );
    }
}