package net.derggy.craft.derggycraft.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds Q and Ctrl+Q support inside containers to drop items from hovered slots.
 */
@Mixin(HandledScreen.class)
public abstract class ContainerDropMixin extends Screen {

    @Shadow
    public ScreenHandler handler;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleQDrop(char character, int keyCode, CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.options == null) return;

        // Check if the pressed key is the drop key
        if (keyCode != this.minecraft.options.dropKey.code) return;

        // Get current mouse position
        int mouseX = Mouse.getX() * this.width / this.minecraft.displayWidth;
        int mouseY = this.height - Mouse.getY() * this.height / this.minecraft.displayHeight - 1;

        // Find the slot under the mouse cursor
        Slot hoveredSlot = getSlotAtPosition(mouseX, mouseY);
        if (hoveredSlot == null || !hoveredSlot.hasStack()) return;

        boolean ctrlHeld = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);

        // Use button value 4 for single drop, 5 for full stack drop
        int dropButton = ctrlHeld ? 5 : 4;

        this.minecraft.interactionManager.clickSlot(
                this.handler.syncId, hoveredSlot.id, dropButton, false, this.minecraft.player
        );

        ci.cancel();
    }

    private Slot getSlotAtPosition(int x, int y) {
        int guiLeft = (this.width - this.backgroundWidth) / 2;
        int guiTop = (this.height - this.backgroundHeight) / 2;

        for (int i = 0; i < this.handler.slots.size(); i++) {
            Slot slot = (Slot) this.handler.slots.get(i);
            int slotX = slot.x + guiLeft;
            int slotY = slot.y + guiTop;
            if (x >= slotX - 1 && x < slotX + 17 && y >= slotY - 1 && y < slotY + 17) {
                return slot;
            }
        }
        return null;
    }
}
