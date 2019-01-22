/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.bgp.BgpPeer;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import cn.org.upbnc.util.netconf.bgp.ImportRoute;
import cn.org.upbnc.util.netconf.bgp.NetworkRoute;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EbgpXml {
    private static final Logger LOG = LoggerFactory.getLogger(EbgpXml.class);

    public static String createEbgpXml(BgpVrf bgpVrf) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <edit-config>\n" +
                "    <target>\n" +
                "      <running/>\n" +
                "    </target>\n" +
                "    <config>\n" +
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
                if (!("".equals(networkRoute.getNetworkAddress()))) {
                    networkRoutes = networkRoutes +
                            "                    <networkRoute>\n" +
                            "                      <networkAddress>" + networkRoute.getNetworkAddress() + "</networkAddress>\n" +
                            "                      <maskLen>" + networkRoute.getMaskLen() + "</maskLen>\n" +
                            "                    </networkRoute>\n";
                }
            }
        }
        String end =
                "                  </networkRoutes>\n" +
                        "                </bgpVrfAF>\n" +
                        "              </bgpVrfAFs>\n" +
                        "            </bgpVrf>\n" +
                        "          </bgpVrfs>\n" +
                        "        </bgpcomm>\n" +
                        "      </bgp>\n" +
                        "    </config>\n" +
                        "  </edit-config>\n" +
                        "</rpc>";
        return start + bgpPeers + middle + importRoutes + middle1 + networkRoutes + end;
    }


    public static List<BgpVrf> getEbgpFromXml(String xml) {
        List<BgpVrf> bgpVrfs = new ArrayList<>();
        List<BgpPeer> BgpPeers = new ArrayList<>();
        List<NetworkRoute> NetworkRoutes;
        List<ImportRoute> ImportRoutes;
        BgpVrf bgpVrf;
        BgpPeer bgpPeer;
        NetworkRoute networkRoute;
        ImportRoute importRoute;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element child : childElements) {
                    if ("_public_".equals(child.elementText("vrfName"))) {
                        LOG.info("this instance is invalid.");
                    } else {
                        bgpVrf = new BgpVrf();
                        NetworkRoutes = new ArrayList<>();
                        BgpPeers = new ArrayList<>();
                        ImportRoutes = new ArrayList<>();
                        bgpVrf.setVrfName(child.elementText("vrfName"));
                        for (org.dom4j.Element child1 : child.elements("bgpVrfAFs").get(0).elements().get(0).elements("networkRoutes").get(0).elements()) {
                            networkRoute = new NetworkRoute(child1.elementText("networkAddress"), child1.elementText("maskLen"));
                            NetworkRoutes.add(networkRoute);
                        }
                        try {
                            for (org.dom4j.Element child1 : child.elements("bgpVrfAFs").get(0).elements().get(0).elements("importRoutes").get(0).elements()) {
                                importRoute = new ImportRoute(child1.elementText("importProtocol"), child1.elementText("importProcessId"));
                                ImportRoutes.add(importRoute);
                            }
                        } catch (Exception e) {
                            continue;
                        } finally {
                            bgpVrf.setImportRoutes(ImportRoutes);
                            bgpVrf.setNetworkRoutes(NetworkRoutes);
                            try {
                                for (org.dom4j.Element child2 : child.elements("bgpPeers").get(0).elements()) {
                                    bgpPeer = new BgpPeer(child2.elementText("peerAddr"), child2.elementText("remoteAs"));
                                    BgpPeers.add(bgpPeer);
                                }
                            } catch (Exception e1) {
                                continue;
                            } finally {
                                bgpVrf.setBgpPeers(BgpPeers);
                                bgpVrfs.add(bgpVrf);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return bgpVrfs;
    }

    public static String getEbgpXml(String vrfName) {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <bgp:bgp xmlns:bgp=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                "      <bgp:bgpcomm>\n" +
                "        <bgp:bgpVrfs>\n" +
                "          <bgp:bgpVrf>\n" +
                "            <vrfName>" + vrfName + "</vrfName>\n" +
                "            <bgp:bgpPeers/>\n" +
                "            <bgp:bgpVrfAFs>\n" +
                "              <bgp:bgpVrfAF>\n" +
                "                <afType>ipv4uni</afType>\n" +
                "                <bgp:importRoutes/>\n" +
                "                <bgp:networkRoutes/>\n" +
                "              </bgp:bgpVrfAF>\n" +
                "            </bgp:bgpVrfAFs>\n" +
                "          </bgp:bgpVrf>\n" +
                "        </bgp:bgpVrfs>\n" +
                "      </bgp:bgpcomm>\n" +
                "    </bgp:bgp>\n" +
                "  </filter>\n" +
                "</get>" +
                "</rpc>";
        return str;
    }

    public static String getDeleteEbgpXml(String vrfName) {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "    <bgp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bgp\">\n" +
                "      <bgpcomm>\n" +
                "        <bgpVrfs>\n" +
                "          <bgpVrf xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                "            <vrfName>" + vrfName + "</vrfName>\n" +
                "          </bgpVrf>\n" +
                "        </bgpVrfs>\n" +
                "      </bgpcomm>\n" +
                "    </bgp>\n" +
                "  </config>\n" +
                "</edit-config>" +
                "</rpc>";
        return str;
    }
}
