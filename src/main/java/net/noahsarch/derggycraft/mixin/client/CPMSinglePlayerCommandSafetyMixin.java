package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.tom.cpm.client.SinglePlayerCommands", remap = false)
public abstract class CPMSinglePlayerCommandSafetyMixin {
    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void derggycraft$guardMalformedGiveCommand(Minecraft mc, String command, CallbackInfo ci) {
        // CPM hooks Minecraft.isCommand on both SP/MP; block local command execution in MP to avoid ghost inventory state.
        if (mc != null && mc.isWorldRemote()) {
            ci.cancel();
            return;
        }

        if (command == null) {
            return;
        }

        String trimmed = command.trim();
        if (!trimmed.startsWith("/give")) {
            return;
        }

        // Fully handle /give so CPM's parser cannot crash on malformed input.
        ci.cancel();

        if (mc == null || mc.inGameHud == null || mc.player == null) {
            return;
        }

        String[] tokens = trimmed.split("\\s+");
        if (tokens.length < 2) {
            mc.inGameHud.addChatMessage("Usage: /give <id> [amount] [meta]");
            return;
        }

        int itemId;
        try {
            itemId = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException ignored) {
            mc.inGameHud.addChatMessage("There's no item with id " + tokens[1]);
            return;
        }

        if (itemId < 0 || itemId >= Item.ITEMS.length || Item.ITEMS[itemId] == null) {
            mc.inGameHud.addChatMessage("There's no item with id " + itemId);
            return;
        }

        int amount = 1;
        if (tokens.length > 2) {
            try {
                amount = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }

        int meta = 0;
        if (tokens.length > 3) {
            try {
                meta = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException ignored) {
                meta = 0;
            }
        }

        if (amount < 1) {
            amount = 1;
        }
        if (amount > 64) {
            amount = 64;
        }

        ItemStack stack = new ItemStack(itemId, amount, meta);
        mc.inGameHud.addChatMessage("Giving some " + itemId);
        if (!mc.player.inventory.addStack(stack)) {
            mc.player.dropItem(stack, false);
        }
    }
}
