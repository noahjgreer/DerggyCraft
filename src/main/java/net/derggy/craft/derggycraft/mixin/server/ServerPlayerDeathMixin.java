package net.derggy.craft.derggycraft.mixin.server;

import net.derggy.craft.derggycraft.gamerule.GameruleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.play.ChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Broadcasts death messages and supports keepInventory gamerule.
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerDeathMixin {

    /**
     * Broadcast death message when a player dies.
     */
    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void broadcastDeathMessage(Entity adversary, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        self.server.playerManager.sendToAll(
                new ChatMessagePacket(self.name + " died.")
        );
    }

    /**
     * Conditionally skip inventory drop when keepInventory is enabled.
     */
    @Redirect(
            method = "onKilledBy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropInventory()V")
    )
    private void conditionallyDropInventory(PlayerInventory inventory) {
        if (!GameruleManager.getKeepInventory()) {
            inventory.dropInventory();
        }
    }
}
