package org.cuiyang.adb;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * adb 过滤流.
 * <p>在api level 24以下,
 * 执行adb shell会将\n转换\r\n, 因此需要用将\r\n转换为\n
 *
 * @author cuiyang
 * @since 2017/3/24
 */
public class AdbFilterInputStream extends FilterInputStream {

    /** \r */
    private static final int BACKSLASH_R = 0x0d;
    /** \n */
    private static final int BACKSLASH_N = 0x0a;

    public AdbFilterInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int b1 = in.read();
        if (b1 == BACKSLASH_R) {
            in.mark(1);
            int b2 = in.read();
            if (b2 == BACKSLASH_N) {
                return b2;
            }
            in.reset();
        }
        return b1;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int n = 0;
        for (int i = 0; i < len; i++) {
            int c = read();
            if (c == -1) return n == 0 ? -1 : n;
            b[off + n] = (byte) c;
            n++;

            if (in.available() <= 0) {
                return n;
            }
        }
        return n;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }
}
