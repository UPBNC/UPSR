package cn.org.upbnc.base.entity;

public class VPNInstance {
    private Integer id;
    private Device device;
    private List<DeviceInterface> deviceInterfaceList;
    private String vpnName;
    private String businessRegion;
    private String RD;
    private String importRT;
    private String exportRT;
    private Integer peerAS;
    private Address peerIP;
    private Integer routeSelectDelay;
    private Integer importDirectRouteEnable;
    private List<NetworkSeg> networkSegList;
}
