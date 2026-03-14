package net.derggy.craft.derggycraft.mixin.server;

import net.derggy.craft.derggycraft.events.init.BlockInit;
import net.derggy.craft.derggycraft.gamerule.GameruleManager;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Handles torch extinguishing when the gamerule is enabled.
 * Schedules a tick when a torch is placed and replaces it with an extinguished torch.
 */
@Mixin(TorchBlock.class)
public class TorchPlacementMixin {

    /**
     * Schedule torch extinguish after placement (directional placement method).
     */
    @Inject(method = "onPlaced(Lnet/minecraft/world/World;IIII)V", at = @At("TAIL"))
    private void scheduleTorchExtinguish(World world, int x, int y, int z, int direction, CallbackInfo ci) {
        if (GameruleManager.getExtinguishTorches() && !world.isRemote) {
            world.scheduleBlockUpdate(x, y, z, Block.TORCH.id, 20);
        }
    }

    /**
     * Handle the scheduled tick to extinguish the torch.
     */
    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private void handleExtinguishTick(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (GameruleManager.getExtinguishTorches() && !world.isRemote) {
            int meta = world.getBlockMeta(x, y, z);
            world.setBlock(x, y, z, BlockInit.EXTINGUISHED_TORCH.id, meta);
            world.playSound(x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5f, 2.6f);
            ci.cancel();
        }
    }
}
