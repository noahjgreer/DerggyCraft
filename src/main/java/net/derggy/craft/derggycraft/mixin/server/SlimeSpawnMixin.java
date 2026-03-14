package net.derggy.craft.derggycraft.mixin.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * Triples slime spawn rates by changing the 10% chance to 30%.
 */
@Mixin(SlimeEntity.class)
public abstract class SlimeSpawnMixin extends Entity {

    public SlimeSpawnMixin() {
        super(null);
    }

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private void tripleSpawnRate(CallbackInfoReturnable<Boolean> cir) {
        SlimeEntity self = (SlimeEntity) (Object) this;

        Chunk chunk = self.world.getChunkFromPos(
                MathHelper.floor(self.x),
                MathHelper.floor(self.z)
        );

        boolean canSpawn = (self.getSize() == 1 || self.world.difficulty > 0)
                && this.random.nextInt(10) < 3  // 30% instead of 10%
                && chunk.getSlimeRandom(987234911L).nextInt(10) == 0
                && self.y < 16.0;

        cir.setReturnValue(canSpawn);
    }
}
