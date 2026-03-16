package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.PlayerManager;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerKeepInventoryRespawnMixin {
    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void derggycraft$copyInventoryOnRespawn(ServerPlayerEntity oldPlayer, int dimensionId, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ServerPlayerEntity newPlayer = cir.getReturnValue();
        if (newPlayer == null || oldPlayer == null) {
            return;
        }

        DerggyCraftGameRules.ensureLoaded(newPlayer.world);
        if (!DerggyCraftGameRules.get(DerggyCraftGameRules.Rule.KEEP_INVENTORY)) {
            return;
        }

        for (int i = 0; i < oldPlayer.inventory.main.length; ++i) {
            ItemStack stack = oldPlayer.inventory.main[i];
            newPlayer.inventory.main[i] = stack == null ? null : ItemStack.clone(stack);
        }

        for (int i = 0; i < oldPlayer.inventory.armor.length; ++i) {
            ItemStack stack = oldPlayer.inventory.armor[i];
            newPlayer.inventory.armor[i] = stack == null ? null : ItemStack.clone(stack);
        }

        newPlayer.inventory.selectedSlot = oldPlayer.inventory.selectedSlot;

        if (oldPlayer.inventory instanceof CollarInventoryAccess oldCollar && newPlayer.inventory instanceof CollarInventoryAccess newCollar) {
            int collarSlots = Math.min(oldCollar.derggycraft$getCollarSize(), newCollar.derggycraft$getCollarSize());
            for (int i = 0; i < collarSlots; ++i) {
                ItemStack stack = oldCollar.derggycraft$getCollarStack(i);
                newCollar.derggycraft$setCollarStack(i, stack == null ? null : ItemStack.clone(stack));
            }
        }

        newPlayer.inventory.markDirty();
        newPlayer.currentScreenHandler.sendContentUpdates();
    }
}
