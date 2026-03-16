package net.noahsarch.derggycraft.stamina;

public final class StaminaConfig {
    public static final double MAX_STAMINA = 100.0;
    public static final double SPRINT_DRAIN_PER_TICK = MAX_STAMINA / (6.0 * 20.0);
    public static final double ATTACK_DRAIN_PER_HIT = 5.0;
    public static final double SWIM_DRAIN_PER_TICK = MAX_STAMINA / (20.0 * 20.0);
    public static final double REGEN_PER_TICK = MAX_STAMINA / (10.0 * 20.0);
    public static final double IDLE_WATER_REGEN_PER_TICK = MAX_STAMINA / (30.0 * 20.0);
    public static final int REGEN_DELAY_TICKS = 20;
    public static final double SPRINT_SPEED_MULTIPLIER = 1.3;
    public static final double SPRINT_AIR_CONTROL_MULTIPLIER = 1.2;
    public static final double SPRINT_JUMP_HORIZONTAL_BOOST = 0.2;
    public static final double SINK_ACCELERATION_PER_TICK = 0.03;
    public static final double SINK_MAX_DOWNWARD_SPEED = -0.25;
    public static final int DOUBLE_TAP_WINDOW_TICKS = 7;
    public static final double DISPLAY_SMOOTHING = 0.2;
    public static final double EMPTY_ATTACK_DAMAGE = 1.0;

    private StaminaConfig() {
    }
}