package org.cuiyang.adb;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * adb client和adb server传输类.
 * <p>负责adb client和adb server交互.
 *
 * @author cuiyang
 * @since 2017/3/23
 */
public class Transport extends Socket {

    public Transport(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * 向adb server发送命令
     * @param command adb 命令
     * @throws IOException 和adb server连接异常
     * @throws AdbException adb命令执行失败
     */
    public void send(String command) throws IOException, AdbException {
        OutputStreamWriter writer = new OutputStreamWriter(getOutputStream());
        writer.write(getHexLength(command));
        writer.write(command);
        writer.flush();
        verifyResponse();
    }

    /**
     * 获取16进制长度
     * @return 16进制长度
     */
    private String getHexLength(String command) {
        return String.format("%04x", command.length());
    }

    /**
     * 验证adb server响应结果
     * @throws IOException 和adb server连接异常
     * @throws AdbException adb响应为失败
     */
    public void verifyResponse() throws IOException, AdbException {
        String response = read(4);
        if (!"OKAY".equals(response)) {
            String error = read();
            throw new AdbException(error);
        }
    }

    /**
     * 从adb server读取内容
     * @param length 读取长度
     * @return 读取的内容
     * @throws IOException 和adb server连接异常
     */
    public String read(int length) throws IOException {
        DataInputStream reader = new DataInputStream(getInputStream());
        byte[] buffer = new byte[length];
        reader.readFully(buffer);
        return new String(buffer, Charset.forName("UTF-8"));
    }

    /**
     * 从adb server读取所有内容
     * @return 读取的内容
     * @throws IOException 和adb server连接异常
     */
    public String read() throws IOException {
        String encodedLength = read(4);
        int length = Integer.parseInt(encodedLength, 16);
        return read(length);
    }

}
