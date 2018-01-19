package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.actionCfg.SCheckPointInfo;
import cn.org.upbnc.util.netconf.actionCfg.SPointChangeInfo;
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
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "<cfg:rollbackByCommitId xmlns:cfg=\"http://www.huawei.com/netconf/vrp/huawei-cfg\">  \n" +
                "<cfg:commitId>" + commitId + "</cfg:commitId>                                        \n" +
                "</cfg:rollbackByCommitId>                                                            \n" +
                "</rpc>";
        return xml;
    }

    public static String getCheckPointInfoXml(String commitId) {
        if ((commitId == null) || (commitId.equals(""))) {
            return "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                    + GetMessageId.getId() + "\">\n" +
                    "<get>                                                                      \n" +
                    "  <filter type=\"subtree\">                                                \n" +
                    "    <cfg:cfg xmlns:cfg=\"http://www.huawei.com/netconf/vrp/huawei-cfg\">   \n" +
                    "      <cfg:checkPointInfos>                                                \n" +
                    "        <cfg:checkPointInfo>                                                \n" +
                    "          <cfg:userLabel/>                                                   \n" +
                    "          <cfg:userName/>                                                    \n" +
                    "          <cfg:line/>                                                        \n" +
                    "          <cfg:client/>                                                      \n" +
                    "          <cfg:timeStamp/>                                                   \n" +
                    "          <cfg:description/>                                                 \n" +
                    "        </cfg:checkPointInfo>                                                \n" +
                    "      </cfg:checkPointInfos>                                                 \n" +
                    "    </cfg:cfg>                                                               \n" +
                    "  </filter>                                                                   \n" +
                    "</get>                                                                         " +
                    "</rpc>";
        } else {
            return "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                    + GetMessageId.getId() + "\">\n" +
                    "<get>                                                                          \n" +
                    "  <filter type=\"subtree\">                                                   \n" +
                    "    <cfg:cfg xmlns:cfg=\"http://www.huawei.com/netconf/vrp/huawei-cfg\">      \n" +
                    "      <cfg:checkPointInfos>                                                   \n" +
                    "        <cfg:checkPointInfo>                                                  \n" +
                    "          <commitId>" + commitId + "</commitId>                               \n" +
                    "          <cfg:currentPointChanges/>                                          \n" +
                    "          <cfg:sincePointChanges/>                                            \n" +
                    "        </cfg:checkPointInfo>                                                 \n" +
                    "      </cfg:checkPointInfos>                                                  \n" +
                    "    </cfg:cfg>                                                                \n" +
                    "  </filter>                                                                   \n" +
                    "</get>                                                                         " +
                    "</rpc>";
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
            List<Element> checkPointInfoElements = root.element("data").element("cfg").element("checkPointInfos").elements("checkPointInfo");
            for (org.dom4j.Element checkPointInfo : checkPointInfoElements) {
                SCheckPointInfo sCheckPointInfo = new SCheckPointInfo();
                sCheckPointInfo.setCommitId(checkPointInfo.elementText("commitId"));
                sCheckPointInfo.setUserLabel(checkPointInfo.elementText("userLabel"));
                sCheckPointInfo.setUserName(checkPointInfo.elementText("userName"));
                sCheckPointInfo.setTimeStamp(checkPointInfo.elementText("timeStamp"));
                List<SPointChangeInfo> currList = new ArrayList<>();
                List<SPointChangeInfo> sinceList = new ArrayList<>();
                sCheckPointInfo.setCurrList(currList);
                sCheckPointInfo.setSinceList(sinceList);
                if (checkPointInfo.element("currentPointChanges") != null) {
                    for (org.dom4j.Element currentPointChange : checkPointInfo.element("currentPointChanges").elements("currentPointChange")) {
                        SPointChangeInfo sPointChangeInfo = new SPointChangeInfo();
                        sPointChangeInfo.setIndex(currentPointChange.elementText("index"));
                        sPointChangeInfo.setChange(currentPointChange.elementText("configChange"));
                        currList.add(sPointChangeInfo);
                    }
                }
                if (checkPointInfo.element("sincePointChanges") != null) {
                    for (org.dom4j.Element currentPointChange : checkPointInfo.element("sincePointChanges").elements("sincePointChange")) {
                        SPointChangeInfo sPointChangeInfo = new SPointChangeInfo();
                        sPointChangeInfo.setIndex(currentPointChange.elementText("index"));
                        sPointChangeInfo.setChange(currentPointChange.elementText("configChange"));
                        sinceList.add(sPointChangeInfo);
                    }
                }
                sCheckPointInfoList.add(sCheckPointInfo);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sCheckPointInfoList;
    }
}
