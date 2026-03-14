package net.derggy.craft.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Adds Ctrl+Q support to drop the entire selected stack from the hotbar.
 */
@Mixin(Minecraft.class)
public class StackDropMixin {

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;dropSelectedItem()V")
    )
    private void handleCtrlDrop(ClientPlayerEntity player) {
        boolean ctrlHeld = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157); // Left/Right Ctrl

        if (ctrlHeld) {
            // Drop entire stack
            ItemStack selected = player.inventory.getSelectedItem();
            if (selected != null) {
                ItemStack toDrop = player.inventory.removeStack(player.inventory.selectedSlot, selected.count);
                player.dropItem(toDrop, false);
            }
        } else {
            // Default: drop single item
            player.dropSelectedItem();
        }
    }
}
