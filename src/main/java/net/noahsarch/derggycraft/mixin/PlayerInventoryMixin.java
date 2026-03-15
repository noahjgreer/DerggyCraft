package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.noahsarch.derggycraft.inventory.CollarInventoryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements CollarInventoryAccess {
    @Unique
    private static final int DERGGYCRAFT$COLLAR_SLOT_COUNT = 3;
    @Unique
    private static final int DERGGYCRAFT$COLLAR_SLOT_BASE = 200;

    @Unique
    private ItemStack[] derggycraft$collar = new ItemStack[DERGGYCRAFT$COLLAR_SLOT_COUNT];

    @Shadow
    public PlayerEntity player;

    @Shadow
    public abstract void markDirty();

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void derggycraft$readCollarNbt(NbtList nbt, CallbackInfo ci) {
        this.derggycraft$collar = new ItemStack[DERGGYCRAFT$COLLAR_SLOT_COUNT];
        for (int i = 0; i < nbt.size(); ++i) {
            NbtCompound nbtCompound = (NbtCompound) nbt.get(i);
            int slot = nbtCompound.getByte("Slot") & 255;
            if (slot < DERGGYCRAFT$COLLAR_SLOT_BASE || slot >= DERGGYCRAFT$COLLAR_SLOT_BASE + DERGGYCRAFT$COLLAR_SLOT_COUNT) {
                continue;
            }

            ItemStack itemStack = new ItemStack(nbtCompound);
            if (itemStack.getItem() == null) {
                continue;
            }

            this.derggycraft$collar[slot - DERGGYCRAFT$COLLAR_SLOT_BASE] = itemStack;
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void derggycraft$writeCollarNbt(NbtList nbt, CallbackInfoReturnable<NbtList> cir) {
        for (int i = 0; i < this.derggycraft$collar.length; ++i) {
            ItemStack stack = this.derggycraft$collar[i];
            if (stack == null) {
                continue;
            }

            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte) (DERGGYCRAFT$COLLAR_SLOT_BASE + i));
            stack.writeNbt(nbtCompound);
            nbt.add(nbtCompound);
        }
    }

    @Inject(method = "dropInventory", at = @At("TAIL"))
    private void derggycraft$dropCollarInventory(CallbackInfo ci) {
        for (int i = 0; i < this.derggycraft$collar.length; ++i) {
            ItemStack stack = this.derggycraft$collar[i];
            if (stack == null) {
                continue;
            }

            this.player.dropItem(stack, true);
            this.derggycraft$collar[i] = null;
        }
    }

    @Override
    public int derggycraft$getCollarSize() {
        return this.derggycraft$collar.length;
    }

    @Override
    public ItemStack derggycraft$getCollarStack(int slot) {
        if (slot < 0 || slot >= this.derggycraft$collar.length) {
            return null;
        }
        return this.derggycraft$collar[slot];
    }

    @Override
    public void derggycraft$setCollarStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.derggycraft$collar.length) {
            return;
        }

        this.derggycraft$collar[slot] = stack;
        this.markDirty();
    }

    @Override
    public ItemStack derggycraft$removeCollarStack(int slot, int amount) {
        if (slot < 0 || slot >= this.derggycraft$collar.length) {
            return null;
        }

        ItemStack stack = this.derggycraft$collar[slot];
        if (stack == null) {
            return null;
        }

        if (stack.count <= amount) {
            this.derggycraft$collar[slot] = null;
            this.markDirty();
            return stack;
        }

        ItemStack split = stack.split(amount);
        if (stack.count == 0) {
            this.derggycraft$collar[slot] = null;
        }
        this.markDirty();
        return split;
    }
}