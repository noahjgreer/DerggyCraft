package net.noahsarch.derggycraft.mixin.server;

import net.minecraft.world.ServerWorld;
import net.noahsarch.derggycraft.server.gamerule.DerggyCraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldGamerulesInitMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void derggycraft$loadPersistentGamerules(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.dimension != null && world.dimension.id == 0) {
            DerggyCraftGameRules.ensureLoaded(world);
        }
    }
}