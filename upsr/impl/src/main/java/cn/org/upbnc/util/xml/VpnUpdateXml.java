package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.bgp.BgpPeer;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import cn.org.upbnc.util.netconf.bgp.ImportRoute;
import cn.org.upbnc.util.netconf.bgp.NetworkRoute;

import java.util.List;
import java.util.Map;

public class VpnUpdateXml {
    public static String getUpdateVpnDeleteXml(Map<String, Boolean> map, L3vpnInstance l3vpnInstance, BgpVrf bgpVrf) {
        String result;
        boolean rdChange = map.get("isRdChanged");
        if (map.get("isRtChanged")) {
            rdChange = true;
        }
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <error-option>rollback-on-error</error-option>\n" +
                "  <config>\n";
        String ebgp = "    <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                "      <bgpcomm>\n" +
                "        <bgpVrfs>\n" +
                "          <bgpVrf xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n" +
                "          </bgpVrf>\n" +
                "        </bgpVrfs>\n" +
                "      </bgpcomm>\n" +
                "    </bgp>";
        String vpn =
                "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                        "      <l3vpncomm>\n" +
                        "        <l3vpnInstances>\n" +
                        "          <l3vpnInstance>\n" +
                        "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n" +
                        "            <asNotationCfg/>\n";
        String vpnInstAFs = "            <vpnInstAFs xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>\n";
        String l3vpnIfs = "            <l3vpnIfs xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>\n";
        String l3vpnEnd = "          </l3vpnInstance>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n";
        String end = "  </config>\n" +
                "</edit-config>" +
                "</rpc>";
        result = start;
        if (rdChange && map.get("isIfmChanged")) {
            result = result + ebgp + vpn + vpnInstAFs + l3vpnIfs + l3vpnEnd;
        } else if (rdChange) {
            result = result + ebgp + vpn + vpnInstAFs + l3vpnEnd;
        } else if (map.get("isIfmChanged") && map.get("isEbgpChanged")) {
            result = result + ebgp + vpn + l3vpnIfs + l3vpnEnd;
        } else if (map.get("isIfmChanged")) {
            result = result + vpn + l3vpnIfs + l3vpnEnd;
        } else if (map.get("isEbgpChanged")) {
            result = result + ebgp;
        }
        return result + end;
    }

    public static String getUpdateVpnAddXml(Map<String, Boolean> map, L3vpnInstance l3vpnInstance, BgpVrf bgpVrf) {
        List<L3vpnIf> l3vpnIfList = l3vpnInstance.getL3vpnIfs();
        boolean rdChange = map.get("isRdChanged");
        if (map.get("isRtChanged")) {
            rdChange = true;
        }
        String result;
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <error-option>rollback-on-error</error-option>\n" +
                "  <config>\n";
        String vpnStart = "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "      <l3vpncomm>\n" +
                "        <l3vpnInstances>\n" +
                "          <l3vpnInstance>\n" +
                "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n" +
                "            <vrfDescription>" + l3vpnInstance.getVrfDescription() + "</vrfDescription>\n";
        String vpnInstAFs = "            <vpnInstAFs>\n" +
                "              <vpnInstAF>\n" +
                "                <afType>ipv4uni</afType>\n" +
                "                <vrfRD>" + l3vpnInstance.getVrfRD() + "</vrfRD>\n" +
                "                <vpnTargets>\n" +
                "                  <vpnTarget>\n" +
                "                    <vrfRTValue>" + l3vpnInstance.getVrfRTValue() + "</vrfRTValue>\n" +
                "                    <vrfRTType>export_extcommunity</vrfRTType>\n" +
                "                  </vpnTarget>\n" +
                "                  <vpnTarget>\n" +
                "                    <vrfRTValue>" + l3vpnInstance.getVrfRTValue() + "</vrfRTValue>\n" +
                "                    <vrfRTType>import_extcommunity</vrfRTType>\n" +
                "                  </vpnTarget>\n" +
                "                </vpnTargets>\n" +
                "              </vpnInstAF>\n" +
                "            </vpnInstAFs>\n";
        String l3vpnIfsStart = "    <l3vpnIfs>\n";
        String l3vpnIfsMiddle = "";
        for (L3vpnIf l3vpnIf : l3vpnIfList) {
            l3vpnIfsMiddle = l3vpnIfsMiddle + "              <l3vpnIf>\n" +
                    "                <ifName>" + l3vpnIf.getIfName() + "</ifName>\n" +
                    "                <ipv4Addr>" + l3vpnIf.getIpv4Addr() + "</ipv4Addr>\n" +
                    "                <subnetMask>" + l3vpnIf.getSubnetMask() + "</subnetMask>\n" +
                    "              </l3vpnIf>\n";
        }
        String l3vpnIfsEnd = "            </l3vpnIfs>\n";
        String VpnEnd = "          </l3vpnInstance>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n";

        String bgpStart =
                "      <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                        "        <bgpcomm>\n" +
                        "          <bgpVrfs>\n" +
                        "            <bgpVrf nc:operation=\"create\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                        "              <vrfName>" + bgpVrf.getVrfName() + "</vrfName>\n" +
                        "                    <bgpPeers>\n";
        String bgpPeers = "";
        if (null != bgpVrf.getBgpPeers()) {
            for (BgpPeer bgpPeer : bgpVrf.getBgpPeers()) {
                bgpPeers = bgpPeers + "                       <bgpPeer>\n" +
                        "                         <peerAddr>" + bgpPeer.getPeerAddr() + "</peerAddr>\n" +
                        "                         <remoteAs>" + bgpPeer.getRemoteAs() + "</remoteAs>\n" +
                        "                           </bgpPeer>\n";
            }
        }
        String middle = "                      </bgpPeers>\n" +
                "              <bgpVrfAFs>\n" +
                "                <bgpVrfAF>\n" +
                "                  <afType>ipv4uni</afType>\n" +
                "                  <importRoutes>\n";
        String importRoutes = "";
        if (null != bgpVrf.getImportRoutes()) {
            for (ImportRoute importRoute : bgpVrf.getImportRoutes()) {
                importRoutes = importRoutes + "                    <importRoute>\n" +
                        "                      <importProtocol>" + importRoute.getImportProtocol() + "</importProtocol>\n" +
                        "                      <importProcessId>" + importRoute.getImportProcessId() + "</importProcessId>\n" +
                        "                    </importRoute>\n";
            }
        }
        String middle1 =
                "                  </importRoutes>\n" +
                        "                  <networkRoutes>\n";
        String networkRoutes = "";
        if (null != bgpVrf.getNetworkRoutes()) {
            for (NetworkRoute networkRoute : bgpVrf.getNetworkRoutes()) {
                networkRoutes = networkRoutes +
                        "                    <networkRoute>\n" +
                        "                      <networkAddress>" + networkRoute.getNetworkAddress() + "</networkAddress>\n" +
                        "                      <maskLen>" + networkRoute.getMaskLen() + "</maskLen>\n" +
                        "                    </networkRoute>\n";
            }
        }
        String bgpEnd =
                "                  </networkRoutes>\n" +
                        "                </bgpVrfAF>\n" +
                        "              </bgpVrfAFs>\n" +
                        "            </bgpVrf>\n" +
                        "          </bgpVrfs>\n" +
                        "        </bgpcomm>\n" +
                        "      </bgp>\n";
        String end = "  </config>\n" +
                "</edit-config>" +
                "</rpc>";
        result = start;
        String ebgp = bgpStart + bgpPeers + middle + importRoutes + middle1 + networkRoutes + bgpEnd;
        String intf = l3vpnIfsStart + l3vpnIfsMiddle + l3vpnIfsEnd;
        if (rdChange && map.get("isIfmChanged") && l3vpnIfList.size() > 0) {
            result = result + vpnStart + vpnInstAFs + intf + VpnEnd + ebgp;
        } else if (rdChange) {
            result = result + vpnStart + vpnInstAFs + VpnEnd + ebgp;
        } else if (map.get("isIfmChanged") && map.get("isEbgpChanged")) {
            result = result + vpnStart + intf + VpnEnd + ebgp;
        } else if (map.get("isIfmChanged")) {
            result = result + vpnStart + intf + VpnEnd;
        } else if (map.get("isEbgpChanged")) {
            result = result + ebgp;
        }
        return result + end;
    }
}
