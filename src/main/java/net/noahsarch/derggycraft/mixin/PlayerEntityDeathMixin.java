package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDeathMixin {
    @Redirect(
            method = "onKilledBy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropInventory()V")
    )
    private void derggycraft$respectKeepInventoryInSingleplayer(PlayerInventory inventory) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        DerggyCraftGameRules.ensureLoaded(player.world);
        if (!DerggyCraftGameRules.get(DerggyCraftGameRules.Rule.KEEP_INVENTORY)) {
            inventory.dropInventory();
        }
    }
}
