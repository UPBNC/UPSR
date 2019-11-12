package cn.org.upbnc.util.xml;

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
        org.dom4j.Document document = null;
        try {
            document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> aclGroupElements = root.element("data").element("acl").element("aclGroups").elements("aclGroup");
            for (org.dom4j.Element aclGroupElement : aclGroupElements) {
                STrafficClassInfo sTrafficClassInfo = new STrafficClassInfo();
                sTrafficClassInfo.setTrafficClassName(aclGroupElement.elementText("aclNumOrName"));

                sTrafficClassInfoList.add(sTrafficClassInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTrafficClassInfoList;
    }
}
