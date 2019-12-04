package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.SPingLspResultInfo;
import cn.org.upbnc.util.netconf.STraceLspHopInfo;
import cn.org.upbnc.util.netconf.STraceLspResultInfo;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

public class TunnelDetectXml {
    private static final Logger LOG = LoggerFactory.getLogger(TunnelDetectXml.class);
    public static final String LSPPATH_WORKING = "working-path";
    public static final String LSPPATH_HOT = "hot-standby";

    public static String startLspTraceXml(String tunnelName, String lspPath) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<dgntl:lsp-startLspTrace xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"  \n" +
                "                         xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">              \n" +
                "  <dgntl:testName>" + tunnelName + "</dgntl:testName>                                      \n" +
                "  <dgntl:tunnelName>" + tunnelName + "</dgntl:tunnelName>                                  \n" +
                "  <dgntl:lspType>srte</dgntl:lspType>                                                    \n" +
                "  <dgntl:lspPath>" + lspPath + "</dgntl:lspPath>                                          \n" +
                "</dgntl:lsp-startLspTrace>                                                               \n" +
                "</rpc>";
    }

    public static String stopLspTraceXml(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<dgntl:lsp-stopLspTrace xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"   \n" +
                "                        xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">               \n" +
                "  <dgntl:testName>" + tunnelName + "</dgntl:testName>                                      \n" +
                "</dgntl:lsp-stopLspTrace>                                                                \n" +
                "</rpc>";
    }

    public static String deleteLspTraceXml(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<lsp-deleteLspTrace xmlns=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"             \n" +
                "                    xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">      \n" +
                "  <testName>" + tunnelName + "</testName>                                                  \n" +
                "</lsp-deleteLspTrace>                                                                    \n" +
                "</rpc>";
    }

    public static String getLspTraceResult(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                \n" +
                "  <filter type=\"subtree\">                                                          \n" +
                "    <dgntl:dgntl xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">     \n" +
                "      <dgntl:lsp>                                                                    \n" +
                "        <dgntl:lspTraceResults>                                                      \n" +
                "          <dgntl:lspTraceResult>                                                     \n" +
                "            <testName>" + tunnelName + "</testName>                                    \n" +
                "          </dgntl:lspTraceResult>                                                    \n" +
                "        </dgntl:lspTraceResults>                                                     \n" +
                "      </dgntl:lsp>                                                                   \n" +
                "    </dgntl:dgntl>                                                                   \n" +
                "  </filter>                                                                          \n" +
                "</get>                                                                               \n" +
                "</rpc>";
    }

    public static String startLspPingXml(String tunnelName, String lspPath) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<lsp-startLspPing xmlns=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"            \n" +
                "                  xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">     \n" +
                "  <testName>" + tunnelName + "</testName>                                               \n" +
                "  <tunnelName>" + tunnelName + "</tunnelName>                                           \n" +
                "  <lspType>srte</lspType>                                                             \n" +
                "  <lspPath>" + lspPath + "</lspPath>                                                    \n" +
                "</lsp-startLspPing>                                                                   \n" +
                "</rpc>";
    }

    public static String stopLspPingXml(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<lsp-stopLspPing xmlns=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"             \n" +
                "                 xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">      \n" +
                "  <testName>" + tunnelName + "</testName>                                               \n" +
                "</lsp-stopLspPing>                                                                    \n" +
                "</rpc>";
    }

    public static String deleteLspPingXml(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<lsp-deleteLspPing xmlns=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\"           \n" +
                "                   xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">    \n" +
                "  <testName>" + tunnelName + "</testName>                                               \n" +
                "</lsp-deleteLspPing>                                                                  \n" +
                "</rpc>";
    }

    public static String getLspPingResult(String tunnelName) {
        return "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                               \n" +
                "  <filter type=\"subtree\">                                                         \n" +
                "    <dgntl:dgntl xmlns:dgntl=\"http://www.huawei.com/netconf/vrp/huawei-dgntl\">    \n" +
                "      <dgntl:lsp>                                                                   \n" +
                "        <dgntl:lspPingResults>                                                      \n" +
                "          <dgntl:lspPingResult>                                                     \n" +
                "            <testName>" + tunnelName + "</testName>                                   \n" +
                "          </dgntl:lspPingResult>                                                    \n" +
                "        </dgntl:lspPingResults>                                                     \n" +
                "      </dgntl:lsp>                                                                  \n" +
                "    </dgntl:dgntl>                                                                  \n" +
                "  </filter>                                                                         \n" +
                "</get>                                                                              \n" +
                "</rpc>";
    }
    public static SPingLspResultInfo pingLspResultFromResultXml(String xml){
        SPingLspResultInfo sPingLspResultInfo = new SPingLspResultInfo();
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            Element lspPingResultElement = root.element("data").element("dgntl").element("lsp").element("lspPingResults").element("lspPingResult");
            sPingLspResultInfo.setTunnelName(lspPingResultElement.elementText("tunnelName"));
            sPingLspResultInfo.setPacketSend(lspPingResultElement.elementText("packetSend"));
            sPingLspResultInfo.setPacketRecv(lspPingResultElement.elementText("packetRecv"));
            sPingLspResultInfo.setLossRatio(lspPingResultElement.elementText("lossRatio"));
            if (lspPingResultElement.element("pingResultDetails") != null) {
                List<Element> pingResultDetailElements = lspPingResultElement.element("pingResultDetails").elements("pingResultDetail");
                for (org.dom4j.Element pingResultDetail : pingResultDetailElements) {
                    sPingLspResultInfo.setRttValue(pingResultDetail.elementText("rtt"));
                    break;
                }
            }
        } catch (Exception e) {
                LOG.info(e.toString());
        }
        return  sPingLspResultInfo;
    }
    public static STraceLspResultInfo traceLspResultFromResultXml(String xml){
        STraceLspResultInfo sTraceLspResultInfo = new STraceLspResultInfo();
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            Element lspPingResultElement = root.element("data").element("dgntl").element("lsp").element("lspTraceResults").element("lspTraceResult");
            sTraceLspResultInfo.setTunnelName(lspPingResultElement.elementText("tunnelName"));
            sTraceLspResultInfo.setStatus(lspPingResultElement.elementText("status"));
            sTraceLspResultInfo.setErrorType(lspPingResultElement.elementText("errorType"));
            if (sTraceLspResultInfo.getErrorType().equals(STraceLspResultInfo.TRACE_SUCCESS)) {
                List<Element> traceResultDetail = lspPingResultElement.element("traceResultDetails").elements("traceResultDetail");
                for (org.dom4j.Element hopElement : traceResultDetail) {
                    STraceLspHopInfo sTraceLspHopInfo = new STraceLspHopInfo();
                    sTraceLspHopInfo.setHopIndex(hopElement.elementText("hopIndex"));
                    sTraceLspHopInfo.setDsIpAddr(hopElement.elementText("dsIpAddr"));
                    sTraceLspHopInfo.setDownStreamIpAddr(hopElement.elementText("downStreamIpAddr"));
                    sTraceLspHopInfo.setType(hopElement.elementText("type"));
                    sTraceLspResultInfo.addSTraceLspHopInfoList(sTraceLspHopInfo);
                }
            }
        } catch (Exception e) {
                LOG.info(e.toString());
        }
        return  sTraceLspResultInfo;
    }
}
