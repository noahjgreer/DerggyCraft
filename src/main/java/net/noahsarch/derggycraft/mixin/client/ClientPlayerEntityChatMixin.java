package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
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

        if (trimmed.startsWith("/")) {
            this.derggycraft$handleLocalCommand(player, trimmed);
            ci.cancel();
            return;
        }

        String formattedMessage = "<" + player.name + "> " + trimmed;

        if (this.minecraft != null && this.minecraft.inGameHud != null) {
            this.minecraft.inGameHud.addChatMessage(formattedMessage);
        }

        if (DerggyCraft.LOGGER != null) {
            DerggyCraft.LOGGER.info(formattedMessage);
        }

        ci.cancel();
    }

    private void derggycraft$handleLocalCommand(PlayerEntity player, String commandLine) {
        if (this.minecraft == null || this.minecraft.inGameHud == null) {
            return;
        }

        String[] args = commandLine.trim().split("\\s+");
        if (args.length == 0) {
            return;
        }

        String command = args[0].toLowerCase();
        if ("/help".equals(command) || "/?".equals(command)) {
            this.minecraft.inGameHud.addChatMessage("\u00a7eSingleplayer commands:");
            this.minecraft.inGameHud.addChatMessage("\u00a7e/help");
            this.minecraft.inGameHud.addChatMessage("\u00a7e/gamerule [rule] [true|false]");
            this.minecraft.inGameHud.addChatMessage("\u00a77Rules: sendDeathMessages, keepInventory, extinguishTorches");
            return;
        }

        if ("/gamerule".equals(command)) {
            DerggyCraftGameRules.ensureLoaded(player.world);

            if (args.length == 1) {
                this.minecraft.inGameHud.addChatMessage("\u00a7e" + DerggyCraftGameRules.formatAllRules());
                return;
            }

            DerggyCraftGameRules.Rule rule = DerggyCraftGameRules.parseRule(args[1]);
            if (rule == null) {
                this.minecraft.inGameHud.addChatMessage("\u00a7cUnknown gamerule. Available: sendDeathMessages, keepInventory, extinguishTorches");
                return;
            }

            if (args.length == 2) {
                this.minecraft.inGameHud.addChatMessage("\u00a7e" + DerggyCraftGameRules.formatRuleName(rule) + " = " + DerggyCraftGameRules.get(player.world, rule));
                return;
            }

            Boolean value = DerggyCraftGameRules.parseBoolean(args[2]);
            if (value == null) {
                this.minecraft.inGameHud.addChatMessage("\u00a7cInvalid value. Use true or false.");
                return;
            }

            DerggyCraftGameRules.set(player.world, rule, value);
            this.minecraft.inGameHud.addChatMessage("\u00a7aSet " + DerggyCraftGameRules.formatRuleName(rule) + " = " + value);
            return;
        }

        this.minecraft.inGameHud.addChatMessage("\u00a7cUnknown command. Try /help");
    }
}
