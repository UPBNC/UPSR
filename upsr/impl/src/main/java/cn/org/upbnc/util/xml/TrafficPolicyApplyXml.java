package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficIfPolicyInfo;
import cn.org.upbnc.util.netconf.TrafficPolicy.STrafficPolicyInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficPolicyApplyXml {
    public static String getTrafficPolicyApplyXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                             \n" +
                "  <filter type=\"subtree\">                                                       \n" +
                "    <qos:qos xmlns:qos=\"http://www.huawei.com/netconf/vrp/huawei-qos\">          \n" +
                "      <qos:qosIfQoss>                                                             \n" +
                "        <qos:qosIfQos>                                                            \n" +
                "          <qos:qosPolicyApplys>                                                   \n" +
                "            <qos:qosPolicyApply>                                                  \n" +
                "              <qos:policyName/>                                                   \n" +
                "            </qos:qosPolicyApply>                                                 \n" +
                "          </qos:qosPolicyApplys>                                                  \n" +
                "        </qos:qosIfQos>                                                           \n" +
                "      </qos:qosIfQoss>                                                            \n" +
                "    </qos:qos>                                                                    \n" +
                "  </filter>                                                                       \n" +
                "</get>                                                                            \n" +
                "</rpc>";
    }
    public static String getDeleteTrafficPolicyApplyXml(String ifName) {
        return null;
    }
    public static List<STrafficIfPolicyInfo> getSTrafficIfPolicyFromXml(String xml){
        List<STrafficIfPolicyInfo> sTrafficIfPolicyInfoList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sTrafficIfPolicyInfoList;
        }
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> qosIfQosElements = root.element("data").element("qos").element("qosIfQoss").elements("qosIfQos");
            for (org.dom4j.Element qosIfQos : qosIfQosElements) {
                STrafficIfPolicyInfo sTrafficIfPolicyInfo = new STrafficIfPolicyInfo();
                sTrafficIfPolicyInfo.setIfName(qosIfQos.elementText("ifName"));
                if (qosIfQos.element("qosPolicyApplys") != null ) {
                    sTrafficIfPolicyInfo.setDirection(qosIfQos.element("qosPolicyApplys").element("qosPolicyApply").elementText("direction"));
                    sTrafficIfPolicyInfo.setPolicyName(qosIfQos.element("qosPolicyApplys").element("qosPolicyApply").elementText("policyName"));
                }
                sTrafficIfPolicyInfoList.add(sTrafficIfPolicyInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sTrafficIfPolicyInfoList;
    }
}
