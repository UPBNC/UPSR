package cn.org.upbnc.util.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunningXml {
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitPathXml.class);

    public static String getRunningXml() {
        return null;
    }

    public static String getRunningTunnelXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
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
                "          <mpls:explicitPaths>\n" +
                "            <mpls:explicitPath>\n" +
                "              <mpls:explicitPathName></mpls:explicitPathName>\n" +
                "              <mpls:explicitPathHops>\n" +
                "                <mpls:explicitPathHop>\n" +
                "                  <mpls:mplsTunnelHopIndex></mpls:mplsTunnelHopIndex>\n" +
                "                  <mpls:mplsTunnelHopMode></mpls:mplsTunnelHopMode>\n" +
                "                  <mpls:mplsTunnelHopSidLabel></mpls:mplsTunnelHopSidLabel>\n" +
                "                  <mpls:mplsTunnelHopSidLabelType></mpls:mplsTunnelHopSidLabelType>\n" +
                "                </mpls:explicitPathHop>\n" +
                "              </mpls:explicitPathHops>\n" +
                "            </mpls:explicitPath>\n" +
                "          </mpls:explicitPaths>\n" +
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

    public static String getRunningSrLabelXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
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

    public static String getAreasXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
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
                "                      <ospfv2:ifName>LoopBack0</ospfv2:ifName>" +
                "                      <ospfv2:srInterface>\n" +
                "                        <ospfv2:prefixSidType/>\n" +
                "                        <ospfv2:prefixLabel/>\n" +
                "                      </ospfv2:srInterface>\n" +
                "                    </ospfv2:interface>\n" +
                "                  </ospfv2:interfaces>\n" +
                "                </ospfv2:area>\n" +
                "              </ospfv2:areas>\n" +
                "            </ospfv2:ospfSite>\n" +
                "          </ospfv2:ospfSites>\n" +
                "        </ospfv2:ospfv2comm>\n" +
                "      </ospfv2:ospfv2>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getSrgbXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "        <ospfv2:ospfv2comm>\n" +
                "          <ospfv2:ospfSites>\n" +
                "            <ospfv2:ospfSite>\n" +
                "              <ospfv2:ospfSrgbs>\n" +
                "                <ospfv2:ospfSrgb/>\n" +
                "              </ospfv2:ospfSrgbs>\n" +
                "            </ospfv2:ospfSite>\n" +
                "          </ospfv2:ospfSites>\n" +
                "        </ospfv2:ospfv2comm>\n" +
                "      </ospfv2:ospfv2>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getSegrXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <segr:segr xmlns:segr=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "        <segr:staticIpv4Adjs/>\n" +
                "      </segr:segr>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getEbgpXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <bgp:bgp xmlns:bgp=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                "        <bgp:bgpcomm>\n" +
                "          <bgp:bgpVrfs>\n" +
                "            <bgp:bgpVrf>\n" +
                "              <bgp:vrfName></bgp:vrfName>\n" +
                "              <bgp:bgpPeers>\n" +
                "                <bgp:bgpPeer>\n" +
                "                  <bgp:peerAddr></bgp:peerAddr>\n" +
                "                  <bgp:remoteAs></bgp:remoteAs>\n" +
                "                </bgp:bgpPeer>\n" +
                "              </bgp:bgpPeers>\n" +
                "              <bgp:bgpVrfAFs>\n" +
                "                <bgp:bgpVrfAF>\n" +
                "                  <bgp:afType></bgp:afType>\n" +
                "              <bgp:preferenceExternal></bgp:preferenceExternal>\n" +
                "              <bgp:preferenceInternal></bgp:preferenceInternal>\n" +
                "              <bgp:preferenceLocal></bgp:preferenceLocal>\n" +
                "                <bgp:peerAFs>\n" +
                "                <bgp:peerAF>\n" +
                "                   <bgp:advertiseCommunity></bgp:advertiseCommunity>\n" +
                "                   <bgp:remoteAddress></bgp:remoteAddress>\n" +
                "                  <bgp:importRtPolicyName></bgp:importRtPolicyName>\n" +
                "                  <bgp:exportRtPolicyName></bgp:exportRtPolicyName>\n" +
                "                </bgp:peerAF>\n" +
                "                </bgp:peerAFs>\n" +
                "                  <bgp:importRoutes>\n" +
                "                    <bgp:importRoute>\n" +
                "                      <bgp:importProtocol></bgp:importProtocol>\n" +
                "                      <bgp:importProcessId></bgp:importProcessId>\n" +
                "                    </bgp:importRoute>\n" +
                "                  </bgp:importRoutes>\n" +
                "                  <bgp:networkRoutes>\n" +
                "                    <bgp:networkRoute>\n" +
                "                      <bgp:networkAddress></bgp:networkAddress>\n" +
                "                      <bgp:maskLen></bgp:maskLen>\n" +
                "                    </bgp:networkRoute>\n" +
                "                  </bgp:networkRoutes>\n" +
                "                </bgp:bgpVrfAF>\n" +
                "              </bgp:bgpVrfAFs>\n" +
                "            </bgp:bgpVrf>\n" +
                "          </bgp:bgpVrfs>\n" +
                "        </bgp:bgpcomm>\n" +
                "      </bgp:bgp>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getVpnXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "        <l3vpncomm>\n" +
                "          <l3vpnInstances>\n" +
                "            <l3vpnInstance>\n" +
                "              <vrfName/>\n" +
                "              <vrfDescription/>\n" +
                "              <trafficStatisticEnable/>\n" +
                "              <vpnInstAFs>\n" +
                "                <vpnInstAF xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "                  <afType>ipv4uni</afType>\n" +
                "                  <vrfRD/>\n" +
                "                  <vpnFrr/>\n" +
                "                  <vrfLabelMode/>\n" +
                "                  <tnlPolicyName/>\n" +
                "                  <l3vpnTtlMode>\n" +
                "                    <ttlMode></ttlMode>\n" +
                "                  </l3vpnTtlMode>\n" +
                "                  <vpnTargets/>\n" +
                "                </vpnInstAF>\n" +
                "              </vpnInstAFs>\n" +
//                "              <l3vpnIfs/>\n" +
                "            </l3vpnInstance>\n" +
                "          </l3vpnInstances>\n" +
                "        </l3vpncomm>\n" +
                "      </l3vpn>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getvpn() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<get-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <source>\n" +
                "    <running/>\n" +
                "  </source>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "      <l3vpncomm>\n" +
                "        <l3vpnInstances>\n" +
                "          <l3vpnInstance/>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n" +
                "  </filter>\n" +
                "</get-config>" +
                "</rpc>";
        return xml;
    }

    public static String getIfmXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "        <ifm:interfaces>\n" +
                "          <ifm:interface>\n" +
                "            <ifm:ipv4Config></ifm:ipv4Config>\n" +
                "          </ifm:interface>\n" +
                "        </ifm:interfaces>\n" +
                "      </ifm:ifm>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;

    }

    public static String getRunningVpnXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <bgp:bgp xmlns:bgp=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                "        <bgp:bgpcomm>\n" +
                "          <bgp:bgpVrfs>\n" +
                "            <bgp:bgpVrf>\n" +
                "              <bgp:vrfName></bgp:vrfName>\n" +
                "              <bgp:bgpPeers>\n" +
                "                <bgp:bgpPeer>\n" +
                "                  <bgp:peerAddr></bgp:peerAddr>\n" +
                "                  <bgp:remoteAs></bgp:remoteAs>\n" +
                "                </bgp:bgpPeer>\n" +
                "              </bgp:bgpPeers>\n" +
                "              <bgp:bgpVrfAFs>\n" +
                "                <bgp:bgpVrfAF>\n" +
                "                  <bgp:afType></bgp:afType>\n" +
                "                  <bgp:importRoutes>\n" +
                "                    <bgp:importRoute>\n" +
                "                      <bgp:importProtocol></bgp:importProtocol>\n" +
                "                      <bgp:importProcessId></bgp:importProcessId>\n" +
                "                    </bgp:importRoute>\n" +
                "                  </bgp:importRoutes>\n" +
                "                  <bgp:networkRoutes>\n" +
                "                    <bgp:networkRoute>\n" +
                "                      <bgp:networkAddress></bgp:networkAddress>\n" +
                "                      <bgp:maskLen></bgp:maskLen>\n" +
                "                    </bgp:networkRoute>\n" +
                "                  </bgp:networkRoutes>\n" +
                "                </bgp:bgpVrfAF>\n" +
                "              </bgp:bgpVrfAFs>\n" +
                "            </bgp:bgpVrf>\n" +
                "          </bgp:bgpVrfs>\n" +
                "        </bgp:bgpcomm>\n" +
                "      </bgp:bgp>\n" +
                "      <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "        <ifm:interfaces>\n" +
                "          <ifm:interface>\n" +
                "            <ifm:ipv4Config></ifm:ipv4Config>\n" +
                "          </ifm:interface>\n" +
                "        </ifm:interfaces>\n" +
                "      </ifm:ifm>\n" +
                "      <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "        <l3vpncomm>\n" +
                "          <l3vpnInstances>\n" +
                "            <l3vpnInstance>\n" +
                "              <vrfName/>\n" +
                "              <vrfDescription/>\n" +
                "              <trafficStatisticEnable/>\n" +
                "              <vpnInstAFs>\n" +
                "                <vpnInstAF xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "                  <afType>ipv4uni</afType>\n" +
                "                  <vrfRD/>\n" +
                "                  <vpnFrr/>\n" +
                "                  <vrfLabelMode/>\n" +
                "                  <tnlPolicyName/>\n" +
                "                  <l3vpnTtlMode>\n" +
                "                    <ttlMode></ttlMode>\n" +
                "                  </l3vpnTtlMode>\n" +
                "                  <vpnTargets/>\n" +
                "                </vpnInstAF>\n" +
                "              </vpnInstAFs>\n" +
                "              <l3vpnIfs/>\n" +
                "            </l3vpnInstance>\n" +
                "          </l3vpnInstances>\n" +
                "        </l3vpncomm>\n" +
                "      </l3vpn>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

    public static String getTunnelIfmXml() {
        String str = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
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
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return str;
    }

    public static String getTunnelXml() {
        String str = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
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
        return str;
    }

    public static String getExplicitPathXml() {
        String str = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "        <mpls:mplsTe>\n" +
                "          <mpls:explicitPaths>\n" +
                "            <mpls:explicitPath>\n" +
                "              <mpls:explicitPathName></mpls:explicitPathName>\n" +
                "              <mpls:explicitPathHops>\n" +
                "                <mpls:explicitPathHop>\n" +
                "                  <mpls:mplsTunnelHopIndex></mpls:mplsTunnelHopIndex>\n" +
                "                  <mpls:mplsTunnelHopMode></mpls:mplsTunnelHopMode>\n" +
                "                  <mpls:mplsTunnelHopSidLabel></mpls:mplsTunnelHopSidLabel>\n" +
                "                  <mpls:mplsTunnelHopSidLabelType></mpls:mplsTunnelHopSidLabelType>\n" +
                "                </mpls:explicitPathHop>\n" +
                "              </mpls:explicitPathHops>\n" +
                "            </mpls:explicitPath>\n" +
                "          </mpls:explicitPaths>\n" +
                "        </mpls:mplsTe>\n" +
                "      </mpls:mpls>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return str;
    }
}
