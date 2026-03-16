package net.noahsarch.derggycraft.client.stamina;

import net.minecraft.client.option.GameOptions;
import net.noahsarch.derggycraft.stamina.StaminaConfig;
import net.noahsarch.derggycraft.stamina.StaminaSprintState;
import org.lwjgl.input.Keyboard;

public final class StaminaInputState {
    private static int sprintTapTimer;
    private StaminaInputState() {
    }

    public static void onUpdateKey(GameOptions options, int key, boolean keyDown) {
        if (options == null || key != options.forwardKey.code) {
            return;
        }

        if (keyDown && !StaminaSprintState.isForwardDown()) {
            if (sprintTapTimer > 0) {
                StaminaSprintState.requestSprint();
            }
            sprintTapTimer = StaminaConfig.DOUBLE_TAP_WINDOW_TICKS;
        }

        StaminaSprintState.setForwardDown(keyDown);
    }

    public static void onTick(GameOptions options) {
        if (options == null) {
            return;
        }

        if (sprintTapTimer > 0) {
            sprintTapTimer--;
        }

        boolean forwardDown = Keyboard.isKeyDown(options.forwardKey.code);
        StaminaSprintState.setForwardDown(forwardDown);
        boolean sneaking = Keyboard.isKeyDown(options.sneakKey.code);
        boolean ctrlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean sprintHeld = ctrlDown && forwardDown && !sneaking;
        StaminaSprintState.setSprintHeld(sprintHeld);

        if (sprintHeld) {
            StaminaSprintState.requestSprint();
        }
    }

    public static void reset() {
        sprintTapTimer = 0;
        StaminaSprintState.reset();
    }
}