/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class WaterColors {
    private static int[] colorMap = new int[65536];

    public static void setColorMap(int[] colorMap) {
        WaterColors.colorMap = colorMap;
    }
}

