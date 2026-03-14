package net.derggy.craft.derggycraft.mixin.server;

import net.derggy.craft.derggycraft.network.GoldenCompassTracker;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks into server tick to update golden compass tracking.
 */
@Mixin(MinecraftServer.class)
public class ServerTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onServerTick(CallbackInfo ci) {
        GoldenCompassTracker.onServerTick((MinecraftServer) (Object) this);
    }
}
