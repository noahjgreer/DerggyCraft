package net.noahsarch.derggycraft.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.noahsarch.derggycraft.DerggyCraft;
import net.noahsarch.derggycraft.sound.IntroLogoSound;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class DerggyCraftLogoScreen extends Screen {
    private static final String ICON_TEXTURE_PATH = "/assets/derggycraft/intro.png";

    private static final int BLEND_ONE = 1;
    private static final int BLEND_SRC_ALPHA = 770;
    private static final int BLEND_ONE_MINUS_SRC_ALPHA = 771;

    private static final int PARAM_COUNT = 22;

    private static final double DEFAULT_ICON_FADE_IN_START_SECONDS = 3.0;
    private static final double DEFAULT_ICON_FADE_DURATION_SECONDS = 0.85;
    private static final double DEFAULT_ICON_FADE_OUT_START_SECONDS = 9.0;

    private static final double DEFAULT_TITLE_TRANSITION_START_SECONDS = 12.0;
    private static final double DEFAULT_TITLE_TRANSITION_DURATION_SECONDS = 0.85;

    private static final float DEFAULT_JITTER_X_AMPLITUDE_PIXELS = 0.40F;
    private static final float DEFAULT_JITTER_Y_AMPLITUDE_PIXELS = 0.20F;
    private static final float DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ = 1.5F;
    private static final float DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ = 1.25F;
    private static final float DEFAULT_JITTER_SECONDARY_WEIGHT = 0.35F;
    private static final float DEFAULT_JITTER_PHASE_OFFSET_RADIANS = 1.2F;

    private static final double DEFAULT_FLASH_ONE_START_SECONDS = 5.8;
    private static final double DEFAULT_FLASH_DURATION_SECONDS = 0.3;
    private static final double DEFAULT_FLASH_GAP_SECONDS = 0.3;
    private static final float DEFAULT_FLASH_ATTACK_RATIO = 0.78F;
    private static final float DEFAULT_FLASH_INTENSITY = 1.0F;

    private static final double DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS = 0.5;
    private static final double DEFAULT_FINAL_FLASH_DURATION_SECONDS = 0.5;
    private static final float DEFAULT_FINAL_FLASH_ATTACK_RATIO = 0.72F;
    private static final float DEFAULT_FINAL_FLASH_INTENSITY = 1.0F;

    private static final int DEFAULT_EXPOSURE_LAYER_COUNT = 10;
    private static final float DEFAULT_EXPOSURE_LAYER_DECAY = 0.78F;

    private static final float DEFAULT_CHROMA_SPLIT_PIXELS = 0.95F;
    private static final float DEFAULT_CHROMA_ALPHA = 0.30F;

    private static final float DEFAULT_TEAR_CHANCE = 0.02F;
    private static final float DEFAULT_TEAR_MAX_OFFSET_PIXELS = 10.10F;
    private static final float DEFAULT_TEAR_BAND_HEIGHT_PIXELS = 18.0F;

    private static final float DEFAULT_GHOST_ALPHA = 0.18F;
    private static final float DEFAULT_GHOST_OFFSET_X_PIXELS = 1.8F;
    private static final float DEFAULT_GHOST_OFFSET_Y_PIXELS = 0.8F;

    private static final float DEFAULT_BLOOM_ALPHA = 0.16F;
    private static final float DEFAULT_BLOOM_SCALE = 1.175F;
    private static final float DEFAULT_BLOOM_PULSE_HZ = 1.8F;
    private static final float DEFAULT_BLOOM_PULSE_AMOUNT = 0.4F;

    private static final int DEFAULT_SCANLINE_SPACING_PIXELS = 2;
    private static final float DEFAULT_SCANLINE_ALPHA = 0.20F;
    private static final float DEFAULT_SCANLINE_SCROLL_SPEED = 17.0F;

    private static final float DEFAULT_NOISE_ALPHA = 0.11F;
    private static final float DEFAULT_NOISE_DENSITY = 0.14F;
    private static final int DEFAULT_NOISE_MAX_BLOCK_SIZE = 3;

    private static final double DEFAULT_GHOST_BURST_DURATION_SECONDS = 0.325;
    private static final float DEFAULT_GHOST_BURST_INTENSITY = 1.00F;
    private static final float DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS = 13.20F;
    private static final float DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS = 20.40F;
    private static final double DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS = 0.08;

    private static final double DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS = 0.5;
    private static final double DEFAULT_SMEAR_BURST_DURATION_SECONDS = 0.40;
    private static final float DEFAULT_SMEAR_BURST_INTENSITY = 0.52F;

    private static final double DEBUG_LOOP_DURATION_SECONDS = DEFAULT_TITLE_TRANSITION_START_SECONDS + DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;

    private static double iconFadeInStartSeconds = DEFAULT_ICON_FADE_IN_START_SECONDS;
    private static double iconFadeDurationSeconds = DEFAULT_ICON_FADE_DURATION_SECONDS;
    private static double iconFadeOutStartSeconds = DEFAULT_ICON_FADE_OUT_START_SECONDS;

    private static double titleTransitionStartSeconds = DEFAULT_TITLE_TRANSITION_START_SECONDS;
    private static double titleTransitionDurationSeconds = DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;

    private static float jitterXAmplitudePixels = DEFAULT_JITTER_X_AMPLITUDE_PIXELS;
    private static float jitterYAmplitudePixels = DEFAULT_JITTER_Y_AMPLITUDE_PIXELS;
    private static float jitterPrimaryFrequencyHz = DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ;
    private static float jitterSecondaryFrequencyHz = DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ;
    private static float jitterSecondaryWeight = DEFAULT_JITTER_SECONDARY_WEIGHT;
    private static float jitterPhaseOffsetRadians = DEFAULT_JITTER_PHASE_OFFSET_RADIANS;

    private static double flashOneStartSeconds = DEFAULT_FLASH_ONE_START_SECONDS;
    private static double flashDurationSeconds = DEFAULT_FLASH_DURATION_SECONDS;
    private static double flashGapSeconds = DEFAULT_FLASH_GAP_SECONDS;
    private static float flashAttackRatio = DEFAULT_FLASH_ATTACK_RATIO;
    private static float flashIntensity = DEFAULT_FLASH_INTENSITY;

    private static double finalFlashOffsetFromFadeOutSeconds = DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS;
    private static double finalFlashDurationSeconds = DEFAULT_FINAL_FLASH_DURATION_SECONDS;
    private static float finalFlashAttackRatio = DEFAULT_FINAL_FLASH_ATTACK_RATIO;
    private static float finalFlashIntensity = DEFAULT_FINAL_FLASH_INTENSITY;

    private static int exposureLayerCount = DEFAULT_EXPOSURE_LAYER_COUNT;
    private static float exposureLayerDecay = DEFAULT_EXPOSURE_LAYER_DECAY;

    private static float chromaSplitPixels = DEFAULT_CHROMA_SPLIT_PIXELS;
    private static float chromaAlpha = DEFAULT_CHROMA_ALPHA;

    private static float tearChance = DEFAULT_TEAR_CHANCE;
    private static float tearMaxOffsetPixels = DEFAULT_TEAR_MAX_OFFSET_PIXELS;
    private static float tearBandHeightPixels = DEFAULT_TEAR_BAND_HEIGHT_PIXELS;

    private static float ghostAlpha = DEFAULT_GHOST_ALPHA;
    private static float ghostOffsetXPixels = DEFAULT_GHOST_OFFSET_X_PIXELS;
    private static float ghostOffsetYPixels = DEFAULT_GHOST_OFFSET_Y_PIXELS;

    private static float bloomAlpha = DEFAULT_BLOOM_ALPHA;
    private static float bloomScale = DEFAULT_BLOOM_SCALE;
    private static float bloomPulseHz = DEFAULT_BLOOM_PULSE_HZ;
    private static float bloomPulseAmount = DEFAULT_BLOOM_PULSE_AMOUNT;

    private static int scanlineSpacingPixels = DEFAULT_SCANLINE_SPACING_PIXELS;
    private static float scanlineAlpha = DEFAULT_SCANLINE_ALPHA;
    private static float scanlineScrollSpeed = DEFAULT_SCANLINE_SCROLL_SPEED;

    private static float noiseAlpha = DEFAULT_NOISE_ALPHA;
    private static float noiseDensity = DEFAULT_NOISE_DENSITY;
    private static int noiseMaxBlockSize = DEFAULT_NOISE_MAX_BLOCK_SIZE;

    private static double ghostBurstDurationSeconds = DEFAULT_GHOST_BURST_DURATION_SECONDS;
    private static float ghostBurstIntensity = DEFAULT_GHOST_BURST_INTENSITY;
    private static float ghostBurstRangeMinPixels = DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS;
    private static float ghostBurstRangeMaxPixels = DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS;
    private static double ghostBurstBeforeFirstFlashSeconds = DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS;

    private static double smearBurstDelayAfterFirstFlashSeconds = DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS;
    private static double smearBurstDurationSeconds = DEFAULT_SMEAR_BURST_DURATION_SECONDS;
    private static float smearBurstIntensity = DEFAULT_SMEAR_BURST_INTENSITY;

    private static boolean introSoundPlayed;
    private static long introSoundStartNanos;

    private final Screen nextScreen;
    private long soundStartNanos;
    private boolean introSoundStarted;
    private boolean nextScreenInitialized;
    private boolean debugOverlayEnabled;
    private boolean debugLoopEnabled;
    private int selectedParameter;
    private int iconTextureId = -1;

    public DerggyCraftLogoScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void init() {
        this.debugOverlayEnabled = false;
        this.debugLoopEnabled = false;
        this.selectedParameter = 0;
        this.soundStartNanos = this.resolveSharedSoundStartTime();
        this.startIntroSound();
        this.iconTextureId = this.minecraft.textureManager.getTextureId(ICON_TEXTURE_PATH);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_F6) {
            this.debugOverlayEnabled = !this.debugOverlayEnabled;
            return;
        }

        if (keyCode == Keyboard.KEY_F7) {
            this.debugLoopEnabled = !this.debugLoopEnabled;
            if (this.debugLoopEnabled) {
                this.restartDebugLoopCycle();
            }
            return;
        }

        if (keyCode == Keyboard.KEY_R) {
            resetAllParametersToDefaults();
            return;
        }

        if (!this.debugOverlayEnabled) {
            return;
        }

        if (keyCode == Keyboard.KEY_UP) {
            this.selectedParameter = (this.selectedParameter - 1 + PARAM_COUNT) % PARAM_COUNT;
            return;
        }

        if (keyCode == Keyboard.KEY_DOWN) {
            this.selectedParameter = (this.selectedParameter + 1) % PARAM_COUNT;
            return;
        }

        boolean coarse = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        if (keyCode == Keyboard.KEY_LEFT) {
            this.adjustSelectedParameter(-1, coarse);
            return;
        }

        if (keyCode == Keyboard.KEY_RIGHT) {
            this.adjustSelectedParameter(1, coarse);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (!this.introSoundStarted) {
            this.startIntroSound();
        }

        double elapsedSeconds = this.getElapsedSeconds();

        if (this.debugLoopEnabled && elapsedSeconds >= DEBUG_LOOP_DURATION_SECONDS) {
            this.restartDebugLoopCycle();
            elapsedSeconds = this.getElapsedSeconds();
        }

        if (!this.debugLoopEnabled && elapsedSeconds >= titleTransitionStartSeconds) {
            this.ensureNextScreenInitialized();
            this.nextScreen.render(mouseX, mouseY, delta);

            float transitionProgress = clamp01((float) ((elapsedSeconds - titleTransitionStartSeconds) / titleTransitionDurationSeconds));
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
            int iconSize = Math.max(128, Math.min(256, Math.min(this.width, this.height) / 2));
            float jitterX = this.computeJitterX(elapsedSeconds);
            float jitterY = this.computeJitterY(elapsedSeconds);
            float centerX = this.width * 0.5F + jitterX;
            float centerY = this.height * 0.5F + jitterY;

            this.renderLogoEffects(elapsedSeconds, iconAlpha, centerX, centerY, iconSize);
        }

        this.renderScanlines(elapsedSeconds);
        this.renderNoiseOverlay(elapsedSeconds);

        if (this.debugOverlayEnabled) {
            this.renderDebugOverlay(elapsedSeconds);
        }
    }

    private void renderLogoEffects(double elapsedSeconds, float iconAlpha, float centerX, float centerY, int iconSize) {
        float bloomPulse = 0.5F + 0.5F * (float) Math.sin(2.0 * Math.PI * bloomPulseHz * elapsedSeconds);
        float bloomMultiplier = 1.0F + (bloomPulse - 0.5F) * 2.0F * bloomPulseAmount;

        float[] burstOffset = this.computeGhostBurstOffset(elapsedSeconds);
        float burstX = burstOffset[0];
        float burstY = burstOffset[1];
        float burstStrength = burstOffset[2];

        centerX += burstX;
        centerY += burstY;

        float ghostPassAlpha = clamp01(iconAlpha * ghostAlpha);
        if (ghostPassAlpha > 0.0F) {
            float burstGhostX = burstX * (0.6F + 0.4F * burstStrength);
            float burstGhostY = burstY * (0.6F + 0.4F * burstStrength);
            this.renderIconPass(centerX + ghostOffsetXPixels + burstGhostX, centerY + ghostOffsetYPixels + burstGhostY, iconSize, 1.0F, 1.0F, 1.0F, 1.0F, ghostPassAlpha, BLEND_SRC_ALPHA, BLEND_ONE_MINUS_SRC_ALPHA);
        }

        float bloomPassAlpha = clamp01(iconAlpha * bloomAlpha * bloomMultiplier);
        if (bloomPassAlpha > 0.0F) {
            this.renderIconPass(centerX, centerY, iconSize, bloomScale, 1.0F, 1.0F, 1.0F, bloomPassAlpha, BLEND_SRC_ALPHA, BLEND_ONE);
        }

        this.renderIconPass(centerX, centerY, iconSize, 1.0F, 1.0F, 1.0F, 1.0F, iconAlpha, BLEND_SRC_ALPHA, BLEND_ONE_MINUS_SRC_ALPHA);

        float chromaPassAlpha = clamp01(iconAlpha * chromaAlpha);
        if (chromaPassAlpha > 0.0F) {
            this.renderIconPass(centerX - chromaSplitPixels, centerY, iconSize, 1.0F, 0.25F, 1.0F, 1.0F, chromaPassAlpha, BLEND_SRC_ALPHA, BLEND_ONE);
            this.renderIconPass(centerX + chromaSplitPixels, centerY, iconSize, 1.0F, 1.0F, 0.35F, 0.35F, chromaPassAlpha, BLEND_SRC_ALPHA, BLEND_ONE);
        }

        float exposureAlpha = this.computeExposureAlpha(elapsedSeconds);
        if (exposureAlpha > 0.0F) {
            this.renderExposure(centerX, centerY, iconSize, iconAlpha, exposureAlpha);
        }

        float smearAlpha = this.computeSmearBurstAlpha(elapsedSeconds);
        if (smearAlpha > 0.0F) {
            this.renderVerticalSmear(centerX, centerY, iconSize, iconAlpha, smearAlpha);
        }

        this.renderTearBand(elapsedSeconds, centerX, centerY, iconSize, iconAlpha);
    }

    private float[] computeGhostBurstOffset(double elapsedSeconds) {
        double firstStart = flashOneStartSeconds - ghostBurstBeforeFirstFlashSeconds;
        double finalFlashStartSeconds = iconFadeOutStartSeconds - finalFlashOffsetFromFadeOutSeconds;

        float burst1 = computePulse(elapsedSeconds, firstStart, ghostBurstDurationSeconds, 0.62F, ghostBurstIntensity);
        float burst2 = computePulse(elapsedSeconds, finalFlashStartSeconds, ghostBurstDurationSeconds, 0.62F, ghostBurstIntensity);

        if (burst1 <= 0.0F && burst2 <= 0.0F) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }

        float rangeMin = Math.min(ghostBurstRangeMinPixels, ghostBurstRangeMaxPixels);
        float rangeMax = Math.max(ghostBurstRangeMinPixels, ghostBurstRangeMaxPixels);
        float rangeA = lerp(rangeMin, rangeMax, noiseHash01(271, 17));
        float rangeB = lerp(rangeMin, rangeMax, noiseHash01(271, 31));

        float signX1 = noiseHash01(271, 43) < 0.5F ? -1.0F : 1.0F;
        float signY1 = noiseHash01(271, 59) < 0.5F ? -1.0F : 1.0F;
        float signX2 = noiseHash01(887, 43) < 0.5F ? -1.0F : 1.0F;
        float signY2 = noiseHash01(887, 59) < 0.5F ? -1.0F : 1.0F;

        float dx = signX1 * rangeA * burst1 + signX2 * rangeB * burst2;
        float dy = signY1 * rangeA * 0.92F * burst1 + signY2 * rangeB * 0.92F * burst2;
        float strength = Math.max(burst1, burst2);

        return new float[]{dx, dy, strength};
    }

    private float computeSmearBurstAlpha(double elapsedSeconds) {
        double smearStart = flashOneStartSeconds + smearBurstDelayAfterFirstFlashSeconds;
        return computePulse(elapsedSeconds, smearStart, smearBurstDurationSeconds, 0.68F, smearBurstIntensity);
    }

    private void renderVerticalSmear(float centerX, float centerY, int iconSize, float iconAlpha, float smearAlpha) {
        float baseAlpha = clamp01(iconAlpha * smearAlpha);
        if (baseAlpha <= 0.0F) {
            return;
        }

        int streakCount = 6 + Math.round(smearBurstIntensity * 8.0F);
        for (int i = 0; i < streakCount; ++i) {
            float progress = i / (float) Math.max(1, streakCount - 1);
            float stretch = 1.0F + 0.35F * smearBurstIntensity + progress * 1.15F * smearBurstIntensity;
            float alpha = baseAlpha * (1.0F - progress) * 0.75F;
            float offsetY = (progress - 0.5F) * iconSize * 0.48F * smearBurstIntensity;
            float offsetX = (noiseHash01((int) (progress * 2000), 913) - 0.5F) * 2.0F;
            this.renderIconPass(centerX + offsetX, centerY + offsetY, iconSize, 1.0F, stretch, 1.0F, 1.0F, 1.0F, alpha, BLEND_SRC_ALPHA, BLEND_ONE);
        }
    }

    private void renderExposure(float centerX, float centerY, int iconSize, float iconAlpha, float exposureAlpha) {
        float layerAlpha = clamp01(iconAlpha * exposureAlpha);
        for (int i = 0; i < exposureLayerCount && layerAlpha > 0.01F; ++i) {
            this.renderIconPass(centerX, centerY, iconSize, 1.0F + i * 0.01F, 1.0F, 1.0F, 1.0F, layerAlpha, BLEND_SRC_ALPHA, BLEND_ONE);
            layerAlpha *= exposureLayerDecay;
        }
    }

    private void renderTearBand(double elapsedSeconds, float centerX, float centerY, int iconSize, float iconAlpha) {
        if (tearChance <= 0.0F || iconAlpha <= 0.0F) {
            return;
        }

        if (noiseHash01((int) (elapsedSeconds * 1000.0), 97) > tearChance) {
            return;
        }

        float bandHeight = clamp(tearBandHeightPixels, 4.0F, iconSize * 0.6F);
        float topPx = noiseHash01((int) (elapsedSeconds * 770.0), 1337) * Math.max(1.0F, iconSize - bandHeight);
        float offset = (noiseHash01((int) (elapsedSeconds * 1300.0), 777) * 2.0F - 1.0F) * tearMaxOffsetPixels;
        this.renderIconSlicePass(centerX + offset, centerY, iconSize, topPx, bandHeight, iconAlpha * 0.9F);

        int iconTop = Math.round(centerY - iconSize * 0.5F);
        int yLine = iconTop + Math.round(topPx);
        int alpha = (int) (clamp01(iconAlpha * 0.25F) * 255.0F);
        this.fill(Math.round(centerX - iconSize * 0.5F), yLine, Math.round(centerX + iconSize * 0.5F), yLine + 1, (alpha << 24) | 0xFFFFFF);
    }

    private void renderIconSlicePass(float centerX, float centerY, int iconSize, float topPx, float bandHeight, float alpha) {
        if (alpha <= 0.0F || bandHeight <= 0.0F) {
            return;
        }

        float left = centerX - iconSize * 0.5F;
        float top = centerY - iconSize * 0.5F;
        float right = left + iconSize;
        float y1 = top + topPx;
        float y2 = y1 + bandHeight;

        float v1 = topPx / iconSize;
        float v2 = (topPx + bandHeight) / iconSize;

        GL11.glEnable(3042);
        GL11.glBlendFunc(BLEND_SRC_ALPHA, BLEND_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, clamp01(alpha));
        GL11.glBindTexture(3553, this.iconTextureId);

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(left, y2, this.zOffset, 0.0, v2);
        tessellator.vertex(right, y2, this.zOffset, 1.0, v2);
        tessellator.vertex(right, y1, this.zOffset, 1.0, v1);
        tessellator.vertex(left, y1, this.zOffset, 0.0, v1);
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    private void renderScanlines(double elapsedSeconds) {
        if (scanlineAlpha <= 0.0F) {
            return;
        }

        int spacing = Math.max(1, scanlineSpacingPixels);
        for (int y = 0; y < this.height; y += spacing) {
            float wave = 0.75F + 0.25F * (float) Math.sin(elapsedSeconds * scanlineScrollSpeed + y * 0.14F);
            int alpha = (int) (clamp01(scanlineAlpha * wave) * 255.0F);
            this.fill(0, y, this.width, y + 1, alpha << 24);
        }
    }

    private void renderNoiseOverlay(double elapsedSeconds) {
        if (noiseAlpha <= 0.0F || noiseDensity <= 0.0F) {
            return;
        }

        int frame = (int) (elapsedSeconds * 60.0);
        int samples = Math.max(1, (int) (noiseDensity * 140.0F));
        int maxSize = Math.max(1, noiseMaxBlockSize);

        for (int i = 0; i < samples; ++i) {
            float gate = noiseHash01(frame, i * 23 + 11);
            if (gate < 0.6F) {
                continue;
            }

            int x = (int) (noiseHash01(frame, i * 41 + 3) * this.width);
            int y = (int) (noiseHash01(frame, i * 59 + 17) * this.height);
            int size = 1 + (int) (noiseHash01(frame, i * 83 + 9) * maxSize);
            int alpha = (int) (clamp01(noiseAlpha * (0.35F + 0.65F * gate)) * 255.0F);
            int color = (alpha << 24) | 0xFFFFFF;
            this.fill(x, y, x + size, y + size, color);
        }
    }

    private void renderDebugOverlay(double elapsedSeconds) {
        if (this.textRenderer == null) {
            return;
        }

        int y = 6;
        this.drawTextWithShadow(this.textRenderer, "Logo Debug: F6 overlay  F7 loop  Arrows select/edit  Shift=coarse  R reset", 6, y, 0x90FF90);
        y += 10;
        this.drawTextWithShadow(this.textRenderer, String.format("t=%.2fs  loop=%s", elapsedSeconds, this.debugLoopEnabled ? "on" : "off"), 6, y, 0xFFFFFF);
        y += 10;

        for (int i = 0; i < PARAM_COUNT; ++i) {
            int color = i == this.selectedParameter ? 0xFFE066 : 0xC8C8C8;
            this.drawTextWithShadow(this.textRenderer, this.getParameterLine(i), 6, y, color);
            y += 9;
        }
    }

    private String getParameterLine(int index) {
        switch (index) {
            case 0: return String.format("Jitter X amp: %.2f", jitterXAmplitudePixels);
            case 1: return String.format("Jitter Y amp: %.2f", jitterYAmplitudePixels);
            case 2: return String.format("Jitter primary Hz: %.2f", jitterPrimaryFrequencyHz);
            case 3: return String.format("Jitter secondary Hz: %.2f", jitterSecondaryFrequencyHz);
            case 4: return String.format("Flash intensity: %.2f", flashIntensity);
            case 5: return String.format("Final flash intensity: %.2f", finalFlashIntensity);
            case 6: return String.format("Chroma split px: %.2f", chromaSplitPixels);
            case 7: return String.format("Chroma alpha: %.2f", chromaAlpha);
            case 8: return String.format("Tear chance: %.2f", tearChance);
            case 9: return String.format("Tear max offset: %.2f", tearMaxOffsetPixels);
            case 10: return String.format("Ghost alpha: %.2f", ghostAlpha);
            case 11: return String.format("Bloom alpha: %.2f", bloomAlpha);
            case 12: return String.format("Bloom scale: %.3f", bloomScale);
            case 13: return String.format("Scanline alpha: %.2f", scanlineAlpha);
            case 14: return String.format("Scanline spacing: %d", scanlineSpacingPixels);
            case 15: return String.format("Noise alpha: %.2f", noiseAlpha);
            case 16: return String.format("Noise density: %.2f", noiseDensity);
            case 17: return String.format("Exposure layers: %d", exposureLayerCount);
            case 18: return String.format("Smear burst intensity: %.2f", smearBurstIntensity);
            case 19: return String.format("Ghost burst range min: %.2f", ghostBurstRangeMinPixels);
            case 20: return String.format("Ghost burst range max: %.2f", ghostBurstRangeMaxPixels);
            case 21: return String.format("Ghost burst intensity: %.2f", ghostBurstIntensity);
            default: return "n/a";
        }
    }

    private void adjustSelectedParameter(int direction, boolean coarse) {
        float mult = coarse ? 5.0F : 1.0F;
        switch (this.selectedParameter) {
            case 0:
                jitterXAmplitudePixels = clamp(jitterXAmplitudePixels + direction * 0.1F * mult, 0.0F, 12.0F);
                break;
            case 1:
                jitterYAmplitudePixels = clamp(jitterYAmplitudePixels + direction * 0.1F * mult, 0.0F, 12.0F);
                break;
            case 2:
                jitterPrimaryFrequencyHz = clamp(jitterPrimaryFrequencyHz + direction * 0.1F * mult, 0.0F, 50.0F);
                break;
            case 3:
                jitterSecondaryFrequencyHz = clamp(jitterSecondaryFrequencyHz + direction * 0.1F * mult, 0.0F, 70.0F);
                break;
            case 4:
                flashIntensity = clamp(flashIntensity + direction * 0.05F * mult, 0.0F, 1.0F);
                break;
            case 5:
                finalFlashIntensity = clamp(finalFlashIntensity + direction * 0.05F * mult, 0.0F, 1.0F);
                break;
            case 6:
                chromaSplitPixels = clamp(chromaSplitPixels + direction * 0.1F * mult, 0.0F, 18.0F);
                break;
            case 7:
                chromaAlpha = clamp(chromaAlpha + direction * 0.02F * mult, 0.0F, 1.0F);
                break;
            case 8:
                tearChance = clamp(tearChance + direction * 0.02F * mult, 0.0F, 1.0F);
                break;
            case 9:
                tearMaxOffsetPixels = clamp(tearMaxOffsetPixels + direction * 0.2F * mult, 0.0F, 64.0F);
                break;
            case 10:
                ghostAlpha = clamp(ghostAlpha + direction * 0.02F * mult, 0.0F, 1.0F);
                break;
            case 11:
                bloomAlpha = clamp(bloomAlpha + direction * 0.02F * mult, 0.0F, 1.0F);
                break;
            case 12:
                bloomScale = clamp(bloomScale + direction * 0.005F * mult, 1.0F, 1.5F);
                break;
            case 13:
                scanlineAlpha = clamp(scanlineAlpha + direction * 0.01F * mult, 0.0F, 1.0F);
                break;
            case 14:
                scanlineSpacingPixels = (int) clamp(scanlineSpacingPixels + direction * mult, 1.0F, 8.0F);
                break;
            case 15:
                noiseAlpha = clamp(noiseAlpha + direction * 0.01F * mult, 0.0F, 1.0F);
                break;
            case 16:
                noiseDensity = clamp(noiseDensity + direction * 0.02F * mult, 0.0F, 1.0F);
                break;
            case 17:
                exposureLayerCount = (int) clamp(exposureLayerCount + direction * mult, 1.0F, 10.0F);
                break;
            case 18:
                smearBurstIntensity = clamp(smearBurstIntensity + direction * 0.03F * mult, 0.0F, 1.0F);
                break;
            case 19:
                ghostBurstRangeMinPixels = clamp(ghostBurstRangeMinPixels + direction * 0.2F * mult, 0.0F, 64.0F);
                break;
            case 20:
                ghostBurstRangeMaxPixels = clamp(ghostBurstRangeMaxPixels + direction * 0.2F * mult, 0.0F, 80.0F);
                break;
            case 21:
                ghostBurstIntensity = clamp(ghostBurstIntensity + direction * 0.03F * mult, 0.0F, 1.0F);
                break;
            default:
                break;
        }

        if (ghostBurstRangeMinPixels > ghostBurstRangeMaxPixels) {
            float tmp = ghostBurstRangeMinPixels;
            ghostBurstRangeMinPixels = ghostBurstRangeMaxPixels;
            ghostBurstRangeMaxPixels = tmp;
        }
    }

    private static void resetAllParametersToDefaults() {
        iconFadeInStartSeconds = DEFAULT_ICON_FADE_IN_START_SECONDS;
        iconFadeDurationSeconds = DEFAULT_ICON_FADE_DURATION_SECONDS;
        iconFadeOutStartSeconds = DEFAULT_ICON_FADE_OUT_START_SECONDS;
        titleTransitionStartSeconds = DEFAULT_TITLE_TRANSITION_START_SECONDS;
        titleTransitionDurationSeconds = DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;

        jitterXAmplitudePixels = DEFAULT_JITTER_X_AMPLITUDE_PIXELS;
        jitterYAmplitudePixels = DEFAULT_JITTER_Y_AMPLITUDE_PIXELS;
        jitterPrimaryFrequencyHz = DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ;
        jitterSecondaryFrequencyHz = DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ;
        jitterSecondaryWeight = DEFAULT_JITTER_SECONDARY_WEIGHT;
        jitterPhaseOffsetRadians = DEFAULT_JITTER_PHASE_OFFSET_RADIANS;

        flashOneStartSeconds = DEFAULT_FLASH_ONE_START_SECONDS;
        flashDurationSeconds = DEFAULT_FLASH_DURATION_SECONDS;
        flashGapSeconds = DEFAULT_FLASH_GAP_SECONDS;
        flashAttackRatio = DEFAULT_FLASH_ATTACK_RATIO;
        flashIntensity = DEFAULT_FLASH_INTENSITY;

        finalFlashOffsetFromFadeOutSeconds = DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS;
        finalFlashDurationSeconds = DEFAULT_FINAL_FLASH_DURATION_SECONDS;
        finalFlashAttackRatio = DEFAULT_FINAL_FLASH_ATTACK_RATIO;
        finalFlashIntensity = DEFAULT_FINAL_FLASH_INTENSITY;

        exposureLayerCount = DEFAULT_EXPOSURE_LAYER_COUNT;
        exposureLayerDecay = DEFAULT_EXPOSURE_LAYER_DECAY;

        chromaSplitPixels = DEFAULT_CHROMA_SPLIT_PIXELS;
        chromaAlpha = DEFAULT_CHROMA_ALPHA;

        tearChance = DEFAULT_TEAR_CHANCE;
        tearMaxOffsetPixels = DEFAULT_TEAR_MAX_OFFSET_PIXELS;
        tearBandHeightPixels = DEFAULT_TEAR_BAND_HEIGHT_PIXELS;

        ghostAlpha = DEFAULT_GHOST_ALPHA;
        ghostOffsetXPixels = DEFAULT_GHOST_OFFSET_X_PIXELS;
        ghostOffsetYPixels = DEFAULT_GHOST_OFFSET_Y_PIXELS;

        bloomAlpha = DEFAULT_BLOOM_ALPHA;
        bloomScale = DEFAULT_BLOOM_SCALE;
        bloomPulseHz = DEFAULT_BLOOM_PULSE_HZ;
        bloomPulseAmount = DEFAULT_BLOOM_PULSE_AMOUNT;

        scanlineSpacingPixels = DEFAULT_SCANLINE_SPACING_PIXELS;
        scanlineAlpha = DEFAULT_SCANLINE_ALPHA;
        scanlineScrollSpeed = DEFAULT_SCANLINE_SCROLL_SPEED;

        noiseAlpha = DEFAULT_NOISE_ALPHA;
        noiseDensity = DEFAULT_NOISE_DENSITY;
        noiseMaxBlockSize = DEFAULT_NOISE_MAX_BLOCK_SIZE;

        ghostBurstDurationSeconds = DEFAULT_GHOST_BURST_DURATION_SECONDS;
        ghostBurstIntensity = DEFAULT_GHOST_BURST_INTENSITY;
        ghostBurstRangeMinPixels = DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS;
        ghostBurstRangeMaxPixels = DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS;
        ghostBurstBeforeFirstFlashSeconds = DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS;

        smearBurstDelayAfterFirstFlashSeconds = DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS;
        smearBurstDurationSeconds = DEFAULT_SMEAR_BURST_DURATION_SECONDS;
        smearBurstIntensity = DEFAULT_SMEAR_BURST_INTENSITY;
    }

    private void restartDebugLoopCycle() {
        introSoundStartNanos = System.nanoTime();
        this.soundStartNanos = introSoundStartNanos;
        this.minecraft.soundManager.playSound(IntroLogoSound.PLAYBACK_ID, 0.0F, 0.0F, 0.0F, 2.0F, 1.0F);
        if (DerggyCraft.LOGGER != null) {
            DerggyCraft.LOGGER.info("Restarting logo debug loop and replaying intro id {}", IntroLogoSound.PLAYBACK_ID);
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
        if (elapsedSeconds < iconFadeInStartSeconds) {
            return 0.0F;
        }

        if (elapsedSeconds < iconFadeInStartSeconds + iconFadeDurationSeconds) {
            return clamp01((float) ((elapsedSeconds - iconFadeInStartSeconds) / iconFadeDurationSeconds));
        }

        if (elapsedSeconds < iconFadeOutStartSeconds) {
            return 1.0F;
        }

        if (elapsedSeconds < iconFadeOutStartSeconds + iconFadeDurationSeconds) {
            float fadeOut = (float) ((elapsedSeconds - iconFadeOutStartSeconds) / iconFadeDurationSeconds);
            return 1.0F - clamp01(fadeOut);
        }

        return 0.0F;
    }

    private void renderIconPass(float centerX, float centerY, int iconSize, float scaleX, float scaleY, float red, float green, float blue, float alpha, int blendSrc, int blendDst) {
        if (alpha <= 0.0F) {
            return;
        }

        float scaledWidth = iconSize * scaleX;
        float scaledHeight = iconSize * scaleY;
        float x = centerX - scaledWidth * 0.5F;
        float y = centerY - scaledHeight * 0.5F;

        GL11.glEnable(3042);
        GL11.glBlendFunc(blendSrc, blendDst);
        GL11.glColor4f(red, green, blue, clamp01(alpha));
        GL11.glBindTexture(3553, this.iconTextureId);

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        tessellator.vertex(x, y + scaledHeight, this.zOffset, 0.0, 1.0);
        tessellator.vertex(x + scaledWidth, y + scaledHeight, this.zOffset, 1.0, 1.0);
        tessellator.vertex(x + scaledWidth, y, this.zOffset, 1.0, 0.0);
        tessellator.vertex(x, y, this.zOffset, 0.0, 0.0);
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    private void renderIconPass(float centerX, float centerY, int iconSize, float scale, float red, float green, float blue, float alpha, int blendSrc, int blendDst) {
        this.renderIconPass(centerX, centerY, iconSize, scale, scale, red, green, blue, alpha, blendSrc, blendDst);
    }

    private float computeJitterX(double elapsedSeconds) {
        double primary = Math.sin(2.0 * Math.PI * jitterPrimaryFrequencyHz * elapsedSeconds);
        double secondary = Math.sin(2.0 * Math.PI * jitterSecondaryFrequencyHz * elapsedSeconds + jitterPhaseOffsetRadians);
        return (float) ((primary + secondary * jitterSecondaryWeight) * jitterXAmplitudePixels);
    }

    private float computeJitterY(double elapsedSeconds) {
        double primary = Math.cos(2.0 * Math.PI * jitterPrimaryFrequencyHz * elapsedSeconds + jitterPhaseOffsetRadians * 0.5);
        double secondary = Math.cos(2.0 * Math.PI * jitterSecondaryFrequencyHz * elapsedSeconds);
        return (float) ((primary + secondary * jitterSecondaryWeight) * jitterYAmplitudePixels);
    }

    private float computeExposureAlpha(double elapsedSeconds) {
        double finalFlashStartSeconds = iconFadeOutStartSeconds - finalFlashOffsetFromFadeOutSeconds;

        float first = computePulse(
                elapsedSeconds,
            flashOneStartSeconds,
            flashDurationSeconds,
            flashAttackRatio,
            flashIntensity
        );

        float second = computePulse(
                elapsedSeconds,
            flashOneStartSeconds + flashDurationSeconds + flashGapSeconds,
            flashDurationSeconds,
            flashAttackRatio,
            flashIntensity
        );

        float finalBurst = computePulse(
                elapsedSeconds,
            finalFlashStartSeconds,
            finalFlashDurationSeconds,
            finalFlashAttackRatio,
            finalFlashIntensity
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

    private static float noiseHash01(int seedA, int seedB) {
        int n = seedA * 374761393 + seedB * 668265263;
        n = (n ^ (n >>> 13)) * 1274126177;
        n = n ^ (n >>> 16);
        return (n & 0x7fffffff) / (float) 0x7fffffff;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
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

    private static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}