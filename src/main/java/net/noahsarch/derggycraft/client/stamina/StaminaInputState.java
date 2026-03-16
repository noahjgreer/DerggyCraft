package net.noahsarch.derggycraft.client.stamina;

import net.minecraft.client.option.GameOptions;
import net.noahsarch.derggycraft.stamina.StaminaSprintState;
import org.lwjgl.input.Keyboard;

public final class StaminaInputState {
    private StaminaInputState() {
    }

    public static void onUpdateKey(GameOptions options, int key, boolean keyDown) {
        if (options == null || key != options.forwardKey.code) {
            return;
        }

        StaminaSprintState.setForwardDown(keyDown);
    }

    public static void onTick(GameOptions options) {
        if (options == null) {
            return;
        }

        boolean forwardDown = Keyboard.isKeyDown(options.forwardKey.code);
        StaminaSprintState.setForwardDown(forwardDown);
        boolean sneaking = Keyboard.isKeyDown(options.sneakKey.code);
        boolean ctrlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean sprintHeld = ctrlDown && forwardDown && !sneaking;
        StaminaSprintState.setSprintHeld(sprintHeld);
    }

    public static void reset() {
        StaminaSprintState.reset();
    }
}