package org.cuiyang.adb;

import java.io.IOException;

/**
 * Transport 工厂.
 *
 * @author cuiyang
 * @since 2017/3/23
 */
public interface TransportFactory {

    /**
     * 获取 Transport
     * @return Transport
     * @throws IOException IO异常
     */
    Transport getTransport() throws IOException;

}
