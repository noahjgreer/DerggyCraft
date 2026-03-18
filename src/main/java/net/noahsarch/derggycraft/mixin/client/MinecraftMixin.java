package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.noahsarch.derggycraft.client.music.ClientMusicSyncManager;
import net.noahsarch.derggycraft.client.screen.DerggyCraftLogoScreen;
import net.noahsarch.derggycraft.client.screen.DerggyCraftUpdateScreen;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 2000)
public class MinecraftMixin {
    @Unique
    private int derggycraft$lastViewportWidth = -1;

    @Unique
    private int derggycraft$lastViewportHeight = -1;

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

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void derggycraft$tickMusicSyncScheduler(CallbackInfo ci) {
        ClientMusicSyncManager.tick((Minecraft) (Object) this);
        this.derggycraft$syncViewportToDisplay();
    }

    @Inject(method = "isCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void derggycraft$preventMultiplayerChatEcho(String command, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (minecraft.isWorldRemote() && command != null && !command.startsWith("/")) {
            // Let vanilla/network handling own chat dispatch in multiplayer.
            cir.setReturnValue(false);
        }
    }

    @Unique
    private void derggycraft$syncViewportToDisplay() {
        if (!Display.isCreated()) {
            return;
        }

        int width = Display.getWidth();
        int height = Display.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        if (width == this.derggycraft$lastViewportWidth && height == this.derggycraft$lastViewportHeight) {
            return;
        }

        GL11.glViewport(0, 0, width, height);
        this.derggycraft$lastViewportWidth = width;
        this.derggycraft$lastViewportHeight = height;
    }
}
