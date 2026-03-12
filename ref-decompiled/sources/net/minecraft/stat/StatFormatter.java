/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.stat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface StatFormatter {
    @Environment(value=EnvType.CLIENT)
    public String format(int var1);
}

