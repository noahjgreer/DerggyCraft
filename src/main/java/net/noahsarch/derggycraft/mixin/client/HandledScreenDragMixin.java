package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenDragMixin {
    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Shadow
    public ScreenHandler handler;

    private boolean derggycraft$dragging;
    private int derggycraft$dragButton = -1;
    private int derggycraft$dragStartSlotId = -1;
    private boolean derggycraft$dragMoved;
    private Slot derggycraft$focusedSlot;
    private final Set<Slot> derggycraft$dragSlots = new LinkedHashSet<>();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void derggycraft$beginDrag(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button != 0 && button != 1) {
            return;
        }

        if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
            return;
        }

        Slot slot = this.derggycraft$getSlotAt(mouseX, mouseY);
        if (slot == null) {
            return;
        }

        Minecraft minecraft = this.derggycraft$getMinecraft();
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
        if (cursorStack == null || !this.derggycraft$canDistributeTo(slot, cursorStack)) {
            return;
        }

        ci.cancel();
        this.derggycraft$dragging = true;
        this.derggycraft$dragButton = button;
        this.derggycraft$dragStartSlotId = slot.id;
        this.derggycraft$dragMoved = false;
        this.derggycraft$dragSlots.clear();
        this.derggycraft$dragSlots.add(slot);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void derggycraft$trackDraggedSlots(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.derggycraft$focusedSlot = this.derggycraft$getSlotAt(mouseX, mouseY);

        if (!this.derggycraft$dragging || this.derggycraft$dragButton < 0) {
            return;
        }

        if (!Mouse.isButtonDown(this.derggycraft$dragButton)) {
            this.derggycraft$renderDragPreview(mouseX, mouseY);
            return;
        }

        Slot slot = this.derggycraft$getSlotAt(mouseX, mouseY);
        if (slot != null) {
            if (slot.id != this.derggycraft$dragStartSlotId) {
                this.derggycraft$dragMoved = true;
            }
            this.derggycraft$dragSlots.add(slot);
        }

        this.derggycraft$renderDragPreview(mouseX, mouseY);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleModernDropShortcut(char character, int keyCode, CallbackInfo ci) {
        Minecraft minecraft = this.derggycraft$getMinecraft();
        if (minecraft == null || minecraft.player == null || minecraft.options == null || minecraft.interactionManager == null) {
            return;
        }

        if (keyCode != minecraft.options.dropKey.code) {
            return;
        }

        Slot slot = this.derggycraft$focusedSlot;
        if (slot == null || !slot.hasStack() || minecraft.player.inventory.getCursorStack() != null) {
            return;
        }

        boolean dropAll = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);

        minecraft.interactionManager.clickSlot(this.handler.syncId, slot.id, 0, false, minecraft.player);
        minecraft.interactionManager.clickSlot(this.handler.syncId, -999, dropAll ? 0 : 1, false, minecraft.player);

        if (!dropAll && minecraft.player.inventory.getCursorStack() != null) {
            minecraft.interactionManager.clickSlot(this.handler.syncId, slot.id, 0, false, minecraft.player);
        }

        ci.cancel();
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void derggycraft$finishDrag(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (!this.derggycraft$dragging || button != this.derggycraft$dragButton) {
            return;
        }

        ci.cancel();

        Minecraft minecraft = this.derggycraft$getMinecraft();
        if (minecraft == null || minecraft.player == null || minecraft.interactionManager == null) {
            this.derggycraft$clearDragState();
            return;
        }

        Slot slot = this.derggycraft$getSlotAt(mouseX, mouseY);
        if (slot != null) {
            if (slot.id != this.derggycraft$dragStartSlotId) {
                this.derggycraft$dragMoved = true;
            }
            this.derggycraft$dragSlots.add(slot);
        }

        if (this.derggycraft$dragMoved) {
            this.derggycraft$distributeDraggedStack(minecraft);
        } else {
            minecraft.interactionManager.clickSlot(this.handler.syncId, this.derggycraft$dragStartSlotId, this.derggycraft$dragButton, false, minecraft.player);
        }

        this.derggycraft$clearDragState();
    }

    private void derggycraft$distributeDraggedStack(Minecraft minecraft) {
        PlayerInventory inventory = minecraft.player.inventory;
        ItemStack cursorStack = inventory.getCursorStack();
        if (cursorStack == null || cursorStack.count <= 0) {
            return;
        }

        List<Slot> eligibleSlots = new ArrayList<>();
        for (Slot slot : this.derggycraft$dragSlots) {
            if (this.derggycraft$canDistributeTo(slot, cursorStack)) {
                eligibleSlots.add(slot);
            }
        }

        if (eligibleSlots.isEmpty()) {
            return;
        }

        int amountPerSlot = this.derggycraft$dragButton == 1 ? 1 : cursorStack.count / eligibleSlots.size();
        if (amountPerSlot <= 0) {
            return;
        }

        for (Slot slot : eligibleSlots) {
            ItemStack current = slot.getStack();
            int existingCount = current == null ? 0 : current.count;
            int slotLimit = Math.min(slot.getMaxItemCount(), cursorStack.getMaxCount());
            int toPlace = Math.min(amountPerSlot, slotLimit - existingCount);
            while (toPlace > 0) {
                ItemStack heldNow = inventory.getCursorStack();
                if (heldNow == null || heldNow.count <= 0) {
                    return;
                }
                minecraft.interactionManager.clickSlot(this.handler.syncId, slot.id, 1, false, minecraft.player);
                --toPlace;
            }
        }
    }

    private void derggycraft$renderDragPreview(int mouseX, int mouseY) {
        Minecraft minecraft = this.derggycraft$getMinecraft();
        if (minecraft == null || minecraft.player == null || minecraft.textRenderer == null) {
            return;
        }

        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
        if (cursorStack == null || cursorStack.count <= 0) {
            return;
        }

        List<Slot> eligibleSlots = new ArrayList<>();
        for (Slot slot : this.derggycraft$dragSlots) {
            if (this.derggycraft$canDistributeTo(slot, cursorStack)) {
                eligibleSlots.add(slot);
            }
        }

        if (eligibleSlots.isEmpty()) {
            return;
        }

        int amountPerSlot = this.derggycraft$dragButton == 1 ? 1 : cursorStack.count / eligibleSlots.size();
        if (amountPerSlot <= 0) {
            return;
        }

        Screen screen = this.derggycraft$asScreen();
        int left = (screen.width - this.backgroundWidth) / 2;
        int top = (screen.height - this.backgroundHeight) / 2;
        int placedTotal = 0;

        for (Slot slot : eligibleSlots) {
            ItemStack current = slot.getStack();
            int existingCount = current == null ? 0 : current.count;
            int slotLimit = Math.min(slot.getMaxItemCount(), cursorStack.getMaxCount());
            int toPlace = Math.min(amountPerSlot, slotLimit - existingCount);
            if (toPlace <= 0) {
                continue;
            }

            int x = left + slot.x;
            int y = top + slot.y;
            ((DrawContextInvoker) (Object) this).derggycraft$invokeFillGradient(x, y, x + 16, y + 16, 0x66FFFFFF, 0x66FFFFFF);

            int projectedCount = existingCount + toPlace;
            String projectedText = Integer.toString(projectedCount);
            int textX = x + 17 - minecraft.textRenderer.getWidth(projectedText);
            int textY = y + 9;
            minecraft.textRenderer.drawWithShadow(projectedText, textX, textY, 0xFFFFAA);
            placedTotal += toPlace;
        }

        int remainder = cursorStack.count - placedTotal;
        if (remainder >= 0 && remainder != cursorStack.count) {
            String remainderText = Integer.toString(remainder);
            minecraft.textRenderer.drawWithShadow(remainderText, mouseX + 10, mouseY - 18, 0xAAAAAA);
        }
    }

    private boolean derggycraft$canDistributeTo(Slot slot, ItemStack cursorStack) {
        if (slot == null || cursorStack == null || !slot.canInsert(cursorStack)) {
            return false;
        }

        ItemStack current = slot.getStack();
        if (current == null) {
            return true;
        }

        if (current.itemId != cursorStack.itemId) {
            return false;
        }

        if (current.hasSubtypes() && current.getDamage() != cursorStack.getDamage()) {
            return false;
        }

        int slotLimit = Math.min(slot.getMaxItemCount(), cursorStack.getMaxCount());
        return current.count < slotLimit;
    }

    private Slot derggycraft$getSlotAt(int mouseX, int mouseY) {
        Screen screen = this.derggycraft$asScreen();
        int left = (screen.width - this.backgroundWidth) / 2;
        int top = (screen.height - this.backgroundHeight) / 2;
        int guiX = mouseX - left;
        int guiY = mouseY - top;

        for (int i = 0; i < this.handler.slots.size(); ++i) {
            Slot slot = (Slot) this.handler.slots.get(i);
            if (guiX >= slot.x - 1 && guiX < slot.x + 17 && guiY >= slot.y - 1 && guiY < slot.y + 17) {
                return slot;
            }
        }

        return null;
    }

    private Screen derggycraft$asScreen() {
        return (Screen) (Object) this;
    }

    private Minecraft derggycraft$getMinecraft() {
        return ((ScreenAccessor) (Object) this).derggycraft$getMinecraft();
    }

    private void derggycraft$clearDragState() {
        this.derggycraft$dragging = false;
        this.derggycraft$dragButton = -1;
        this.derggycraft$dragStartSlotId = -1;
        this.derggycraft$dragMoved = false;
        this.derggycraft$dragSlots.clear();
    }
}
