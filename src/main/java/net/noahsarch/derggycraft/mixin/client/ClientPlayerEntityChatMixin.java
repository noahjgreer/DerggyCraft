package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.DerggyCraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityChatMixin {
    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void derggycraft$handleSingleplayerChat(String message, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
            return;
        }

        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            ci.cancel();
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        String formattedMessage = "<" + player.name + "> " + trimmed;

        if (this.minecraft != null && this.minecraft.inGameHud != null) {
            this.minecraft.inGameHud.addChatMessage(formattedMessage);
        }

        if (DerggyCraft.LOGGER != null) {
            DerggyCraft.LOGGER.info(formattedMessage);
        }

        ci.cancel();
    }
}
