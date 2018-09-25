package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.L3vpnIf;

import java.util.List;

public class VpnXml {
    private static int message_id = 100;

    public static int genarate_message_id() {
        if (message_id > 65535)
            message_id = 100;
        else
            message_id++;
        return message_id;
    }

    public static String createVpnXml(String vrfName, String vrfDescription, String vrfRD, String vrfRTValue, List<L3vpnIf> l3vpnIfs) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + genarate_message_id() + "\">\n" +
                "  <edit-config>\n" +
                "    <target>\n" +
                "      <running/>\n" +
                "    </target>\n" +
                "    <config>\n" +
                "      <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "        <l3vpncomm>\n" +
                "          <l3vpnInstances>\n" +
                "            <l3vpnInstance>\n" +
                "              <vrfName>" + vrfName + "</vrfName>\n" +
                "              <vrfDescription>" + vrfDescription + "</vrfDescription>\n" +
                "              <asNotationCfg/>\n" +
                "              <vpnInstAFs>\n" +
                "                <vpnInstAF>\n" +
                "                  <afType>ipv4uni</afType>\n" +
                "                  <vrfRD>" + vrfRD + "</vrfRD>\n" +
                "                  <vpnTargets>\n" +
                "                    <vpnTarget>\n" +
                "                      <vrfRTValue>" + vrfRTValue + "</vrfRTValue>\n" +
                "                      <vrfRTType>export_extcommunity</vrfRTType>\n" +
                "                    </vpnTarget>\n" +
                "                    <vpnTarget>\n" +
                "                      <vrfRTValue>" + vrfRTValue + "</vrfRTValue>\n" +
                "                      <vrfRTType>import_extcommunity</vrfRTType>\n" +
                "                    </vpnTarget>\n" +
                "                  </vpnTargets>\n" +
                "                </vpnInstAF>\n" +
                "              </vpnInstAFs>\n" +
                "              <l3vpnIfs>\n";
        String middle = null;
        for (L3vpnIf l3vpnIf : l3vpnIfs) {
            middle = middle + "                <l3vpnIf>\n" +
                    "                  <ifName>" + l3vpnIf.getIfName() + "</ifName>\n" +
                    "                  <ipv4Addr>" + l3vpnIf.getIpv4Addr() + "</ipv4Addr>\n" +
                    "                  <subnetMask>" + l3vpnIf.getSubnetMask() + "</subnetMask>\n" +
                    "                </l3vpnIf>\n";
        }
        String end = "              </l3vpnIfs>\n" +
                "            </l3vpnInstance>\n" +
                "          </l3vpnInstances>\n" +
                "        </l3vpncomm>\n" +
                "      </l3vpn>\n" +
                "    </config>\n" +
                "  </edit-config>\n" +
                "</rpc>";

        return start + middle + end;
    }

    public static String getVpnXml(String vrfName) {
        String str = "<rpc message-id =\"" + genarate_message_id() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "      <l3vpncomm>\n" +
                "        <l3vpnInstances>\n" +
                "          <l3vpnInstance>\n" +
                "            <vrfName>" + vrfName + "</vrfName>\n" +
                "            <vrfDescription/>\n" +
                "            <trafficStatisticEnable/>\n" +
                "            <vpnInstAFs>\n" +
                "              <vpnInstAF xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "                <afType>ipv4uni</afType>\n" +
                "                <vrfRD/>\n" +
                "                <vpnTargets/>\n" +
                "              </vpnInstAF>\n" +
                "            </vpnInstAFs>\n" +
                "            <l3vpnIfs/>\n" +
                "          </l3vpnInstance>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n" +
                "  </filter>\n" +
                "</get>" +
                "</rpc>";
        return str;
    }
}
