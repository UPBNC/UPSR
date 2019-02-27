package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SBfdCfgSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BfdCfgSessionXml {
    private static final Logger LOG = LoggerFactory.getLogger(cn.org.upbnc.util.xml.BfdCfgSessionXml.class);

    public static String createBfdCfgSessionsXml(List<SBfdCfgSession> bfdCfgSessions) {
        String head = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <edit-config >\n";

        String target = "    <target>\n" +
                "      <running/>\n" +
                "    </target>\n";

        String configStart = "    <config>\n";

        String bfdStart = "      <bfd xmlns=\"http://www.huawei.com/netconf/vrp/huawei-bfd\">\n"+
                "        <bfdCfgSessions>\n";


        String bfdSession = "";

        for(SBfdCfgSession sBfdCfgSession: bfdCfgSessions){
            bfdSession = bfdSession + getBfdSession(sBfdCfgSession);
        }

        String bfdEnd = "        </bfdCfgSessions>\n" +
                "      </bfd>\n";

        String configEnd = "    </config>\n";

        String  end =
                "  </edit-config>\n" +
                        "</rpc>";
        return head + target + configStart + bfdStart + bfdSession + bfdEnd + configEnd +end;
    }

    private static String getBfdSession(SBfdCfgSession sBfdCfgSession) {
        String ret =
                "        <bfdCfgSession xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" nc:operation=\"create\">\n" +
                "            < sessName >"+ sBfdCfgSession.getSessName() +"</sessName >\n" +
                        "            <minTxInt >" + sBfdCfgSession.getMinTxInt() + "</minTxInt >\n" +
                        "            <minRxInt >" + sBfdCfgSession.getMinRxInt() + "</minRxInt >\n" +
                        "            <linkType >" + sBfdCfgSession.getLinkType() + "</linkType >\n" +
                        "            <tunnelName >" + sBfdCfgSession.getTunnelName() + "</tunnelName >\n" +
                        "            <createType >" + sBfdCfgSession.getCreateType() + "</createType >\n" +
                        "            <localDiscr >" + sBfdCfgSession.getLocalDiscr() + "</localDiscr >\n" +
                        "            <remoteDiscr >" + sBfdCfgSession.getRemoteDiscr() + "</remoteDiscr >\n" +
                        "        </bfdCfgSession >\n";
        return ret;
    }

}
