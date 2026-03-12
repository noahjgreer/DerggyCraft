/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.OperatingSystem;

@Environment(value=EnvType.CLIENT)
public class class_375 {
    public static final /* synthetic */ int[] field_1426;

    static {
        field_1426 = new int[OperatingSystem.values().length];
        try {
            class_375.field_1426[OperatingSystem.LINUX.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            class_375.field_1426[OperatingSystem.SOLARIS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            class_375.field_1426[OperatingSystem.WINDOWS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            class_375.field_1426[OperatingSystem.MACOS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}

