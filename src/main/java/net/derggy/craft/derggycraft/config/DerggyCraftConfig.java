package net.derggy.craft.derggycraft.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class DerggyCraftConfig {

    @ConfigRoot(value = "derggycraft", visibleName = "DerggyCraft")
    public static final StaminaConfig CONFIG = new StaminaConfig();

    public static class StaminaConfig {

        @ConfigCategory(name = "Stamina", description = "Stamina bar settings")
        public StaminaSettings stamina = new StaminaSettings();

    }

    public static class StaminaSettings {

        @ConfigEntry(name = "Max Stamina", description = "Maximum stamina value (in half-bars)")
        public int maxStamina = 20;

        @ConfigEntry(name = "Sprint Drain Rate", description = "Stamina drain per tick while sprinting")
        public float sprintDrainRate = 0.15f;

        @ConfigEntry(name = "Attack Stamina Cost", description = "Stamina cost per attack")
        public float attackCost = 2.0f;

        @ConfigEntry(name = "Regen Rate", description = "Stamina regeneration per tick when idle")
        public float regenRate = 0.1f;

        @ConfigEntry(name = "Regen Delay", description = "Ticks before stamina starts regenerating")
        public int regenDelay = 40;

        @ConfigEntry(name = "Low Stamina Threshold", description = "Stamina level when hand sway begins (percentage)")
        public float lowStaminaThreshold = 0.25f;

        @ConfigEntry(name = "Empty Stamina Penalty", description = "Disable attacking when stamina is empty")
        public boolean emptyPenalty = true;

        @ConfigEntry(name = "Sprint Key Double Tap", description = "Use double-tap W to sprint (otherwise use a key)")
        public boolean doubleTapSprint = true;
    }
}
