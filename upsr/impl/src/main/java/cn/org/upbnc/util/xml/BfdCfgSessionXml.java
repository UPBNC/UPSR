package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SBfdCfgSession;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BfdCfgSessionXml {
    private static final Logger LOG = LoggerFactory.getLogger(cn.org.upbnc.util.xml.BfdCfgSessionXml.class);

    public static String createBfdCfgSessionsXml(List<SBfdCfgSession> bfdCfgSessions) {
        String head = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <edit-config >\n";

        String target = "    <target>\n" +
                "      <candidate/>\n" +
                "    </target>\n";

        String configStart = "    <config>\n";

        String bfdStart = "      <bfd xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bfd\">\n" +
                "        <bfdCfgSessions>\n";


        String bfdSession = "";

        for (SBfdCfgSession sBfdCfgSession : bfdCfgSessions) {
            bfdSession = bfdSession + getBfdSession(sBfdCfgSession);
        }

        String bfdEnd = "        </bfdCfgSessions>\n" +
                "      </bfd>\n";

        String configEnd = "    </config>\n";

        String end =
                "  </edit-config>\n" +
                        "</rpc>";
        return head + target + configStart + bfdStart + bfdSession + bfdEnd + configEnd + end;
    }


    public static String deleteBfdCfgSessionsXml(List<String> sessNames) {

        String head = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <edit-config>\n" +
                "    <target>\n" +
                "      <candidate/>\n" +
                "    </target>\n" +
                "    <error-option>continue-on-error</error-option>\n" +
                "    <config>\n" +
                "      <bfd xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bfd\">\n" +
                "        <bfdCfgSessions> \n";
        String bfdSession = "";

        for (String sessName : sessNames) {
            bfdSession = bfdSession + getBfdSessionName(sessName);
        }

        String end =
                "        </bfdCfgSessions>\n" +
                        "      </bfd>\n" +
                        "    </config>\n" +
                        "  </edit-config>\n" +
                        "</rpc>\n";
        return head + bfdSession + end;
    }

    public static String getBfdCfgSessionsXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <bfd:bfd xmlns:bfd=\"http://www.huawei.com/netconf/vrp/huawei-bfd\">\n" +
                "        <bfd:bfdCfgSessions/>\n" +
                "      </bfd:bfd>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>\n";

        return xml;
    }

    public static String getBfdCfgSessionsXml(String database) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <" + database + "/>\n" +
                "    </source>\n" +
                "    <filter type=\"subtree\">\n" +
                "      <bfd:bfd xmlns:bfd=\"http://www.huawei.com/netconf/vrp/huawei-bfd\">\n" +
                "        <bfd:bfdCfgSessions/>\n" +
                "      </bfd:bfd>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>\n";
        return xml;
    }


    public static List<SBfdCfgSession> getBfdCfgSessionsFromXml(String xml) {
        List<SBfdCfgSession> ret = new ArrayList<SBfdCfgSession>();
        SBfdCfgSession sBfdCfgSession = null;

        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.elements().get(0).elements().get(0).elements().get(0).elements();
                for (org.dom4j.Element element : childElements) {
                    sBfdCfgSession = new SBfdCfgSession();
                    sBfdCfgSession.setSessName(element.elementText("sessName"));
                    sBfdCfgSession.setTunnelName(element.elementText("tunnelName"));
                    sBfdCfgSession.setLinkType(element.elementText("linkType"));
                    sBfdCfgSession.setCreateType(element.elementText("createType"));
                    sBfdCfgSession.setMultiplier(element.elementText("detectMulti"));
                    sBfdCfgSession.setMinRxInt(element.elementText("minRxInt"));
                    sBfdCfgSession.setMinTxInt(element.elementText("minTxInt"));
                    sBfdCfgSession.setLocalDiscr(element.elementText("localDiscr"));
                    sBfdCfgSession.setRemoteDiscr(element.elementText("remoteDiscr"));
                    ret.add(sBfdCfgSession);
                }

            } catch (Exception ex) {

            }
        }

        return ret;
    }

    private static String getBfdSessionName(String sessname) {
        String ret =
                "          <bfdCfgSession nc:operation=\"delete\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                        "            <sessName>" + sessname + "</sessName>\n" +
                        "          </bfdCfgSession>\n";
        return ret;
    }


    private static String getBfdSession(SBfdCfgSession sBfdCfgSession) {
        String ret =
                "        <bfdCfgSession>\n" +
                        "            <sessName >" + sBfdCfgSession.getSessName() + "</sessName >\n" +
                        "            <minTxInt >" + sBfdCfgSession.getMinTxInt() + "</minTxInt >\n" +
                        "            <minRxInt >" + sBfdCfgSession.getMinRxInt() + "</minRxInt >\n" +
                        "            <linkType >" + sBfdCfgSession.getLinkType() + "</linkType >\n" +
                        "            <detectMulti >" + sBfdCfgSession.getMultiplier() + "</detectMulti >\n" +
                        "            <tunnelName >" + sBfdCfgSession.getTunnelName() + "</tunnelName >\n" +
                        "            <createType >" + sBfdCfgSession.getCreateType() + "</createType >\n" +
                        "            <localDiscr >" + sBfdCfgSession.getLocalDiscr() + "</localDiscr >\n" +
                        "            <remoteDiscr >" + sBfdCfgSession.getRemoteDiscr() + "</remoteDiscr >\n" +
                        "        </bfdCfgSession >\n";
        return ret;
    }

}
