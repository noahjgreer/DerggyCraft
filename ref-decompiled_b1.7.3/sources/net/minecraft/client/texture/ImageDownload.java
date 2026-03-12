/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ImageProcessor;

@Environment(value=EnvType.CLIENT)
public class ImageDownload {
    public BufferedImage image;
    public int requestCount = 1;
    public int textureId = -1;
    public boolean uploaded = false;

    public ImageDownload(String url, ImageProcessor textureProcessor) {
        new Thread(url, textureProcessor).start();
    }

    @Environment(value=EnvType.CLIENT)
    class Thread
    extends java.lang.Thread {
        final /* synthetic */ String url;
        final /* synthetic */ ImageProcessor processor;

        Thread(String string, ImageProcessor imageProcessor) {
            this.url = string;
            this.processor = imageProcessor;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            HttpURLConnection httpURLConnection = null;
            try {
                URL uRL = new URL(this.url);
                httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 == 4) {
                    return;
                }
                ImageDownload.this.image = this.processor == null ? ImageIO.read(httpURLConnection.getInputStream()) : this.processor.process(ImageIO.read(httpURLConnection.getInputStream()));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                httpURLConnection.disconnect();
            }
        }
    }
}

