/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.GigabitEthernet;
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
                "     <error-option>rollback-on-error</error-option>" +
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
                    if ("_public_".equals(child.elementText("vrfName")) || "__LOCAL_OAM_VPN__".equals(child.elementText("vrfName"))
                            || "__dcn_vpn__".equals(child.elementText("vrfName"))) {
                        LOG.info("this instance is invalid.");
                    } else {
                        l3vpnInstance = new L3vpnInstance();
                        l3vpnIfs = new ArrayList<>();
                        l3vpnInstance.setVrfName(child.elementText("vrfName"));
                        l3vpnInstance.setVrfDescription(child.elementText("vrfDescription"));
                        String vrfRD = null;
                        String vrfRTValue = null;
                        try {
                            for (org.dom4j.Element children : child.element("vpnInstAFs").elements()) {
                                vrfRD = children.elementText("vrfRD");
                                vrfRTValue = children.element("vpnTargets").elements().get(0).elementText("vrfRTValue");
                            }
                        } catch (Exception e) {
                            continue;
                        } finally {
                            l3vpnInstance.setVrfRD(vrfRD);
                            l3vpnInstance.setVrfRTValue(vrfRTValue);
                            try {
                                for (org.dom4j.Element children : child.element("l3vpnIfs").elements()) {
                                    l3vpnIf = new L3vpnIf();
                                    l3vpnIf.setIfName(children.elementText("ifName"));
                                    l3vpnIf.setIpv4Addr(children.elementText("ipv4Addr"));
                                    l3vpnIf.setSubnetMask(children.elementText("subnetMask"));
                                    l3vpnIfs.add(l3vpnIf);
                                }
                            } catch (Exception e) {
                                continue;
                            } finally {
                                l3vpnInstance.setL3vpnIfs(l3vpnIfs);
                                l3vpnInstanceList.add(l3vpnInstance);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return l3vpnInstanceList;
    }

    public static String getDeleteL3vpnXml(String vrfName) {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "  <edit-config>\n" +
                "     <target>\n" +
                "\t\t<running/>\n" +
                "\t  </target>\n" +
                "\t  <default-operation>none</default-operation>\n" +
                "\t  <error-option>rollback-on-error</error-option>\n" +
                "    <config>\n" +
                "      <l3vpn xmlns=\"http://www.huawei.com/netconf/vrp/huawei-l3vpn\">\n" +
                "        <l3vpncomm>\n" +
                "          <l3vpnInstances>\n" +
                "            <l3vpnInstance nc:operation=\"delete\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "              <vrfName>" + vrfName + "</vrfName>\n" +
                "            </l3vpnInstance>\n" +
                "          </l3vpnInstances>\n" +
                "        </l3vpncomm>\n" +
                "      </l3vpn>\n" +
                "    </config>\n" +
                "  </edit-config>\n" +
                "</rpc>";
        return str;
    }

    public static String getGigabitEthernetsXml() {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <filter type=\"subtree\">\n" +
                "    <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "      <ifm:interfaces>\n" +
                "        <ifm:interface>\n" +
                "          <ifm:ifName/>\n" +
                "          <ifm:ifIndex/>\n" +
                "          <ifm:ifClass/>\n" +
                "          <ifm:ifPhyType>GigabitEthernet</ifm:ifPhyType>\n" +
                "          <ifm:ifNumber/>\n" +
                "          <ifm:vrfName/>\n" +
                "          <ifm:ifDynamicInfo>\n" +
                "            <ifm:ifOperStatus/>\n" +
                "            <ifm:ifPhyStatus/>\n" +
                "            <ifm:ifLinkStatus/>\n" +
                "             <ifm:ifOperMac/>" +
                "          </ifm:ifDynamicInfo>\n" +
                "          <ifm:ipv4Oper/>\n" +
                "        </ifm:interface>\n" +
                "      </ifm:interfaces>\n" +
                "    </ifm:ifm>\n" +
                "  </filter>\n" +
                "</get>" +
                "</rpc>";
        return str;
    }

    public static List<GigabitEthernet> getGigabitEthernetsFromXml(String xml) {
        List<GigabitEthernet> gigabitEthernets = new ArrayList<>();
        GigabitEthernet gigabitEthernet;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element child : childElements) {
                    gigabitEthernet = new GigabitEthernet();
                    gigabitEthernet.setIfName(child.elementText("ifName"));
                    gigabitEthernet.setIfIndex(child.elementText("ifIndex"));
                    gigabitEthernet.setIfPhyType(child.elementText("ifPhyType"));
                    gigabitEthernet.setIfNumber(child.elementText("ifNumber"));
                    gigabitEthernet.setVrfName(child.elementText("vrfName"));
                    gigabitEthernet.setIfClass(child.elementText("ifClass"));
                    gigabitEthernet.setIfOperStatus(child.elements("ifDynamicInfo").get(0).elementText("ifOperStatus"));
                    gigabitEthernet.setIfPhyStatus(child.elements("ifDynamicInfo").get(0).elementText("ifPhyStatus"));
                    gigabitEthernet.setIfLinkStatus(child.elements("ifDynamicInfo").get(0).elementText("ifLinkStatus"));
                    gigabitEthernet.setIfOperMac(child.elements("ifDynamicInfo").get(0).elementText("ifOperMac"));
                    try {
                        gigabitEthernet.setIfIpAddr(child.elements("ipv4Oper").get(0).elements().get(0).elements().get(0).elementText("ifIpAddr"));
                        gigabitEthernet.setSubnetMask(child.elements("ipv4Oper").get(0).elements().get(0).elements().get(0).elementText("subnetMask"));
                    } catch (Exception e) {
                        continue;
                    } finally {
                        gigabitEthernets.add(gigabitEthernet);
                    }
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return gigabitEthernets;
    }
}
