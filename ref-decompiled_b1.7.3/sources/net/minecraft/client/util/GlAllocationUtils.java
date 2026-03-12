/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public class GlAllocationUtils {
    private static List DISPLAY_LISTS = new ArrayList();
    private static List TEXTURE_NAMES = new ArrayList();

    public static synchronized int generateDisplayLists(int range) {
        int n = GL11.glGenLists((int)range);
        DISPLAY_LISTS.add(n);
        DISPLAY_LISTS.add(range);
        return n;
    }

    public static synchronized void generateTextureNames(IntBuffer textures) {
        GL11.glGenTextures((IntBuffer)textures);
        for (int i = textures.position(); i < textures.limit(); ++i) {
            TEXTURE_NAMES.add(textures.get(i));
        }
    }

    public static synchronized void deleteDisplayLists(int index) {
        int n = DISPLAY_LISTS.indexOf(index);
        GL11.glDeleteLists((int)((Integer)DISPLAY_LISTS.get(n)), (int)((Integer)DISPLAY_LISTS.get(n + 1)));
        DISPLAY_LISTS.remove(n);
        DISPLAY_LISTS.remove(n);
    }

    public static synchronized void clear() {
        for (int i = 0; i < DISPLAY_LISTS.size(); i += 2) {
            GL11.glDeleteLists((int)((Integer)DISPLAY_LISTS.get(i)), (int)((Integer)DISPLAY_LISTS.get(i + 1)));
        }
        IntBuffer intBuffer = GlAllocationUtils.allocateIntBuffer(TEXTURE_NAMES.size());
        intBuffer.flip();
        GL11.glDeleteTextures((IntBuffer)intBuffer);
        for (int i = 0; i < TEXTURE_NAMES.size(); ++i) {
            intBuffer.put((Integer)TEXTURE_NAMES.get(i));
        }
        intBuffer.flip();
        GL11.glDeleteTextures((IntBuffer)intBuffer);
        DISPLAY_LISTS.clear();
        TEXTURE_NAMES.clear();
    }

    public static synchronized ByteBuffer allocateByteBuffer(int size) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        return byteBuffer;
    }

    public static IntBuffer allocateIntBuffer(int size) {
        return GlAllocationUtils.allocateByteBuffer(size << 2).asIntBuffer();
    }

    public static FloatBuffer allocateFloatBuffer(int size) {
        return GlAllocationUtils.allocateByteBuffer(size << 2).asFloatBuffer();
    }
}

