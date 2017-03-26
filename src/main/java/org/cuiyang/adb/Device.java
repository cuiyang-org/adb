package org.cuiyang.adb;

import org.cuiyang.adb.exception.CommandException;
import org.cuiyang.adb.exception.DeviceException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 设备.
 *
 * @author cuiyang
 * @since 2017/3/22
 */
public interface Device {

    /**
     * 设备状态
     */
    enum State {
        /** 没有连接设备 */
        UNKNOWN,
        /** 连接出现异常，设备无响应 */
        OFFLINE,
        /** 设备正常连接 */
        DEVICE,
        BOOTLOADER
    }

    /**
     * 设备类型
     */
    enum Type {
        /** 连接usb上的设备，如果usb上有不止一个设备，会失败 */
        USB("host:transport-usb"),
        /** 通过tcp方式连接模拟器，如果有多个模拟器在运行，会失败 */
        LOCAL("host:transport-local"),
        /** 连接usb设备或者模拟器都可以，但是如果有超过一个设备或模拟器，会失败 */
        ANY("host:transport-any"),
        /** 连接指定serial-number的设备或者模拟器 */
        SERIAL_NO("host:transport:");

        private String command;

        Type(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    /**
     * 获取该设备序列号.
     * @return 序列号
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    String getSerialNo() throws IOException, DeviceException;

    /**
     * 获取该设备状态.
     * @return 设备状态
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    State getState() throws IOException, DeviceException;

    /**
     * 获取设备类型
     * @return 设备类型
     */
    Type getType();

    /**
     * 执行shell命令.
     * @param command shell 命令
     * @param args 命令参数
     * @return 获取执行结果输入流
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     * @throws CommandException 命令异常
     */
    InputStream shell(String command, String... args) throws IOException, DeviceException, CommandException;

}
