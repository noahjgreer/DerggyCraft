package net.derggy.craft.derggycraft.stamina;

/**
 * Interface injected into PlayerEntity via mixin to store stamina data.
 */
public interface StaminaAccessor {

    /**
     * Get current stamina value.
     */
    float derggycraft_getStamina();

    /**
     * Set current stamina value.
     */
    void derggycraft_setStamina(float stamina);

    /**
     * Get regeneration cooldown ticks.
     */
    int derggycraft_getRegenCooldown();

    /**
     * Set regeneration cooldown ticks.
     */
    void derggycraft_setRegenCooldown(int cooldown);

    /**
     * Check if player is currently sprinting.
     */
    boolean derggycraft_isSprinting();

    /**
     * Set sprinting state.
     */
    void derggycraft_setSprinting(boolean sprinting);



    /**
     * Get previous stamina value (for smooth interpolation).
     */
    float derggycraft_getPrevStamina();

    /**
     * Set previous stamina value.
     */
    void derggycraft_setPrevStamina(float prevStamina);
}
