/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

@Environment(value=EnvType.SERVER)
public interface PlayerSaveHandler {
    public void savePlayerData(PlayerEntity var1);

    public void loadPlayerData(PlayerEntity var1);
}

