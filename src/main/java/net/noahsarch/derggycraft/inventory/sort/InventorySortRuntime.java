package net.noahsarch.derggycraft.inventory.sort;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.noahsarch.derggycraft.mixin.SlotAccessor;

import java.util.ArrayList;
import java.util.List;

public final class InventorySortRuntime {
    public static final int BUTTON_SORT_CONTAINER = 7;
    public static final int BUTTON_SORT_PLAYER = 8;

    private InventorySortRuntime() {
    }

    public static SortTarget targetFromButton(int button) {
        if (button == BUTTON_SORT_CONTAINER) {
            return SortTarget.CONTAINER;
        }
        if (button == BUTTON_SORT_PLAYER) {
            return SortTarget.PLAYER;
        }
        return null;
    }

    public static boolean canSort(ScreenHandler handler, SortTarget target) {
        return getSortableSlotIds(handler, target).size() > 1;
    }

    public static boolean sort(ScreenHandler handler, PlayerEntity player, SortTarget target) {
        List<Integer> sortableSlotIds = getSortableSlotIds(handler, target);
        if (sortableSlotIds.size() <= 1) {
            return false;
        }

        List<Slot> sortableSlots = new ArrayList<>(sortableSlotIds.size());
        for (int slotId : sortableSlotIds) {
            sortableSlots.add(handler.getSlot(slotId));
        }

        List<ItemStack> sortedStacks = buildSortedStacks(sortableSlots);
        if (sortedStacks.isEmpty()) {
            return false;
        }

        for (int i = 0; i < sortableSlots.size(); ++i) {
            Slot slot = sortableSlots.get(i);
            ItemStack stack = sortedStacks.get(i);
            if (stack == null) {
                continue;
            }

            int slotLimit = Math.min(slot.getMaxItemCount(), stack.getMaxCount());
            if (!slot.canInsert(stack) || stack.count > slotLimit) {
                return false;
            }
        }

        for (int i = 0; i < sortableSlots.size(); ++i) {
            Slot slot = sortableSlots.get(i);
            ItemStack stack = sortedStacks.get(i);
            slot.setStack(stack == null ? null : stack.copy());
        }

        return true;
    }

    private static List<ItemStack> buildSortedStacks(List<Slot> sortableSlots) {
        List<ItemStack> sorted = new ArrayList<>(sortableSlots.size());

        for (Slot slot : sortableSlots) {
            ItemStack stack = slot.getStack();
            if (stack == null || stack.count <= 0) {
                continue;
            }

            // Keep full stack metadata (Station NBT/custom data) so compass targets and
            // other per-stack state survive sorting.
            sorted.add(stack.copy());
        }

        if (sorted.size() <= 1) {
            return List.of();
        }

        sorted.sort((left, right) -> {
            if (left.itemId != right.itemId) {
                return Integer.compare(left.itemId, right.itemId);
            }
            if (left.getDamage() != right.getDamage()) {
                return Integer.compare(left.getDamage(), right.getDamage());
            }
            return Integer.compare(right.count, left.count);
        });

        List<ItemStack> merged = new ArrayList<>(sorted.size());
        for (ItemStack stack : sorted) {
            if (stack == null || stack.count <= 0) {
                continue;
            }

            int remaining = stack.count;
            while (remaining > 0) {
                ItemStack tail = merged.isEmpty() ? null : merged.get(merged.size() - 1);
                if (tail != null && derggycraft$canMergeStacks(tail, stack) && tail.count < tail.getMaxCount()) {
                    int moved = Math.min(remaining, tail.getMaxCount() - tail.count);
                    tail.count += moved;
                    remaining -= moved;
                    continue;
                }

                ItemStack chunk = stack.copy();
                chunk.count = Math.min(remaining, chunk.getMaxCount());
                merged.add(chunk);
                remaining -= chunk.count;
            }
        }

        while (merged.size() < sortableSlots.size()) {
            merged.add(null);
        }

        return merged;
    }

    private static boolean derggycraft$canMergeStacks(ItemStack left, ItemStack right) {
        if (left == null || right == null) {
            return false;
        }

        if (!left.isStackable() || !right.isStackable()) {
            return false;
        }

        if (left.itemId != right.itemId) {
            return false;
        }

        return !left.hasSubtypes() || left.getDamage() == right.getDamage();
    }

    private static List<Integer> getSortableSlotIds(ScreenHandler handler, SortTarget target) {
        if (handler == null) {
            return List.of();
        }

        boolean playerSort = target == SortTarget.PLAYER;
        boolean supportsContainerSort = handler instanceof GenericContainerScreenHandler;
        boolean supportsPlayerSort = handler instanceof GenericContainerScreenHandler || handler instanceof PlayerScreenHandler;

        if (!playerSort && !supportsContainerSort) {
            return List.of();
        }

        if (playerSort && !supportsPlayerSort) {
            return List.of();
        }

        List<Integer> slotIds = new ArrayList<>();
        for (int i = 0; i < handler.slots.size(); ++i) {
            Slot slot = (Slot) handler.slots.get(i);
            if (slot == null || slot.getMaxItemCount() <= 1) {
                continue;
            }

            boolean isPlayerSlot = ((SlotAccessor) slot).derggycraft$getInventory() instanceof PlayerInventory;
            if (playerSort != isPlayerSlot) {
                continue;
            }

            // Preserve hotbar order when sorting the player's inventory section.
            if (playerSort) {
                int inventoryIndex = ((SlotAccessor) slot).derggycraft$getInventoryIndex();
                if (inventoryIndex >= 0 && inventoryIndex <= 8) {
                    continue;
                }
            }

            // ScreenHandler APIs address slots by list position, not inventory-local slot id.
            slotIds.add(i);
        }

        return slotIds;
    }

    public enum SortTarget {
        CONTAINER,
        PLAYER
    }
}