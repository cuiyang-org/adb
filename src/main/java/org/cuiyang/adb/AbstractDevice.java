package org.cuiyang.adb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * @throws AdbException 设备异常
     */
    protected Transport getTransport() throws IOException, AdbException {
        Transport transport = null;
        try {
            transport = factory.getTransport();
            if (Type.SERIAL_NO == getType()) {
                transport.send(getType().getCommand() + getSerialNo());
            } else {
                transport.send(getType().getCommand());
            }
            return transport;
        } catch (Exception e) {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (Exception ignore) {
            }
            throw e;
        }
    }

    @Override
    public String getSerialNo() throws IOException, AdbException {
        if (Type.SERIAL_NO == getType()) {
            throw new AdbException("请重写getSerialNo()方法");
        }
        try(Transport transport = factory.getTransport()) {
            transport.send("host:get-serialno");
            return transport.read();
        }
    }

    @Override
    public State getState() throws IOException, AdbException {
        try(Transport transport = factory.getTransport()) {
            if (Type.SERIAL_NO == getType()) {
                transport.send("host-serial:" + getSerialNo() + ":get-state");
            } else {
                transport.send("host:get-state");
            }
            String result = transport.read();
            return State.valueOf(result.toUpperCase());
        }
    }

    @Override
    public ShellResponse shell(String command, String... args) throws IOException, AdbException {
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
    public void push(InputStream local, String remote, long lastModified, int mode) throws IOException, AdbException {
        try (Transport transport = getTransport()) {
            transport.send("sync:");
            DataOutputStream out = new DataOutputStream(transport.getOutputStream());
            out.writeBytes("SEND");
            StringBuilder arg = new StringBuilder(remote).append(",").append(Integer.toString(mode));
            out.writeInt(Integer.reverseBytes(arg.length()));
            out.writeBytes(arg.toString());

            int len;
            byte[] buffer = new byte[1024];
            while ((len = local.read(buffer)) != -1) {
                out.writeBytes("DATA");
                out.writeInt(Integer.reverseBytes(len));
                out.write(buffer, 0, len);
            }

            out.writeBytes("DONE");
            out.writeLong(Long.reverseBytes(lastModified));
            transport.verifyResponse();
        }
    }

    @Override
    public void push(File local, String remote) throws IOException, AdbException {
        try (InputStream inputStream = new FileInputStream(local)) {
            push(inputStream, remote, local.lastModified(), 644);
        }
    }

    @Override
    public void pull(String remote, OutputStream local) throws IOException, AdbException {
        try (Transport transport = getTransport()) {
            transport.send("sync:");
            DataOutputStream out = new DataOutputStream(transport.getOutputStream());
            out.writeBytes("RECV");
            out.writeInt(Integer.reverseBytes(remote.length()));
            out.writeBytes(remote);

            DataInputStream in = new DataInputStream(transport.getInputStream());
            int len = 0;
            byte[] buffer = new byte[1024];
            while (len != -1) {
                String result = transport.read(4);
                len = Integer.reverseBytes(in.readInt());
                if ("FAIL".equals(result)) {
                    throw new AdbException(transport.read(len));
                }
                if (!"DATA".equals(result)) {
                    break;
                }
                in.readFully(buffer, 0, len);
                local.write(buffer, 0, len);
            }
        }
    }

    @Override
    public void pull(String remote, File local) throws IOException, AdbException {
        try (FileOutputStream outputStream = new FileOutputStream(local)) {
            pull(remote, outputStream);
        }
    }

    @Override
    public String toString() {
        String serial = "unknown";
        try {
            serial = getSerialNo();
        } catch (IOException | AdbException ignore) {
        }
        return "The device's serial number is " + serial;
    }

}
