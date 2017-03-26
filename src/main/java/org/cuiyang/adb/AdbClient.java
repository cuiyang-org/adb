package org.cuiyang.adb;

import org.cuiyang.adb.exception.CommandException;
import org.cuiyang.adb.exception.DeviceException;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * adb 客户端.
 *
 * @author cuiyang
 * @since 2017/3/22
 */
public class AdbClient implements TransportFactory {

    /** 默认的adb server地址 */
    private static final String DEFAULT_HOST = "127.0.0.1";
    /** 默认的adb server端口 */
    private static final int DEFAULT_PORT = 5037;

    /** adb server地址 */
    private String host;
    /** db server端口 */
    private int port;

    public AdbClient() {
        this.host = DEFAULT_HOST;
        this.port = getDefaultPort();
    }

    public AdbClient(String host) {
        this.host = host;
        this.port = getDefaultPort();
    }

    public AdbClient(int port) {
        this.host = DEFAULT_HOST;
        this.port = port;
    }

    public AdbClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Transport getTransport() throws IOException {
        return new Transport(new Socket(host, port));
    }

    /**
     * 获取默认端口.
     * <p>优先返回环境变量ANDROID_ADB_SERVER_PORT所配置的端口号
     * @return 端口
     */
    public int getDefaultPort() {
        String port = System.getenv("ANDROID_ADB_SERVER_PORT");
        if (port != null) {
            try {
                return Integer.valueOf(port);
            } catch (NumberFormatException ignore) {
            }
        }
        return DEFAULT_PORT;
    }

    /**
     * 获取设备列表
     * @return 设备列表
     * @throws IOException 和adb server连接异常
     * @throws CommandException adb命令执行失败
     */
    public List<Device> getDevices() throws IOException, CommandException {
        Transport transport = getTransport();
        transport.send("host:devices");
        String response = transport.read();
        transport.close();
        return parseDevices(response);
    }

    /**
     * 解析设备列表
     * @param response 执行adb命令host:devices返回的结果
     * @throws CommandException adb命令host:devices异常
     */
    private List<Device> parseDevices(String response) throws CommandException {
        List<Device> devices = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                devices.add(new Device(this, parts[0]));
            } else {
                throw new CommandException("adb命令host:devices异常");
            }
        }
        return devices;
    }

    /**
     * 获取usb设备或者模拟器
     * @return 设备
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 可能有超过一个设备或模拟器
     */
    public Device getAnyDevice() throws IOException, DeviceException {
        Device device = new Device(this, Device.TransportType.ANY);
        device.getSerial();
        return device;
    }

    /**
     * 获取usb设备
     * @return 设备
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 可能有超过一个设备或模拟器
     */
    public Device getUsbDevice() throws IOException, DeviceException {
        Device device = new Device(this, Device.TransportType.USB);
        device.getSerial();
        return device;
    }

    /**
     * 获取tcp方式连接模拟器
     * @return 设备
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 可能有超过一个设备或模拟器
     */
    public Device getLocalDevice() throws IOException, DeviceException {
        Device device = new Device(this, Device.TransportType.LOCAL);
        device.getSerial();
        return device;
    }

}
