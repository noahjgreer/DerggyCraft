package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.mob.MonsterEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MonsterEntity.class)
public abstract class MonsterEntitySpawnLightMixin {
    @Inject(method = "canSpawn", at = @At("RETURN"), cancellable = true)
    private void derggycraft$requireBlockLightZero(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        MonsterEntity monster = (MonsterEntity) (Object) this;
        int x = MathHelper.floor(monster.x);
        int y = MathHelper.floor(monster.boundingBox.minY);
        int z = MathHelper.floor(monster.z);
        cir.setReturnValue(monster.world.getBrightness(LightType.BLOCK, x, y, z) == 0);
    }
}