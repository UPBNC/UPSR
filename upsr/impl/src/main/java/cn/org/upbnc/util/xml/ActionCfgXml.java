package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.actionCfg.SCheckPointInfo;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ActionCfgXml {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgXml.class);
    public static String getCommitCfgXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                + GetMessageId.getId() + "\">\n" +
                "    <commit/>\n" +
                "</rpc>";
        return xml;
    }

    public static String getCancelCfgXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                + GetMessageId.getId() + "\">\n" +
                "    <discard-changes/>\n" +
                "</rpc>";
        return xml;
    }

    public static String getRollBackToCommitIdXml(String commitId) {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                + GetMessageId.getId() + "\">\n" +
                "    <discard-changes/>\n" +
                "</rpc>";
        return xml;
    }

    public static String getCheckPointInfoXml(String commitId) {
        if ("".equals(commitId)) {
            return "<get>\n" +
                    "  <filter type=\"subtree\">\n" +
                    "    <cfg:cfg xmlns:cfg=\"http://www.huawei.com/netconf/vrp/huawei-cfg\">\n" +
                    "      <cfg:checkPointInfos>\n" +
                    "        <cfg:checkPointInfo>\n" +
                    "          <cfg:userLabel/>\n" +
                    "          <cfg:userName/>\n" +
                    "          <cfg:line/>\n" +
                    "          <cfg:client/>\n" +
                    "          <cfg:timeStamp/>\n" +
                    "          <cfg:description/>\n" +
                    "        </cfg:checkPointInfo>\n" +
                    "      </cfg:checkPointInfos>\n" +
                    "    </cfg:cfg>\n" +
                    "  </filter>\n" +
                    "</get>";
        } else {
            return "<get>\n" +
                    "  <filter type=\"subtree\">\n" +
                    "    <cfg:cfg xmlns:cfg=\"http://www.huawei.com/netconf/vrp/huawei-cfg\">\n" +
                    "      <cfg:checkPointInfos>\n" +
                    "        <cfg:checkPointInfo>\n" +
                    "          <commitId>" + commitId + "</commitId>\n" +
                    "          <cfg:currentPointChanges/>\n" +
                    "          <cfg:sincePointChanges/>\n" +
                    "        </cfg:checkPointInfo>\n" +
                    "      </cfg:checkPointInfos>\n" +
                    "    </cfg:cfg>\n" +
                    "  </filter>\n" +
                    "</get>";
        }
    }
    public static List<SCheckPointInfo> getCheckPointInfoFromXml(String xml) {
        List<SCheckPointInfo> sCheckPointInfoList = new ArrayList<>();
        if (null == xml || xml.isEmpty()) {//判断xml是否为空
            return sCheckPointInfoList;
        }
        SAXReader reader = new SAXReader();
        try {
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            List<Element> aclGroupElements = root.element("data").element("acl").element("aclGroups").elements("aclGroup");
        }catch (DocumentException e) {
            e.printStackTrace();
        }
        return sCheckPointInfoList;
    }
}
