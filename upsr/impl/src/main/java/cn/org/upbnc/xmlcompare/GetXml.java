package cn.org.upbnc.xmlcompare;

import cn.org.upbnc.util.netconf.*;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GetXml {
    public static List<SSrTeTunnel> getSrTeTunnelFromXml(String xml, List<Attribute> attributes) {
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        SSrTeTunnel srTeTunnel;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                Element root = document.getRootElement();
                List<Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                Element element = childElements.get(attributes.get(attributes.size() - 1).getIndex() - 1);
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

}
