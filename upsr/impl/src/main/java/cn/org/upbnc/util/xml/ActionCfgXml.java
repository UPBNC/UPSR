package cn.org.upbnc.util.xml;

import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
