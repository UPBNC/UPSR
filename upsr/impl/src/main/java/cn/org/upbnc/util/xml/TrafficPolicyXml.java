package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficPolicyInfo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficPolicyNodeInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
public class TrafficPolicyXml {
    public static String getTrafficPolicyXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                         \n" +
                "  <filter type=\"subtree\">                                                   \n" +
                "    <qos:qos xmlns:qos=\"http://www.huawei.com/netconf/vrp/huawei-qos\">      \n" +
                "      <qos:qosCbQos>                                                          \n" +
                "        <qos:qosPolicys>                                                      \n" +
                "          <qos:qosPolicy/>                                                    \n" +
                "        </qos:qosPolicys>                                                     \n" +
                "      </qos:qosCbQos>                                                         \n" +
                "    </qos:qos>                                                                \n" +
                "  </filter>                                                                   \n" +
                "</get>                                                                        \n" +
                "</rpc>";
    }

    public static List<STrafficPolicyInfo> getSTrafficPolicyFromXml(String xml){
        List<STrafficPolicyInfo> sTrafficPolicyInfoList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sTrafficPolicyInfoList;
        }
        SAXReader reader = new SAXReader();
        try {
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> qosPolicyElements = root.element("data").element("qos").element("qosCbQos").element("qosPolicys").elements("qosPolicy");
            for (org.dom4j.Element qosPolicy : qosPolicyElements) {
                STrafficPolicyInfo sTrafficPolicyInfo = new STrafficPolicyInfo();
                sTrafficPolicyInfo.setTrafficPolicyName(qosPolicy.elementText("policyName"));
                List<STrafficPolicyNodeInfo> sTrafficPolicyNodeInfoList = new ArrayList<>();
                if (qosPolicy.element("qosPolicyNodes") != null) {
                    List<Element> qosPolicyNodeElements = qosPolicy.element("qosPolicyNodes").elements("qosPolicyNode");
                    for (org.dom4j.Element qosPolicyNode : qosPolicyNodeElements) {
                        STrafficPolicyNodeInfo sTrafficPolicyNodeInfo = new STrafficPolicyNodeInfo();
                        sTrafficPolicyNodeInfo.setClassName(qosPolicyNode.elementText("classifierName"));
                        sTrafficPolicyNodeInfo.setBehaveName(qosPolicyNode.elementText("behaviorName"));
                        sTrafficPolicyNodeInfoList.add(sTrafficPolicyNodeInfo);
                    }
                }
                sTrafficPolicyInfo.setsTrafficPolicyNodeInfoList(sTrafficPolicyNodeInfoList);
                sTrafficPolicyInfoList.add(sTrafficPolicyInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTrafficPolicyInfoList;
    }
}
