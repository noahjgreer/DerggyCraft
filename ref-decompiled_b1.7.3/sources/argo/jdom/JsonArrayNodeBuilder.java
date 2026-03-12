/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package argo.jdom;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeBuilder;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import java.util.LinkedList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class JsonArrayNodeBuilder
implements JsonNodeBuilder {
    private final List elementBuilders = new LinkedList();

    JsonArrayNodeBuilder() {
    }

    public JsonArrayNodeBuilder withElement(JsonNodeBuilder jsonNodeBuilder) {
        this.elementBuilders.add(jsonNodeBuilder);
        return this;
    }

    public JsonRootNode build() {
        LinkedList<JsonNode> linkedList = new LinkedList<JsonNode>();
        for (JsonNodeBuilder jsonNodeBuilder : this.elementBuilders) {
            linkedList.add(jsonNodeBuilder.build());
        }
        return JsonNodeFactories.aJsonArray(linkedList);
    }
}

