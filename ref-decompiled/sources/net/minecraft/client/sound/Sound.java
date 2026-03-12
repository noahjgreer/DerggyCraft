/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import java.net.URL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Sound {
    public String id;
    public URL soundFile;

    public Sound(String id, URL soundFile) {
        this.id = id;
        this.soundFile = soundFile;
    }
}

