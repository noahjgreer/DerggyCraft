package net.derggy.craft.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds drop-from-slot support to ScreenHandler.onSlotClick.
 * Button values 4 (drop one) and 5 (drop all) are used for this.
 */
@Mixin(ScreenHandler.class)
public class ScreenHandlerDropMixin {

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void handleDropAction(int index, int button, boolean shift, PlayerEntity player,
                                   CallbackInfoReturnable<ItemStack> cir) {
        // Only handle our custom drop buttons (4 = drop one, 5 = drop stack)
        if (button != 4 && button != 5) return;
        if (index < 0) return;

        ScreenHandler self = (ScreenHandler) (Object) this;
        PlayerInventory inventory = player.inventory;

        // Don't process if cursor is holding something
        if (inventory.getCursorStack() != null) return;

        Slot slot = (Slot) self.slots.get(index);
        if (slot == null || !slot.hasStack()) {
            cir.setReturnValue(null);
            return;
        }

        ItemStack slotStack = slot.getStack();

        if (button == 4) {
            // Drop single item
            ItemStack toDrop = slot.takeStack(1);
            player.dropItem(toDrop, false);
            if (slotStack.count == 0) {
                slot.setStack(null);
            }
        } else {
            // Drop entire stack (button == 5)
            int count = slotStack.count;
            ItemStack toDrop = slot.takeStack(count);
            player.dropItem(toDrop, false);
            slot.setStack(null);
        }

        cir.setReturnValue(null);
    }
}
