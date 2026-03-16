package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.stamina.StaminaAccessor;
import net.noahsarch.derggycraft.stamina.StaminaConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class EntitySprintAccelerationMixin {
    @ModifyVariable(method = "moveNonSolid", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private float derggycraft$boostPlayerAcceleration(float acceleration) {
        Object self = this;
        if (self instanceof PlayerEntity && self instanceof StaminaAccessor stamina && stamina.derggycraft$isSprinting()) {
            return (float) (acceleration * StaminaConfig.SPRINT_SPEED_MULTIPLIER);
        }
        return acceleration;
    }
}