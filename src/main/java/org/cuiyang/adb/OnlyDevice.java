package org.cuiyang.adb;

/**
 * 只有一部设备.
 * <p>
 * <ul>
 * <li>1.通过usb连接的设备只有一部.
 * <li>2.通过tcp连接的设备只有一部.
 * <li>3.通过usb连接或通过tcp连接的设备只有一部.
 *
 * @author cuiyang
 * @since 2017/3/26
 */
public class OnlyDevice extends AbstractDevice {

    /** 设备类型 */
    private Type type;

    /**
     * 构造函数
     * @param factory Transport工厂
     * @param type 设备类型
     */
    public OnlyDevice(TransportFactory factory, Type type) {
        super(factory);
        if (type == Type.SERIAL_NO) {
            throw new IllegalArgumentException("设备类型不能是SERIAL_NO");
        }
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

}
