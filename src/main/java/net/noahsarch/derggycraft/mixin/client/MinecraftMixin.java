package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(
        method = "tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;isWorldRemote()Z",
            ordinal = 0
        )
    )
    private boolean redirectIsWorldRemote(Minecraft instance) {
        // return instance.isWorldRemote() || instance.isIntegratedServerRunning();
        return true;
    }
}
