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
public class Device {

    /**
     * 手机状态
     */
    public enum State {
        UNKNOWN,
        OFFLINE,
        DEVICE,
        BOOTLOADER
    }

    /**
     * 传输类型
     */
    public enum TransportType {
        /** 连接usb上的设备，如果usb上有不止一个设备，会失败 */
        USB("host:transport-usb"),
        /** 通过tcp方式连接模拟器，如果有多个模拟器在运行，会失败 */
        LOCAL("host:transport-local"),
        /** 连接usb设备或者模拟器都可以，但是如果有超过一个设备或模拟器，会失败 */
        ANY("host:transport-any"),
        /** 连接指定serial-number的设备或者模拟器 */
        SERIAL_NO("host:transport:");

        private String command;

        TransportType(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    /** 序列号 */
    private String serial;
    /** Transport 工厂 */
    private TransportFactory factory;
    /** 传输类型 */
    private TransportType type;

    public Device(TransportFactory factory, String serial) {
        this.serial = serial;
        this.factory = factory;
        this.type = TransportType.SERIAL_NO;
    }

    public Device(TransportFactory factory, TransportType type) {
        this.factory = factory;
        this.type = type;
    }

    /**
     * 获取Transport.
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    private Transport getTransport() throws IOException, DeviceException {
        Transport transport = factory.getTransport();
        try {
            if (TransportType.SERIAL_NO == type) {
                transport.send(type.getCommand() + serial);
            } else {
                transport.send(type.getCommand());
            }
        } catch (CommandException e) {
            throw new DeviceException(e);
        }
        return transport;
    }

    /**
     * 获取该设备序列号
     * @return 序列号
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    public String getSerial() throws IOException, DeviceException {
        if (TransportType.SERIAL_NO == type) {
            return serial;
        } else {
            try(Transport transport = factory.getTransport()) {
                transport.send("host:get-serialno");
                serial = transport.read();
                return serial;
            } catch (CommandException e) {
                throw new DeviceException(e);
            }
        }
    }

    /**
     * 获取该设备状态
     * @return 设备状态
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    public State getState() throws IOException, DeviceException {
        try(Transport transport = factory.getTransport()) {
            if (TransportType.SERIAL_NO == type) {
                transport.send("host-serial:" + serial + ":get-state");
            } else {
                transport.send("host:get-state");
            }
            String result = transport.read();
            return State.valueOf(result.toUpperCase());
        } catch (CommandException e) {
            throw new DeviceException(e);
        }
    }

    /**
     * 执行shell命令.
     * @param command shell 命令
     * @param args 命令参数
     * @return 获取执行结果输入流
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     * @throws CommandException 命令执行失败
     */
    public InputStream shell(String command, String... args) throws IOException, DeviceException, CommandException {
        Transport transport = getTransport();
        StringBuilder shellLine = buildCmdLine(command, args);
        transport.send("shell:" + shellLine.toString());
        return transport.getInputStream();
    }

    /**
     * 构建命令.
     */
    private StringBuilder buildCmdLine(String command, String... args) {
        StringBuilder shellLine = new StringBuilder(command);
        for (String arg : args) {
            shellLine.append(" ");
            shellLine.append(arg);
        }
        return shellLine;
    }

    @Override
    public String toString() {
        String serial = "unknown";
        try {
            serial = getSerial();
        } catch (IOException | DeviceException ignore) {
        }
        return "The device's serial number is " + serial;
    }

}
