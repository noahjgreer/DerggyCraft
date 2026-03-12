/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package argo.jdom;

import argo.jdom.Functor;
import argo.jdom.JsonNodeDoesNotMatchChainedJsonNodeSelectorException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
abstract class LeafFunctor
implements Functor {
    LeafFunctor() {
    }

    public final Object applyTo(Object object) {
        if (!this.matchesNode(object)) {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.createJsonNodeDoesNotMatchJsonNodeSelectorException(this);
        }
        return this.typeSafeApplyTo(object);
    }

    protected abstract Object typeSafeApplyTo(Object var1);
}

