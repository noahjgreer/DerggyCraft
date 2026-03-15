package net.noahsarch.derggycraft.inventory;

import net.minecraft.item.ItemStack;

public interface CollarInventoryAccess {
    int derggycraft$getCollarSize();

    ItemStack derggycraft$getCollarStack(int slot);

    void derggycraft$setCollarStack(int slot, ItemStack stack);

    ItemStack derggycraft$removeCollarStack(int slot, int amount);
}