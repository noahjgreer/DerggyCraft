package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerPickupAllMixin {
    private static final int DERGGYCRAFT_PICKUP_ALL_BUTTON = 2;

    @Shadow
    public List slots;

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handlePickupAll(int index, int button, boolean shift, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (button != DERGGYCRAFT_PICKUP_ALL_BUTTON || shift || player == null) {
            return;
        }

        if (index < 0 || index >= this.slots.size()) {
            cir.setReturnValue(null);
            return;
        }

        PlayerInventory inventory = player.inventory;
        if (inventory == null) {
            cir.setReturnValue(null);
            return;
        }

        ItemStack cursorStack = inventory.getCursorStack();
        if (cursorStack == null || cursorStack.count <= 0) {
            cir.setReturnValue(null);
            return;
        }

        Slot clickedSlot = (Slot) this.slots.get(index);
        ItemStack originalClickedStack = clickedSlot != null && clickedSlot.hasStack() ? clickedSlot.getStack().copy() : null;

        for (int pass = 0; pass < 2 && cursorStack.count < cursorStack.getMaxCount(); ++pass) {
            for (int i = 0; i < this.slots.size() && cursorStack.count < cursorStack.getMaxCount(); ++i) {
                Slot slot = (Slot) this.slots.get(i);
                if (slot == null || !slot.hasStack()) {
                    continue;
                }

                ItemStack slotStack = slot.getStack();
                if (!this.derggycraft$canPickupAllMerge(cursorStack, slotStack)) {
                    continue;
                }

                if (pass == 0 && slotStack.count == slotStack.getMaxCount()) {
                    continue;
                }

                int room = cursorStack.getMaxCount() - cursorStack.count;
                if (room <= 0) {
                    break;
                }

                int moved = Math.min(slotStack.count, room);
                if (moved <= 0) {
                    continue;
                }

                slotStack.count -= moved;
                cursorStack.count += moved;

                if (slotStack.count <= 0) {
                    slot.setStack(null);
                } else {
                    slot.markDirty();
                }
            }
        }

        cir.setReturnValue(originalClickedStack);
    }

    private boolean derggycraft$canPickupAllMerge(ItemStack cursorStack, ItemStack slotStack) {
        if (cursorStack.itemId != slotStack.itemId) {
            return false;
        }

        return !cursorStack.hasSubtypes() || cursorStack.getDamage() == slotStack.getDamage();
    }
}
