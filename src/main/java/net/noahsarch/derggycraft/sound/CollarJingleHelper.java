package net.noahsarch.derggycraft.sound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;

import java.util.concurrent.ThreadLocalRandom;

public final class CollarJingleHelper {
    public static final byte COLLAR_JINGLE_STATUS = 29;

    private CollarJingleHelper() {
    }

    public static boolean hasCollarEquipped(PlayerEntity player) {
        if (player == null || DerggyCraft.COLLAR_ITEM == null || !(player.inventory instanceof CollarInventoryAccess access)) {
            return false;
        }

        for (int i = 0; i < access.derggycraft$getCollarSize(); ++i) {
            ItemStack stack = access.derggycraft$getCollarStack(i);
            if (stack != null && stack.itemId == DerggyCraft.COLLAR_ITEM.id) {
                return true;
            }
        }

        return false;
    }

    public static void playRandomNearbyJingle(Entity source, float minVolume, float maxVolume, float minPitch, float maxPitch) {
        if (source == null || source.world == null) {
            return;
        }

        if (source.world.isRemote) {
            playRandomNearbyJingleClient(source, minVolume, maxVolume, minPitch, maxPitch);
            return;
        }

        source.world.broadcastEntityEvent(source, COLLAR_JINGLE_STATUS);
    }

    public static void playRandomNearbyJingleClient(Entity source, float minVolume, float maxVolume, float minPitch, float maxPitch) {
        if (source == null || source.world == null) {
            return;
        }

        String[] jingleSoundIds = CollarJingleSounds.PLAYBACK_IDS;
        if (jingleSoundIds.length == 0) {
            return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        String soundId = jingleSoundIds[random.nextInt(jingleSoundIds.length)];
        float volume = minVolume + random.nextFloat() * (maxVolume - minVolume);
        float pitch = minPitch + random.nextFloat() * (maxPitch - minPitch);
        source.world.playSound(source, soundId, volume, pitch);
    }
}