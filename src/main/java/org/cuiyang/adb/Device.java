package org.cuiyang.adb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * @throws AdbException 获取设备序列号失败
     */
    String getSerialNo() throws IOException, AdbException;

    /**
     * 获取该设备状态.
     * @return 设备状态
     * @throws IOException 和adb server连接异常
     * @throws AdbException 获取设备状态失败
     */
    State getState() throws IOException, AdbException;

    /**
     * 获取设备类型
     * @return 设备类型
     */
    Type getType();

    /**
     * 执行shell命令.
     * @param command shell 命令
     * @param args 命令参数
     * @return shell执行结果
     * @throws IOException 和adb server连接异常
     * @throws AdbException 执行shell命令失败
     */
    ShellResponse shell(String command, String... args) throws IOException, AdbException;

    /**
     * 推送文件到设备
     * @param local 本地文件输入流
     * @param remote 远程路径
     * @param lastModified 修改时间
     * @param mode 文件模式
     * @throws IOException 和adb server连接异常
     * @throws AdbException 推送文件失败
     */
    void push(InputStream local, String remote, long lastModified, int mode) throws IOException, AdbException;

    /**
     * 推送文件到设备
     * @param local 本地文件
     * @param remote 远程路径
     * @throws IOException 和adb server连接异常
     * @throws AdbException 推送文件失败
     */
    void push(File local, String remote) throws IOException, AdbException;

    /**
     * 从设备拉取文件到本地
     * @param remote 远程文件路径
     * @param local 本地文件输出流
     * @throws IOException 和adb server连接异常
     * @throws AdbException 拉取文件失败
     */
    void pull(String remote, OutputStream local) throws IOException, AdbException;

    /**
     * 从设备拉取文件到本地
     * @param remote 远程文件路径
     * @param local 本地文件
     * @throws IOException 和adb server连接异常
     * @throws AdbException 拉取文件失败
     */
    void pull(String remote, File local) throws IOException, AdbException;

}
