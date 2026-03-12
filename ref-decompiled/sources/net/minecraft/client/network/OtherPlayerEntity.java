/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class OtherPlayerEntity
extends PlayerEntity {
    private int field_2649;
    private double field_2650;
    private double field_2651;
    private double field_2652;
    private double field_2653;
    private double field_2654;
    float field_2648 = 0.0f;

    public OtherPlayerEntity(World world, String name) {
        super(world);
        this.name = name;
        this.standingEyeHeight = 0.0f;
        this.stepHeight = 0.0f;
        if (name != null && name.length() > 0) {
            this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + name + ".png";
        }
        this.noClip = true;
        this.sleepOffsetY = 0.25f;
        this.renderDistanceMultiplier = 10.0;
    }

    protected void resetEyeHeight() {
        this.standingEyeHeight = 0.0f;
    }

    public boolean damage(Entity damageSource, int amount) {
        return true;
    }

    public void setPositionAndAnglesAvoidEntities(double x, double y, double z, float pitch, float yaw, int interpolationSteps) {
        this.field_2650 = x;
        this.field_2651 = y;
        this.field_2652 = z;
        this.field_2653 = pitch;
        this.field_2654 = yaw;
        this.field_2649 = interpolationSteps;
    }

    public void tick() {
        this.sleepOffsetY = 0.0f;
        super.tick();
        this.lastWalkAnimationSpeed = this.walkAnimationSpeed;
        double d = this.x - this.prevX;
        double d2 = this.z - this.prevZ;
        float f = MathHelper.sqrt(d * d + d2 * d2) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.walkAnimationSpeed += (f - this.walkAnimationSpeed) * 0.4f;
        this.walkAnimationProgress += this.walkAnimationSpeed;
    }

    public float getShadowRadius() {
        return 0.0f;
    }

    public void tickMovement() {
        super.tickLiving();
        if (this.field_2649 > 0) {
            double d;
            double d2 = this.x + (this.field_2650 - this.x) / (double)this.field_2649;
            double d3 = this.y + (this.field_2651 - this.y) / (double)this.field_2649;
            double d4 = this.z + (this.field_2652 - this.z) / (double)this.field_2649;
            for (d = this.field_2653 - (double)this.yaw; d < -180.0; d += 360.0) {
            }
            while (d >= 180.0) {
                d -= 360.0;
            }
            this.yaw = (float)((double)this.yaw + d / (double)this.field_2649);
            this.pitch = (float)((double)this.pitch + (this.field_2654 - (double)this.pitch) / (double)this.field_2649);
            --this.field_2649;
            this.setPosition(d2, d3, d4);
            this.setRotation(this.yaw, this.pitch);
        }
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
        float f2 = (float)Math.atan(-this.velocityY * (double)0.2f) * 15.0f;
        if (f > 0.1f) {
            f = 0.1f;
        }
        if (!this.onGround || this.health <= 0) {
            f = 0.0f;
        }
        if (this.onGround || this.health <= 0) {
            f2 = 0.0f;
        }
        this.stepBobbingAmount += (f - this.stepBobbingAmount) * 0.4f;
        this.tilt += (f2 - this.tilt) * 0.8f;
    }

    public void setEquipmentStack(int armorSlot, int itemId, int meta) {
        ItemStack itemStack = null;
        if (itemId >= 0) {
            itemStack = new ItemStack(itemId, 1, meta);
        }
        if (armorSlot == 0) {
            this.inventory.main[this.inventory.selectedSlot] = itemStack;
        } else {
            this.inventory.armor[armorSlot - 1] = itemStack;
        }
    }

    public void spawn() {
    }
}

