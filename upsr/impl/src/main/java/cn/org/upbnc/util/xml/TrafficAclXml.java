package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.SAclInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficAclXml {

    public static String getSTrafficAclXml() {
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

    public static List<SAclInfo> getSTrafficAclFromXml(String xml) {
        List<SAclInfo> sAclInfoList = new ArrayList<>();

        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sAclInfoList;
        }
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> aclGroupElements = root.element("data").element("acl").element("aclGroups").elements("aclGroup");
            for (org.dom4j.Element aclGroupElement : aclGroupElements) {
                SAclInfo sAclInfo = new SAclInfo();
                sAclInfo.setAclNumOrName(aclGroupElement.elementText("aclNumOrName"));

                sAclInfoList.add(sAclInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sAclInfoList;
    }
}
