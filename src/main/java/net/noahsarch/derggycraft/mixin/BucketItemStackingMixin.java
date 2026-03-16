package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemStackingMixin {
    @Inject(
            method = "use(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void derggycraft$stackedEmptyBucketsFillOneAtATime(ItemStack stack, World world, PlayerEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack == null || user == null || user.inventory == null || stack.itemId != Item.BUCKET.id || stack.count <= 1) {
            return;
        }

        ItemStack result = cir.getReturnValue();
        if (result == null || result.count <= 0) {
            return;
        }

        int resultItemId = result.itemId;
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
}