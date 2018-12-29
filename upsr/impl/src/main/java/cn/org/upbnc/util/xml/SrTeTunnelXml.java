package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SrTeTunnelXml {
    private static final Logger LOG = LoggerFactory.getLogger(SrTeTunnelXml.class);

    public static String createSrTeTunnelXml(List<SSrTeTunnel> srTeTunnels) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n";
        String middle = "";
        for (SSrTeTunnel srTeTunnel : srTeTunnels) {
            middle = middle +
                    "    <mpls xmlns=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                    "      <mplsTe>\n" +
                    "        <srTeTunnels>\n" +
                    "          <srTeTunnel>\n" +
                    "            <tunnelName>" + srTeTunnel.getTunnelName() + "</tunnelName>\n" +
                    "            <mplsTunnelEgressLSRId>" + srTeTunnel.getMplsTunnelEgressLSRId() + "</mplsTunnelEgressLSRId>\n" +
                    "            <mplsTunnelIndex>" + srTeTunnel.getMplsTunnelIndex() + "</mplsTunnelIndex>\n" +
                    "            <mplsTeTunnelSetupPriority>" + srTeTunnel.getMplsTeTunnelSetupPriority() + "</mplsTeTunnelSetupPriority>\n" +
                    "            <holdPriority>" + srTeTunnel.getHoldPriority() + "</holdPriority>\n" +
                    "            <mplsTunnelBandwidth>" + srTeTunnel.getMplsTunnelBandwidth() + "</mplsTunnelBandwidth>\n" +
                    "            <resvForBinding>true</resvForBinding>\n" +
                    "            <tunnelInterface>\n" +
                    "              <interfaceName>" + srTeTunnel.getTunnelName() + "</interfaceName>\n" +
                    "              <statEnable>true</statEnable>\n" +
                    "            </tunnelInterface>\n" +
                    "            <mplsTeTunnelBfd>\n" +
                    "              <mplsTeTunnelBfdMinTx>" + srTeTunnel.getMplsTeTunnelBfdMinTx() + "</mplsTeTunnelBfdMinTx>\n" +
                    "              <mplsTeTunnelBfdMinnRx>" + srTeTunnel.getMplsTeTunnelBfdMinnRx() + "</mplsTeTunnelBfdMinnRx>\n" +
                    "            </mplsTeTunnelBfd>\n" +
                    "            <srTeTunnelPaths>\n" +
                    "              <srTeTunnelPath>\n" +
                    "                <pathType>" + srTeTunnel.getPathType() + "</pathType>\n" +
                    "                <explicitPathName>" + srTeTunnel.getExplicitPathName() + "</explicitPathName>\n" +
                    "              </srTeTunnelPath>\n" +
                    "            </srTeTunnelPaths>\n" +
                    "          </srTeTunnel>\n" +
                    "        </srTeTunnels>\n" +
                    "      </mplsTe>\n" +
                    "    </mpls>\n" +
                    "    <ifm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                    "      <interfaces>\n" +
                    "        <interface>\n" +
                    "          <ifName>" + srTeTunnel.getTunnelName() + "</ifName>\n" +
                    "          <ipv4Config>\n" +
                    "            <unNumIfName>" + srTeTunnel.getUnNumIfName() + "</unNumIfName>\n" +
                    "            <addrCfgType>" + srTeTunnel.getAddrCfgType() + "</addrCfgType>\n" +
                    "          </ipv4Config>\n" +
                    "        </interface>\n" +
                    "      </interfaces>\n" +
                    "    </ifm>\n" +
                    "  </config>\n";
        }
        String end =
                "</edit-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getSrTeTunnelXml(String tunnelName) {
        String str = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<get-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <source>\n" +
                "    <running/>\n" +
                "  </source>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mpls:mplsTe>\n" +
                "        <mpls:srTeTunnels>\n" +
                "          <mpls:srTeTunnel>\n" +
                "            <mpls:tunnelName>" + tunnelName + "</mpls:tunnelName>\n" +
                "          </mpls:srTeTunnel>\n" +
                "        </mpls:srTeTunnels>\n" +
                "      </mpls:mplsTe>\n" +
                "    </mpls:mpls>\n" +
                "    <ifm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "      <interfaces>\n" +
                "        <interface>\n" +
                "          <ifName>" + tunnelName + "</ifName>\n" +
                "        </interface>\n" +
                "      </interfaces>\n" +
                "    </ifm>\n" +
                "  </filter>\n" +
                "</get-config>" +
                "</rpc>";
        return str;
    }

    public static List<SSrTeTunnel> getSrTeTunnelFromXml(String xml) {
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        if (!("".equals(xml))) {


        }
        return srTeTunnels;
    }

    public static String getDeleteSrTeTunnelXml(String tunnelName) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "      <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mpls:mplsTe>\n" +
                "        <mpls:srTeTunnels>\n" +
                "          <mpls:srTeTunnel xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                "            <mpls:tunnelName>" + tunnelName + "</mpls:tunnelName>\n" +
                "          </mpls:srTeTunnel>\n" +
                "        </mpls:srTeTunnels>\n" +
                "      </mpls:mplsTe>\n" +
                "    </mpls:mpls>\n" +
                "  <ifm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "      <interfaces>\n" +
                "        <interface xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                "          <ifName>" + tunnelName + "</ifName>\n" +
                "        </interface>\n" +
                "      </interfaces>\n" +
                "    </ifm>\n" +
                "  </config>\n" +
                "</edit-config>\n" +
                "</rpc>";
        return start;
    }


}
