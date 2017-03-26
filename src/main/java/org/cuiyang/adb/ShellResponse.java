package org.cuiyang.adb;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 设备执行shell命令的响应.
 *
 * @author cuiyang
 * @since 2017/3/26
 */
public class ShellResponse implements Closeable {

    private Transport transport;

    public ShellResponse(Transport transport) {
        this.transport = transport;
    }

    /**
     * 获取响应流
     * <p>api level 24以上包括（api level 24）获取响应调用该方法
     * @return 响应流
     * @throws IOException 和adb server连接异常
     */
    public InputStream getInputStream() throws IOException {
        return transport.getInputStream();
    }

    /**
     * 获取响应流
     * <p>api level 24以下获取响应调用该方法
     * @return 响应流
     * @throws IOException 和adb server连接异常
     */
    public InputStream getAdbFilterInputStream() throws IOException {
        return new AdbFilterInputStream(new BufferedInputStream(transport.getInputStream()));
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }

}
