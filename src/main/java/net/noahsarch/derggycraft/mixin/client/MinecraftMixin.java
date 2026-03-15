package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.CompassSprite;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.Item;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.client.render.texture.NewCompassSprite;
import net.noahsarch.derggycraft.item.GoldenCompass;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @ModifyArg(
        method = "init()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/texture/TextureManager;addDynamicTexture(Lnet/minecraft/client/render/texture/DynamicTexture;)V"
        ),
        index = 0
    )
    private DynamicTexture derggycraft$replaceCompass(DynamicTexture original) {
        // Check if the original texture is the compass texture
        if (original instanceof CompassSprite) {
            return new NewCompassSprite((Minecraft)(Object)this, Item.COMPASS.getTextureId(0));
        }
        return original;
    }

    @Shadow public TextureManager textureManager;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void derggycraft$registerGoldenCompassDynamicTexture(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        // int goldenSprite = DerggyCraft.GOLDEN_COMPASS_ITEM.getTextureId(0);
        this.textureManager.addDynamicTexture(new NewCompassSprite(mc, DerggyCraft.GOLDEN_COMPASS_ITEM.getTextureId(0)));
    }
}
