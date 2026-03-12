/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum JsonNodeType {
    OBJECT,
    ARRAY,
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL;

}

