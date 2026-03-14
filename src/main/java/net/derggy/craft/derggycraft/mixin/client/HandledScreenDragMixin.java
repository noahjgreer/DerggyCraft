package net.derggy.craft.derggycraft.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Adds drag interactions to HandledScreen:
 * - Left-click drag: divide cursor stack evenly across hovered slots
 * - Right-click drag: place 1 item from cursor stack in each hovered slot
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenDragMixin extends Screen {

    @Shadow
    public ScreenHandler handler;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Unique
    private boolean cursorDragging = false;

    @Unique
    private int heldButtonType = -1;

    @Unique
    private final Set<Slot> cursorDragSlots = new LinkedHashSet<>();

    @Unique
    private int draggedStackRemainder = 0;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
        PlayerInventory inv = this.minecraft.player.inventory;

        if ((button == 0 || button == 1) && inv.getCursorStack() != null) {
            Slot slot = getSlotAtPos(mouseX, mouseY);
            if (slot != null) {
                // Start drag
                this.cursorDragging = true;
                this.heldButtonType = button; // 0 = left, 1 = right
                this.cursorDragSlots.clear();

                // Add the initial slot
                if (canInsertItemIntoSlot(slot, inv.getCursorStack()) && slot.canInsert(inv.getCursorStack())) {
                    this.cursorDragSlots.add(slot);
                    calculateOffset();
                }

                // Don't cancel - let normal click through for non-drag case
                // The actual click will be handled normally; if they release on the same slot, it's a click
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderDetectDrag(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!this.cursorDragging) return;

        PlayerInventory inv = this.minecraft.player.inventory;
        ItemStack cursorStack = inv.getCursorStack();
        if (cursorStack == null) {
            endDrag();
            return;
        }

        // Check if mouse button is still held
        boolean buttonStillHeld;
        if (this.heldButtonType == 0) {
            buttonStillHeld = Mouse.isButtonDown(0);
        } else {
            buttonStillHeld = Mouse.isButtonDown(1);
        }

        if (!buttonStillHeld) {
            // Mouse released - finalize drag
            if (this.cursorDragSlots.size() > 1) {
                // Send 3-phase quick craft protocol
                int encodedStart = 8 + 0 + (this.heldButtonType << 2); // stage 0
                this.minecraft.interactionManager.clickSlot(
                        this.handler.syncId, -999, encodedStart, false, this.minecraft.player);

                for (Slot slot : this.cursorDragSlots) {
                    int encodedSlot = 8 + 1 + (this.heldButtonType << 2); // stage 1
                    this.minecraft.interactionManager.clickSlot(
                            this.handler.syncId, slot.id, encodedSlot, false, this.minecraft.player);
                }

                int encodedEnd = 8 + 2 + (this.heldButtonType << 2); // stage 2
                this.minecraft.interactionManager.clickSlot(
                        this.handler.syncId, -999, encodedEnd, false, this.minecraft.player);
            }
            endDrag();
            return;
        }

        // Detect slot under mouse and add to drag set
        Slot hovered = getSlotAtPos(mouseX, mouseY);
        if (hovered != null && !this.cursorDragSlots.contains(hovered)) {
            if (canInsertItemIntoSlot(hovered, cursorStack) && hovered.canInsert(cursorStack)) {
                int maxSlots = (this.heldButtonType == 1) ? cursorStack.count : cursorStack.count;
                if (cursorStack.count > this.cursorDragSlots.size() || this.heldButtonType == 1) {
                    this.cursorDragSlots.add(hovered);
                    calculateOffset();
                }
            }
        }
    }

    @Unique
    private void endDrag() {
        this.cursorDragging = false;
        this.heldButtonType = -1;
        this.cursorDragSlots.clear();
        this.draggedStackRemainder = 0;
    }

    @Unique
    private void calculateOffset() {
        PlayerInventory inv = this.minecraft.player.inventory;
        ItemStack cursorStack = inv.getCursorStack();
        if (cursorStack == null || !this.cursorDragging) return;

        this.draggedStackRemainder = cursorStack.count;
        for (Slot slot : this.cursorDragSlots) {
            int existing = slot.hasStack() ? slot.getStack().count : 0;
            int maxForSlot = Math.min(cursorStack.getMaxCount(), slot.getMaxItemCount());
            int perSlot;
            if (this.heldButtonType == 0) {
                perSlot = Math.max(1, cursorStack.count / this.cursorDragSlots.size());
            } else {
                perSlot = 1;
            }
            int toPlace = Math.min(perSlot + existing, maxForSlot);
            this.draggedStackRemainder -= (toPlace - existing);
        }
    }

    @Unique
    private Slot getSlotAtPos(int x, int y) {
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

    @Unique
    private static boolean canInsertItemIntoSlot(Slot slot, ItemStack stack) {
        if (!slot.hasStack()) return true;
        ItemStack slotStack = slot.getStack();
        return slotStack.itemId == stack.itemId
                && (!stack.hasSubtypes() || slotStack.getDamage() == stack.getDamage())
                && slotStack.count < slotStack.getMaxCount();
    }
}
