package cn.org.upbnc.entity;

public class BgpLink {
    private Integer id;
    private String name;
    private BgpDeviceInterface bgpDeviceInterface1;
    private BgpDeviceInterface bgpDeviceInterface2;
    private Long metric;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBgpDeviceInterface1(BgpDeviceInterface bgpDeviceInterface1) {
        this.bgpDeviceInterface1 = bgpDeviceInterface1;
    }

    public BgpDeviceInterface getBgpDeviceInterface1() {
        return bgpDeviceInterface1;
    }

    public void setBgpDeviceInterface2(BgpDeviceInterface bgpDeviceInterface2) {
        this.bgpDeviceInterface2 = bgpDeviceInterface2;
    }

    public BgpDeviceInterface getBgpDeviceInterface2() {
        return bgpDeviceInterface2;
    }

    public void setMetric(Long metric) {
        this.metric = metric;
    }

    public Long getMetric() {
        return metric;
    }
}
