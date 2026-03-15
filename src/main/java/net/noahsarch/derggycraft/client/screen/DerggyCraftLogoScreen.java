package net.noahsarch.derggycraft.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.sound.IntroLogoSound;
import org.lwjgl.opengl.GL11;

public class DerggyCraftLogoScreen extends Screen {
    private static final String ICON_TEXTURE_PATH = "/assets/derggycraft/icon.png";

    private static final double ICON_FADE_IN_START_SECONDS = 3.0;
    private static final double ICON_FADE_DURATION_SECONDS = 0.85;
    private static final double ICON_FADE_OUT_START_SECONDS = 9.0;

    private static final double TITLE_TRANSITION_START_SECONDS = 12.0;
    private static final double TITLE_TRANSITION_DURATION_SECONDS = 0.85;

    private static final float JITTER_X_AMPLITUDE_PIXELS = 0.75F;
    private static final float JITTER_Y_AMPLITUDE_PIXELS = 0.5F;
    private static final float JITTER_PRIMARY_FREQUENCY_HZ = 1F;
    private static final float JITTER_SECONDARY_FREQUENCY_HZ = 1.75F;
    private static final float JITTER_SECONDARY_WEIGHT = 0.35F;
    private static final float JITTER_PHASE_OFFSET_RADIANS = 1.2F;

    private static final double FLASH_ONE_START_SECONDS = 5.8;
    private static final double FLASH_DURATION_SECONDS = 0.3;
    private static final double FLASH_GAP_SECONDS = 0.3;
    private static final float FLASH_ATTACK_RATIO = 0.78F;
    private static final float FLASH_INTENSITY = 1.0F;

    private static final double FINAL_FLASH_START_SECONDS = ICON_FADE_OUT_START_SECONDS - 0.5;
    private static final double FINAL_FLASH_DURATION_SECONDS = 0.5;
    private static final float FINAL_FLASH_ATTACK_RATIO = 0.72F;
    private static final float FINAL_FLASH_INTENSITY = 1.0F;

    private static final int EXPOSURE_LAYER_COUNT = 3;
    private static final float EXPOSURE_LAYER_DECAY = 0.72F;

    private static boolean introSoundPlayed;
    private static long introSoundStartNanos;

    private final Screen nextScreen;
    private long soundStartNanos;
    private boolean introSoundStarted;
    private boolean nextScreenInitialized;
    private int iconTextureId = -1;

    public DerggyCraftLogoScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void init() {
        this.soundStartNanos = this.resolveSharedSoundStartTime();
        this.startIntroSound();
        this.iconTextureId = this.minecraft.textureManager.getTextureId(ICON_TEXTURE_PATH);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (!this.introSoundStarted) {
            this.startIntroSound();
        }

        double elapsedSeconds = this.getElapsedSeconds();

        if (elapsedSeconds >= TITLE_TRANSITION_START_SECONDS) {
            this.ensureNextScreenInitialized();
            this.nextScreen.render(mouseX, mouseY, delta);

            float transitionProgress = clamp01((float) ((elapsedSeconds - TITLE_TRANSITION_START_SECONDS) / TITLE_TRANSITION_DURATION_SECONDS));
            float blackAlpha = 1.0F - transitionProgress;
            this.fillBlackOverlay(blackAlpha);

            if (transitionProgress >= 1.0F) {
                this.minecraft.setScreen(this.nextScreen);
            }
            return;
        }

        this.fillBlackOverlay(1.0F);

        float iconAlpha = this.computeIconAlpha(elapsedSeconds);
        if (iconAlpha > 0.0F) {
            int iconSize = Math.max(128, Math.min(256, Math.min(this.width, this.height) / 3));
            float jitterX = this.computeJitterX(elapsedSeconds);
            float jitterY = this.computeJitterY(elapsedSeconds);
            int x = (this.width - iconSize) / 2 + Math.round(jitterX);
            int y = (this.height - iconSize) / 2 + Math.round(jitterY);

            this.renderIcon(x, y, iconSize, iconAlpha, 770, 771);

            float exposureAlpha = this.computeExposureAlpha(elapsedSeconds);
            if (exposureAlpha > 0.0F) {
                this.renderExposure(x, y, iconSize, iconAlpha, exposureAlpha);
            }
        }
    }

    private void renderExposure(int x, int y, int iconSize, float iconAlpha, float exposureAlpha) {
        float layerAlpha = clamp01(iconAlpha * exposureAlpha);
        for (int i = 0; i < EXPOSURE_LAYER_COUNT && layerAlpha > 0.01F; ++i) {
            this.renderIcon(x, y, iconSize, layerAlpha, 770, 1);
            layerAlpha *= EXPOSURE_LAYER_DECAY;
        }
    }

    private void startIntroSound() {
        if (!introSoundPlayed) {
            this.minecraft.soundManager.playSound(IntroLogoSound.PLAYBACK_ID, 0.0F, 0.0F, 0.0F, 2.0F, 1.0F);
            introSoundPlayed = true;
            if (DerggyCraft.LOGGER != null) {
                DerggyCraft.LOGGER.info("Triggering intro logo sound playback id {}", IntroLogoSound.PLAYBACK_ID);
            }
        }

        this.introSoundStarted = true;
    }

    private long resolveSharedSoundStartTime() {
        if (!introSoundPlayed) {
            introSoundStartNanos = System.nanoTime();
        }

        return introSoundStartNanos;
    }

    private double getElapsedSeconds() {
        return (System.nanoTime() - this.soundStartNanos) / 1_000_000_000.0;
    }

    private float computeIconAlpha(double elapsedSeconds) {
        if (elapsedSeconds < ICON_FADE_IN_START_SECONDS) {
            return 0.0F;
        }

        if (elapsedSeconds < ICON_FADE_IN_START_SECONDS + ICON_FADE_DURATION_SECONDS) {
            return clamp01((float) ((elapsedSeconds - ICON_FADE_IN_START_SECONDS) / ICON_FADE_DURATION_SECONDS));
        }

        if (elapsedSeconds < ICON_FADE_OUT_START_SECONDS) {
            return 1.0F;
        }

        if (elapsedSeconds < ICON_FADE_OUT_START_SECONDS + ICON_FADE_DURATION_SECONDS) {
            float fadeOut = (float) ((elapsedSeconds - ICON_FADE_OUT_START_SECONDS) / ICON_FADE_DURATION_SECONDS);
            return 1.0F - clamp01(fadeOut);
        }

        return 0.0F;
    }

    private void renderIcon(int x, int y, int iconSize, float alpha, int blendSrc, int blendDst) {
        if (alpha <= 0.0F) {
            return;
        }

        GL11.glEnable(3042);
        GL11.glBlendFunc(blendSrc, blendDst);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        GL11.glBindTexture(3553, this.iconTextureId);

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + iconSize, this.zOffset, 0.0, 1.0);
        tessellator.vertex(x + iconSize, y + iconSize, this.zOffset, 1.0, 1.0);
        tessellator.vertex(x + iconSize, y, this.zOffset, 1.0, 0.0);
        tessellator.vertex(x, y, this.zOffset, 0.0, 0.0);
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    private float computeJitterX(double elapsedSeconds) {
        double primary = Math.sin(2.0 * Math.PI * JITTER_PRIMARY_FREQUENCY_HZ * elapsedSeconds);
        double secondary = Math.sin(2.0 * Math.PI * JITTER_SECONDARY_FREQUENCY_HZ * elapsedSeconds + JITTER_PHASE_OFFSET_RADIANS);
        return (float) ((primary + secondary * JITTER_SECONDARY_WEIGHT) * JITTER_X_AMPLITUDE_PIXELS);
    }

    private float computeJitterY(double elapsedSeconds) {
        double primary = Math.cos(2.0 * Math.PI * JITTER_PRIMARY_FREQUENCY_HZ * elapsedSeconds + JITTER_PHASE_OFFSET_RADIANS * 0.5);
        double secondary = Math.cos(2.0 * Math.PI * JITTER_SECONDARY_FREQUENCY_HZ * elapsedSeconds);
        return (float) ((primary + secondary * JITTER_SECONDARY_WEIGHT) * JITTER_Y_AMPLITUDE_PIXELS);
    }

    private float computeExposureAlpha(double elapsedSeconds) {
        float first = computePulse(
                elapsedSeconds,
                FLASH_ONE_START_SECONDS,
                FLASH_DURATION_SECONDS,
                FLASH_ATTACK_RATIO,
                FLASH_INTENSITY
        );

        float second = computePulse(
                elapsedSeconds,
                FLASH_ONE_START_SECONDS + FLASH_DURATION_SECONDS + FLASH_GAP_SECONDS,
                FLASH_DURATION_SECONDS,
                FLASH_ATTACK_RATIO,
                FLASH_INTENSITY
        );

        float finalBurst = computePulse(
                elapsedSeconds,
                FINAL_FLASH_START_SECONDS,
                FINAL_FLASH_DURATION_SECONDS,
                FINAL_FLASH_ATTACK_RATIO,
                FINAL_FLASH_INTENSITY
        );

        return Math.max(first, Math.max(second, finalBurst));
    }

    private static float computePulse(double elapsedSeconds, double startSeconds, double durationSeconds, float attackRatio, float intensity) {
        if (elapsedSeconds < startSeconds || elapsedSeconds > startSeconds + durationSeconds) {
            return 0.0F;
        }

        float progress = clamp01((float) ((elapsedSeconds - startSeconds) / durationSeconds));
        float attack = clamp01(attackRatio);
        float curve;

        if (progress <= attack) {
            float attackProgress = clamp01(progress / Math.max(attack, 0.0001F));
            curve = easeInExpo(attackProgress);
        } else {
            float releaseProgress = clamp01((progress - attack) / Math.max(1.0F - attack, 0.0001F));
            curve = (float) Math.exp(-8.0F * releaseProgress);
        }

        return clamp01(intensity * curve);
    }

    private static float easeInExpo(float t) {
        if (t <= 0.0F) {
            return 0.0F;
        }
        if (t >= 1.0F) {
            return 1.0F;
        }
        return (float) Math.pow(2.0, 10.0 * (t - 1.0F));
    }

    private void fillBlackOverlay(float alpha) {
        int overlayAlpha = (int) (clamp01(alpha) * 255.0F);
        int color = overlayAlpha << 24;
        this.fill(0, 0, this.width, this.height, color);
    }

    private void ensureNextScreenInitialized() {
        if (this.nextScreenInitialized) {
            return;
        }

        this.nextScreen.init(this.minecraft, this.width, this.height);
        this.nextScreenInitialized = true;
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }
}