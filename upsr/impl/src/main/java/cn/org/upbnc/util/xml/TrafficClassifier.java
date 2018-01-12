package cn.org.upbnc.util.xml;

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
}
