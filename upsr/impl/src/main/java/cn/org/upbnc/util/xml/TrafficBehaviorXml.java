package cn.org.upbnc.util.xml;

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
}
