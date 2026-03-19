package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.noahsarch.derggycraft.sound.BackportedVanillaSounds;
import net.noahsarch.derggycraft.sound.BucketSoundStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(BucketItem.class)
public abstract class BucketItemStackingMixin {
    @Inject(
            method = "use(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void derggycraft$stackedEmptyBucketsFillOneAtATime(ItemStack stack, World world, PlayerEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack == null || user == null) {
            return;
        }

        ItemStack result = cir.getReturnValue();
        if (result == null || result.count <= 0) {
            return;
        }

        int sourceItemId = stack.itemId;
        int resultItemId = result.itemId;
        if (sourceItemId == Item.BUCKET.id) {
            if (resultItemId == Item.WATER_BUCKET.id) {
                playBucketSound(world, user, BackportedVanillaSounds.BUCKET_FILL_PLAYBACK_ID, 1.0F);
            } else if (resultItemId == Item.LAVA_BUCKET.id) {
                playBucketSound(world, user, BackportedVanillaSounds.BUCKET_FILL_LAVA_PLAYBACK_ID, 1.0F);
            }
        } else if (resultItemId == Item.BUCKET.id) {
            if (sourceItemId == Item.WATER_BUCKET.id) {
                playBucketSound(world, user, BackportedVanillaSounds.BUCKET_EMPTY_PLAYBACK_ID, 1.0F);
            } else if (sourceItemId == Item.LAVA_BUCKET.id) {
                playBucketSound(world, user, BackportedVanillaSounds.BUCKET_EMPTY_LAVA_PLAYBACK_ID, 1.0F);
            }
        }

        if (user.inventory == null || stack.itemId != Item.BUCKET.id || stack.count <= 1) {
            return;
        }

        boolean filledBucket = resultItemId == Item.WATER_BUCKET.id
                || resultItemId == Item.LAVA_BUCKET.id
                || resultItemId == Item.MILK_BUCKET.id;
        if (!filledBucket) {
            return;
        }

        stack.count -= 1;

        ItemStack filledStack = new ItemStack(resultItemId, 1, result.getDamage());
        if (!user.inventory.addStack(filledStack) && world != null && !world.isRemote) {
            user.dropItem(filledStack, false);
        }

        cir.setReturnValue(stack);
    }

    private static void playBucketSound(World world, PlayerEntity user, String playbackId, float basePitch) {
        if (world == null || user == null) {
            return;
        }

        float pitch = basePitch + (world.random.nextFloat() - world.random.nextFloat()) * 0.1F;
        world.playSound(user, playbackId, 1.0F, pitch);

        if (!world.isRemote) {
            byte status = BucketSoundStatus.toStatus(playbackId);
            if (status != 0) {
                world.broadcastEntityEvent(user, status);
            }
        }

        // In some singleplayer flows world event listeners don't forward custom sounds reliably.
        // This client fallback keeps local bucket audio consistent without affecting dedicated servers.
        if (!world.isRemote) {
            playClientFallback(playbackId, pitch);
        }
    }

    private static void playClientFallback(String playbackId, float pitch) {
        try {
            FabricLoader loader = FabricLoader.getInstance();
            if (loader.getEnvironmentType() != EnvType.CLIENT) {
                return;
            }

            Object gameInstance = loader.getGameInstance();
            if (gameInstance == null) {
                return;
            }

            Field soundManagerField = gameInstance.getClass().getField("soundManager");
            Object soundManager = soundManagerField.get(gameInstance);
            if (soundManager == null) {
                return;
            }

            Method playSound = soundManager.getClass().getMethod("playSound", String.class, float.class, float.class);
            playSound.invoke(soundManager, playbackId, 1.0F, pitch);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}