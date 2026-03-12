/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package paulscode.sound.codecs;

import java.io.InputStream;
import java.net.URL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import paulscode.sound.codecs.CodecJOrbis;

@Environment(value=EnvType.CLIENT)
public class CodecMus
extends CodecJOrbis {
    protected InputStream openInputStream() {
        return new MusInputStream(this.url, this.urlConnection.getInputStream());
    }

    @Environment(value=EnvType.CLIENT)
    class MusInputStream
    extends InputStream {
        private int hash;
        private InputStream inputStream;
        byte[] buffer = new byte[1];

        public MusInputStream(URL fileURL, InputStream inputStream) {
            this.inputStream = inputStream;
            String string = fileURL.getPath();
            string = string.substring(string.lastIndexOf("/") + 1);
            this.hash = string.hashCode();
        }

        public int read() {
            int n = this.read(this.buffer, 0, 1);
            if (n < 0) {
                return n;
            }
            return this.buffer[0];
        }

        public int read(byte[] bs, int i, int j) {
            j = this.inputStream.read(bs, i, j);
            for (int k = 0; k < j; ++k) {
                int n = i + k;
                byte by = (byte)(bs[n] ^ this.hash >> 8);
                bs[n] = by;
                byte by2 = by;
                this.hash = this.hash * 498729871 + 85731 * by2;
            }
            return j;
        }
    }
}

