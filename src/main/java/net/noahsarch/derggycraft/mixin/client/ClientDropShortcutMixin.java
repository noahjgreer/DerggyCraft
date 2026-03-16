package net.noahsarch.derggycraft.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.MultiplayerClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class, MultiplayerClientPlayerEntity.class})
public abstract class ClientDropShortcutMixin {
    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleCtrlQDropAll(CallbackInfo ci) {
        if (!derggycraft$isCtrlHeld()) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.inventory == null) {
            return;
        }

        Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (minecraft == null || minecraft.player != player || minecraft.interactionManager == null || player.currentScreenHandler == null) {
            return;
        }

        if (player.inventory.getCursorStack() != null) {
            return;
        }

        int hotbarSlotId = 36 + player.inventory.selectedSlot;
        if (hotbarSlotId < 0 || hotbarSlotId >= player.currentScreenHandler.slots.size()) {
            return;
        }

        Slot selectedSlot = (Slot) player.currentScreenHandler.slots.get(hotbarSlotId);
        if (selectedSlot == null || !selectedSlot.hasStack()) {
            return;
        }

        minecraft.interactionManager.clickSlot(player.currentScreenHandler.syncId, selectedSlot.id, 0, false, player);
        minecraft.interactionManager.clickSlot(player.currentScreenHandler.syncId, -999, 0, false, player);

        ci.cancel();
    }

    @Unique
    private static boolean derggycraft$isCtrlHeld() {
        return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }
}