package cn.org.upbnc.util.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandidateXml {
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitPathXml.class);

    public static String getCandidateXml() {
        return null;
    }
    public static String getCandidateTunnelXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <candidate/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "        <ifm:interfaces>\n" +
                "          <ifm:interface>\n" +
                "            <ifm:ifName/>\n" +
                "            <ifm:ifPhyType>Tunnel</ifm:ifPhyType>\n" +
                "            <ifm:ifDescr/>\n" +
                "            <ifm:ipv4Config>\n" +
                "              <ifm:addrCfgType/>\n" +
                "              <ifm:unNumIfName/>\n" +
                "            </ifm:ipv4Config>\n" +
                "          </ifm:interface>\n" +
                "        </ifm:interfaces>\n" +
                "      </ifm:ifm>\n" +
                "      <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "        <mpls:mplsTe>\n" +
                "          <mpls:srTeTunnels>\n" +
                "            <mpls:srTeTunnel>\n" +
                "              <mpls:tunnelName/>\n" +
                "              <mpls:mplsTunnelEgressLSRId/>\n" +
                "              <mpls:mplsTunnelIndex/>\n" +
                "              <mpls:mplsTunnelBandwidth/>\n" +
                "              <mpls:mplsTeTunnelSetupPriority/>\n" +
                "              <mpls:holdPriority/>\n" +
                "              <mpls:hotStandbyEnable/>\n" +
                "              <mpls:resvForBinding/>\n" +
                "              <mpls:srTeTunnelPaths>\n" +
                "                <mpls:srTeTunnelPath>\n" +
                "                  <mpls:pathType/>\n" +
                "                  <mpls:explicitPathName/>\n" +
                "                </mpls:srTeTunnelPath>\n" +
                "              </mpls:srTeTunnelPaths>\n" +
                "              <mpls:tunnelInterface>\n" +
                "                <mpls:interfaceName/>\n" +
                "                <mpls:lsp_tpEnable/>\n" +
                "                <mpls:statEnable/>\n" +
                "                <mpls:mplsteServiceClass/>\n" +
                "              </mpls:tunnelInterface>\n" +
                "              <mpls:mplsTeTunnelBfd/>\n" +
                "            </mpls:srTeTunnel>\n" +
                "          </mpls:srTeTunnels>\n" +
                "        </mpls:mplsTe>\n" +
                "      </mpls:mpls>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }
    public static String getCandidateSrLabelXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <candidate/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "        <ospfv2:ospfv2comm>\n" +
                "          <ospfv2:ospfSites>\n" +
                "            <ospfv2:ospfSite>\n" +
                "              <ospfv2:areas>\n" +
                "                <ospfv2:area>\n" +
                "                  <ospfv2:interfaces>\n" +
                "                    <ospfv2:interface>\n" +
                "                      <ospfv2:srInterface>\n" +
                "                        <ospfv2:prefixSidType/>\n" +
                "                        <ospfv2:prefixLabel/>\n" +
                "                      </ospfv2:srInterface>\n" +
                "                    </ospfv2:interface>\n" +
                "                  </ospfv2:interfaces>\n" +
                "                </ospfv2:area>\n" +
                "              </ospfv2:areas>\n" +
                "              <ospfv2:ospfSrgbs>\n" +
                "                <ospfv2:ospfSrgb/>\n" +
                "              </ospfv2:ospfSrgbs>\n" +
                "            </ospfv2:ospfSite>\n" +
                "          </ospfv2:ospfSites>\n" +
                "        </ospfv2:ospfv2comm>\n" +
                "      </ospfv2:ospfv2>\n" +
                "      <segr:segr xmlns:segr=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "        <segr:staticIpv4Adjs/>\n" +
                "      </segr:segr>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }
}
