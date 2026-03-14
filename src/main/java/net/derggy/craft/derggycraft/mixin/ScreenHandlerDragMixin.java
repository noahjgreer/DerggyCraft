package net.derggy.craft.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Adds QUICK_CRAFT (drag) protocol to ScreenHandler.onSlotClick.
 *
 * Button encoding for drag actions (button >= 8):
 *   button = 8 + (stage & 3) + ((dragButton & 3) << 2)
 *   stage 0 = begin, stage 1 = add slot, stage 2 = finalize
 *   dragButton 0 = left (divide evenly), dragButton 1 = right (place 1 each)
 */
@Mixin(ScreenHandler.class)
public class ScreenHandlerDragMixin {

    @Unique
    private int quickCraftStage = 0;

    @Unique
    private int quickCraftButton = -1;

    @Unique
    private final Set<Slot> quickCraftSlots = new LinkedHashSet<>();

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void handleDragAction(int index, int button, boolean shift, PlayerEntity player,
                                  CallbackInfoReturnable<ItemStack> cir) {
        // Only handle drag buttons (button >= 8)
        if (button < 8) return;

        int encoded = button - 8;
        int stage = encoded & 3;
        int dragBtn = (encoded >> 2) & 3;

        ScreenHandler self = (ScreenHandler) (Object) this;
        PlayerInventory inventory = player.inventory;

        if (stage == 0) {
            // Begin drag
            this.quickCraftButton = dragBtn;
            this.quickCraftStage = 1;
            this.quickCraftSlots.clear();
            if (inventory.getCursorStack() == null) {
                endQuickCraft();
            }
        } else if (stage == 1) {
            // Add slot
            if (this.quickCraftStage != 1) {
                endQuickCraft();
                cir.setReturnValue(null);
                return;
            }
            if (index >= 0 && index < self.slots.size()) {
                Slot slot = (Slot) self.slots.get(index);
                ItemStack cursorStack = inventory.getCursorStack();
                if (cursorStack != null && canInsertItemIntoSlot(slot, cursorStack) && slot.canInsert(cursorStack)) {
                    this.quickCraftSlots.add(slot);
                }
            }
        } else if (stage == 2) {
            // Finalize: distribute items
            if (this.quickCraftStage != 1) {
                endQuickCraft();
                cir.setReturnValue(null);
                return;
            }

            ItemStack cursorStack = inventory.getCursorStack();
            if (cursorStack != null && !this.quickCraftSlots.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) {
                    // Single slot: treat as normal click
                    Slot singleSlot = this.quickCraftSlots.iterator().next();
                    endQuickCraft();
                    self.onSlotClick(singleSlot.id, this.quickCraftButton, false, player);
                    cir.setReturnValue(null);
                    return;
                }

                int perSlot = calculateStackSize(this.quickCraftSlots.size(), this.quickCraftButton, cursorStack);
                int remaining = cursorStack.count;

                for (Slot slot : this.quickCraftSlots) {
                    if (cursorStack.count <= 0) break;

                    int existing = slot.hasStack() ? slot.getStack().count : 0;
                    int maxForSlot = Math.min(cursorStack.getMaxCount(), slot.getMaxItemCount());
                    int toPlace = Math.min(perSlot + existing, maxForSlot);
                    int actualAdd = toPlace - existing;

                    if (actualAdd <= 0) continue;
                    if (actualAdd > cursorStack.count) actualAdd = cursorStack.count;

                    remaining -= actualAdd;

                    if (slot.hasStack()) {
                        slot.getStack().count += actualAdd;
                    } else {
                        ItemStack newStack = cursorStack.copy();
                        newStack.count = actualAdd;
                        slot.setStack(newStack);
                    }
                }

                cursorStack.count = remaining;
                if (cursorStack.count <= 0) {
                    inventory.setCursorStack(null);
                }
            }

            endQuickCraft();
        } else {
            endQuickCraft();
        }

        cir.setReturnValue(null);
    }

    @Unique
    private void endQuickCraft() {
        this.quickCraftStage = 0;
        this.quickCraftSlots.clear();
    }

    @Unique
    private static boolean canInsertItemIntoSlot(Slot slot, ItemStack stack) {
        if (!slot.hasStack()) return true;
        ItemStack slotStack = slot.getStack();
        return slotStack.itemId == stack.itemId
                && (!stack.hasSubtypes() || slotStack.getDamage() == stack.getDamage())
                && slotStack.count < slotStack.getMaxCount();
    }

    @Unique
    private static int calculateStackSize(int slotCount, int mode, ItemStack stack) {
        if (mode == 0) {
            // Left-click drag: divide evenly
            return Math.max(1, stack.count / slotCount);
        } else {
            // Right-click drag: 1 each
            return 1;
        }
    }
}
