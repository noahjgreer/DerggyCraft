package net.noahsarch.derggycraft.stamina;

public final class StaminaSprintState {
    private static boolean sprintHeld;
    private static boolean forwardDown;

    private StaminaSprintState() {
    }

    public static void setSprintHeld(boolean held) {
        sprintHeld = held;
    }

    public static boolean isSprintHeld() {
        return sprintHeld;
    }

    public static void setForwardDown(boolean down) {
        forwardDown = down;
    }

    public static boolean isForwardDown() {
        return forwardDown;
    }

    public static void reset() {
        sprintHeld = false;
        forwardDown = false;
    }
}