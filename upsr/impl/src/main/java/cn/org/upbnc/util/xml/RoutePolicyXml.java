package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SRoutePolicy;
import cn.org.upbnc.util.netconf.SRoutePolicyNode;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutePolicyXml {
    private static final Logger LOG = LoggerFactory.getLogger(RoutePolicyXml.class);

    public static String getRoutePolicyXml(String routePolicyName) {
        String start = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <rtp:rtp xmlns:rtp=\"http://www.huawei.com/netconf/vrp/huawei-rtp\">\n" +
                "      <rtp:routePolicys>\n" +
                "        <rtp:routePolicy>\n";
        String middle = "";
        if (!(("").equals(routePolicyName))) {
            middle = middle +
                    "          <name>" + routePolicyName + "</name>\n";
        }
        String end =
                "        </rtp:routePolicy>\n" +
                        "      </rtp:routePolicys>\n" +
                        "    </rtp:rtp>\n" +
                        "  </filter>\n" +
                        "</get>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getCreateRoutePolicyXml(List<SRoutePolicy> sRoutePolicies, Map<String, List<String>> map) {
        String start = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "  <edit-config>\n" +
                "    <target>\n" +
                "      <running/>\n" +
                "    </target>\n" +
                "    <config>\n" +
                "      <rtp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-rtp\">\n" +
                "        <routePolicys>\n";
        String middle = "";
        for (SRoutePolicy routePolicy : sRoutePolicies) {
            middle = middle +
                    "          <routePolicy>\n" +
                    "            <name>" + routePolicy.getName() + "</name>\n" +
                    "            <routePolicyNodes>\n";
            for (SRoutePolicyNode routePolicyNode : routePolicy.getRoutePolicyNodes()) {
                middle = middle +
                        "              <routePolicyNode>\n" +
                        "                <nodeSequence>" + routePolicyNode.getNodeSequence() + "</nodeSequence>\n" +
                        "                <matchMode>permit</matchMode>\n" +
                        "                <nextNodeChoice>\n" +
                        "                  <isGotoNextNode>false</isGotoNextNode>\n" +
                        "                </nextNodeChoice>\n" +
                        "              </routePolicyNode>\n";
            }
            if (map.containsKey(routePolicy.getName())) {
                for (String str : map.get(routePolicy.getName())) {
                    middle = middle + "            <routePolicyNode xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                            "              <nodeSequence>" + str + "</nodeSequence>\n" +
                            "            </routePolicyNode>";
                }
            }
            middle = middle +
                    "            </routePolicyNodes>\n" +
                    "          </routePolicy>\n";
        }
        String end =
                "        </routePolicys>\n" +
                        "      </rtp>\n" +
                        "    </config>\n" +
                        "  </edit-config>\n" +
                        "</rpc>";
        return start + middle + end;
    }

    public static String getDeleteRoutePolicyXml(List<SRoutePolicy> sRoutePolicies) {
        String start = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<edit-config xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <target>\n" +
                "    <running/>\n" +
                "  </target>\n" +
                "  <config>\n" +
                "    <rtp xmlns=\"http://www.huawei.com/netconf/vrp/huawei-rtp\">\n" +
                "      <routePolicys>\n";
        String middle = "";
        for (SRoutePolicy routePolicy : sRoutePolicies) {
            middle = middle +
                    "        <routePolicy xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"delete\">\n" +
                    "          <name>" + routePolicy.getName() + "</name>\n" +
                    "        </routePolicy>\n";
        }
        String end =
                "      </routePolicys>\n" +
                        "    </rtp>\n" +
                        "  </config>\n" +
                        "</edit-config>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static List<SRoutePolicy> getRoutePolicyFromXml(String xml) {
        List<SRoutePolicy> routePolicies = new ArrayList<>();
        SRoutePolicy routePolicy;
        List<SRoutePolicyNode> routePolicyNodes;
        SRoutePolicyNode routePolicyNode;
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0)
                        .elements().get(0).elements();
                for (org.dom4j.Element child : childElements) {

                    routePolicy = new SRoutePolicy();
                    routePolicy.setName(child.elementText("name"));
                    routePolicyNodes = new ArrayList<>();
                    for (org.dom4j.Element child1 : child.elements("routePolicyNodes").get(0).elements()) {
                        routePolicyNode = new SRoutePolicyNode();
                        routePolicyNode.setNodeSequence(child1.elementText("nodeSequence"));
                        routePolicyNodes.add(routePolicyNode);
                    }
                    routePolicy.setRoutePolicyNodes(routePolicyNodes);
                    routePolicies.add(routePolicy);
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        LOG.info("routePolicies : " + routePolicies.toString());
        return routePolicies;
    }
}
