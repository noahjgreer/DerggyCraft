package net.noahsarch.derggycraft.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ClientPlayerEntityDeathMessageMixin {
    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void derggycraft$showSingleplayerDeathMessage(Entity adversary, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.world == null) {
            return;
        }

        // In Beta singleplayer worlds are non-remote; multiplayer worlds are remote.
        if (player.world.isRemote) {
            return;
        }

        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.inGameHud == null || minecraft.player != player) {
            return;
        }

        DerggyCraftGameRules.ensureLoaded(player.world);
        if (!DerggyCraftGameRules.get(player.world, DerggyCraftGameRules.Rule.SEND_DEATH_MESSAGES)) {
            return;
        }

        minecraft.inGameHud.addChatMessage(player.name + " died.");
    }

    @SuppressWarnings("deprecation")
    private static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }
}
