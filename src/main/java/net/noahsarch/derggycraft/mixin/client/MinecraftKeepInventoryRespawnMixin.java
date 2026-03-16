package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftKeepInventoryRespawnMixin {
    @Shadow
    public World world;

    @Shadow
    public ClientPlayerEntity player;

    @Unique
    private ItemStack[] derggycraft$respawnMain;
    @Unique
    private ItemStack[] derggycraft$respawnArmor;
    @Unique
    private ItemStack[] derggycraft$respawnCollar;
    @Unique
    private int derggycraft$respawnSelectedSlot;

    @Inject(method = "respawnPlayer", at = @At("HEAD"))
    private void derggycraft$captureInventoryBeforeRespawn(boolean worldSpawn, int dimension, CallbackInfo ci) {
        this.derggycraft$clearRespawnSnapshot();
        if (this.world == null || this.player == null || this.world.isRemote) {
            return;
        }

        DerggyCraftGameRules.ensureLoaded(this.world);
        if (!DerggyCraftGameRules.get(this.world, DerggyCraftGameRules.Rule.KEEP_INVENTORY)) {
            return;
        }

        PlayerInventory oldInventory = this.player.inventory;
        this.derggycraft$respawnMain = this.derggycraft$copyStacks(oldInventory.main);
        this.derggycraft$respawnArmor = this.derggycraft$copyStacks(oldInventory.armor);
        this.derggycraft$respawnSelectedSlot = oldInventory.selectedSlot;

        if (oldInventory instanceof CollarInventoryAccess oldCollar) {
            this.derggycraft$respawnCollar = new ItemStack[oldCollar.derggycraft$getCollarSize()];
            for (int i = 0; i < this.derggycraft$respawnCollar.length; ++i) {
                this.derggycraft$respawnCollar[i] = this.derggycraft$cloneStack(oldCollar.derggycraft$getCollarStack(i));
            }
        }
    }

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void derggycraft$restoreInventoryAfterRespawn(boolean worldSpawn, int dimension, CallbackInfo ci) {
        if (this.player == null || this.derggycraft$respawnMain == null || this.world == null || this.world.isRemote) {
            this.derggycraft$clearRespawnSnapshot();
            return;
        }

        PlayerInventory newInventory = this.player.inventory;
        this.derggycraft$restoreStacks(this.derggycraft$respawnMain, newInventory.main);
        this.derggycraft$restoreStacks(this.derggycraft$respawnArmor, newInventory.armor);
        newInventory.selectedSlot = this.derggycraft$respawnSelectedSlot;

        if (this.derggycraft$respawnCollar != null && newInventory instanceof CollarInventoryAccess newCollar) {
            int collarSlots = Math.min(this.derggycraft$respawnCollar.length, newCollar.derggycraft$getCollarSize());
            for (int i = 0; i < collarSlots; ++i) {
                newCollar.derggycraft$setCollarStack(i, this.derggycraft$cloneStack(this.derggycraft$respawnCollar[i]));
            }
        }

        newInventory.markDirty();
        if (this.player.currentScreenHandler != null) {
            this.player.currentScreenHandler.sendContentUpdates();
        }

        this.derggycraft$clearRespawnSnapshot();
    }

    @Unique
    private ItemStack[] derggycraft$copyStacks(ItemStack[] source) {
        ItemStack[] copy = new ItemStack[source.length];
        for (int i = 0; i < source.length; ++i) {
            copy[i] = this.derggycraft$cloneStack(source[i]);
        }
        return copy;
    }

    @Unique
    private void derggycraft$restoreStacks(ItemStack[] source, ItemStack[] destination) {
        int length = Math.min(source.length, destination.length);
        for (int i = 0; i < length; ++i) {
            destination[i] = this.derggycraft$cloneStack(source[i]);
        }
    }

    @Unique
    private ItemStack derggycraft$cloneStack(ItemStack stack) {
        return stack == null ? null : ItemStack.clone(stack);
    }

    @Unique
    private void derggycraft$clearRespawnSnapshot() {
        this.derggycraft$respawnMain = null;
        this.derggycraft$respawnArmor = null;
        this.derggycraft$respawnCollar = null;
        this.derggycraft$respawnSelectedSlot = 0;
    }
}