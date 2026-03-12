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
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.client.util.Session;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.network.packet.play.EntityAnimationPacket;
import net.minecraft.network.packet.play.PlayerMoveFullPacket;
import net.minecraft.network.packet.play.PlayerMoveLookAndOnGroundPacket;
import net.minecraft.network.packet.play.PlayerMovePacket;
import net.minecraft.network.packet.play.PlayerMovePositionAndOnGroundPacket;
import net.minecraft.network.packet.play.PlayerRespawnPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class MultiplayerClientPlayerEntity
extends ClientPlayerEntity {
    public ClientNetworkHandler networkHandler;
    private int field_2506 = 0;
    private boolean field_2507 = false;
    private double field_2508;
    private double field_2509;
    private double field_2510;
    private double field_2511;
    private float field_2512;
    private float field_2513;
    private boolean field_2514 = false;
    private boolean field_2515 = false;
    private int field_2516 = 0;

    public MultiplayerClientPlayerEntity(Minecraft minecraft, World world, Session session, ClientNetworkHandler networkHandler) {
        super(minecraft, world, session, 0);
        this.networkHandler = networkHandler;
    }

    public boolean damage(Entity damageSource, int amount) {
        return false;
    }

    public void heal(int amount) {
    }

    public void tick() {
        if (!this.world.isPosLoaded(MathHelper.floor(this.x), 64, MathHelper.floor(this.z))) {
            return;
        }
        super.tick();
        this.method_1922();
    }

    public void method_1922() {
        boolean bl;
        boolean bl2;
        if (this.field_2506++ == 20) {
            this.method_1923();
            this.field_2506 = 0;
        }
        if ((bl2 = this.isSneaking()) != this.field_2515) {
            if (bl2) {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, 1));
            } else {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, 2));
            }
            this.field_2515 = bl2;
        }
        double d = this.x - this.field_2508;
        double d2 = this.boundingBox.minY - this.field_2509;
        double d3 = this.y - this.field_2510;
        double d4 = this.z - this.field_2511;
        double d5 = this.yaw - this.field_2512;
        double d6 = this.pitch - this.field_2513;
        boolean bl3 = d2 != 0.0 || d3 != 0.0 || d != 0.0 || d4 != 0.0;
        boolean bl4 = bl = d5 != 0.0 || d6 != 0.0;
        if (this.vehicle != null) {
            if (bl) {
                this.networkHandler.sendPacket(new PlayerMovePositionAndOnGroundPacket(this.velocityX, -999.0, -999.0, this.velocityZ, this.onGround));
            } else {
                this.networkHandler.sendPacket(new PlayerMoveFullPacket(this.velocityX, -999.0, -999.0, this.velocityZ, this.yaw, this.pitch, this.onGround));
            }
            bl3 = false;
        } else if (bl3 && bl) {
            this.networkHandler.sendPacket(new PlayerMoveFullPacket(this.x, this.boundingBox.minY, this.y, this.z, this.yaw, this.pitch, this.onGround));
            this.field_2516 = 0;
        } else if (bl3) {
            this.networkHandler.sendPacket(new PlayerMovePositionAndOnGroundPacket(this.x, this.boundingBox.minY, this.y, this.z, this.onGround));
            this.field_2516 = 0;
        } else if (bl) {
            this.networkHandler.sendPacket(new PlayerMoveLookAndOnGroundPacket(this.yaw, this.pitch, this.onGround));
            this.field_2516 = 0;
        } else {
            this.networkHandler.sendPacket(new PlayerMovePacket(this.onGround));
            this.field_2516 = this.field_2514 != this.onGround || this.field_2516 > 200 ? 0 : ++this.field_2516;
        }
        this.field_2514 = this.onGround;
        if (bl3) {
            this.field_2508 = this.x;
            this.field_2509 = this.boundingBox.minY;
            this.field_2510 = this.y;
            this.field_2511 = this.z;
        }
        if (bl) {
            this.field_2512 = this.yaw;
            this.field_2513 = this.pitch;
        }
    }

    public void dropSelectedItem() {
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(4, 0, 0, 0, 0));
    }

    private void method_1923() {
    }

    protected void spawnItem(ItemEntity itemEntity) {
    }

    public void sendChatMessage(String message) {
        this.networkHandler.sendPacket(new ChatMessagePacket(message));
    }

    public void swingHand() {
        super.swingHand();
        this.networkHandler.sendPacket(new EntityAnimationPacket(this, 1));
    }

    public void respawn() {
        this.method_1923();
        this.networkHandler.sendPacket(new PlayerRespawnPacket((byte)this.dimensionId));
    }

    protected void applyDamage(int amount) {
        this.health -= amount;
    }

    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
        this.inventory.setCursorStack(null);
        super.closeHandledScreen();
    }

    public void damageTo(int health) {
        if (this.field_2507) {
            super.damageTo(health);
        } else {
            this.health = health;
            this.field_2507 = true;
        }
    }

    public void increaseStat(Stat stat, int amount) {
        if (stat == null) {
            return;
        }
        if (stat.localOnly) {
            super.increaseStat(stat, amount);
        }
    }

    public void handleIncreaseStat(Stat stat, int amount) {
        if (stat == null) {
            return;
        }
        if (!stat.localOnly) {
            super.increaseStat(stat, amount);
        }
    }
}

