package cn.org.upbnc.service.entity;

public class TunnelHopServiceEntity {
    private String adjlabel;
    private String deviceName;
    private String ifAddress;
    private String index;
    private String routerId;

    public String getAdjlabel() {
        return adjlabel;
    }

    public void setAdjlabel(String adjlabel) {
        this.adjlabel = adjlabel;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIfAddress() {
        return ifAddress;
    }

    public void setIfAddress(String ifAddress) {
        this.ifAddress = ifAddress;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    @Override
    public String toString() {
        return "TunnelHopServiceEntity{" +
                "adjlabel='" + adjlabel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", ifAddress='" + ifAddress + '\'' +
                ", index='" + index + '\'' +
                ", routerId='" + routerId + '\'' +
                '}';
    }
}
