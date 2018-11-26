package cn.org.upbnc.entity;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.LinkInfo;

import java.util.ArrayList;
import java.util.List;

public class BgpTopoInfo {
    List<BgpDevice> bgpDeviceList;
    List<BgpLink> bgpLinkList;

    public BgpTopoInfo(){
        this.bgpDeviceList = null;
        this.bgpLinkList = null;
    }

    public BgpTopoInfo(List<BgpDevice> bgpDeviceList, List<BgpLink> bgpLinkList){
        this.bgpLinkList = bgpLinkList;
        this.bgpDeviceList = bgpDeviceList;
        return;
    }

    public List<BgpDevice> getBgpDeviceList() {
        return bgpDeviceList;
    }

    public void setBgpDeviceList(List<BgpDevice> bgpDeviceList) {
        this.bgpDeviceList = bgpDeviceList;
    }

    public void setBgpLinkList(List<BgpLink> bgpLinkList) {
        this.bgpLinkList = bgpLinkList;
    }

    public List<BgpLink> getBgpLinkList() {
        return bgpLinkList;
    }
}
