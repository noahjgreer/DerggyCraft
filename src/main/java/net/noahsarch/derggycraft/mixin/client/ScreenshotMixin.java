package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.Screenshot;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;

@Mixin(Screenshot.class)
public abstract class ScreenshotMixin {
    @Shadow
    private static DateFormat DATE_FORMAT;

    @Shadow
    private static ByteBuffer pixels;

    @Shadow
    private static byte[] colorBuffer;

    @Shadow
    private static int[] pixelBuffer;

    @Inject(method = "take(Ljava/io/File;II)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private static void derggycraft$useSafeScreenshotBufferSizing(File gameDir, int width, int height, CallbackInfoReturnable<String> cir) {
        try {
            if (width <= 0 || height <= 0) {
                cir.setReturnValue("Failed to save: invalid screenshot dimensions " + width + "x" + height);
                return;
            }

            File screenshotsDirectory = new File(gameDir, "screenshots");
            screenshotsDirectory.mkdir();

            int pixelCount = width * height;
            int rgbByteCount = pixelCount * 3;

            if (pixels == null || pixels.capacity() < rgbByteCount) {
                pixels = BufferUtils.createByteBuffer(rgbByteCount);
            }
            if (colorBuffer == null || colorBuffer.length < rgbByteCount) {
                colorBuffer = new byte[rgbByteCount];
            }
            if (pixelBuffer == null || pixelBuffer.length < pixelCount) {
                pixelBuffer = new int[pixelCount];
            }

            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            pixels.clear();
            GL11.glReadPixels(0, 0, width, height, 6407, 5121, pixels);
            pixels.flip();

            if (pixels.remaining() < rgbByteCount) {
                cir.setReturnValue("Failed to save: screenshot buffer underflow");
                return;
            }

            String timestamp = DATE_FORMAT.format(new Date());
            int suffix = 1;
            File outputFile;
            do {
                outputFile = new File(screenshotsDirectory, timestamp + (suffix == 1 ? "" : "_" + suffix) + ".png");
                ++suffix;
            } while (outputFile.exists());

            pixels.get(colorBuffer, 0, rgbByteCount);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int sourceIndex = x + (height - y - 1) * width;
                    int r = colorBuffer[sourceIndex * 3] & 0xFF;
                    int g = colorBuffer[sourceIndex * 3 + 1] & 0xFF;
                    int b = colorBuffer[sourceIndex * 3 + 2] & 0xFF;
                    pixelBuffer[x + y * width] = 0xFF000000 | (r << 16) | (g << 8) | b;
                }
            }

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, width, height, pixelBuffer, 0, width);
            ImageIO.write((RenderedImage) image, "png", outputFile);
            cir.setReturnValue("Saved screenshot as " + outputFile.getName());
        } catch (Exception exception) {
            exception.printStackTrace();
            cir.setReturnValue("Failed to save: " + exception);
        }
    }
}
