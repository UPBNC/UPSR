package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SExplicitPath;
import cn.org.upbnc.util.netconf.SExplicitPathHop;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ExplicitPathXml {
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitPathXml.class);

    public static String createExplicitPathXml(List<SExplicitPath> explicitPaths) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "    <mpls xmlns=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mplsTe>\n" +
                "        <explicitPaths>\n";
        String middle = "";
        for (SExplicitPath explicitPath : explicitPaths) {
            middle = middle +
                    "          <explicitPath>\n" +
                    "            <explicitPathName>" + explicitPath.getExplicitPathName() + "</explicitPathName>\n" +
                    "            <explicitPathHops>\n";
            String middleStart = "";

            for (SExplicitPathHop explicitPathHop : explicitPath.getExplicitPathHops()) {
                middleStart = middleStart +
                        "              <explicitPathHop>\n" +
                        "                <mplsTunnelHopIndex>" + explicitPathHop.getMplsTunnelHopIndex() + "</mplsTunnelHopIndex>\n" +
                        "                <mplsTunnelHopMode>SID_LABEL</mplsTunnelHopMode>\n" +
                        "                <mplsTunnelHopSidLabel>" + explicitPathHop.getMplsTunnelHopSidLabel() + "</mplsTunnelHopSidLabel>\n" +
                        "                <mplsTunnelHopSidLabelType>" + explicitPathHop.getMplsTunnelHopSidLabelType() + "</mplsTunnelHopSidLabelType>\n" +
                        "              </explicitPathHop>\n";
            }
            String middleEnd =
                    "            </explicitPathHops>\n" +
                            "          </explicitPath>\n";
            middle = middle + middleStart + middleEnd;
        }
        String end =
                "        </explicitPaths>\n" +
                        "      </mplsTe>\n" +
                        "    </mpls>\n" +
                        "  </config>\n" +
                        "</edit-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getExplicitPathXml(List<SExplicitPath> explicitPaths) {
        String start =
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                        "<get-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                        "  <source>\n" +
                        "    <running/>\n" +
                        "  </source>\n" +
                        "  <filter type=\"subtree\">\n" +
                        "    <mpls:mpls xmlns:mpls=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                        "      <mpls:mplsTe>\n" +
                        "        <mpls:explicitPaths>\n";
        String middle = "";
        for (SExplicitPath explicitPath : explicitPaths) {
            middle = middle +
                    "          <mpls:explicitPath>\n" +
                    "            <mpls:explicitPathName>" + explicitPath.getExplicitPathName() + "</mpls:explicitPathName>\n" +
                    "          </mpls:explicitPath>\n";
        }
        String end =
                "        </mpls:explicitPaths>\n" +
                        "      </mpls:mplsTe>\n" +
                        "    </mpls:mpls>\n" +
                        "  </filter>\n" +
                        "</get-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getDeleteExplicitPathXml(List<SExplicitPath> explicitPaths) {
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "    <mpls xmlns=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mplsTe>\n" +
                "        <explicitPaths>\n";
        String middle = "";
        for (SExplicitPath explicitPath : explicitPaths) {
            middle = middle +

                    "          <explicitPath xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                    "            <explicitPathName>" + explicitPath.getExplicitPathName() + "</explicitPathName>\n" +
                    "          </explicitPath>\n";
        }
        String end =
                "        </explicitPaths>\n" +
                        "      </mplsTe>\n" +
                        "    </mpls>\n" +
                        "  </config>\n" +
                        "</edit-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getDeleteExplicitPathByNamesXml(List<String> explicitPaths){
        String start = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "    <mpls xmlns=\"http://www.huawei.com/netconf/vrp/huawei-mpls\">\n" +
                "      <mplsTe>\n" +
                "        <explicitPaths>\n";
        String middle = "";
        for (String explicitPath : explicitPaths) {
            middle = middle +

                    "          <explicitPath xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                    "            <explicitPathName>" + explicitPath + "</explicitPathName>\n" +
                    "          </explicitPath>\n";
        }
        String end =
                "        </explicitPaths>\n" +
                        "      </mplsTe>\n" +
                        "    </mpls>\n" +
                        "  </config>\n" +
                        "</edit-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static List<SExplicitPath> getExplicitPathFromXml(String xml) {
        List<SExplicitPath> explicitPaths = new ArrayList<>();
        SExplicitPath explicitPath;
        List<SExplicitPathHop> explicitPathHops;
        SExplicitPathHop explicitPathHop;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element element : childElements) {
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
                }
            } catch (Exception e) {

            }
        }
        return explicitPaths;
    }
}
