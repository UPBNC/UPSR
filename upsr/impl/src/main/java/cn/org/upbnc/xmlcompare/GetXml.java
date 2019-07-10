package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.AdjLabel;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.util.netconf.*;
import cn.org.upbnc.util.netconf.bgp.BgpPeer;
import cn.org.upbnc.util.netconf.bgp.BgpVrf;
import cn.org.upbnc.util.netconf.bgp.ImportRoute;
import cn.org.upbnc.util.netconf.bgp.NetworkRoute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GetXml {
    public static List<SSrTeTunnel> getSrTeTunnelFromXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        SSrTeTunnel srTeTunnel;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                Element root = document.getRootElement();
                List<Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements();
                Element element = childElements.get(attributes.get(attributes.size() - 3).getIndex() - 1);
                srTeTunnel = new SSrTeTunnel();
                srTeTunnel.setTunnelName(element.elementText("tunnelName"));
                srTeTunnel.setMplsTunnelEgressLSRId(element.elementText("mplsTunnelEgressLSRId"));
                srTeTunnel.setMplsTunnelIndex(element.elementText("mplsTunnelIndex"));
                srTeTunnel.setMplsTunnelBandwidth(element.elementText("mplsTunnelBandwidth"));
                srTeTunnel.setMplsTeTunnelBfdEnable(
                        element.elements("mplsTeTunnelBfd").get(0).elementText("mplsTeTunnelBfdEnable"));
                srTeTunnel.setMplsTeTunnelBfdMinTx(
                        element.elements("mplsTeTunnelBfd").get(0).elementText("mplsTeTunnelBfdMinTx"));
                srTeTunnel.setMplsTeTunnelBfdMinnRx(
                        element.elements("mplsTeTunnelBfd").get(0).elementText("mplsTeTunnelBfdMinnRx"));
                srTeTunnel.setMplsTeTunnelBfdDetectMultiplier(
                        element.elements("mplsTeTunnelBfd").get(0).elementText("mplsTeTunnelBfdDetectMultiplier"));
                List<SSrTeTunnelPath> srTeTunnelPaths = new ArrayList<>();
                SSrTeTunnelPath srTeTunnelPath;
                for (Element child : element.elements("srTeTunnelPaths").get(0).elements()) {
                    srTeTunnelPath = new SSrTeTunnelPath();
                    srTeTunnelPath.setPathType(child.elementText("pathType"));
                    srTeTunnelPath.setExplicitPathName(child.elementText("explicitPathName"));
                    if (null != child.elementText("explicitPathName")) {
                        srTeTunnelPaths.add(srTeTunnelPath);
                    }
                }
                Element elementSc = element.element("tunnelInterface").element("mplsteServiceClass");
                if (elementSc != null) {
                    STunnelServiceClass sc = new STunnelServiceClass();
                    sc.setDefaultServiceClassEnable(Boolean.valueOf(elementSc.elementText("defaultServiceClassEnable")));
                    sc.setBeServiceClassEnable(Boolean.valueOf(elementSc.elementText("beServiceClassEnable")));
                    sc.setAf1ServiceClassEnable(Boolean.valueOf(elementSc.elementText("af1ServiceClassEnable")));
                    sc.setAf2ServiceClassEnable(Boolean.valueOf(elementSc.elementText("af2ServiceClassEnable")));
                    sc.setAf3ServiceClassEnable(Boolean.valueOf(elementSc.elementText("af3ServiceClassEnable")));
                    sc.setAf4ServiceClassEnable(Boolean.valueOf(elementSc.elementText("af4ServiceClassEnable")));
                    sc.setEfServiceClassEnable(Boolean.valueOf(elementSc.elementText("efServiceClassEnable")));
                    sc.setCs6ServiceClassEnable(Boolean.valueOf(elementSc.elementText("cs6ServiceClassEnable")));
                    sc.setCs7ServiceClassEnable(Boolean.valueOf(elementSc.elementText("cs7ServiceClassEnable")));
                    srTeTunnel.setMplsteServiceClass(sc);
                }
                srTeTunnel.setSrTeTunnelPaths(srTeTunnelPaths);
                srTeTunnels.add(srTeTunnel);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return srTeTunnels;
    }

    public static List<SExplicitPath> getExplicitPathFromXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<SExplicitPath> explicitPaths = new ArrayList<>();
        SExplicitPath explicitPath;
        List<SExplicitPathHop> explicitPathHops;
        SExplicitPathHop explicitPathHop;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                Element element;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    element = childElements.get(attributes.get(attributes.size() - 5).getIndex() - 1);
                } else {
                    element = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
                }
                explicitPath = new SExplicitPath();
                explicitPathHops = new ArrayList<>();
                explicitPath.setExplicitPathName(element.elementText("explicitPathName"));
                for (org.dom4j.Element child : element.elements("explicitPathHops").get(0).elements()) {
                    explicitPathHop = new SExplicitPathHop();
                    explicitPathHop.setMplsTunnelHopIndex(child.elementText("mplsTunnelHopIndex"));
                    explicitPathHop.setMplsTunnelHopMode(child.elementText("mplsTunnelHopMode"));
                    explicitPathHop.setMplsTunnelHopSidLabel(child.elementText("mplsTunnelHopSidLabel"));
                    explicitPathHop.setMplsTunnelHopSidLabelType(child.elementText("mplsTunnelHopSidLabelType"));
                    explicitPathHops.add(explicitPathHop);
                }
                explicitPath.setExplicitPathHops(explicitPathHops);
                explicitPaths.add(explicitPath);
            } catch (Exception e) {

            }
        }
        return explicitPaths;
    }

    public static List<BgpVrf> getEbgpFromXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<BgpVrf> bgpVrfs = new ArrayList<>();
        List<BgpPeer> BgpPeers = new ArrayList<>();
        List<NetworkRoute> NetworkRoutes;
        List<ImportRoute> ImportRoutes;
        List<SBgpVrfAF> bgpVrfAFs;
        List<SPeerAF> peerAFs;
        SPeerAF peerAF;
        SBgpVrfAF bgpVrfAF;
        BgpVrf bgpVrf;
        BgpPeer bgpPeer;
        NetworkRoute networkRoute;
        ImportRoute importRoute;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                Element child = null;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    child = childElements.get(attributes.get(attributes.size() - 5).getIndex() - 1);
                } else {
                    child = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
                }
                if ("_public_".equals(child.elementText("vrfName"))) {
//                        LOG.info("this instance is invalid.");
                } else {
                    bgpVrf = new BgpVrf();
                    NetworkRoutes = new ArrayList<>();
                    BgpPeers = new ArrayList<>();
                    ImportRoutes = new ArrayList<>();
                    bgpVrfAFs = new ArrayList<>();
                    bgpVrf.setVrfName(child.elementText("vrfName"));
                    peerAFs = new ArrayList<>();
                    bgpVrfAF = new SBgpVrfAF();
                    try {
                        if (child.elements("bgpVrfAFs").size() > 0) {
                            for (org.dom4j.Element child1 : child.elements("bgpVrfAFs").get(0).elements()) {
                                bgpVrfAF.setPreferenceExternal(child1.elementText("preferenceExternal"));
                                bgpVrfAF.setPreferenceInternal(child1.elementText("preferenceInternal"));
                                bgpVrfAF.setPreferenceLocal(child1.elementText("preferenceLocal"));
                                if (child1.elements("peerAFs").size() > 0) {
                                    for (org.dom4j.Element child2 : child1.elements("peerAFs").get(0).elements()) {
                                        peerAF = new SPeerAF();
                                        peerAF.setImportRtPolicyName(child2.elementText("importRtPolicyName"));
                                        peerAF.setExportRtPolicyName(child2.elementText("exportRtPolicyName"));
                                        peerAF.setAdvertiseCommunity(child2.elementText("advertiseCommunity"));
                                        peerAF.setRemoteAddress(child2.elementText("remoteAddress"));
                                        peerAFs.add(peerAF);
                                    }
                                }
                            }
                            bgpVrfAF.setPeerAFs(peerAFs);
                            bgpVrfAFs.add(bgpVrfAF);
                            bgpVrf.setBgpVrfAFs(bgpVrfAFs);
                            if (child.elements("bgpVrfAFs").get(0).elements().get(0).elements("networkRoutes").size() > 0) {
                                for (org.dom4j.Element child1 : child.elements("bgpVrfAFs").get(0).elements().get(0).elements("networkRoutes").get(0).elements()) {
                                    networkRoute = new NetworkRoute(child1.elementText("networkAddress"), child1.elementText("maskLen"));
                                    NetworkRoutes.add(networkRoute);
                                }
                            } else {
//                                    LOG.info("networkRoutes is null.");
                            }
                            for (org.dom4j.Element child1 : child.elements("bgpVrfAFs").get(0).elements().get(0).elements("importRoutes").get(0).elements()) {
                                importRoute = new ImportRoute(child1.elementText("importProtocol"), child1.elementText("importProcessId"));
                                ImportRoutes.add(importRoute);
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        bgpVrf.setImportRoutes(ImportRoutes);
                        bgpVrf.setNetworkRoutes(NetworkRoutes);
                        try {
                            for (org.dom4j.Element child2 : child.elements("bgpPeers").get(0).elements()) {
                                bgpPeer = new BgpPeer(child2.elementText("peerAddr"), child2.elementText("remoteAs"));
                                BgpPeers.add(bgpPeer);
                            }
                        } catch (Exception e1) {
                        } finally {
                            bgpVrf.setBgpPeers(BgpPeers);
                            bgpVrfs.add(bgpVrf);
                        }
                    }
                }
            } catch (Exception e) {
//                LOG.info(e.toString());
            }
        }
        return bgpVrfs;
    }

    public static List<L3vpnInstance> getVpnFromXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<L3vpnInstance> l3vpnInstanceList = new ArrayList<>();
        List<L3vpnIf> l3vpnIfs;
        L3vpnInstance l3vpnInstance;
        L3vpnIf l3vpnIf;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                Element child = null;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    child = childElements.get(attributes.get(attributes.size() - 5).getIndex() - 1);
                } else {
                    child = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
                }
                l3vpnInstance = new L3vpnInstance();
                l3vpnIfs = new ArrayList<>();
                l3vpnInstance.setVrfName(child.elementText("vrfName"));
                l3vpnInstance.setVrfDescription(child.elementText("vrfDescription"));
                String vrfRD = null;
                String vrfLabelMode = null;
                String tnlPolicyName = null;
                String ttlMode = null;
                String vpnFrr = null;
                String vrfRTValue = null;
                try {
                    for (org.dom4j.Element children : child.element("vpnInstAFs").elements()) {
                        vrfRD = children.elementText("vrfRD");
                        vrfLabelMode = children.elementText("vrfLabelMode");
                        tnlPolicyName = children.elementText("tnlPolicyName");
                        ttlMode = children.element("l3vpnTtlMode").elementText("ttlMode");
                        vpnFrr = children.elementText("vpnFrr");
                        vrfRTValue = children.element("vpnTargets").elements().get(0).elementText("vrfRTValue");
                    }
                } catch (Exception e) {
                } finally {
                    l3vpnInstance.setVrfRD(vrfRD);
                    l3vpnInstance.setApplyLabel(vrfLabelMode);
                    l3vpnInstance.setTunnelPolicy(tnlPolicyName);
                    l3vpnInstance.setTtlMode(ttlMode);
                    l3vpnInstance.setVpnFrr(vpnFrr);
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
                    } finally {
                        l3vpnInstance.setL3vpnIfs(l3vpnIfs);
                        l3vpnInstanceList.add(l3vpnInstance);
                    }
                }
            } catch (Exception e) {
            }
        }
        return l3vpnInstanceList;
    }

    public static List<SBfdCfgSession> getBfdCfgSessionsFromXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<SBfdCfgSession> ret = new ArrayList<SBfdCfgSession>();
        SBfdCfgSession sBfdCfgSession;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements();
                Element child = null;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    child = childElements.get(attributes.get(attributes.size() - 5).getIndex() - 1);
                } else {
                    child = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
                }
                sBfdCfgSession = new SBfdCfgSession();
                sBfdCfgSession.setSessName(child.elementText("sessName"));
                sBfdCfgSession.setTunnelName(child.elementText("tunnelName"));
                sBfdCfgSession.setLinkType(child.elementText("linkType"));
                sBfdCfgSession.setCreateType(child.elementText("createType"));
                sBfdCfgSession.setMultiplier(child.elementText("detectMulti"));
                sBfdCfgSession.setMinRxInt(child.elementText("minRxInt"));
                sBfdCfgSession.setMinTxInt(child.elementText("minTxInt"));
                sBfdCfgSession.setLocalDiscr(child.elementText("localDiscr"));
                sBfdCfgSession.setRemoteDiscr(child.elementText("remoteDiscr"));
                ret.add(sBfdCfgSession);
            } catch (Exception ex) {
            }
        }
        return ret;
    }

    public static List<AdjLabel> getSrAdjLabelFromSrAdjLabelXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        List<AdjLabel> adjLabelList = new ArrayList<>();
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> childElements = root.elements().get(0).elements().get(0).elements();
            Element child;
            if (ActionTypeEnum.modify == actionTypeEnum) {
                child = childElements.get(attributes.get(attributes.size() - 5).getIndex() - 1);
            } else {
                child = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
            }
            AdjLabel adjLabel = new AdjLabel();
            adjLabel.setAddressLocal(new Address(child.elementText("localIpAddress"), AddressTypeEnum.V4));
            adjLabel.setAddressRemote(new Address(child.elementText("remoteIpAddress"), AddressTypeEnum.V4));
            adjLabel.setValue(Integer.valueOf(child.elementText("segmentId")));
            adjLabelList.add(adjLabel);
        } catch (DocumentException e) {
        }
        return adjLabelList;
    }

    public static NetconfSrLabelInfo getSrNodeLabelFromgSrNodeLabelXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                Element root = document.getRootElement();
                List<Element> ospfSiteElements = root.elements().get(0).elements().get(0).elements().get(0).elements()
                        .get(0).elements();
                Element child;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    child = ospfSiteElements.get(1).elements().get(0).elements().get(1).elements()
                            .get(attributes.get(attributes.size() - 4).getIndex() - 1);
                } else {
                    child = ospfSiteElements.get(1).elements().get(0).elements().get(1).elements().get(attributes
                            .get(attributes.size() - 1).getIndex() - 1);
                }
                netconfSrLabelInfo.setPrefixIfName(child.elementText("ifName"));
                netconfSrLabelInfo.setPrefixLabel(child.element("srInterface").elementText("prefixLabel"));
                netconfSrLabelInfo.setPrefixType(child.element("srInterface").elementText("prefixSidType"));
                return netconfSrLabelInfo;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static NetconfSrLabelInfo getSrNodeLabelRangeFromNodeLabelRangeXml(String xml, List<Attribute> attributes, ActionTypeEnum actionTypeEnum) {
        NetconfSrLabelInfo netconfSrLabelInfo = new NetconfSrLabelInfo();
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<Element> ospfSiteElements = root.elements().get(0).elements().get(0).elements().get(0).elements()
                        .get(0).elements();
                Element child;
                if (ActionTypeEnum.modify == actionTypeEnum) {
                    child = ospfSiteElements.get(1).elements().get(0).elements().get(1).elements()
                            .get(attributes.get(attributes.size() - 4).getIndex() - 1);
                } else {
                    child = ospfSiteElements.get(1).elements().get(attributes.get(attributes.size() - 1).getIndex() - 1);
                }
                netconfSrLabelInfo.setSrgbBegin(child.elementText("srgbBegin"));
                netconfSrLabelInfo.setSrgbEnd(child.elementText("srgbEnd"));
                return netconfSrLabelInfo;
            } catch (Exception e) {
            }
        }
        return null;
    }
}
