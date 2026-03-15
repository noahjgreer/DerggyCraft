package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.noahsarch.derggycraft.client.sound.MusicSyncSoundController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public abstract class SoundManagerMusicSyncMixin implements MusicSyncSoundController {
    @Shadow
    private SoundEntry music;

    @Shadow
    private GameOptions gameOptions;

    @Shadow
    private static SoundSystem soundSystem;

    @Shadow
    private static boolean started;

    @Override
    public void derggycraft$playSynchronizedMusic(String trackKey, float normalizedVolume) {
        if (trackKey == null || trackKey.isBlank()) {
            return;
        }

        if (!started || soundSystem == null || this.gameOptions == null || this.gameOptions.musicVolume <= 0.0F) {
            return;
        }

        Sound track = this.music.get(trackKey.trim());
        if (track == null) {
            return;
        }

        if (soundSystem.playing("streaming")) {
            soundSystem.stop("streaming");
        }
        soundSystem.stop("BgMusic");

        soundSystem.backgroundMusic("BgMusic", track.soundFile, track.id, false);
        soundSystem.setVolume("BgMusic", clamp01(normalizedVolume) * this.gameOptions.musicVolume);
        soundSystem.play("BgMusic");
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}