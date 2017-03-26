package org.cuiyang.adb;

import org.cuiyang.adb.exception.CommandException;
import org.cuiyang.adb.util.IOUtils;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class Transport implements Closeable {

    /** 输入流 */
    private final InputStream inputStream;
    /** 输出流 */
    private final OutputStream outputStream;

    public Transport(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public Transport(Socket socket) throws IOException {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    /**
     * 获取输入流
     * @return 输入流
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 获取输出流
     * @return 输出流
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * 向adb server发送命令
     * @param command adb 命令
     * @throws IOException 和adb server连接异常
     * @throws CommandException adb命令执行失败
     */
    public void send(String command) throws IOException, CommandException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
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
     * @throws CommandException 连接adb server失败
     */
    private void verifyResponse() throws IOException, CommandException {
        String response = read(4);
        if (!"OKAY".equals(response)) {
            String error = read();
            throw new CommandException(error);
        }
    }

    /**
     * 从adb server读取内容
     * @param length 读取长度
     * @return 读取的内容
     * @throws IOException 和adb server连接异常
     */
    public String read(int length) throws IOException {
        DataInput reader = new DataInputStream(inputStream);
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

    /**
     * 关闭
     */
    @Override
    public void close() {
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
    }

}
