package org.cuiyang.adb.exception;

/**
 * 设备异常.
 *
 * @author cuiyang
 * @since 2017/3/25
 */
public class DeviceException extends AdbException {

    public DeviceException() {
        super("The device may be offline or transport type error.");
    }

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceException(Throwable cause) {
        super(cause);
    }

    public DeviceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
