package net.noahsarch.derggycraft.mixin;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.noahsarch.derggycraft.stamina.StaminaAccessor;
import net.noahsarch.derggycraft.stamina.StaminaConfig;
import net.noahsarch.derggycraft.stamina.StaminaSprintState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityStaminaMixin implements StaminaAccessor {
    @Unique
    private double derggycraft$stamina = StaminaConfig.MAX_STAMINA;

    @Unique
    private double derggycraft$displayStamina = StaminaConfig.MAX_STAMINA;

    @Unique
    private boolean derggycraft$sprinting;

    @Unique
    private int derggycraft$regenDelay;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void derggycraft$updateStaminaState(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        this.derggycraft$updateSprintState(player);

        boolean inWater = player.isSubmergedInWater() || player.isInFluid(Material.WATER);
        boolean moving = Math.abs(player.velocityX) + Math.abs(player.velocityZ) > 0.03;
        boolean activelySwimming = inWater && (moving || player.velocityY > 0.02);
        boolean climbingLadder = player.isOnLadder() && (Math.abs(player.velocityY) > 0.02 || moving);

        if (this.derggycraft$sprinting && moving) {
            this.derggycraft$drain(StaminaConfig.SPRINT_DRAIN_PER_TICK);
        }

        if (activelySwimming) {
            this.derggycraft$drain(StaminaConfig.SWIM_DRAIN_PER_TICK);
            if (this.derggycraft$stamina <= 0.0) {
                player.velocityY -= StaminaConfig.SINK_ACCELERATION_PER_TICK;
                if (player.velocityY < StaminaConfig.SINK_MAX_DOWNWARD_SPEED) {
                    player.velocityY = StaminaConfig.SINK_MAX_DOWNWARD_SPEED;
                }
            }
        } else if (climbingLadder) {
            this.derggycraft$drain(StaminaConfig.LADDER_DRAIN_PER_TICK);
        } else if (inWater && !moving && this.derggycraft$regenDelay <= 0) {
            this.derggycraft$stamina += StaminaConfig.IDLE_WATER_REGEN_PER_TICK;
        }

        if (this.derggycraft$regenDelay > 0) {
            this.derggycraft$regenDelay--;
        }

        if (!this.derggycraft$sprinting && !inWater && !moving && this.derggycraft$regenDelay <= 0) {
            this.derggycraft$stamina += StaminaConfig.REGEN_PER_TICK;
        }

        this.derggycraft$stamina = this.derggycraft$clamp(this.derggycraft$stamina);
        this.derggycraft$displayStamina += (this.derggycraft$stamina - this.derggycraft$displayStamina) * StaminaConfig.DISPLAY_SMOOTHING;
        if (Math.abs(this.derggycraft$displayStamina - this.derggycraft$stamina) < 0.05) {
            this.derggycraft$displayStamina = this.derggycraft$stamina;
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void derggycraft$applyAttackStamina(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (target == null || player.world == null || player.world.isRemote) {
            return;
        }

        if (this.derggycraft$stamina <= 0.0) {
            target.damage(player, (int) StaminaConfig.EMPTY_ATTACK_DAMAGE);
            ci.cancel();
            return;
        }

        this.derggycraft$drain(StaminaConfig.ATTACK_DRAIN_PER_HIT);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void derggycraft$preventSwimmingJumpWhenExhausted(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if ((player.isSubmergedInWater() || player.isInFluid(Material.WATER)) && this.derggycraft$stamina <= 0.0) {
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void derggycraft$applySprintJumpMomentum(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        this.derggycraft$drain(StaminaConfig.JUMP_DRAIN_PER_JUMP);

        if (!this.derggycraft$sprinting) {
            return;
        }

        if (player.isSubmergedInWater() || player.isInFluid(Material.WATER)) {
            return;
        }

        float yawRadians = player.yaw * (float) Math.PI / 180.0F;
        player.velocityY += StaminaConfig.SPRINT_JUMP_VERTICAL_BOOST;
        player.velocityX -= MathHelper.sin(yawRadians) * StaminaConfig.SPRINT_JUMP_HORIZONTAL_BOOST;
        player.velocityZ += MathHelper.cos(yawRadians) * StaminaConfig.SPRINT_JUMP_HORIZONTAL_BOOST;
    }

    @Unique
    private void derggycraft$updateSprintState(PlayerEntity player) {
        boolean inWater = player.isSubmergedInWater() || player.isInFluid(Material.WATER);
        boolean canMaintainSprint = player.health > 0 && !player.isSneaking() && !inWater && this.derggycraft$stamina > 0.0;
        boolean canStartSprint = canMaintainSprint && player.onGround;
        boolean movingForward = StaminaSprintState.isForwardDown() || Math.abs(player.velocityX) + Math.abs(player.velocityZ) > 0.03;

        boolean sprintHeld = StaminaSprintState.isSprintHeld();

        if (this.derggycraft$sprinting) {
            if (!canMaintainSprint || !movingForward || !sprintHeld) {
                this.derggycraft$sprinting = false;
            }
            return;
        }

        if (!canStartSprint || !movingForward || !sprintHeld) {
            return;
        }

        this.derggycraft$sprinting = true;
    }

    @Unique
    private void derggycraft$drain(double amount) {
        if (amount <= 0.0) {
            return;
        }

        this.derggycraft$stamina -= amount;
        this.derggycraft$regenDelay = StaminaConfig.REGEN_DELAY_TICKS;
        if (this.derggycraft$stamina <= 0.0) {
            this.derggycraft$stamina = 0.0;
            this.derggycraft$sprinting = false;
        }
    }

    @Unique
    private double derggycraft$clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        return Math.min(value, StaminaConfig.MAX_STAMINA);
    }

    @Override
    public double derggycraft$getStamina() {
        return this.derggycraft$stamina;
    }

    @Override
    public double derggycraft$getDisplayStamina() {
        return this.derggycraft$displayStamina;
    }

    @Override
    public double derggycraft$getMaxStamina() {
        return StaminaConfig.MAX_STAMINA;
    }

    @Override
    public boolean derggycraft$isSprinting() {
        return this.derggycraft$sprinting;
    }
}