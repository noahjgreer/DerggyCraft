package net.derggy.craft.derggycraft.stamina;

import net.derggy.craft.derggycraft.config.DerggyCraftConfig;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Core stamina management system.
 * Handles stamina tracking, draining, and regeneration for players.
 */
public class StaminaManager {

    private static final DerggyCraftConfig.StaminaSettings CONFIG = DerggyCraftConfig.CONFIG.stamina;

    /**
     * Get stamina data from a player entity.
     */
    public static StaminaAccessor getStamina(PlayerEntity player) {
        return (StaminaAccessor) player;
    }

    /**
     * Drain stamina from a player.
     * @return true if stamina was successfully drained
     */
    public static boolean drainStamina(PlayerEntity player, float amount) {
        StaminaAccessor accessor = getStamina(player);
        float current = accessor.derggycraft_getStamina();
        
        if (current >= amount) {
            accessor.derggycraft_setStamina(current - amount);
            accessor.derggycraft_setRegenCooldown(CONFIG.regenDelay);
            return true;
        }
        return false;
    }

    /**
     * Force drain stamina even if insufficient.
     */
    public static void forceDrainStamina(PlayerEntity player, float amount) {
        StaminaAccessor accessor = getStamina(player);
        float current = accessor.derggycraft_getStamina();
        accessor.derggycraft_setStamina(Math.max(0, current - amount));
        accessor.derggycraft_setRegenCooldown(CONFIG.regenDelay);
    }

    /**
     * Check if player can sprint (has enough stamina).
     */
    public static boolean canSprint(PlayerEntity player) {
        return getStamina(player).derggycraft_getStamina() > 0;
    }

    /**
     * Check if player can attack (has enough stamina or penalty is disabled).
     */
    public static boolean canAttack(PlayerEntity player) {
        if (!CONFIG.emptyPenalty) return true;
        return getStamina(player).derggycraft_getStamina() > 0;
    }

    /**
     * Check if player is in low stamina state (for hand sway effect).
     */
    public static boolean isLowStamina(PlayerEntity player) {
        float current = getStamina(player).derggycraft_getStamina();
        float max = CONFIG.maxStamina;
        return (current / max) <= CONFIG.lowStaminaThreshold;
    }

    /**
     * Get stamina ratio (0.0 - 1.0) for rendering and effects.
     */
    public static float getStaminaRatio(PlayerEntity player) {
        return getStamina(player).derggycraft_getStamina() / CONFIG.maxStamina;
    }

    /**
     * Tick stamina regeneration for a player.
     */
    public static void tickRegen(PlayerEntity player) {
        StaminaAccessor accessor = getStamina(player);
        
        int cooldown = accessor.derggycraft_getRegenCooldown();
        if (cooldown > 0) {
            accessor.derggycraft_setRegenCooldown(cooldown - 1);
            return;
        }
        
        float current = accessor.derggycraft_getStamina();
        float max = CONFIG.maxStamina;
        
        if (current < max) {
            accessor.derggycraft_setStamina(Math.min(max, current + CONFIG.regenRate));
        }
    }

    /**
     * Initialize stamina for a player to max value.
     */
    public static void initializeStamina(PlayerEntity player) {
        StaminaAccessor accessor = getStamina(player);
        accessor.derggycraft_setStamina(CONFIG.maxStamina);
        accessor.derggycraft_setRegenCooldown(0);
    }
}
