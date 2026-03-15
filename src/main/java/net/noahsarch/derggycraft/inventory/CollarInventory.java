package net.noahsarch.derggycraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CollarInventory implements Inventory {
    private final CollarInventoryAccess backing;
    private final PlayerEntity player;

    public CollarInventory(CollarInventoryAccess backing, PlayerEntity player) {
        this.backing = backing;
        this.player = player;
    }

    @Override
    public int size() {
        return this.backing.derggycraft$getCollarSize();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.backing.derggycraft$getCollarStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.backing.derggycraft$removeCollarStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.backing.derggycraft$setCollarStack(slot, stack);
    }

    @Override
    public String getName() {
        return "Collar";
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.player.dead) {
            return false;
        }
        return !(player.getSquaredDistance(this.player) > 64.0);
    }
}