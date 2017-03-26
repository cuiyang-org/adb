package org.cuiyang.adb.exception;

/**
 * adb 命令异常.
 *
 * @author cuiyang
 * @since 2017/3/25
 */
public class CommandException extends AdbException {

    public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }

    public CommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
