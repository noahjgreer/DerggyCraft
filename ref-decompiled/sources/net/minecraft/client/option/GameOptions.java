/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.input.Keyboard
 */
package net.minecraft.client.option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Keyboard;

@Environment(value=EnvType.CLIENT)
public class GameOptions {
    private static final String[] RENDER_DISTANCE_KEYS = new String[]{"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
    private static final String[] DIFFICULTY_KEYS = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
    private static final String[] GUI_SCALE_KEYS = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PERFORMANCE_KEYS = new String[]{"performance.max", "performance.balanced", "performance.powersaver"};
    public float musicVolume = 1.0f;
    public float soundVolume = 1.0f;
    public float mouseSensitivity = 0.5f;
    public boolean invertYMouse = false;
    public int viewDistance = 0;
    public boolean bobView = true;
    public boolean anaglyph3d = false;
    public boolean advancedOpengl = false;
    public int fpsLimit = 1;
    public boolean fancyGraphics = true;
    public boolean ao = true;
    public String skin = "Default";
    public KeyBinding forwardKey = new KeyBinding("key.forward", 17);
    public KeyBinding leftKey = new KeyBinding("key.left", 30);
    public KeyBinding backKey = new KeyBinding("key.back", 31);
    public KeyBinding rightKey = new KeyBinding("key.right", 32);
    public KeyBinding jumpKey = new KeyBinding("key.jump", 57);
    public KeyBinding inventoryKey = new KeyBinding("key.inventory", 18);
    public KeyBinding dropKey = new KeyBinding("key.drop", 16);
    public KeyBinding chatKey = new KeyBinding("key.chat", 20);
    public KeyBinding fogKey = new KeyBinding("key.fog", 33);
    public KeyBinding sneakKey = new KeyBinding("key.sneak", 42);
    public KeyBinding[] allKeys = new KeyBinding[]{this.forwardKey, this.leftKey, this.backKey, this.rightKey, this.jumpKey, this.sneakKey, this.dropKey, this.inventoryKey, this.chatKey, this.fogKey};
    protected Minecraft minecraft;
    private File file;
    public int difficulty = 2;
    public boolean hideHud = false;
    public boolean thirdPerson = false;
    public boolean debugHud = false;
    public String lastServer = "";
    public boolean discreteScroll = false;
    public boolean cinematicMode = false;
    public boolean debugCamera = false;
    public float totalDiscreteScroll = 1.0f;
    public float field_1449 = 1.0f;
    public int guiScale = 0;

    public GameOptions(Minecraft minecraft, File file) {
        this.minecraft = minecraft;
        this.file = new File(file, "options.txt");
        this.load();
    }

    public GameOptions() {
    }

    public String getKeybindName(int index) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        return translationStorage.get(this.allKeys[index].translationKey);
    }

    public String getKeybindKey(int index) {
        return Keyboard.getKeyName((int)this.allKeys[index].code);
    }

    public void setKeybindKey(int index, int keyCode) {
        this.allKeys[index].code = keyCode;
        this.save();
    }

    public void setFloat(Option option, float value) {
        if (option == Option.MUSIC) {
            this.musicVolume = value;
            this.minecraft.soundManager.updateMusicVolume();
        }
        if (option == Option.SOUND) {
            this.soundVolume = value;
            this.minecraft.soundManager.updateMusicVolume();
        }
        if (option == Option.SENSITIVITY) {
            this.mouseSensitivity = value;
        }
    }

    public void setInt(Option option, int value) {
        if (option == Option.INVERT_MOUSE) {
            boolean bl = this.invertYMouse = !this.invertYMouse;
        }
        if (option == Option.RENDER_DISTANCE) {
            this.viewDistance = this.viewDistance + value & 3;
        }
        if (option == Option.GUI_SCALE) {
            this.guiScale = this.guiScale + value & 3;
        }
        if (option == Option.VIEW_BOBBING) {
            boolean bl = this.bobView = !this.bobView;
        }
        if (option == Option.ADVANCED_OPENGL) {
            this.advancedOpengl = !this.advancedOpengl;
            this.minecraft.worldRenderer.reload();
        }
        if (option == Option.ANAGLYPH) {
            this.anaglyph3d = !this.anaglyph3d;
            this.minecraft.textureManager.reload();
        }
        if (option == Option.FRAMERATE_LIMIT) {
            this.fpsLimit = (this.fpsLimit + value + 3) % 3;
        }
        if (option == Option.DIFFICULTY) {
            this.difficulty = this.difficulty + value & 3;
        }
        if (option == Option.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.minecraft.worldRenderer.reload();
        }
        if (option == Option.AMBIENT_OCCLUSION) {
            this.ao = !this.ao;
            this.minecraft.worldRenderer.reload();
        }
        this.save();
    }

    public float getFloat(Option option) {
        if (option == Option.MUSIC) {
            return this.musicVolume;
        }
        if (option == Option.SOUND) {
            return this.soundVolume;
        }
        if (option == Option.SENSITIVITY) {
            return this.mouseSensitivity;
        }
        return 0.0f;
    }

    public boolean getBoolean(Option option) {
        switch (option) {
            case INVERT_MOUSE: {
                return this.invertYMouse;
            }
            case VIEW_BOBBING: {
                return this.bobView;
            }
            case ANAGLYPH: {
                return this.anaglyph3d;
            }
            case ADVANCED_OPENGL: {
                return this.advancedOpengl;
            }
            case AMBIENT_OCCLUSION: {
                return this.ao;
            }
        }
        return false;
    }

    public String getString(Option option) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        String string = translationStorage.get(option.getKey()) + ": ";
        if (option.isSlider()) {
            float f = this.getFloat(option);
            if (option == Option.SENSITIVITY) {
                if (f == 0.0f) {
                    return string + translationStorage.get("options.sensitivity.min");
                }
                if (f == 1.0f) {
                    return string + translationStorage.get("options.sensitivity.max");
                }
                return string + (int)(f * 200.0f) + "%";
            }
            if (f == 0.0f) {
                return string + translationStorage.get("options.off");
            }
            return string + (int)(f * 100.0f) + "%";
        }
        if (option.isToggle()) {
            boolean bl = this.getBoolean(option);
            if (bl) {
                return string + translationStorage.get("options.on");
            }
            return string + translationStorage.get("options.off");
        }
        if (option == Option.RENDER_DISTANCE) {
            return string + translationStorage.get(RENDER_DISTANCE_KEYS[this.viewDistance]);
        }
        if (option == Option.DIFFICULTY) {
            return string + translationStorage.get(DIFFICULTY_KEYS[this.difficulty]);
        }
        if (option == Option.GUI_SCALE) {
            return string + translationStorage.get(GUI_SCALE_KEYS[this.guiScale]);
        }
        if (option == Option.FRAMERATE_LIMIT) {
            return string + I18n.getTranslation(PERFORMANCE_KEYS[this.fpsLimit]);
        }
        if (option == Option.GRAPHICS) {
            if (this.fancyGraphics) {
                return string + translationStorage.get("options.graphics.fancy");
            }
            return string + translationStorage.get("options.graphics.fast");
        }
        return string;
    }

    public void load() {
        try {
            if (!this.file.exists()) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file));
            String string = "";
            while ((string = bufferedReader.readLine()) != null) {
                try {
                    String[] stringArray = string.split(":");
                    if (stringArray[0].equals("music")) {
                        this.musicVolume = this.parseFloat(stringArray[1]);
                    }
                    if (stringArray[0].equals("sound")) {
                        this.soundVolume = this.parseFloat(stringArray[1]);
                    }
                    if (stringArray[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(stringArray[1]);
                    }
                    if (stringArray[0].equals("invertYMouse")) {
                        this.invertYMouse = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("viewDistance")) {
                        this.viewDistance = Integer.parseInt(stringArray[1]);
                    }
                    if (stringArray[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(stringArray[1]);
                    }
                    if (stringArray[0].equals("bobView")) {
                        this.bobView = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("anaglyph3d")) {
                        this.anaglyph3d = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("advancedOpengl")) {
                        this.advancedOpengl = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("fpsLimit")) {
                        this.fpsLimit = Integer.parseInt(stringArray[1]);
                    }
                    if (stringArray[0].equals("difficulty")) {
                        this.difficulty = Integer.parseInt(stringArray[1]);
                    }
                    if (stringArray[0].equals("fancyGraphics")) {
                        this.fancyGraphics = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("ao")) {
                        this.ao = stringArray[1].equals("true");
                    }
                    if (stringArray[0].equals("skin")) {
                        this.skin = stringArray[1];
                    }
                    if (stringArray[0].equals("lastServer") && stringArray.length >= 2) {
                        this.lastServer = stringArray[1];
                    }
                    for (int i = 0; i < this.allKeys.length; ++i) {
                        if (!stringArray[0].equals("key_" + this.allKeys[i].translationKey)) continue;
                        this.allKeys[i].code = Integer.parseInt(stringArray[1]);
                    }
                }
                catch (Exception exception) {
                    System.out.println("Skipping bad option: " + string);
                }
            }
            bufferedReader.close();
        }
        catch (Exception exception) {
            System.out.println("Failed to load options");
            exception.printStackTrace();
        }
    }

    private float parseFloat(String string) {
        if (string.equals("true")) {
            return 1.0f;
        }
        if (string.equals("false")) {
            return 0.0f;
        }
        return Float.parseFloat(string);
    }

    public void save() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(this.file));
            printWriter.println("music:" + this.musicVolume);
            printWriter.println("sound:" + this.soundVolume);
            printWriter.println("invertYMouse:" + this.invertYMouse);
            printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printWriter.println("viewDistance:" + this.viewDistance);
            printWriter.println("guiScale:" + this.guiScale);
            printWriter.println("bobView:" + this.bobView);
            printWriter.println("anaglyph3d:" + this.anaglyph3d);
            printWriter.println("advancedOpengl:" + this.advancedOpengl);
            printWriter.println("fpsLimit:" + this.fpsLimit);
            printWriter.println("difficulty:" + this.difficulty);
            printWriter.println("fancyGraphics:" + this.fancyGraphics);
            printWriter.println("ao:" + this.ao);
            printWriter.println("skin:" + this.skin);
            printWriter.println("lastServer:" + this.lastServer);
            for (int i = 0; i < this.allKeys.length; ++i) {
                printWriter.println("key_" + this.allKeys[i].translationKey + ":" + this.allKeys[i].code);
            }
            printWriter.close();
        }
        catch (Exception exception) {
            System.out.println("Failed to save options");
            exception.printStackTrace();
        }
    }
}

