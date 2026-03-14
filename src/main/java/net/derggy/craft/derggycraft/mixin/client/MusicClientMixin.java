package net.derggy.craft.derggycraft.mixin.client;

import net.derggy.craft.derggycraft.network.MusicNetworkInit;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

/**
 * Overrides client music scheduling to respond to server sync packets.
 * Disables the local timer-based music and uses server triggers instead.
 */
@Mixin(SoundManager.class)
public class MusicClientMixin {

    @Shadow
    private static SoundSystem soundSystem;

    @Shadow
    private static boolean started;

    @Shadow
    private GameOptions gameOptions;

    @Shadow
    private SoundEntry music;

    @Shadow
    private int timeUntilNextSong;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void overrideMusicTick(CallbackInfo ci) {
        if (!started || this.gameOptions.musicVolume == 0.0f) {
            ci.cancel();
            return;
        }

        // Prevent the vanilla timer from ever reaching 0
        this.timeUntilNextSong = 12000;

        // Check if the server triggered music
        if (MusicNetworkInit.shouldPlayMusic) {
            MusicNetworkInit.shouldPlayMusic = false;

            // Only play if nothing is currently playing
            if (!soundSystem.playing("BgMusic") && !soundSystem.playing("streaming")) {
                Sound sound = this.music.getSounds();
                if (sound != null) {
                    soundSystem.backgroundMusic("BgMusic", sound.soundFile, sound.id, false);
                    soundSystem.setVolume("BgMusic", this.gameOptions.musicVolume);
                    soundSystem.play("BgMusic");
                }
            }
        }

        ci.cancel();
    }
}
