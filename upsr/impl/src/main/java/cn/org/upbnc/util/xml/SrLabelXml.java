/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.AdjLabel;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.util.netconf.NetconfSrLabelInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SrLabelXml {
    private static final Logger LOG = LoggerFactory.getLogger(SrLabelXml.class);
    public static final String ncOperationCreate = "create";
    public static final String ncOperationMerge = "merge";
    public static final String ncOperationDelete = "delete";
    public static String getSrAdjLabelXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                        \n" +
                "  <filter type=\"subtree\">                                                  \n" +
                "    <segr:segr xmlns:segr=\"http://www.huawei.com/netconf/vrp/huawei-segr\"> \n" +
                "      <segr:staticIpv4Adjs>                                                  \n" +
                "        <segr:staticIpv4Adj>                                                 \n" +
                "          <segr:segmentId/>                                                  \n" +
                "        </segr:staticIpv4Adj>                                                \n" +
                "      </segr:staticIpv4Adjs>                                                 \n" +
                "    </segr:segr>                                                             \n" +
                "  </filter>                                                                  \n" +
                "</get>                                                                       \n" +
                "</rpc>";
    }
    public static List<AdjLabel> getSrAdjLabelFromSrAdjLabelXml(String xml){
        List<AdjLabel> adjLabelList = new ArrayList<>();
        if ("".equals(xml)){
            return adjLabelList;
        }
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            if (root.element("data").elements().size() == 0){
                return adjLabelList;
            }
            List<Element> childElements = root.element("data").elements().get(0).elements().get(0).elements();
            for (Element child : childElements) {
                if (child.elementText("segmentId") != null) {
                    AdjLabel adjLabel = new AdjLabel();
                    adjLabel.setAddressLocal(new Address(child.elementText("localIpAddress"), AddressTypeEnum.V4));
                    adjLabel.setAddressRemote(new Address(child.elementText("remoteIpAddress"), AddressTypeEnum.V4));
                    adjLabel.setValue(Integer.valueOf(child.elementText("segmentId")));
                    adjLabelList.add(adjLabel);
                }
            }
        } catch (DocumentException e) {
            LOG.info(e.toString());
        }
        return adjLabelList;
    }
    //action:add/del/update
    public static String setSrAdjLabelXml(String operation,String localIpAddress, String remoteIpAddress, String segmentId) {
        String ret =
                "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                              \n" +
                "  <target>                                                                                                   \n" +
                "    <running/>                                                                                               \n" +
                "  </target>                                                                                                  \n" +
                "  <config>                                                                                                   \n" +
                "    <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">                                           \n" +
                "      <staticIpv4Adjs>                                                                                       \n" +
                "        <staticIpv4Adj xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\""+ operation +"\">  \n" +
                "          <localIpAddress>" + localIpAddress + "</localIpAddress>                                               \n" +
                "          <remoteIpAddress>" + remoteIpAddress + "</remoteIpAddress>                                            \n";
        if (ncOperationDelete.equals(operation) != true) {
            ret = ret +
                "          <segmentId>" + segmentId + "</segmentId>                                                             \n";
        }
        ret = ret +
                "        </staticIpv4Adj>                                                                                     \n" +
                "      </staticIpv4Adjs>                                                                                      \n" +
                "    </segr>                                                                                                  \n" +
                "  </config>                                                                                                  \n" +
                "</edit-config>                                                                                               \n" +
                "</rpc>";
        return  ret;
    }
    public static String getSrAdjLabelRangeXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                         \n" +
                "  <filter type=\"subtree\">                                                   \n" +
                "    <segr:segr xmlns:segr=\"http://www.huawei.com/netconf/vrp/huawei-segr\">  \n" +
                "      <segr:srSRGBs/>                                                         \n" +
                "    </segr:segr>                                                              \n" +
                "  </filter>                                                                   \n" +
                "</get>                                                                        \n" +
                "</rpc>";
    }
    public static NetconfSrLabelInfo getAdjLabelRangeFromAdjLabelRangeXml(String xml){
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if ("".equals(xml)){
            return null;
        }
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> childElements = root.element("data").elements().get(0).elements().get(0).elements();
            for (Element child : childElements) {
                netconfSrLabelInfo.setAdjLowerSid(child.elementText("lowerSid"));
                netconfSrLabelInfo.setAdjUpperSid(child.elementText("upperSid"));
            }
        } catch (Exception e) {
        }
        return netconfSrLabelInfo;
    }
    public static String getSrNodeLabelXml(String processId) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                  \n" +
                "  <filter type=\"subtree\">                                                            \n" +
                "    <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">   \n" +
                "      <ospfv2:ospfv2comm>                                                              \n" +
                "        <ospfv2:ospfSites>                                                             \n" +
                "          <ospfv2:ospfSite>                                                            \n" +
                "           <processId>" + processId + "</processId>                                      \n" +
                "            <ospfv2:areas>                                                             \n" +
                "              <ospfv2:area>                                                            \n" +
                "                <ospfv2:interfaces>                                                    \n" +
                "                  <ospfv2:interface>                                                   \n" +
                "                    <ospfv2:srInterface>                                               \n" +
                "                      <ospfv2:prefixSidType/>                                          \n" +
                "                      <ospfv2:prefixLabel/>                                            \n" +
                "                    </ospfv2:srInterface>                                              \n" +
                "                  </ospfv2:interface>                                                  \n" +
                "                </ospfv2:interfaces>                                                   \n" +
                "              </ospfv2:area>                                                           \n" +
                "            </ospfv2:areas>                                                            \n" +
                "          </ospfv2:ospfSite>                                                           \n" +
                "        </ospfv2:ospfSites>                                                            \n" +
                "      </ospfv2:ospfv2comm>                                                             \n" +
                "    </ospfv2:ospfv2>                                                                   \n" +
                "  </filter>                                                                            \n" +
                "</get>                                                                                 \n" +
                "</rpc>";
    }
    public static NetconfSrLabelInfo getSrNodeLabelFromgSrNodeLabelXml(String xml,String processId,String ifName){
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                Element root = document.getRootElement();
                List<Element> ospfSiteElements = root.element("data").element("ospfv2").element("ospfv2comm").element("ospfSites").elements("ospfSite");

                for (org.dom4j.Element ospfSiteElement : ospfSiteElements) {
                    if(ospfSiteElement.elementText("processId").equals(processId)) {
                        netconfSrLabelInfo.setOspfAreaId(ospfSiteElement.element("areas").element("area").elementText("areaId"));
                        List<Element> interfaceElements = ospfSiteElement.element("areas").element("area").element("interfaces").elements("interface");
                        for (Element interfaceElement : interfaceElements) {
                            if (ifName.equals(interfaceElement.elementText("ifName")) == true) {
                                netconfSrLabelInfo.setOspfProcessId(ospfSiteElement.elementText("processId"));
                                netconfSrLabelInfo.setPrefixIfName(interfaceElement.elementText("ifName"));
                                netconfSrLabelInfo.setPrefixLabel(interfaceElement.element("srInterface").elementText("prefixLabel"));
                                netconfSrLabelInfo.setPrefixType(interfaceElement.element("srInterface").elementText("prefixSidType"));
                                return netconfSrLabelInfo;
                            }
                        }
                        break;
                    }
                }
            }catch (Exception e){
                LOG.info(e.toString());
            }
        }
        return null;
    }
    public static String getSrNodeLabelRangeXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                    \n" +
                "  <filter type=\"subtree\">                                                              \n" +
                "    <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">     \n" +
                "      <ospfv2:ospfv2comm>                                                                \n" +
                "        <ospfv2:ospfSites>                                                               \n" +
                "          <ospfv2:ospfSite>                                                              \n" +
                "            <ospfv2:ospfSrgbs/>                                                          \n" +
                "          </ospfv2:ospfSite>                                                             \n" +
                "        </ospfv2:ospfSites>                                                              \n" +
                "      </ospfv2:ospfv2comm>                                                               \n" +
                "    </ospfv2:ospfv2>                                                                     \n" +
                "  </filter>                                                                              \n" +
                "</get>                                                                                   \n" +
                "</rpc>";
    }
    public static NetconfSrLabelInfo getSrNodeLabelRangeFromNodeLabelRangeXml(String xml) {
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<Element> ospfSiteElements = root.element("data").element("ospfv2").element("ospfv2comm").element("ospfSites").elements("ospfSite");
                for (org.dom4j.Element ospfSiteElement : ospfSiteElements) {
                    org.dom4j.Element ospfSrgbsElement = ospfSiteElement.element("ospfSrgbs");
                    if(ospfSrgbsElement != null) {
                        List<Element> ospfSrgbElements = ospfSiteElement.element("ospfSrgbs").elements("ospfSrgb");
                        for (org.dom4j.Element ospfSrgbElement : ospfSrgbElements) {
                            if ((ospfSrgbElement.element("srgbBegin") != null) &&
                                    (ospfSrgbElement.element("srgbEnd") != null)) {
                                netconfSrLabelInfo.setOspfProcessId(ospfSiteElement.elementText("processId"));
                                netconfSrLabelInfo.setSrgbBegin(ospfSrgbElement.elementText("srgbBegin"));
                                netconfSrLabelInfo.setSrgbEnd(ospfSrgbElement.elementText("srgbEnd"));
                                return netconfSrLabelInfo;
                            }
                        }
                    }
                }
            }catch (Exception e){
                LOG.info(e.toString());
            }
        }
        return null;
    }
    public static String setSrNodeLabelXml(String processId,String areaId,String ifName,String prefixSidType,String prefixLabel,String operation) {
        if (operation.equals(SrLabelXml.ncOperationDelete)) {
            return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                    "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                                     \n" +
                    "  <target>                                                                                                          \n" +
                    "    <running/>                                                                                                      \n" +
                    "  </target>                                                                                                         \n" +
                    "  <config>                                                                                                          \n" +
                    "    <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">                                              \n" +
                    "      <ospfv2comm>                                                                                                  \n" +
                    "        <ospfSites>                                                                                                 \n" +
                    "          <ospfSite>                                                                                                \n" +
                    "            <processId>" + processId + "</processId>                                                                  \n" +
                    "            <areas>                                                                                                 \n" +
                    "              <area>                                                                                                \n" +
                    "                <areaId>" + areaId + "</areaId>                                                                      \n" +
                    "                <interfaces>                                                                                        \n" +
                    "                  <interface>                                                                                       \n" +
                    "                    <ifName>" + ifName + "</ifName>                                                                  \n" +
                    "              <srInterface xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"" + operation + "\">  \n" +
                    "                    </srInterface>                                                                                  \n" +
                    "                  </interface>                                                                                      \n" +
                    "                </interfaces>                                                                                       \n" +
                    "              </area>                                                                                               \n" +
                    "            </areas>                                                                                                \n" +
                    "          </ospfSite>                                                                                               \n" +
                    "        </ospfSites>                                                                                                \n" +
                    "      </ospfv2comm>                                                                                                 \n" +
                    "    </ospfv2>                                                                                                       \n" +
                    "  </config>                                                                                                         \n" +
                    "</edit-config>                                                                                                      \n" +
                    "</rpc>";
        } else {
            return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                    "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                                     \n" +
                    "  <target>                                                                                                          \n" +
                    "    <running/>                                                                                                      \n" +
                    "  </target>                                                                                                         \n" +
                    "  <config>                                                                                                          \n" +
                    "    <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">                                              \n" +
                    "      <ospfv2comm>                                                                                                  \n" +
                    "        <ospfSites>                                                                                                 \n" +
                    "          <ospfSite>                                                                                                \n" +
                    "            <processId>" + processId + "</processId>                                                                  \n" +
                    "            <areas>                                                                                                 \n" +
                    "              <area>                                                                                                \n" +
                    "                <areaId>" + areaId + "</areaId>                                                                      \n" +
                    "                <interfaces>                                                                                        \n" +
                    "                  <interface>                                                                                       \n" +
                    "                    <ifName>" + ifName + "</ifName>                                                                  \n" +
                    "              <srInterface xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"" + operation + "\">  \n" +
                    "                      <prefixSidType>" + prefixSidType + "</prefixSidType>                                            \n" +
                    "                      <prefixLabel>" + prefixLabel + "</prefixLabel>                                                  \n" +
                    "                    </srInterface>                                                                                  \n" +
                    "                  </interface>                                                                                      \n" +
                    "                </interfaces>                                                                                       \n" +
                    "              </area>                                                                                               \n" +
                    "            </areas>                                                                                                \n" +
                    "          </ospfSite>                                                                                               \n" +
                    "        </ospfSites>                                                                                                \n" +
                    "      </ospfv2comm>                                                                                                 \n" +
                    "    </ospfv2>                                                                                                       \n" +
                    "  </config>                                                                                                         \n" +
                    "</edit-config>                                                                                                      \n" +
                    "</rpc>";
        }
    }
    public static String setSrNodeLabelRangeXml(String processId,String srgbBegin, String srgbEnd, String operation) {
        if (operation.equals(SrLabelXml.ncOperationDelete)) {
            return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                    "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                          \n" +
                    "  <target>                                                                                               \n" +
                    "    <running/>                                                                                           \n" +
                    "  </target>                                                                                              \n" +
                    "  <config>                                                                                               \n" +
                    "    <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">                                   \n" +
                    "      <ospfv2comm>                                                                                       \n" +
                    "        <ospfSites>                                                                                      \n" +
                    "          <ospfSite>                                                                                     \n" +
                    "            <processId>" + processId + "</processId>                                                       \n" +
                    "      <ospfSrgbs xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"" + operation + "\">  \n" +
                    "            </ospfSrgbs>                                                                                 \n" +
                    "          </ospfSite>                                                                                    \n" +
                    "        </ospfSites>                                                                                     \n" +
                    "      </ospfv2comm>                                                                                      \n" +
                    "    </ospfv2>                                                                                            \n" +
                    "  </config>                                                                                              \n" +
                    "</edit-config>                                                                                           \n" +
                    "</rpc>";
        } else {
            return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                    "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                          \n" +
                    "  <target>                                                                                               \n" +
                    "    <running/>                                                                                           \n" +
                    "  </target>                                                                                              \n" +
                    "  <config>                                                                                               \n" +
                    "    <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">                                   \n" +
                    "      <ospfv2comm>                                                                                       \n" +
                    "        <ospfSites>                                                                                      \n" +
                    "          <ospfSite>                                                                                     \n" +
                    "            <processId>" + processId + "</processId>                                                       \n" +
                    "            <ospfSrgbs>                                                                                  \n" +
                    "       <ospfSrgb xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"" + operation + "\">  \n" +
                    "                <srgbBegin>" + srgbBegin + "</srgbBegin>                                                   \n" +
                    "                <srgbEnd>" + srgbEnd + "</srgbEnd>                                                         \n" +
                    "              </ospfSrgb>                                                                                \n" +
                    "            </ospfSrgbs>                                                                                 \n" +
                    "          </ospfSite>                                                                                    \n" +
                    "        </ospfSites>                                                                                     \n" +
                    "      </ospfv2comm>                                                                                      \n" +
                    "    </ospfv2>                                                                                            \n" +
                    "  </config>                                                                                              \n" +
                    "</edit-config>                                                                                           \n" +
                    "</rpc>";
        }
    }

    public static String getOspfProcessXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                             \n" +
                "  <filter type=\"subtree\">                                                                       \n" +
                "    <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">              \n" +
                "      <ospfv2:ospfv2comm>                                                                         \n" +
                "        <ospfv2:ospfSites>                                                                        \n" +
                "          <ospfv2:ospfSite>                                                                       \n" +
                "            <routerId/>                                                                           \n" +
                "          </ospfv2:ospfSite>                                                                      \n" +
                "        </ospfv2:ospfSites>                                                                       \n" +
                "      </ospfv2:ospfv2comm>                                                                        \n" +
                "    </ospfv2:ospfv2>                                                                              \n" +
                "  </filter>                                                                                       \n" +
                "</get>                                                                                            \n" +
                "</rpc>";
    }

    public static NetconfSrLabelInfo getOspfProcessFromOspfProcessXml(String xml,String routerId) {
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<Element> ospfSiteElements = root.element("data").element("ospfv2").element("ospfv2comm").element("ospfSites").elements("ospfSite");
                for (org.dom4j.Element ospfSiteElement : ospfSiteElements) {
                    String routerIdElement = ospfSiteElement.elementText("routerId");
                    if(routerIdElement.equals(routerId) == true){
                        netconfSrLabelInfo.setOspfProcessId(ospfSiteElement.elementText("processId"));
                        break;
                    }
                }
            }catch (Exception e){
                LOG.info(e.toString());
            }
        }
        return netconfSrLabelInfo;
    }

    public static String getLoopBackXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">                                           \n" +
                "  <filter type=\"subtree\">                                                                       \n" +
                "    <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">                          \n" +
                "      <ifm:interfaces>                                                                            \n" +
                "        <ifm:interface>                                                                           \n" +
                "          <ifm:ifName/>                                                                           \n" +
                "          <ifm:ipv4Oper/>                                                                         \n" +
                "          <ifm:ifPhyType>LoopBack</ifm:ifPhyType>                                                 \n" +
                "        </ifm:interface>                                                                          \n" +
                "      </ifm:interfaces>                                                                           \n" +
                "    </ifm:ifm>                                                                                    \n" +
                "  </filter>                                                                                       \n" +
                "</get>                                                                                            \n" +
                "</rpc>";
    }

    public static String getLoopBackFromLoopBackXml(String xml, String routerId) {
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<Element> interfaceElements = root.element("data").element("ifm").element("interfaces").elements("interface");
                for (org.dom4j.Element interfaceElement : interfaceElements) {
                    if (routerId.equals(interfaceElement.element("ipv4Oper").element("ipv4Addrs").element("ipv4Addr").elementText("ifIpAddr"))) {
                        return interfaceElement.elementText("ifName");
                    }
                }
            }catch (Exception e){
                LOG.info(e.toString());
            }
        }
        return null;
    }
}
