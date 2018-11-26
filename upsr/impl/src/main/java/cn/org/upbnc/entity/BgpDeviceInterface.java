package cn.org.upbnc.entity;

public class BgpDeviceInterface {
    private Integer id;
    private String name;
    private String bgpDeviceName;
    private Integer srStatus;
    private Integer status;
    private Address ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBgpDeviceName(String bgpDeviceName) {
        this.bgpDeviceName = bgpDeviceName;
    }

    public String getBgpDeviceName() {
        return bgpDeviceName;
    }

    public void setIp(Address ip) {
        this.ip = ip;
    }

    public Address getIp() {
        return ip;
    }
}
