package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityDeathMixin {
    @Inject(method = "onKilledBy", at = @At("HEAD"), cancellable = true)
    private void derggycraft$applyDeathGamerules(Entity adversary, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        DerggyCraftGameRules.ensureLoaded(player.world);

        if (player.server != null && player.server.playerManager != null && DerggyCraftGameRules.get(DerggyCraftGameRules.Rule.SEND_DEATH_MESSAGES)) {
            player.server.playerManager.sendToAll(new ChatMessagePacket(player.name + " died."));
        }

        if (DerggyCraftGameRules.get(DerggyCraftGameRules.Rule.KEEP_INVENTORY)) {
            ci.cancel();
        }
    }
}