/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.ScreenScaler;

@Environment(value=EnvType.CLIENT)
public class VideoOptionsScreen
extends Screen {
    private Screen parent;
    protected String title = "Video Settings";
    private GameOptions options;
    private static Option[] VIDEO_OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ANAGLYPH, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ADVANCED_OPENGL};

    public VideoOptionsScreen(Screen parent, GameOptions options) {
        this.parent = parent;
        this.options = options;
    }

    public void init() {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        this.title = translationStorage.get("options.videoTitle");
        int n = 0;
        for (Option option : VIDEO_OPTIONS) {
            if (!option.isSlider()) {
                this.buttons.add(new OptionButtonWidget(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getString(option)));
            } else {
                this.buttons.add(new SliderWidget(option.getId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), option, this.options.getString(option), this.options.getFloat(option)));
            }
            ++n;
        }
        this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, translationStorage.get("gui.done")));
    }

    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }
        if (button.id < 100 && button instanceof OptionButtonWidget) {
            this.options.setInt(((OptionButtonWidget)button).getOption(), 1);
            button.text = this.options.getString(Option.getById(button.id));
        }
        if (button.id == 200) {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.parent);
        }
        ScreenScaler screenScaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
        int n = screenScaler.getScaledWidth();
        int n2 = screenScaler.getScaledHeight();
        this.init(this.minecraft, n, n2);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}

