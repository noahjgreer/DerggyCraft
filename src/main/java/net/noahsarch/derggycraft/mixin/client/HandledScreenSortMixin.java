package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.noahsarch.derggycraft.inventory.sort.InventorySortRuntime;
import net.noahsarch.derggycraft.mixin.SlotAccessor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenSortMixin {
    private static final String DERGGYCRAFT_BUTTON_TEXTURE = "/assets/derggycraft/stationapi/textures/gui/ipn_gui_buttons.png";
    private static final int DERGGYCRAFT_BUTTON_TEXTURE_U = 10;
    private static final int DERGGYCRAFT_BUTTON_TEXTURE_V = 0;
    private static final int DERGGYCRAFT_BUTTON_TEXTURE_HOVER_V = 10;
    private static final int DERGGYCRAFT_BUTTON_SIZE = 10;
    private static final int DERGGYCRAFT_BUTTON_RIGHT_PADDING = 4;
    private static final int DERGGYCRAFT_BUTTON_TOP_PADDING = 4;
    private static final int DERGGYCRAFT_BUTTON_PLAYER_BAND_OFFSET = 2;

    private int derggycraft$lastMouseX = Integer.MIN_VALUE;
    private int derggycraft$lastMouseY = Integer.MIN_VALUE;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Shadow
    public ScreenHandler handler;

    @Inject(method = "render", at = @At("TAIL"))
    private void derggycraft$renderSortButtons(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.derggycraft$lastMouseX = mouseX;
        this.derggycraft$lastMouseY = mouseY;

        Minecraft minecraft = this.derggycraft$getMinecraftForSort();
        Screen screen = (Screen) (Object) this;
        if (minecraft == null || minecraft.player == null || this.handler == null) {
            return;
        }

        boolean canSortContainer = InventorySortRuntime.canSort(this.handler, InventorySortRuntime.SortTarget.CONTAINER);
        boolean canSortPlayer = InventorySortRuntime.canSort(this.handler, InventorySortRuntime.SortTarget.PLAYER);
        if (!canSortContainer && !canSortPlayer) {
            return;
        }

        int left = (screen.width - this.backgroundWidth) / 2;
        int top = (screen.height - this.backgroundHeight) / 2;
        int buttonX = this.derggycraft$getButtonX(left);
        int containerButtonY = this.derggycraft$getContainerButtonY(top);
        int playerButtonY = this.derggycraft$getPlayerButtonY(top);

        if (canSortContainer) {
            this.derggycraft$drawSortButton(buttonX, containerButtonY, mouseX, mouseY);
        }

        if (canSortPlayer) {
            this.derggycraft$drawSortButton(buttonX, playerButtonY, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleSortButtonClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button != 0) {
            return;
        }

        Minecraft minecraft = this.derggycraft$getMinecraftForSort();
        Screen screen = (Screen) (Object) this;
        if (minecraft == null || minecraft.player == null || minecraft.interactionManager == null || this.handler == null) {
            return;
        }

        boolean canSortContainer = InventorySortRuntime.canSort(this.handler, InventorySortRuntime.SortTarget.CONTAINER);
        boolean canSortPlayer = InventorySortRuntime.canSort(this.handler, InventorySortRuntime.SortTarget.PLAYER);
        if (!canSortContainer && !canSortPlayer) {
            return;
        }

        int left = (screen.width - this.backgroundWidth) / 2;
        int top = (screen.height - this.backgroundHeight) / 2;
        int buttonX = this.derggycraft$getButtonX(left);
        int containerButtonY = this.derggycraft$getContainerButtonY(top);
        int playerButtonY = this.derggycraft$getPlayerButtonY(top);

        if (canSortContainer && this.derggycraft$isPointInside(mouseX, mouseY, buttonX, containerButtonY, DERGGYCRAFT_BUTTON_SIZE, DERGGYCRAFT_BUTTON_SIZE)) {
            this.derggycraft$sendSortAction(minecraft, InventorySortRuntime.SortTarget.CONTAINER);
            ci.cancel();
            return;
        }

        if (canSortPlayer && this.derggycraft$isPointInside(mouseX, mouseY, buttonX, playerButtonY, DERGGYCRAFT_BUTTON_SIZE, DERGGYCRAFT_BUTTON_SIZE)) {
            this.derggycraft$sendSortAction(minecraft, InventorySortRuntime.SortTarget.PLAYER);
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void derggycraft$sortUnderCursorWithR(char character, int keyCode, CallbackInfo ci) {
        if (keyCode != Keyboard.KEY_R) {
            return;
        }

        Minecraft minecraft = this.derggycraft$getMinecraftForSort();
        if (minecraft == null || minecraft.player == null || minecraft.interactionManager == null || this.handler == null) {
            return;
        }

        Slot hoveredSlot = this.derggycraft$getHoveredSlot(this.derggycraft$lastMouseX, this.derggycraft$lastMouseY);
        if (hoveredSlot == null) {
            return;
        }

        InventorySortRuntime.SortTarget target = this.derggycraft$getSortTargetForSlot(hoveredSlot);
        if (target == null || !InventorySortRuntime.canSort(this.handler, target)) {
            return;
        }

        this.derggycraft$sendSortAction(minecraft, target);
        ci.cancel();
    }

    private void derggycraft$drawSortButton(int x, int y, int mouseX, int mouseY) {
        DrawContextInvoker drawContext = (DrawContextInvoker) (Object) this;
        Minecraft minecraft = this.derggycraft$getMinecraftForSort();
        boolean hovered = this.derggycraft$isPointInside(mouseX, mouseY, x, y, DERGGYCRAFT_BUTTON_SIZE, DERGGYCRAFT_BUTTON_SIZE);

        if (minecraft != null && minecraft.textureManager != null) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId(DERGGYCRAFT_BUTTON_TEXTURE));
            int v = hovered ? DERGGYCRAFT_BUTTON_TEXTURE_HOVER_V : DERGGYCRAFT_BUTTON_TEXTURE_V;
            drawContext.derggycraft$invokeDrawTexture(x, y, DERGGYCRAFT_BUTTON_TEXTURE_U, v, DERGGYCRAFT_BUTTON_SIZE, DERGGYCRAFT_BUTTON_SIZE);
            return;
        }

        int outer = hovered ? 0xE0D8D8D8 : 0xB0A8A8A8;
        int inner = hovered ? 0xE04C4C4C : 0xC03A3A3A;
        drawContext.derggycraft$invokeFillGradient(x, y, x + DERGGYCRAFT_BUTTON_SIZE, y + DERGGYCRAFT_BUTTON_SIZE, outer, outer);
        drawContext.derggycraft$invokeFillGradient(x + 1, y + 1, x + DERGGYCRAFT_BUTTON_SIZE - 1, y + DERGGYCRAFT_BUTTON_SIZE - 1, inner, inner);
    }

    private void derggycraft$sendSortAction(Minecraft minecraft, InventorySortRuntime.SortTarget target) {
        int actionButton = this.derggycraft$getSortButton(target);
        if (actionButton < 0) {
            return;
        }
        minecraft.interactionManager.clickSlot(this.handler.syncId, -999, actionButton, false, minecraft.player);
    }

    private int derggycraft$getSortButton(InventorySortRuntime.SortTarget target) {
        if (target == InventorySortRuntime.SortTarget.CONTAINER) {
            return InventorySortRuntime.BUTTON_SORT_CONTAINER;
        }
        if (target == InventorySortRuntime.SortTarget.PLAYER) {
            return InventorySortRuntime.BUTTON_SORT_PLAYER;
        }
        return -1;
    }

    private InventorySortRuntime.SortTarget derggycraft$getSortTargetForSlot(Slot slot) {
        boolean playerSlot = ((SlotAccessor) slot).derggycraft$getInventory() instanceof PlayerInventory;
        return playerSlot ? InventorySortRuntime.SortTarget.PLAYER : InventorySortRuntime.SortTarget.CONTAINER;
    }

    private Slot derggycraft$getHoveredSlot(int mouseX, int mouseY) {
        if (this.handler == null || mouseX == Integer.MIN_VALUE || mouseY == Integer.MIN_VALUE) {
            return null;
        }

        Screen screen = (Screen) (Object) this;
        int left = (screen.width - this.backgroundWidth) / 2;
        int top = (screen.height - this.backgroundHeight) / 2;
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            Slot slot = (Slot) this.handler.slots.get(i);
            if (slot == null || slot.id < 0) {
                continue;
            }

            if (this.derggycraft$isPointOverSlot(slot, mouseX, mouseY, left, top)) {
                return slot;
            }
        }
        return null;
    }

    private boolean derggycraft$isPointOverSlot(Slot slot, int mouseX, int mouseY, int left, int top) {
        int relativeX = mouseX - left;
        int relativeY = mouseY - top;
        return relativeX >= slot.x - 1
                && relativeX < slot.x + 17
                && relativeY >= slot.y - 1
                && relativeY < slot.y + 17;
    }

    private int derggycraft$getButtonX(int left) {
        return left + this.backgroundWidth - DERGGYCRAFT_BUTTON_SIZE - DERGGYCRAFT_BUTTON_RIGHT_PADDING;
    }

    private int derggycraft$getContainerButtonY(int top) {
        return top + DERGGYCRAFT_BUTTON_TOP_PADDING;
    }

    private int derggycraft$getPlayerButtonY(int top) {
        return top + Math.max(DERGGYCRAFT_BUTTON_TOP_PADDING, this.backgroundHeight - 96 + DERGGYCRAFT_BUTTON_PLAYER_BAND_OFFSET);
    }

    private boolean derggycraft$isPointInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private Minecraft derggycraft$getMinecraftForSort() {
        return ((ScreenAccessor) (Object) this).derggycraft$getMinecraft();
    }
}