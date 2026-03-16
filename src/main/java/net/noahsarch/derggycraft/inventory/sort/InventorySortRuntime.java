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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<StackKey, Integer> totals = new HashMap<>();
        int nonEmptyCount = 0;

        for (Slot slot : sortableSlots) {
            ItemStack stack = slot.getStack();
            if (stack == null || stack.count <= 0) {
                continue;
            }

            ++nonEmptyCount;
            StackKey key = StackKey.from(stack);
            totals.merge(key, stack.count, Integer::sum);
        }

        if (nonEmptyCount <= 1) {
            return List.of();
        }

        List<StackKey> orderedKeys = new ArrayList<>(totals.keySet());
        orderedKeys.sort((left, right) -> {
            if (left.itemId != right.itemId) {
                return Integer.compare(left.itemId, right.itemId);
            }
            if (left.damage != right.damage) {
                return Integer.compare(left.damage, right.damage);
            }
            return Integer.compare(right.maxCount, left.maxCount);
        });

        List<ItemStack> sorted = new ArrayList<>(sortableSlots.size());
        for (StackKey key : orderedKeys) {
            int remaining = totals.getOrDefault(key, 0);
            int maxCount = Math.max(1, key.maxCount);
            while (remaining > 0) {
                int count = Math.min(maxCount, remaining);
                sorted.add(new ItemStack(key.itemId, count, key.damage));
                remaining -= count;
            }
        }

        while (sorted.size() < sortableSlots.size()) {
            sorted.add(null);
        }

        return sorted;
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
            if (slot == null || slot.id < 0 || slot.getMaxItemCount() <= 1) {
                continue;
            }

            boolean isPlayerSlot = ((SlotAccessor) slot).derggycraft$getInventory() instanceof PlayerInventory;
            if (playerSort != isPlayerSlot) {
                continue;
            }

            slotIds.add(slot.id);
        }

        return slotIds;
    }

    private record StackKey(int itemId, int damage, int maxCount) {
        private static StackKey from(ItemStack stack) {
            return new StackKey(stack.itemId, stack.getDamage(), Math.max(1, stack.getMaxCount()));
        }
    }

    public enum SortTarget {
        CONTAINER,
        PLAYER
    }
}