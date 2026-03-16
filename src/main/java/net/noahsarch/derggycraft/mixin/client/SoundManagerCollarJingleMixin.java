package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.modificationstation.stationapi.api.client.sound.CustomSoundMap;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.sound.BackportedVanillaSounds;
import net.noahsarch.derggycraft.sound.CollarJingleSounds;
import net.noahsarch.derggycraft.sound.FlareLoopSound;
import net.noahsarch.derggycraft.sound.IntroLogoSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;

@Mixin(SoundManager.class)
public abstract class SoundManagerCollarJingleMixin {
    @Shadow
    private SoundEntry sounds;

    @Unique
    private static boolean derggycraft$collarJinglesRegistered;
    @Unique
    private static boolean derggycraft$introSoundRegistered;
    @Unique
    private static boolean derggycraft$flareLoopRegistered;
    @Unique
    private static boolean derggycraft$backportedVanillaSoundsRegistered;

    @Inject(method = "loadSounds", at = @At("TAIL"))
    private void derggycraft$registerCollarJingles(GameOptions gameOptions, CallbackInfo ci) {
        CustomSoundMap soundMap = (CustomSoundMap) this.sounds;

        if (!derggycraft$introSoundRegistered) {
            URL introResource = resolveIntroResource();
            if (introResource != null) {
                soundMap.putSound(IntroLogoSound.REGISTRATION_ID, introResource);
                derggycraft$introSoundRegistered = true;
                if (DerggyCraft.LOGGER != null) {
                    DerggyCraft.LOGGER.info("Registered intro logo sound {} from {}", IntroLogoSound.REGISTRATION_ID, introResource);
                }
            } else if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.warn("Failed to locate intro logo sound resource at /assets/derggycraft/stationapi/sounds/{}", IntroLogoSound.FILE_NAME);
            }
        }

        if (!derggycraft$flareLoopRegistered) {
            URL flareLoopResource = resolveFlareLoopResource();
            if (flareLoopResource != null) {
                soundMap.putSound(FlareLoopSound.REGISTRATION_ID, flareLoopResource);
                derggycraft$flareLoopRegistered = true;
                if (DerggyCraft.LOGGER != null) {
                    DerggyCraft.LOGGER.info("Registered flare loop sound {} from {}", FlareLoopSound.REGISTRATION_ID, flareLoopResource);
                }
            } else if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.warn("Failed to locate flare loop sound resource at /assets/derggycraft/stationapi/sounds/{}", FlareLoopSound.FILE_NAME);
            }
        }

        if (!derggycraft$backportedVanillaSoundsRegistered) {
            registerBackportedVanillaSounds(soundMap);
            derggycraft$backportedVanillaSoundsRegistered = true;
        }

        if (derggycraft$collarJinglesRegistered) {
            return;
        }

        for (int i = 0; i < CollarJingleSounds.FILE_NAMES.length; ++i) {
            String fileName = CollarJingleSounds.FILE_NAMES[i];
            URL resource = resolveJingleResource(fileName);
            if (resource == null) {
                continue;
            }

            soundMap.putSound(CollarJingleSounds.REGISTRATION_IDS[i], resource);
        }

        derggycraft$collarJinglesRegistered = true;
    }

    @Unique
    private static void registerBackportedVanillaSounds(CustomSoundMap soundMap) {
        registerSoundFileSet(soundMap, BackportedVanillaSounds.CAVE_SOUND_FILES);
        registerSoundFileSet(soundMap, BackportedVanillaSounds.BUCKET_FILL_SOUND_FILES);
        registerSoundFileSet(soundMap, BackportedVanillaSounds.BUCKET_FILL_LAVA_SOUND_FILES);
        registerSoundFileSet(soundMap, BackportedVanillaSounds.BUCKET_EMPTY_SOUND_FILES);
        registerSoundFileSet(soundMap, BackportedVanillaSounds.BUCKET_EMPTY_LAVA_SOUND_FILES);
    }

    @Unique
    private static void registerSoundFileSet(CustomSoundMap soundMap, String[] fileNames) {
        for (String fileName : fileNames) {
            URL resource = resolveSoundResource(fileName);
            if (resource != null) {
                soundMap.putSound(fileName, resource);
            }
        }
    }

    @Unique
    private static URL resolveJingleResource(String fileName) {
        return resolveSoundResource("collar/jingle/" + fileName);
    }

    @Unique
    private static URL resolveIntroResource() {
        return resolveSoundResource(IntroLogoSound.FILE_NAME);
    }

    @Unique
    private static URL resolveFlareLoopResource() {
        return resolveSoundResource(FlareLoopSound.FILE_NAME);
    }

    @Unique
    private static URL resolveSoundResource(String fileName) {
        URL direct = SoundManagerCollarJingleMixin.class.getResource("/assets/derggycraft/stationapi/sounds/" + fileName);
        if (direct != null) {
            return direct;
        }

        return SoundManagerCollarJingleMixin.class.getResource("/assets/derggycraft/stationapi/sounds/sound/" + fileName);
    }
}