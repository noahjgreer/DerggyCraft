package net.noahsarch.derggycraft.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.render.Tessellator;
import net.minecraft.item.ItemStack;
import net.noahsarch.derggycraft.DerggyCraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FlareThrownModelRenderer {
    private static final int MODEL_VARIANT_COUNT = 2;

    private static final String[] PRIMARY_MODEL_PATHS = new String[]{
            "/assets/derggycraft/stationapi/models/entity/flare0.json",
            "/assets/derggycraft/stationapi/models/entity/flare1.json"
    };

    private static final String[] FALLBACK_MODEL_PATHS = new String[]{
            "/assets/derggycraft/stationapi/models/entity/flare/flare0.json",
            "/assets/derggycraft/stationapi/models/entity/flare/flare1.json"
    };

    private static final String[] TEXTURE_PATHS = new String[]{
            "/assets/derggycraft/stationapi/textures/entity/flare0.png",
            "/assets/derggycraft/stationapi/textures/entity/flare1.png"
    };

    private static final ModelData[] MODELS = new ModelData[MODEL_VARIANT_COUNT];
    private static final boolean[] LOADED = new boolean[MODEL_VARIANT_COUNT];

    private FlareThrownModelRenderer() {
    }

    public static String getTexturePath(ItemStack stack) {
        return TEXTURE_PATHS[variantIndex(stack)];
    }

    public static void renderVariant(ItemStack stack) {
        int variant = variantIndex(stack);
        ModelData model = getModel(variant);
        if (model == null) {
            renderFallback();
            return;
        }

        for (ModelElement element : model.elements) {
            renderElement(element);
        }
    }

    private static int variantIndex(ItemStack stack) {
        if (stack == null) {
            return 0;
        }

        if (DerggyCraft.FLARE_10M_ITEM != null && stack.itemId == DerggyCraft.FLARE_10M_ITEM.id) {
            return 1;
        }

        if (DerggyCraft.FLARE_1M_ITEM != null && stack.itemId == DerggyCraft.FLARE_1M_ITEM.id) {
            return 0;
        }

        return stack.getDamage() == 1 ? 1 : 0;
    }

    private static ModelData getModel(int variant) {
        if (!LOADED[variant]) {
            MODELS[variant] = loadModel(PRIMARY_MODEL_PATHS[variant]);
            if (MODELS[variant] == null) {
                MODELS[variant] = loadModel(FALLBACK_MODEL_PATHS[variant]);
            }
            LOADED[variant] = true;
        }

        return MODELS[variant];
    }

    private static ModelData loadModel(String path) {
        try (InputStream stream = FlareThrownModelRenderer.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray elementsJson = root.getAsJsonArray("elements");
            if (elementsJson == null || elementsJson.size() == 0) {
                return null;
            }

            List<ModelElement> elements = new ArrayList<>();
            for (JsonElement elementJson : elementsJson) {
                JsonObject elementObject = elementJson.getAsJsonObject();
                float[] from = readVec3(elementObject.getAsJsonArray("from"));
                float[] to = readVec3(elementObject.getAsJsonArray("to"));
                if (from == null || to == null) {
                    continue;
                }

                Map<String, float[]> faceUvs = new HashMap<>();
                JsonObject faces = elementObject.getAsJsonObject("faces");
                if (faces != null) {
                    for (Map.Entry<String, JsonElement> entry : faces.entrySet()) {
                        JsonObject face = entry.getValue().getAsJsonObject();
                        JsonArray uvArray = face.getAsJsonArray("uv");
                        if (uvArray == null || uvArray.size() != 4) {
                            continue;
                        }

                        faceUvs.put(entry.getKey(), new float[]{
                                uvArray.get(0).getAsFloat(),
                                uvArray.get(1).getAsFloat(),
                                uvArray.get(2).getAsFloat(),
                                uvArray.get(3).getAsFloat()
                        });
                    }
                }

                if (!faceUvs.isEmpty()) {
                    elements.add(new ModelElement(from, to, faceUvs));
                }
            }

            if (elements.isEmpty()) {
                return null;
            }

            return new ModelData(elements);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static float[] readVec3(JsonArray array) {
        if (array == null || array.size() != 3) {
            return null;
        }

        return new float[]{
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        };
    }

    private static void renderElement(ModelElement element) {
        float x1 = toWorldX(element.from[0]);
        float y1 = toWorldY(element.from[1]);
        float z1 = toWorldZ(element.from[2]);
        float x2 = toWorldX(element.to[0]);
        float y2 = toWorldY(element.to[1]);
        float z2 = toWorldZ(element.to[2]);

        float minX = Math.min(x1, x2);
        float minY = Math.min(y1, y2);
        float minZ = Math.min(z1, z2);
        float maxX = Math.max(x1, x2);
        float maxY = Math.max(y1, y2);
        float maxZ = Math.max(z1, z2);

        for (Map.Entry<String, float[]> face : element.faceUvs.entrySet()) {
            drawFace(face.getKey(), face.getValue(), minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    private static float toWorldX(float modelX) {
        return modelX / 16.0F - 0.0625F;
    }

    private static float toWorldY(float modelY) {
        return modelY / 16.0F;
    }

    private static float toWorldZ(float modelZ) {
        return modelZ / 16.0F - 0.0625F;
    }

    private static void drawFace(String direction, float[] uv, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        float u1 = uv[0] / 16.0F;
        float v1 = uv[1] / 16.0F;
        float u2 = uv[2] / 16.0F;
        float v2 = uv[3] / 16.0F;

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();

        switch (direction) {
            case "north" -> {
                tessellator.normal(0.0F, 0.0F, -1.0F);
                tessellator.vertex(minX, maxY, minZ, u1, v1);
                tessellator.vertex(maxX, maxY, minZ, u2, v1);
                tessellator.vertex(maxX, minY, minZ, u2, v2);
                tessellator.vertex(minX, minY, minZ, u1, v2);
            }
            case "south" -> {
                tessellator.normal(0.0F, 0.0F, 1.0F);
                tessellator.vertex(maxX, maxY, maxZ, u1, v1);
                tessellator.vertex(minX, maxY, maxZ, u2, v1);
                tessellator.vertex(minX, minY, maxZ, u2, v2);
                tessellator.vertex(maxX, minY, maxZ, u1, v2);
            }
            case "east" -> {
                tessellator.normal(1.0F, 0.0F, 0.0F);
                tessellator.vertex(maxX, maxY, minZ, u1, v1);
                tessellator.vertex(maxX, maxY, maxZ, u2, v1);
                tessellator.vertex(maxX, minY, maxZ, u2, v2);
                tessellator.vertex(maxX, minY, minZ, u1, v2);
            }
            case "west" -> {
                tessellator.normal(-1.0F, 0.0F, 0.0F);
                tessellator.vertex(minX, maxY, maxZ, u1, v1);
                tessellator.vertex(minX, maxY, minZ, u2, v1);
                tessellator.vertex(minX, minY, minZ, u2, v2);
                tessellator.vertex(minX, minY, maxZ, u1, v2);
            }
            case "up" -> {
                tessellator.normal(0.0F, 1.0F, 0.0F);
                tessellator.vertex(minX, maxY, maxZ, u1, v1);
                tessellator.vertex(maxX, maxY, maxZ, u2, v1);
                tessellator.vertex(maxX, maxY, minZ, u2, v2);
                tessellator.vertex(minX, maxY, minZ, u1, v2);
            }
            case "down" -> {
                tessellator.normal(0.0F, -1.0F, 0.0F);
                tessellator.vertex(minX, minY, minZ, u1, v1);
                tessellator.vertex(maxX, minY, minZ, u2, v1);
                tessellator.vertex(maxX, minY, maxZ, u2, v2);
                tessellator.vertex(minX, minY, maxZ, u1, v2);
            }
            default -> {
            }
        }

        tessellator.draw();
    }

    private static void renderFallback() {
        Tessellator tessellator = Tessellator.INSTANCE;
        float minX = -0.0625F;
        float maxX = 0.0625F;
        float minY = 0.0F;
        float maxY = 0.35F;
        float minZ = -0.0625F;
        float maxZ = 0.0625F;

        drawFace("north", new float[]{0, 0, 2, 6}, minX, minY, minZ, maxX, maxY, maxZ);
        drawFace("south", new float[]{0, 0, 2, 6}, minX, minY, minZ, maxX, maxY, maxZ);
        drawFace("east", new float[]{0, 0, 2, 6}, minX, minY, minZ, maxX, maxY, maxZ);
        drawFace("west", new float[]{0, 0, 2, 6}, minX, minY, minZ, maxX, maxY, maxZ);
        drawFace("up", new float[]{0, 0, 2, 2}, minX, minY, minZ, maxX, maxY, maxZ);
        drawFace("down", new float[]{0, 0, 2, 2}, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private record ModelData(List<ModelElement> elements) {
    }

    private record ModelElement(float[] from, float[] to, Map<String, float[]> faceUvs) {
    }
}
