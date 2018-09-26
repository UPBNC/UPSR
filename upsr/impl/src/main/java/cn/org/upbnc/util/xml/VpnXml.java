package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class VpnXml {
    private static final Logger LOG = LoggerFactory.getLogger(VpnXml.class);

    public static String createVpnXml(L3vpnInstance l3vpnInstance) {
        String vrfName = l3vpnInstance.getVrfName();
        String vrfDescription = l3vpnInstance.getVrfDescription();
        String vrfRD = l3vpnInstance.getVrfRD();
        String vrfRTValue = l3vpnInstance.getVrfRTValue();
        List<L3vpnIf> l3vpnIfs = l3vpnInstance.getL3vpnIfs();
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
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
        String middle = "";
        if (null != l3vpnIfs) {
            for (L3vpnIf l3vpnIf : l3vpnIfs) {
                middle = middle + "                <l3vpnIf>\n" +
                        "                  <ifName>" + l3vpnIf.getIfName() + "</ifName>\n" +
                        "                  <ipv4Addr>" + l3vpnIf.getIpv4Addr() + "</ipv4Addr>\n" +
                        "                  <subnetMask>" + l3vpnIf.getSubnetMask() + "</subnetMask>\n" +
                        "                </l3vpnIf>\n";
            }
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
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
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

    public static List<L3vpnInstance> getVpnFromXml(String xml) {
        List<L3vpnInstance> l3vpnInstanceList = new ArrayList<>();
        List<L3vpnIf> l3vpnIfs;
        L3vpnInstance l3vpnInstance;
        L3vpnIf l3vpnIf;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element child : childElements) {
                    l3vpnInstance = new L3vpnInstance();
                    l3vpnIfs = new ArrayList<>();
                    l3vpnInstance.setVrfName(child.elementText("vrfName"));
                    l3vpnInstance.setVrfDescription(child.elementText("vrfDescription"));
                    l3vpnInstance.setVrfRD(child.element("vpnInstAFs").elements().get(0).elementText("vrfRD"));
                    l3vpnInstance.setVrfRTValue(child.element("vpnInstAFs").elements().get(0).element("vpnTargets").elements().get(0).elementText("vrfRTValue"));
                    for (org.dom4j.Element children : child.element("l3vpnIfs").elements()) {
                        l3vpnIf = new L3vpnIf();
                        l3vpnIf.setIfName(children.elementText("ifName"));
                        l3vpnIf.setIpv4Addr(children.elementText("ipv4Addr"));
                        l3vpnIf.setSubnetMask(children.elementText("subnetMask"));
                        l3vpnIfs.add(l3vpnIf);
                    }
                    l3vpnInstance.setL3vpnIfs(l3vpnIfs);
                    l3vpnInstanceList.add(l3vpnInstance);
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return l3vpnInstanceList;
    }
}
