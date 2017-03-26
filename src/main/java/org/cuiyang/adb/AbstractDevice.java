package org.cuiyang.adb;

import org.cuiyang.adb.exception.CommandException;
import org.cuiyang.adb.exception.DeviceException;

import java.io.IOException;

/**
 * Device 基类.
 *
 * @author cuiyang
 * @since 2017/3/26
 */
public abstract class AbstractDevice implements Device {

    /** Transport 工厂 */
    private TransportFactory factory;

    public AbstractDevice(TransportFactory factory) {
        this.factory = factory;
    }

    /**
     * 获取Transport.
     * @throws IOException 和adb server连接异常
     * @throws DeviceException 设备异常
     */
    protected Transport getTransport() throws IOException, DeviceException {
        Transport transport = factory.getTransport();
        try {
            if (Type.SERIAL_NO == getType()) {
                transport.send(getType().getCommand() + getSerialNo());
            } else {
                transport.send(getType().getCommand());
            }
        } catch (CommandException e) {
            throw new DeviceException(e);
        }
        return transport;
    }

    @Override
    public String getSerialNo() throws IOException, DeviceException {
        if (Type.SERIAL_NO == getType()) {
            throw new DeviceException("请重写getSerialNo()方法");
        }
        try(Transport transport = factory.getTransport()) {
            transport.send("host:get-serialno");
            return transport.read();
        } catch (CommandException e) {
            throw new DeviceException(e);
        }
    }

    @Override
    public State getState() throws IOException, DeviceException {
        try(Transport transport = factory.getTransport()) {
            if (Type.SERIAL_NO == getType()) {
                transport.send("host-serial:" + getSerialNo() + ":get-state");
            } else {
                transport.send("host:get-state");
            }
            String result = transport.read();
            return State.valueOf(result.toUpperCase());
        } catch (CommandException e) {
            throw new DeviceException(e);
        }
    }

    @Override
    public ShellResponse shell(String command, String... args) throws IOException, DeviceException, CommandException {
        Transport transport = getTransport();
        StringBuilder shellLine = buildCmdLine(command, args);
        transport.send("shell:" + shellLine.toString());
        return new ShellResponse(transport);
    }

    /**
     * 构建命令.
     */
    protected StringBuilder buildCmdLine(String command, String... args) {
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
            serial = getSerialNo();
        } catch (IOException | DeviceException ignore) {
        }
        return "The device's serial number is " + serial;
    }
}
