package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerChatMixin {
    @ModifyConstant(method = "onChatMessage", constant = @Constant(intValue = 100), require = 0)
    private int derggycraft$expandServerChatLengthLimit(int original) {
        return 32767;
    }
}
