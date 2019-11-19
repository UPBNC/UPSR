package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficClassAclInfo;
import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficClassInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficClassifier {
    public static String getTrafficClassifierXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                            \n" +
                "  <filter type=\"subtree\">                                                      \n" +
                "    <qos:qos xmlns:qos=\"http://www.huawei.com/netconf/vrp/huawei-qos\">         \n" +
                "      <qos:qosCbQos>                                                             \n" +
                "        <qos:qosClassifiers>                                                     \n" +
                "          <qos:qosClassifier/>                                                   \n" +
                "        </qos:qosClassifiers>                                                    \n" +
                "      </qos:qosCbQos>                                                            \n" +
                "    </qos:qos>                                                                   \n" +
                "  </filter>                                                                      \n" +
                "</get>                                                                           \n" +
                "</rpc>";
    }

    public static List<STrafficClassInfo> getSTrafficClassFromXml(String xml) {
        List<STrafficClassInfo> sTrafficClassInfoList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sTrafficClassInfoList;
        }
        SAXReader reader = new SAXReader();
        try {
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> qosClassifierElements = root.element("data").element("qos").element("qosCbQos").element("qosClassifiers").elements("qosClassifier");
            for (org.dom4j.Element qosClassifier : qosClassifierElements) {
                STrafficClassInfo sTrafficClassInfo = new STrafficClassInfo();
                sTrafficClassInfo.setTrafficClassName(qosClassifier.elementText("classifierName"));
                sTrafficClassInfo.setOperator(qosClassifier.elementText("operator"));
                List<STrafficClassAclInfo> sTrafficClassAclInfoList = new ArrayList<>();
                List<Element> qosRuleAclElements = qosClassifier.element("qosRuleAcls").elements("qosRuleAcl");
                for (org.dom4j.Element qosRuleAcl : qosRuleAclElements) {
                    STrafficClassAclInfo sTrafficClassAclInfo = new STrafficClassAclInfo();
                    sTrafficClassAclInfo.setAclName(qosRuleAcl.elementText("aclName"));
                    sTrafficClassAclInfoList.add(sTrafficClassAclInfo);
                }
                sTrafficClassInfo.setsTrafficClassAclInfoList(sTrafficClassAclInfoList);
                sTrafficClassInfoList.add(sTrafficClassInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTrafficClassInfoList;
    }
}
