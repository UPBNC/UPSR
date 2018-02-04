package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SSrTeTunnel;
import cn.org.upbnc.util.netconf.SSrTeTunnelPath;
import cn.org.upbnc.util.netconf.STunnelServiceClass;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
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
                    "            <hotStandbyEnable>true</hotStandbyEnable>\n" +
                    "            <mplsTeTunnelSetupPriority>" + srTeTunnel.getMplsTeTunnelSetupPriority() + "</mplsTeTunnelSetupPriority>\n" +
                    "            <holdPriority>" + srTeTunnel.getHoldPriority() + "</holdPriority>\n";
            if (!("".equals(srTeTunnel.getMplsTunnelBandwidth()))) {
                middle = middle +
                        "            <mplsTunnelBandwidth>" + srTeTunnel.getMplsTunnelBandwidth() + "</mplsTunnelBandwidth>\n";
            }
            middle = middle +
                    "            <resvForBinding>true</resvForBinding>\n" +
                    "            <tunnelInterface>\n" +
                    "              <interfaceName>" + srTeTunnel.getTunnelName() + "</interfaceName>\n" +
                    "              <lsp_tpEnable>true</lsp_tpEnable>" +
                    "              <statEnable>true</statEnable>\n";
            if (srTeTunnel.getMplsteServiceClass() != null) {
                STunnelServiceClass sc = srTeTunnel.getMplsteServiceClass();
                middle = middle +
                        "             <mplsteServiceClass>\n" +
                        "                 <defaultServiceClassEnable>" + String.valueOf(sc.isDefaultServiceClassEnable()) + "</defaultServiceClassEnable>\n" +
                        "                 <beServiceClassEnable>     " + String.valueOf(sc.isBeServiceClassEnable()) + "</beServiceClassEnable>\n" +
                        "                 <af1ServiceClassEnable>    " + String.valueOf(sc.isAf1ServiceClassEnable()) + "</af1ServiceClassEnable>\n" +
                        "                 <af2ServiceClassEnable>    " + String.valueOf(sc.isAf2ServiceClassEnable()) + "</af2ServiceClassEnable>\n" +
                        "                 <af3ServiceClassEnable>    " + String.valueOf(sc.isAf3ServiceClassEnable()) + "</af3ServiceClassEnable>\n" +
                        "                 <af4ServiceClassEnable>    " + String.valueOf(sc.isAf4ServiceClassEnable()) + "</af4ServiceClassEnable>\n" +
                        "                 <efServiceClassEnable>     " + String.valueOf(sc.isEfServiceClassEnable()) + "</efServiceClassEnable>\n" +
                        "                 <cs6ServiceClassEnable>    " + String.valueOf(sc.isCs6ServiceClassEnable()) + "</cs6ServiceClassEnable>\n" +
                        "                 <cs7ServiceClassEnable>    " + String.valueOf(sc.isCs7ServiceClassEnable()) + "</cs7ServiceClassEnable>\n" +
                        "             </mplsteServiceClass>\n";
            }
            middle = middle +
                    "            </tunnelInterface>\n";

            if ("".equals(srTeTunnel.getMplsTeTunnelBfdMinTx()) || "".equals(srTeTunnel.getMplsTeTunnelBfdMinnRx())
                    || "".equals(srTeTunnel.getMplsTeTunnelBfdDetectMultiplier())) {
                LOG.info("bfd is not set.");
            } else {
                middle = middle +
                        "            <mplsTeTunnelBfd>\n" +
                        "              <mplsTeTunnelBfdEnable>true</mplsTeTunnelBfdEnable>" +
                        "              <mplsTeTunnelBfdMinTx>" + srTeTunnel.getMplsTeTunnelBfdMinTx() + "</mplsTeTunnelBfdMinTx>\n" +
                        "              <mplsTeTunnelBfdMinnRx>" + srTeTunnel.getMplsTeTunnelBfdMinnRx() + "</mplsTeTunnelBfdMinnRx>\n" +
                        "              <mplsTeTunnelBfdDetectMultiplier>" + srTeTunnel.getMplsTeTunnelBfdDetectMultiplier() + "</mplsTeTunnelBfdDetectMultiplier>\n" +
                        "            </mplsTeTunnelBfd>\n";
            }

            String pathsStart = "";
            if (srTeTunnel.getSrTeTunnelPaths() != null && srTeTunnel.getSrTeTunnelPaths().size() > 0) {
                pathsStart = "            <srTeTunnelPaths>\n";
                String pathsMiddle = "";
                for (SSrTeTunnelPath srTeTunnelPath : srTeTunnel.getSrTeTunnelPaths()) {
                    pathsMiddle = pathsMiddle +
                            "              <srTeTunnelPath>\n" +
                            "                <pathType>" + srTeTunnelPath.getPathType() + "</pathType>\n" +
                            "                <explicitPathName>" + srTeTunnelPath.getExplicitPathName() + "</explicitPathName>\n" +
                            "              </srTeTunnelPath>\n";

                }
                String pathsEnd =
                        "            </srTeTunnelPaths>\n";
                pathsStart = pathsStart + pathsMiddle + pathsEnd;
            }
            String middleEnd =
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
                            "    </ifm>\n";
            middle = middle + pathsStart + middleEnd;
        }
        String end =
                "  </config>\n" +
                "</edit-config>\n" +
                "</rpc>";
        return start + middle + end;
    }

    public static String getSrTeTunnelXml(String tunnelName) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<get-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <source>\n" +
                "    <running/>\n" +
                "  </source>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mpls:mplsTe>\n" +
                "        <mpls:srTeTunnels>\n" +
                "          <mpls:srTeTunnel>\n";
        String middle = "";
        if (!("".equals(tunnelName))) {
            middle = middle + "            <mpls:tunnelName>" + tunnelName + "</mpls:tunnelName>\n";
        }
        String end = "          </mpls:srTeTunnel>\n" +
                "        </mpls:srTeTunnels>\n" +
                "      </mpls:mplsTe>\n" +
                "    </mpls:mpls>\n" +
//                "    <ifm xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
//                "      <interfaces>\n" +
//                "        <interface>\n" +
//                "          <ifName>" + tunnelName + "</ifName>\n" +
//                "        </interface>\n" +
//                "      </interfaces>\n" +
//                "    </ifm>\n" +
                "  </filter>\n" +
                "</get-config>" +
                "</rpc>";
        return start + middle + end;
    }

    public static List<SSrTeTunnel> getSrTeTunnelFromXml(String xml) {
        List<SSrTeTunnel> srTeTunnels = new ArrayList<>();
        SSrTeTunnel srTeTunnel;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element element : childElements) {
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
                    for (org.dom4j.Element child : element.elements("srTeTunnelPaths").get(0).elements()) {
                        srTeTunnelPath = new SSrTeTunnelPath();
                        srTeTunnelPath.setPathType(child.elementText("pathType"));
                        srTeTunnelPath.setExplicitPathName(child.elementText("explicitPathName"));
                        LOG.info("child.elementText(\"explicitPathName\") :" + child.elementText("explicitPathName"));
                        if (null != child.elementText("explicitPathName")) {
                            srTeTunnelPaths.add(srTeTunnelPath);
                        }
                    }
                    //get service class
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
                }
            } catch (Exception e) {

            }
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
                "  </config>\n" +
                "</edit-config>\n" +
                "</rpc>";
        return start;
    }


    public static String getDeleteSrTeTunnelsXml(List<String> tunnelNames) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "      <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mpls:mplsTe>\n" +
                "        <mpls:srTeTunnels>\n";

        String middle = "";
        for (String tunnelName : tunnelNames) {
            middle = middle +
                    "          <mpls:srTeTunnel xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                    "            <mpls:tunnelName>" + tunnelName + "</mpls:tunnelName>\n" +
                    "          </mpls:srTeTunnel>\n";
        }

        String end ="        </mpls:srTeTunnels>\n" +
                "      </mpls:mplsTe>\n" +
                "    </mpls:mpls>\n" +
                "  </config>\n" +
                "</edit-config>\n" +
                "</rpc>";
        return start + middle + end;
    }
}
