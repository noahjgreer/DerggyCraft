package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.noahsarch.derggycraft.inventory.sort.InventorySortRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerInventorySortMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleSortButtonClick(int index, int button, boolean shift, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (index != -999 || shift || player == null) {
            return;
        }

        InventorySortRuntime.SortTarget target = InventorySortRuntime.targetFromButton(button);
        if (target == null) {
            return;
        }

        // Avoid client-side prediction in dedicated MP; let the server apply and sync slot updates.
        if (player.world != null && player.world.isRemote) {
            cir.setReturnValue(null);
            return;
        }

        ScreenHandler self = (ScreenHandler) (Object) this;
        if (InventorySortRuntime.sort(self, player, target)) {
            self.sendContentUpdates();
        }

        cir.setReturnValue(null);
    }
}