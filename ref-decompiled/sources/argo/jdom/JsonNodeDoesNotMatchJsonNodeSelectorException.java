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
public class JsonNodeDoesNotMatchJsonNodeSelectorException
extends IllegalArgumentException {
    JsonNodeDoesNotMatchJsonNodeSelectorException(String string) {
        super(string);
    }
}

