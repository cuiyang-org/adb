package org.cuiyang.adb;

import java.io.IOException;

/**
 * 指定序列号的设备.
 *
 * @author cuiyang
 * @since 2017/3/26
 */
public class SerialNoDevice extends AbstractDevice {

    /** 序列号 */
    private String serialNo;

    /**
     * 构造函数
     * @param factory Transport工厂
     * @param serialNo 设备序列号
     */
    public SerialNoDevice(TransportFactory factory, String serialNo) {
        super(factory);
        this.serialNo = serialNo;
    }

    @Override
    public String getSerialNo() throws IOException, AdbException {
        return serialNo;
    }

    @Override
    public Type getType() {
        return Type.SERIAL_NO;
    }

}
