/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GLContext
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GLContext;

@Environment(value=EnvType.CLIENT)
public class OpenGlCapabilities {
    private static boolean USE_OCCLUSION_QUERY = true;

    public boolean glArbOcclusionQuery() {
        return USE_OCCLUSION_QUERY && GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
}

