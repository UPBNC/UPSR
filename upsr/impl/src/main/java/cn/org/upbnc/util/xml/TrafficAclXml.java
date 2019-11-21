package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.TrafficPolicy.SAclInfo;
import cn.org.upbnc.util.netconf.TrafficPolicy.SAclRuleInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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

    public static String getDeleteTrafficAclXml(String aclName) {
        return null;
    }

    public static List<SAclInfo> getTrafficAclFromXml(String xml) {
        List<SAclInfo> sAclInfoList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sAclInfoList;
        }
        SAXReader reader = new SAXReader();
        try {
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> aclGroupElements = root.element("data").element("acl").element("aclGroups").elements("aclGroup");
            for (org.dom4j.Element aclGroupElement : aclGroupElements) {
                SAclInfo sAclInfo = new SAclInfo();
                List<SAclRuleInfo> sAclRuleInfoList = new ArrayList<>();
                sAclInfo.setAclNumOrName(aclGroupElement.elementText("aclNumOrName"));
                List<Element> aclRuleAdv4Elements = aclGroupElement.element("aclRuleAdv4s").elements("aclRuleAdv4");
                for (org.dom4j.Element aclRuleAdv4 : aclRuleAdv4Elements) {
                    SAclRuleInfo sAclRuleInfo = new SAclRuleInfo();
                    sAclRuleInfo.setRuleId(aclRuleAdv4.elementText("aclRuleID"));
                    sAclRuleInfo.setRuleType(aclRuleAdv4.elementText("aclAction"));
                    sAclRuleInfo.setProtoType(aclRuleAdv4.elementText("aclProtocol"));
                    sAclRuleInfo.setSourcce(aclRuleAdv4.elementText("aclSourceIp"));
                    sAclRuleInfo.setSourcceWild(aclRuleAdv4.elementText("aclSrcWild"));
                    sAclRuleInfo.setSourcePortOp(aclRuleAdv4.elementText("aclSrcPortOp"));
                    sAclRuleInfo.setSourcePort(aclRuleAdv4.elementText("aclSrcPortBegin"));
                    sAclRuleInfo.setDestination(aclRuleAdv4.elementText("aclDestIp"));
                    sAclRuleInfo.setDestinationWild(aclRuleAdv4.elementText("aclDestWild"));
                    sAclRuleInfo.setDestinationPortOp(aclRuleAdv4.elementText("aclDestPortOp"));
                    sAclRuleInfo.setDestinationPort(aclRuleAdv4.elementText("aclDestPortB"));
                    sAclRuleInfoList.add(sAclRuleInfo);
                }
                sAclInfo.setsAclRuleInfoList(sAclRuleInfoList);
                sAclInfoList.add(sAclInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sAclInfoList;
    }
}
