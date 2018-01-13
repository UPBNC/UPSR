package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficBehaveInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficBehaviorXml {
    public static String getTrafficBehaviorXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                 \n" +
                "  <filter type=\"subtree\">                                                           \n" +
                "    <qos:qos xmlns:qos=\"http://www.huawei.com/netconf/vrp/huawei-qos\">              \n" +
                "      <qos:qosCbQos>                                                                  \n" +
                "        <qos:qosBehaviors>                                                            \n" +
                "          <qos:qosBehavior>                                                           \n" +
                "            <qos:qosActRdrTnls/>                                                      \n" +
                "          </qos:qosBehavior>                                                          \n" +
                "        </qos:qosBehaviors>                                                           \n" +
                "      </qos:qosCbQos>                                                                 \n" +
                "    </qos:qos>                                                                        \n" +
                "  </filter>                                                                           \n" +
                "</get>                                                                                \n" +
                "</rpc>";
    }

    public static List<STrafficBehaveInfo> getSTrafficBehaveFromXml(String xml) {
        List<STrafficBehaveInfo> sTrafficBehaveInfoList = new ArrayList<>();

        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sTrafficBehaveInfoList;
        }
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> qosBehaviorElements = root.element("data").element("qos").element("qosCbQos").element("qosBehaviors").elements("qosBehavior");
            for (org.dom4j.Element qosBehavior : qosBehaviorElements) {
                STrafficBehaveInfo sTrafficBehaveInfo = new STrafficBehaveInfo();
                sTrafficBehaveInfo.setTrafficBehaveName(qosBehavior.elementText("behaviorName"));
                sTrafficBehaveInfo.setRedirectTunnelName(qosBehavior.element("qosActRdrTnls").element("qosActRdrTnl").elementText("ifName"));
                sTrafficBehaveInfoList.add(sTrafficBehaveInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTrafficBehaveInfoList;
    }
}
