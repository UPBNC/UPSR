package cn.org.upbnc.enumtype;

public enum SrLabelErrorCodeEnum {
    EXECUTE_SUCCESS(0, "success"),
    INPUT_INVALID(1, "Input invalid"),
    LABEL_INVALID(2, "Label invalid"),
    DEVICE_INVALID(3, "Device invalid"),
    CONFIG_FAILED(4, "Config failed"),
    LABEL_DUPLICATED(5, "Adjacency label is duplicated"),
    NETCONF_INVALID(6, "Netconf client is null");

    private int code;
    private String message;

    public static final SrLabelErrorCodeEnum[] values = SrLabelErrorCodeEnum.values();

    SrLabelErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static SrLabelErrorCodeEnum valueOf(int i) {
        return values[i];
    }
}
