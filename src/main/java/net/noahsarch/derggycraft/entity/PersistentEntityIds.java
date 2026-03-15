package net.noahsarch.derggycraft.entity;

import net.minecraft.entity.Entity;

import java.util.UUID;

public final class PersistentEntityIds {
    public static final String NBT_KEY = "DerggyPersistentEntityId";

    private PersistentEntityIds() {
    }

    public static String getExisting(Entity entity) {
        if (!(entity instanceof PersistentEntityIdAccess access)) {
            return null;
        }

        return access.derggycraft$getPersistentEntityId();
    }

    public static String getOrCreate(Entity entity) {
        if (!(entity instanceof PersistentEntityIdAccess access)) {
            return null;
        }

        String id = access.derggycraft$getPersistentEntityId();
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            access.derggycraft$setPersistentEntityId(id);
        }

        return id;
    }
}