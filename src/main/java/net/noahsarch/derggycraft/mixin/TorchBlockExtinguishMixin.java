package net.noahsarch.derggycraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.world.World;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import net.noahsarch.derggycraft.world.TorchExtinguishTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TorchBlock.class)
public abstract class TorchBlockExtinguishMixin {
    private static final int DERGGYCRAFT$EXTINGUISH_DELAY_TICKS = 20;

    @Inject(method = "onPlaced(Lnet/minecraft/world/World;IIII)V", at = @At("TAIL"))
    private void derggycraft$scheduleExtinguishOnDirectionalPlacement(World world, int x, int y, int z, int direction, CallbackInfo ci) {
        this.derggycraft$scheduleIfEnabled(world, x, y, z);
    }

    @Inject(method = "onPlaced(Lnet/minecraft/world/World;III)V", at = @At("TAIL"))
    private void derggycraft$scheduleExtinguishOnPlacement(World world, int x, int y, int z, CallbackInfo ci) {
        this.derggycraft$scheduleIfEnabled(world, x, y, z);
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private void derggycraft$extinguishOnDueTick(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (world == null || world.isRemote || world.getBlockId(x, y, z) != Block.TORCH.id) {
            return;
        }

        DerggyCraftGameRules.ensureLoaded(world);
        if (!DerggyCraftGameRules.get(world, DerggyCraftGameRules.Rule.EXTINGUISH_TORCHES)) {
            TorchExtinguishTracker.clear(world, x, y, z);
            return;
        }

        if (!TorchExtinguishTracker.isDue(world, x, y, z)) {
            return;
        }

        if (DerggyCraft.EXTINGUISHED_TORCH_BLOCK == null) {
            return;
        }

        int meta = world.getBlockMeta(x, y, z);
        world.setBlock(x, y, z, DerggyCraft.EXTINGUISHED_TORCH_BLOCK.id);
        world.setBlockMeta(x, y, z, meta);
        ci.cancel();
    }

    private void derggycraft$scheduleIfEnabled(World world, int x, int y, int z) {
        if (world == null || world.isRemote || world.getBlockId(x, y, z) != Block.TORCH.id) {
            return;
        }

        DerggyCraftGameRules.ensureLoaded(world);
        if (!DerggyCraftGameRules.get(world, DerggyCraftGameRules.Rule.EXTINGUISH_TORCHES)) {
            TorchExtinguishTracker.clear(world, x, y, z);
            return;
        }

        TorchExtinguishTracker.schedule(world, x, y, z, DERGGYCRAFT$EXTINGUISH_DELAY_TICKS);
        world.scheduleBlockUpdate(x, y, z, Block.TORCH.id, DERGGYCRAFT$EXTINGUISH_DELAY_TICKS);
    }
}