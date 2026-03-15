package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.modificationstation.stationapi.api.client.sound.CustomSoundMap;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.sound.CollarJingleSounds;
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

    @Inject(method = "loadSounds", at = @At("TAIL"))
    private void derggycraft$registerCollarJingles(GameOptions gameOptions, CallbackInfo ci) {
        if (!derggycraft$introSoundRegistered) {
            URL introResource = resolveIntroResource();
            if (introResource != null) {
                ((CustomSoundMap) this.sounds).putSound(IntroLogoSound.REGISTRATION_ID, introResource);
                derggycraft$introSoundRegistered = true;
                if (DerggyCraft.LOGGER != null) {
                    DerggyCraft.LOGGER.info("Registered intro logo sound {} from {}", IntroLogoSound.REGISTRATION_ID, introResource);
                }
            } else if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.warn("Failed to locate intro logo sound resource at /assets/derggycraft/stationapi/sounds/{}", IntroLogoSound.FILE_NAME);
            }
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

            ((CustomSoundMap) this.sounds).putSound(CollarJingleSounds.REGISTRATION_IDS[i], resource);
        }

        derggycraft$collarJinglesRegistered = true;
    }

    @Unique
    private static URL resolveJingleResource(String fileName) {
        String legacyPath = "/assets/derggycraft/stationapi/sounds/collar/jingle/" + fileName;
        URL legacyResource = SoundManagerCollarJingleMixin.class.getResource(legacyPath);
        if (legacyResource != null) {
            return legacyResource;
        }

        String channelPath = "/assets/derggycraft/stationapi/sounds/sound/collar/jingle/" + fileName;
        return SoundManagerCollarJingleMixin.class.getResource(channelPath);
    }

    @Unique
    private static URL resolveIntroResource() {
        URL direct = SoundManagerCollarJingleMixin.class.getResource("/assets/derggycraft/stationapi/sounds/" + IntroLogoSound.FILE_NAME);
        if (direct != null) {
            return direct;
        }

        return SoundManagerCollarJingleMixin.class.getResource("/assets/derggycraft/stationapi/sounds/sound/" + IntroLogoSound.FILE_NAME);
    }
}