package net.derggy.craft.derggycraft.mixin.server;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Allows chests to be placed on top of each other and opened
 * even when a chest is above them.
 */
@Mixin(ChestBlock.class)
public class ChestStackingMixin {

    /**
     * Redirect shouldSuffocate checks in onUse to allow chest blocks above.
     * When the block above is a chest, pretend it doesn't suffocate
     * so the chest can still be opened.
     */
    @Redirect(
            method = "onUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;shouldSuffocate(III)Z")
    )
    private boolean allowChestAbove(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) == Block.CHEST.id) {
            return false;
        }
        return world.shouldSuffocate(x, y, z);
    }
}
