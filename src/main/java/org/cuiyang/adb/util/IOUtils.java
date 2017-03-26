package org.cuiyang.adb.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IOUtils.
 *
 * @author cuiyang
 * @since 2017/3/26
 */
public class IOUtils {

    private IOUtils() {
    }

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ignore) {
        }
    }
}
