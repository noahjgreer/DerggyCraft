package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;
import net.noahsarch.derggycraft.sound.CollarJingleSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Entity.class)
public abstract class EntityCollarJingleMixin {
    @Inject(
            method = "move(DDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onSteppedOn(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void derggycraft$playCollarJingleOnStep(double dx, double dy, double dz, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        if (DerggyCraft.COLLAR_ITEM == null || !(player.inventory instanceof CollarInventoryAccess access)) {
            return;
        }

        boolean hasCollarEquipped = false;
        for (int i = 0; i < access.derggycraft$getCollarSize(); ++i) {
            ItemStack stack = access.derggycraft$getCollarStack(i);
            if (stack == null || stack.itemId != DerggyCraft.COLLAR_ITEM.id) {
                continue;
            }

            hasCollarEquipped = true;
            break;
        }

        if (!hasCollarEquipped) {
            return;
        }

        String[] jingleSoundIds = CollarJingleSounds.PLAYBACK_IDS;
        if (jingleSoundIds.length == 0) {
            return;
        }

        String soundId = jingleSoundIds[ThreadLocalRandom.current().nextInt(jingleSoundIds.length)];
        float volume = 0.12F + ThreadLocalRandom.current().nextFloat() * 0.08F;
        float pitch = 0.92F + ThreadLocalRandom.current().nextFloat() * 0.16F;
        entity.world.playSound(entity, soundId, volume, pitch);
    }
}