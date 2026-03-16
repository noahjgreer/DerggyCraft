package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerShiftCraftingMixin {
    @Shadow
    public List slots;

    @Shadow
    public abstract ItemStack quickMove(int slot);

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$craftAllOnShiftClick(int index, int button, boolean shift, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (!shift || player == null || index < 0 || index >= this.slots.size()) {
            return;
        }

        Slot slot = (Slot) this.slots.get(index);
        if (slot == null || !slot.hasStack() || !(slot instanceof CraftingResultSlot)) {
            return;
        }

        ItemStack moved = this.quickMove(index);
        if (moved == null) {
            cir.setReturnValue(null);
            return;
        }

        while (slot.hasStack()) {
            ItemStack current = slot.getStack();
            if (current == null || !this.derggycraft$sameStackType(current, moved)) {
                break;
            }

            ItemStack next = this.quickMove(index);
            if (next == null) {
                break;
            }

            moved = next;
        }

        cir.setReturnValue(moved);
    }

    private boolean derggycraft$sameStackType(ItemStack a, ItemStack b) {
        if (a.itemId != b.itemId) {
            return false;
        }

        return !a.hasSubtypes() || a.getDamage() == b.getDamage();
    }
}