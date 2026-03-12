/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum Option {
    MUSIC("options.music", true, false),
    SOUND("options.sound", true, false),
    INVERT_MOUSE("options.invertMouse", false, true),
    SENSITIVITY("options.sensitivity", true, false),
    RENDER_DISTANCE("options.renderDistance", false, false),
    VIEW_BOBBING("options.viewBobbing", false, true),
    ANAGLYPH("options.anaglyph", false, true),
    ADVANCED_OPENGL("options.advancedOpengl", false, true),
    FRAMERATE_LIMIT("options.framerateLimit", false, false),
    DIFFICULTY("options.difficulty", false, false),
    GRAPHICS("options.graphics", false, false),
    AMBIENT_OCCLUSION("options.ao", false, true),
    GUI_SCALE("options.guiScale", false, false);

    private final boolean slider;
    private final boolean toggle;
    private final String key;

    public static Option getById(int id) {
        for (Option option : Option.values()) {
            if (option.getId() != id) continue;
            return option;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private Option(boolean key) {
        void toggle;
        void slider;
        this.key = (String)key;
        this.slider = slider;
        this.toggle = toggle;
    }

    public boolean isSlider() {
        return this.slider;
    }

    public boolean isToggle() {
        return this.toggle;
    }

    public int getId() {
        return this.ordinal();
    }

    public String getKey() {
        return this.key;
    }
}

