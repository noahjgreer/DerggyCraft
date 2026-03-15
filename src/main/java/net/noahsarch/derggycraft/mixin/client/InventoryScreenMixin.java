package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen {
    private static final String DERGGYCRAFT_INVENTORY_TEXTURE = "/assets/derggycraft/stationapi/textures/gui/inventory.png";
    private static final int DERGGYCRAFT_VERTICAL_OFFSET = 18;

    protected InventoryScreenMixin(ScreenHandler handler) {
        super(handler);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void derggycraft$expandInventoryBounds(PlayerEntity player, CallbackInfo ci) {
        this.backgroundHeight += DERGGYCRAFT_VERTICAL_OFFSET;
    }

    @ModifyArg(
            method = "drawBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;getTextureId(Ljava/lang/String;)I"),
            index = 0
    )
    private String derggycraft$useCustomInventoryTexture(String originalPath) {
        return DERGGYCRAFT_INVENTORY_TEXTURE;
    }

    @ModifyConstant(method = "drawForeground", constant = @Constant(intValue = 16))
    private int derggycraft$moveCraftingLabelDown(int original) {
        return original + DERGGYCRAFT_VERTICAL_OFFSET;
    }

    @ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 75))
    private int derggycraft$movePlayerPreviewDown(int original) {
        return original + DERGGYCRAFT_VERTICAL_OFFSET;
    }
}