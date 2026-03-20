package net.noahsarch.derggycraft.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.noahsarch.derggycraft.item.GoldenCompassItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererGlowMixin {
    @Inject(method = "getOverlayColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void derggycraft$renderCompassGlowOverlay(LivingEntity entity, float brightness, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        Minecraft minecraft = getMinecraft();
        if (minecraft == null || minecraft.player == null || entity == null) {
            return;
        }

        PlayerEntity player = minecraft.player;
        if (entity == player) {
            return;
        }

        if (!GoldenCompassItem.shouldHighlightEntityForPlayer(player, entity)) {
            return;
        }

        int alpha = 0x8C;
        int red = 0x66;
        int green = 0xD6;
        int blue = 0xFF;
        cir.setReturnValue((alpha << 24) | (red << 16) | (green << 8) | blue);
    }

    @SuppressWarnings("deprecation")
    private static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }
}
