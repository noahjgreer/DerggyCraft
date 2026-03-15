package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.noahsarch.derggycraft.client.screen.DerggyCraftLogoScreen;
import net.noahsarch.derggycraft.client.screen.DerggyCraftUpdateScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(
        method = "tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;isWorldRemote()Z",
            ordinal = 0
        ),
        require = 0
    )
    private boolean redirectIsWorldRemote(Minecraft instance) {
        // return instance.isWorldRemote() || instance.isIntegratedServerRunning();
        return true;
    }

    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
        ),
        index = 0
    )
    private Screen derggycraft$injectStartupLogoScreen(Screen originalScreen) {
        if (originalScreen instanceof TitleScreen) {
            return new DerggyCraftLogoScreen(new DerggyCraftUpdateScreen(originalScreen));
        }

        return originalScreen;
    }
}
