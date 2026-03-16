package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.client.stamina.StaminaInputState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputSprintMixin {
    @Shadow
    private GameOptions options;

    @Inject(method = "updateKey", at = @At("TAIL"))
    private void derggycraft$trackSprintTaps(int key, boolean keyDown, CallbackInfo ci) {
        StaminaInputState.onUpdateKey(this.options, key, keyDown);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void derggycraft$trackSprintHold(PlayerEntity player, CallbackInfo ci) {
        StaminaInputState.onTick(this.options);
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void derggycraft$resetSprintInput(CallbackInfo ci) {
        StaminaInputState.reset();
    }
}