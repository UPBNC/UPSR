package cn.org.upbnc.util.xml;

public class TrafficPolicyApplyXml {
    public static String getTrafficPolicyXml() {
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
}
