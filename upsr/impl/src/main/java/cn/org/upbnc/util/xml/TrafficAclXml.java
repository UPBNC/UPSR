package cn.org.upbnc.util.xml;

public class TrafficAclXml {
    public static String getTrafficAclXml() {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                     \n" +
                "  <filter type=\"subtree\">                                               \n" +
                "    <acl:acl xmlns:acl=\"http://www.huawei.com/netconf/vrp/huawei-acl\">  \n" +
                "      <acl:aclGroups>                                                     \n" +
                "        <acl:aclGroup>                                                    \n" +
                "          <aclType>Advance</aclType>                                      \n" +
                "        </acl:aclGroup>                                                   \n" +
                "      </acl:aclGroups>                                                    \n" +
                "    </acl:acl>                                                            \n" +
                "  </filter>                                                               \n" +
                "</get>                                                                    \n" +
                "</rpc>";
    }
}
