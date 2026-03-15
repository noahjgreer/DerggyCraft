package net.noahsarch.derggycraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.noahsarch.derggycraft.entity.PersistentEntityIdAccess;
import net.noahsarch.derggycraft.entity.PersistentEntityIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityPersistentIdMixin implements PersistentEntityIdAccess {
    @Unique
    private String derggycraft$persistentEntityId;

    @Inject(method = "read(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void derggycraft$readPersistentEntityId(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(PersistentEntityIds.NBT_KEY)) {
            this.derggycraft$persistentEntityId = nbt.getString(PersistentEntityIds.NBT_KEY);
        }
    }

    @Inject(method = "write(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void derggycraft$writePersistentEntityId(NbtCompound nbt, CallbackInfo ci) {
        String id = PersistentEntityIds.getOrCreate((Entity) (Object) this);
        if (id != null && !id.isEmpty()) {
            nbt.putString(PersistentEntityIds.NBT_KEY, id);
        }
    }

    @Override
    public String derggycraft$getPersistentEntityId() {
        return this.derggycraft$persistentEntityId;
    }

    @Override
    public void derggycraft$setPersistentEntityId(String persistentEntityId) {
        this.derggycraft$persistentEntityId = persistentEntityId;
    }
}