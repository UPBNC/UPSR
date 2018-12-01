package cn.org.upbnc.util.netconf.bgp;

public class NetworkRoute {
    private String networkAddress;
    private String maskLen;

    public NetworkRoute(String networkAddress, String maskLen){
        this.networkAddress=networkAddress;
        this.maskLen=maskLen;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public String getMaskLen(){
        return maskLen;
    }

    public void setMaskLen(String maskLen) {
        this.maskLen = maskLen;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }
}
