/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.L3vpnIf;
import cn.org.upbnc.util.netconf.L3vpnInstance;
import cn.org.upbnc.util.netconf.SBgpVrfAF;
import cn.org.upbnc.util.netconf.SPeerAF;
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
        String ebgp = "";
        if (null != bgpVrf) {
            ebgp = "    <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                    "      <bgpcomm>\n" +
                    "        <bgpVrfs>\n" +
                    "          <bgpVrf xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                    "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n" +
                    "          </bgpVrf>\n" +
                    "        </bgpVrfs>\n" +
                    "      </bgpcomm>\n" +
                    "    </bgp>";
        }
        String vpn =
                "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                        "      <l3vpncomm>\n" +
                        "        <l3vpnInstances>\n" +
                        "          <l3vpnInstance>\n" +
                        "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n" +
                        "            <asNotationCfg/>\n";
        String vpnInstAFs = "            <vpnInstAFs xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>\n";
        String l3vpnIfs = "";
        if (l3vpnInstance.getL3vpnIfs().size() > 0) {
            l3vpnIfs = "            <l3vpnIfs xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>\n";
        }
        String l3vpnEnd = "          </l3vpnInstance>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n";
        String end = "  </config>\n" +
                "</edit-config>" +
                "</rpc>";
        result = start;
        if (rdChange && map.get("isIfmChanged")) {
            if (null == bgpVrf) {
                result = result + vpn + vpnInstAFs + l3vpnIfs + l3vpnEnd;
            } else {
                result = result + ebgp + vpn + vpnInstAFs + l3vpnIfs + l3vpnEnd;
            }
        } else if (rdChange) {
            if (null == bgpVrf) {
                result = result + vpn + vpnInstAFs + l3vpnEnd;
            } else {
                result = result + ebgp + vpn + vpnInstAFs + l3vpnEnd;
            }
        } else if (map.get("isIfmChanged") && map.get("isEbgpChanged")) {
            if (null == bgpVrf) {
                result = result + vpn + l3vpnIfs + l3vpnEnd;
            } else {
                result = result + ebgp + vpn + l3vpnIfs + l3vpnEnd;
            }
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
                "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>\n";
        if (!(null == l3vpnInstance.getVrfDescription() || ("").equals(l3vpnInstance.getVrfDescription()))) {
            String vpnStart1 =
                    "            <vrfDescription>" + l3vpnInstance.getVrfDescription() + "</vrfDescription>\n";
            vpnStart = vpnStart + vpnStart1;
        }

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
        String ipv4Addr;
        String subnetMask;
        String l3vpnIfEnd;
        for (L3vpnIf l3vpnIf : l3vpnIfList) {
            l3vpnIfsMiddle = l3vpnIfsMiddle + "                <l3vpnIf>\n" +
                    "                  <ifName>" + l3vpnIf.getIfName() + "</ifName>\n";
            if (!(null == l3vpnIf.getIpv4Addr() || "".equals(l3vpnIf.getIpv4Addr()))) {
                ipv4Addr =
                        "                  <ipv4Addr>" + l3vpnIf.getIpv4Addr() + "</ipv4Addr>\n";
                l3vpnIfsMiddle = l3vpnIfsMiddle + ipv4Addr;
            }
            if (!(null == l3vpnIf.getSubnetMask() || "".equals(l3vpnIf.getSubnetMask()))) {
                subnetMask =
                        "                  <subnetMask>" + l3vpnIf.getSubnetMask() + "</subnetMask>\n";
                l3vpnIfsMiddle = l3vpnIfsMiddle + subnetMask;
            }
            l3vpnIfEnd =
                    "                </l3vpnIf>\n";
            l3vpnIfsMiddle = l3vpnIfsMiddle + l3vpnIfEnd;
        }
        String l3vpnIfsEnd = "            </l3vpnIfs>\n";
        String VpnEnd = "          </l3vpnInstance>\n" +
                "        </l3vpnInstances>\n" +
                "      </l3vpncomm>\n" +
                "    </l3vpn>\n";

        String networkRoutes = "";
        String bgpStart = "";
        String bgpPeers = "";
        String importRoutes = "";
        String bgpEnd = "";
        String middle1 = "";
        String middle = "";
        if (null != bgpVrf) {
            bgpStart =
                    "      <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                            "        <bgpcomm>\n" +
                            "          <bgpVrfs>\n" +
                            "            <bgpVrf nc:operation=\"create\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                            "              <vrfName>" + bgpVrf.getVrfName() + "</vrfName>\n" +
                            "                    <bgpPeers>\n";

            if (null != bgpVrf.getBgpPeers()) {
                for (BgpPeer bgpPeer : bgpVrf.getBgpPeers()) {
                    bgpPeers = bgpPeers + "                       <bgpPeer>\n" +
                            "                         <peerAddr>" + bgpPeer.getPeerAddr() + "</peerAddr>\n" +
                            "                         <remoteAs>" + bgpPeer.getRemoteAs() + "</remoteAs>\n" +
                            "                           </bgpPeer>\n";
                }
            }
            middle = "                      </bgpPeers>\n" +
                    "              <bgpVrfAFs>\n" +
                    "                <bgpVrfAF>\n" +
                    "                  <afType>ipv4uni</afType>\n" +
                    "                  <importRoutes>\n";

            if (null != bgpVrf.getImportRoutes()) {
                for (ImportRoute importRoute : bgpVrf.getImportRoutes()) {
                    importRoutes = importRoutes + "                    <importRoute>\n" +
                            "                      <importProtocol>" + importRoute.getImportProtocol() + "</importProtocol>\n" +
                            "                      <importProcessId>" + importRoute.getImportProcessId() + "</importProcessId>\n" +
                            "                    </importRoute>\n";
                }
            }
            middle1 =
                    "                  </importRoutes>\n" +
                            "                  <networkRoutes>\n";

            if (null != bgpVrf.getNetworkRoutes()) {
                for (NetworkRoute networkRoute : bgpVrf.getNetworkRoutes()) {
                    if (!("".equals(networkRoute.getNetworkAddress()))) {
                        networkRoutes = networkRoutes +
                                "                    <networkRoute>\n" +
                                "                      <networkAddress>" + networkRoute.getNetworkAddress() + "</networkAddress>\n" +
                                "                      <maskLen>" + networkRoute.getMaskLen() + "</maskLen>\n" +
                                "                    </networkRoute>\n";
                    }
                }
            }
            bgpEnd =
                    "                  </networkRoutes>\n" +
                            "                </bgpVrfAF>\n" +
                            "              </bgpVrfAFs>\n" +
                            "            </bgpVrf>\n" +
                            "          </bgpVrfs>\n" +
                            "        </bgpcomm>\n" +
                            "      </bgp>\n";
        }

        String end = "  </config>\n" +
                "</edit-config>" +
                "</rpc>";
        result = start;
        String ebgp = "";
        ebgp = bgpStart + bgpPeers + middle + importRoutes + middle1 + networkRoutes + bgpEnd;
        String intf = l3vpnIfsStart + l3vpnIfsMiddle + l3vpnIfsEnd;
        if (rdChange && map.get("isIfmChanged") && l3vpnIfList.size() > 0) {
            if (null == bgpVrf) {
                result = result + vpnStart + vpnInstAFs + intf + VpnEnd;
            } else {
                result = result + vpnStart + vpnInstAFs + intf + VpnEnd + ebgp;
            }
        } else if (rdChange) {
            if (null == bgpVrf) {
                result = result + vpnStart + vpnInstAFs + VpnEnd;
            } else {
                result = result + vpnStart + vpnInstAFs + VpnEnd + ebgp;
            }

        } else if (map.get("isIfmChanged") && map.get("isEbgpChanged")) {
            if (null == bgpVrf) {
                result = result + vpnStart + intf + VpnEnd;
            } else {
                result = result + vpnStart + intf + VpnEnd + ebgp;
            }
        } else if (map.get("isIfmChanged")) {
            result = result + vpnStart + intf + VpnEnd;
        } else if (map.get("isEbgpChanged")) {
            result = result + ebgp;
        }
        return result + end;
    }

    public static String vpnApplyLabelUpdateXml(L3vpnInstance l3vpnInstance) {
        String start =
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                        "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                 \n" +
                        "  <target>                                                                                      \n" +
                        "    <running/>                                                                                  \n" +
                        "  </target>                                                                                     \n" +
                        "  <error-option>rollback-on-error</error-option>                                                \n" +
                        "  <config>                                                                                      \n";
        String vpnStart =
                "    <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">                            \n" +
                        "      <l3vpncomm>                                                                               \n" +
                        "        <l3vpnInstances>                                                                        \n" +
                        "          <l3vpnInstance>                                                                       \n" +
                        "            <vrfName>" + l3vpnInstance.getVrfName() + "</vrfName>                                   \n";
        String vpnInstAFs =
                "            <vpnInstAFs>                                                                         \n" +
                        "              <vpnInstAF>                                                                        \n" +
                        "                <afType>ipv4uni</afType>\n" +
                        "                <vrfRD>" + l3vpnInstance.getVrfRD() + "</vrfRD>                                    \n" +
                        "                  <vpnFrr>" + l3vpnInstance.getVpnFrr() + "</vpnFrr>                               \n";
        if (l3vpnInstance.getApplyLabel() == null) {
            vpnInstAFs = vpnInstAFs +
                    "<vrfLabelMode xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>    \n";
        } else {
            vpnInstAFs = vpnInstAFs +
                    "                  <vrfLabelMode>" + l3vpnInstance.getApplyLabel() + "</vrfLabelMode>                \n";
        }
        if (l3vpnInstance.getApplyLabel() == null) {
            vpnInstAFs = vpnInstAFs +
                    "<tnlPolicyName xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\"/>    \n";
        } else {
            if (null != l3vpnInstance.getTunnelPolicy()) {
                vpnInstAFs = vpnInstAFs +
                        "                  <tnlPolicyName>" + l3vpnInstance.getTunnelPolicy() + "</tnlPolicyName>              \n";
            }
        }
        vpnInstAFs = vpnInstAFs +
                "                  <l3vpnTtlMode> <ttlMode>" + l3vpnInstance.getTtlMode() + "</ttlMode> </l3vpnTtlMode>\n" +
                "              </vpnInstAF>\n" +
                "            </vpnInstAFs>\n" +
                "          <l3vpnIfs/>                                                                              \n" +
                "        </l3vpnInstance>                                                                           \n" +
                "      </l3vpnInstances>                                                                            \n" +
                "     </l3vpncomm>                                                                                  \n" +
                "   </l3vpn>                                                                                         \n" +
                "  </config>                                                                                         \n" +
                " </edit-config>                                                                                     \n" +
                "</rpc>";


        return start + vpnStart + vpnInstAFs;
    }

    public static String vpnApplyEbgpXml(BgpVrf bgpVrf) {
        String start =
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                        "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                 \n" +
                        "  <target>                                                                                      \n" +
                        "    <running/>                                                                                  \n" +
                        "  </target>                                                                                     \n" +
                        "  <error-option>rollback-on-error</error-option>                                                \n" +
                        "  <config>                                                                                      \n";
        String bgpStart =

                "      <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                        "        <bgpcomm>\n" +
                        "          <bgpVrfs>\n" +
                        "            <bgpVrf xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                        "              <vrfName>" + bgpVrf.getVrfName() + "</vrfName>\n";
        String bgpVrfAFs = "";
        if (bgpVrf.getBgpVrfAFs() != null) {
            bgpVrfAFs = bgpVrfAFs +
                    "              <bgpVrfAFs>\n" +
                    "                <bgpVrfAF>\n" +
                    "                  <afType>ipv4uni</afType>\n";
            for (SBgpVrfAF sBgpVrfAF : bgpVrf.getBgpVrfAFs()) {
                bgpVrfAFs = bgpVrfAFs +
                        "                  <preferenceExternal>" + sBgpVrfAF.getPreferenceExternal() + "</preferenceExternal>\n" +
                        "                  <preferenceInternal>" + sBgpVrfAF.getPreferenceInternal() + "</preferenceInternal>\n" +
                        "                  <preferenceLocal>" + sBgpVrfAF.getPreferenceLocal() + "</preferenceLocal>\n";
                if (sBgpVrfAF.getPeerAFs() != null) {
                    bgpVrfAFs = bgpVrfAFs +
                            "                <peerAFs>\n" +
                            "                <peerAF>\n";
                    for (SPeerAF sPeerAF : sBgpVrfAF.getPeerAFs()) {
                        bgpVrfAFs = bgpVrfAFs +
                                "                   <advertiseCommunity>" + sPeerAF.getAdvertiseCommunity() + "</advertiseCommunity>\n" +
                                "                   <remoteAddress>" + sPeerAF.getRemoteAddress() + "</remoteAddress>\n";
                        if (sPeerAF.getImportRtPolicyName() == null) {
                            bgpVrfAFs = bgpVrfAFs +
                                    "                  <importRtPolicyName nc:operation=\"delete\"/>\n";
                        } else {
                            bgpVrfAFs = bgpVrfAFs +
                                    "                  <importRtPolicyName>" + sPeerAF.getImportRtPolicyName() + "</importRtPolicyName>\n";
                        }
                        if (sPeerAF.getExportRtPolicyName() == null) {
                            bgpVrfAFs = bgpVrfAFs +
                                    "                  <exportRtPolicyName nc:operation=\"delete\"/>\n";
                        } else {
                            bgpVrfAFs = bgpVrfAFs +
                                    "                  <exportRtPolicyName>" + sPeerAF.getExportRtPolicyName() + "</exportRtPolicyName>\n";
                        }
                    }
                }
                bgpVrfAFs = bgpVrfAFs +
                        "                </peerAF>      \n" +
                        "                </peerAFs>     \n";
            }
            bgpVrfAFs = bgpVrfAFs +
                    "              </bgpVrfAF>      \n" +
                    "            </bgpVrfAFs>       \n";
        }
        String end =
                "       </bgpVrf>               \n" +
                        "      </bgpVrfs>               \n" +
                        "     </bgpcomm>                \n" +
                        "    </bgp>                     \n" +
                        "   </config>                   \n" +
                        "  </edit-config>               \n" +
                        "</rpc>";
        return start + bgpStart + bgpVrfAFs + end;
    }
}
