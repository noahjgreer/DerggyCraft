package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.inventory.CollarInventory;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler {
    private static final int DERGGYCRAFT$VANILLA_SLOT_COUNT = 45;
    private static final int DERGGYCRAFT$VERTICAL_OFFSET = 18;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;Z)V", at = @At("TAIL"))
    private void derggycraft$addCollarSlots(PlayerInventory inventory, boolean isLocal, CallbackInfo ci) {
        for (int i = 0; i < DERGGYCRAFT$VANILLA_SLOT_COUNT && i < this.slots.size(); ++i) {
            Slot slot = (Slot) this.slots.get(i);
            slot.y += DERGGYCRAFT$VERTICAL_OFFSET;
        }

        if (!(inventory instanceof CollarInventoryAccess access)) {
            return;
        }

        CollarInventory collarInventory = new CollarInventory(access, inventory.player);
        for (int i = 0; i < 3; ++i) {
            this.addSlot(new Slot(collarInventory, i, 18 + 8 + i * 18, 8) {
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return stack != null
                            && DerggyCraft.COLLAR_ITEM != null
                            && stack.itemId == DerggyCraft.COLLAR_ITEM.id;
                }
            });
        }
    }
}