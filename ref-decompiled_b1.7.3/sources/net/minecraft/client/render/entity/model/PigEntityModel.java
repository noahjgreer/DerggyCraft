/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;

@Environment(value=EnvType.CLIENT)
public class PigEntityModel
extends QuadrupedEntityModel {
    public PigEntityModel() {
        super(6, 0.0f);
    }

    public PigEntityModel(float dilation) {
        super(6, dilation);
    }
}

