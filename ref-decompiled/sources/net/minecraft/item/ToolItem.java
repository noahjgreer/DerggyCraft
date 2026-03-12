/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.modificationstation.stationapi.api.item.tool.StationToolItem
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.modificationstation.stationapi.api.item.tool.StationToolItem;

public class ToolItem
extends Item
implements StationToolItem {
    private Block[] effectiveOnBlocks;
    private float miningSpeed = 4.0f;
    private int damage;
    protected ToolMaterial toolMaterial;

    public ToolItem(int id, int damageBoost, ToolMaterial toolMaterial, Block[] effectiveOn) {
        super(id);
        this.toolMaterial = toolMaterial;
        this.effectiveOnBlocks = effectiveOn;
        this.maxCount = 1;
        this.setMaxDamage(toolMaterial.getDurability());
        this.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
        this.damage = damageBoost + toolMaterial.getAttackDamage();
    }

    public float getMiningSpeedMultiplier(ItemStack stack, Block block) {
        for (int i = 0; i < this.effectiveOnBlocks.length; ++i) {
            if (this.effectiveOnBlocks[i] != block) continue;
            return this.miningSpeed;
        }
        return 1.0f;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(2, attacker);
        return true;
    }

    public boolean postMine(ItemStack stack, int blockId, int x, int y, int z, LivingEntity miner) {
        stack.damage(1, miner);
        return true;
    }

    public int getAttackDamage(Entity attackedEntity) {
        return this.damage;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHandheld() {
        return true;
    }
}

