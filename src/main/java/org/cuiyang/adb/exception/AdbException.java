package org.cuiyang.adb.exception;

/**
 * adb 异常.
 *
 * @author cuiyang
 * @since 2017/3/22
 */
public class AdbException extends Exception {

    public AdbException() {
    }

    public AdbException(String message) {
        super(message);
    }

    public AdbException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdbException(Throwable cause) {
        super(cause);
    }

    public AdbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}