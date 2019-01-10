package cn.org.upbnc.enumtype;

public enum TunnelErrorCodeEnum {
    EXECUTE_SUCCESS(0, "success"),
    DEVICE_INVALID(1, "Device invalid"),
    INPUT_INVALID(2, "Input invalid");

    private int code;
    private String message;

    TunnelErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
