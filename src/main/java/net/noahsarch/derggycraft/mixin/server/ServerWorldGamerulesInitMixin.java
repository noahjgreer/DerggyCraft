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
    private long derggycraft$nextMusicSyncAtMillis = -1L;

    @Unique
    private static final long DERGGYCRAFT_MUSIC_MIN_INTERVAL_MS = 120_000L;

    @Unique
    private static final long DERGGYCRAFT_MUSIC_MAX_INTERVAL_MS = 240_000L;

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
            this.derggycraft$nextMusicSyncAtMillis = -1L;
            return;
        }

        long now = System.currentTimeMillis();
        if (this.derggycraft$nextMusicSyncAtMillis < 0L) {
            this.derggycraft$scheduleNextMusicSync(world, now);
            return;
        }

        if (now < this.derggycraft$nextMusicSyncAtMillis) {
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
        this.derggycraft$scheduleNextMusicSync(world, now);
    }

    @Unique
    private void derggycraft$scheduleNextMusicSync(ServerWorld world, long now) {
        long range = DERGGYCRAFT_MUSIC_MAX_INTERVAL_MS - DERGGYCRAFT_MUSIC_MIN_INTERVAL_MS;
        long randomOffset = range <= 0L ? 0L : (long) (world.random.nextDouble() * (double) (range + 1L));
        this.derggycraft$nextMusicSyncAtMillis = now + DERGGYCRAFT_MUSIC_MIN_INTERVAL_MS + randomOffset;
    }
}