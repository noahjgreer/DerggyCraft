/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.FoodItem;

public class StackableFoodItem
extends FoodItem {
    public StackableFoodItem(int id, int healthRestored, boolean meat, int maxCount) {
        super(id, healthRestored, meat);
        this.maxCount = maxCount;
    }
}

