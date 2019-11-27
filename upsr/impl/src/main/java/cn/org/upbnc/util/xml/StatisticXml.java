package cn.org.upbnc.util.xml;

import cn.org.upbnc.util.netconf.statistics.SIfClearedStat;
import cn.org.upbnc.util.netconf.statistics.SIfStatistics;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StatisticXml {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticXml.class);

    public static String getIfClearedStatXml(String ifName) {
        String start = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">\n" +
                "      <ifm:interfaces>\n" +
                "        <ifm:interface>\n";
        String middle = "";
        if (null != ifName && !("").equals(ifName)) {
            middle = middle + "          <ifm:ifName></ifm:ifName>\n";
        }
        String end =
                "          <ifm:ifClearedStat/>\n" +
                        "        </ifm:interface>\n" +
                        "      </ifm:interfaces>\n" +
                        "    </ifm:ifm>\n" +
                        "  </filter>\n" +
                        "</get>" +
                        "</rpc>";
        return start + middle + end;
    }

    public static List<SIfClearedStat> getIfClearedStatFromXml(String xml) {
        List<SIfClearedStat> sIfClearedStats = new ArrayList<>();
        SIfClearedStat sIfClearedStat;
        if (!("").equals(xml)) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.element("data").element("ifm").element("interfaces").elements("interface");
                for (org.dom4j.Element child : childElements) {
                    sIfClearedStat = new SIfClearedStat();
                    sIfClearedStat.setIfName(child.elementText("ifName"));
                    sIfClearedStat.setIfIndex(child.elementText("ifIndex"));
                    org.dom4j.Element child1 = child.element("ifClearedStat");
                    sIfClearedStat.setIfOperStatItvl(child1.elementText("ifOperStatItvl"));
                    sIfClearedStat.setInByteRate(child1.elementText("inByteRate"));
                    sIfClearedStat.setInPacketRate(child1.elementText("inPacketRate"));
                    sIfClearedStat.setInUseRate(child1.elementText("inUseRate"));
                    sIfClearedStat.setOutByteRate(child1.elementText("outByteRate"));
                    sIfClearedStat.setOutPacketRate(child1.elementText("outPacketRate"));
                    sIfClearedStat.setOutUseRate(child1.elementText("outUseRate"));
                    sIfClearedStat.setReceiveByte(child1.elementText("receiveByte"));
                    sIfClearedStat.setSendByte(child1.elementText("sendByte"));
                    sIfClearedStat.setReceivePacket(child1.elementText("receivePacket"));
                    sIfClearedStat.setSendPacket(child1.elementText("sendPacket"));
                    sIfClearedStat.setRcvUniPacket(child1.elementText("rcvUniPacket"));
                    sIfClearedStat.setRcvMutiPacket(child1.elementText("rcvMutiPacket"));
                    sIfClearedStat.setRcvBroadPacket(child1.elementText("rcvBroadPacket"));
                    sIfClearedStat.setSendUniPacket(child1.elementText("sendUniPacket"));
                    sIfClearedStat.setSendMutiPacket(child1.elementText("sendMutiPacket"));
                    sIfClearedStat.setSendBroadPacket(child1.elementText("sendBroadPacket"));
                    sIfClearedStat.setRcvErrorPacket(child1.elementText("rcvErrorPacket"));
                    sIfClearedStat.setRcvDropPacket(child1.elementText("rcvDropPacket"));
                    sIfClearedStat.setSendErrorPacket(child1.elementText("sendErrorPacket"));
                    sIfClearedStat.setSendDropPacket(child1.elementText("sendDropPacket"));
                    sIfClearedStat.setSendUniBit(child1.elementText("sendUniBit"));
                    sIfClearedStat.setRcvUniBit(child1.elementText("rcvUniBit"));
                    sIfClearedStat.setSendMutiBit(child1.elementText("sendMutiBit"));
                    sIfClearedStat.setRcvMutiBit(child1.elementText("rcvMutiBit"));
                    sIfClearedStat.setSendBroadBit(child1.elementText("sendBroadBit"));
                    sIfClearedStat.setRcvBroadBit(child1.elementText("rcvBroadBit"));
                    sIfClearedStat.setSendUniBitRate(child1.elementText("sendUniBitRate"));
                    sIfClearedStat.setRcvUniBitRate(child1.elementText("rcvUniBitRate"));
                    sIfClearedStat.setSendMutiBitRate(child1.elementText("sendMutiBitRate"));
                    sIfClearedStat.setRcvMutiBitRate(child1.elementText("rcvMutiBitRate"));
                    sIfClearedStat.setSendBroadBitRate(child1.elementText("sendBroadBitRate"));
                    sIfClearedStat.setRcvBroadBitRate(child1.elementText("rcvBroadBitRate"));
                    sIfClearedStat.setSendUniPacketRate(child1.elementText("sendUniPacketRate"));
                    sIfClearedStat.setRcvUniPacketRate(child1.elementText("rcvUniPacketRate"));
                    sIfClearedStat.setSendMutiPacketRate(child1.elementText("sendMutiPacketRate"));
                    sIfClearedStat.setRcvMutiPacketRate(child1.elementText("rcvMutiPacketRate"));
                    sIfClearedStat.setSendBroadPacketRate(child1.elementText("sendBroadPacketRate"));
                    sIfClearedStat.setRcvBroadPacketRate(child1.elementText("rcvBroadPacketRate"));
                    sIfClearedStats.add(sIfClearedStat);
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return sIfClearedStats;
    }

    public static String getIfStatisticsXml(String ifName) {
        return  "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                      \n" +
                "  <filter type=\"subtree\">                                                  \n" +
                "    <ifm:ifm xmlns:ifm=\"http://www.huawei.com/netconf/vrp/huawei-ifm\">     \n" +
                "      <ifm:interfaces>                                                     \n" +
                "        <ifm:interface>                                                    \n" +
                "          <ifm:ifStatistics>                                               \n" +
                "            <receiveByte/>                                                 \n" +
                "            <sendByte/>                                                    \n" +
                "            <receivePacket/>                                               \n" +
                "            <sendPacket/>                                                  \n" +
                "            <rcvUniPacket/>                                                \n" +
                "            <rcvMutiPacket/>                                               \n" +
                "            <rcvBroadPacket/>                                              \n" +
                "            <sendUniPacket/>                                               \n" +
                "            <sendMutiPacket/>                                              \n" +
                "            <sendBroadPacket/>                                             \n" +
                "            <rcvErrorPacket/>                                              \n" +
                "            <rcvDropPacket/>                                               \n" +
                "            <sendErrorPacket/>                                             \n" +
                "            <sendDropPacket/>                                              \n" +
                "          </ifm:ifStatistics>                                              \n" +
                "        </ifm:interface>                                                   \n" +
                "      </ifm:interfaces>                                                    \n" +
                "    </ifm:ifm>                                                             \n" +
                "  </filter>                                                                \n" +
                "</get>                                                                     \n" +
                "</rpc>";
    }
    public static List<SIfStatistics> getIfStatisticsFromXml(String xml) {
        List<SIfStatistics> sIfStatisticsList = new ArrayList<>();

        if (!("").equals(xml)) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                List<org.dom4j.Element> childElements = root.element("data").element("ifm").element("interfaces").elements("interface");
                for (org.dom4j.Element child : childElements) {
                    SIfStatistics sIfStatistics = new SIfStatistics();
                    sIfStatistics.setIfName(child.elementText("ifName"));
                    sIfStatistics.setIfIndex(child.elementText("ifIndex"));
                    org.dom4j.Element child1 = child.element("ifStatistics");
                    sIfStatistics.setReceiveByte(child1.elementText("receiveByte"));
                    sIfStatistics.setSendByte(child1.elementText("sendByte"));
                    sIfStatistics.setReceivePacket(child1.elementText("receivePacket"));
                    sIfStatistics.setSendPacket(child1.elementText("sendPacket"));
                    sIfStatistics.setRcvUniPacket(child1.elementText("rcvUniPacket"));
                    sIfStatistics.setRcvMutiPacket(child1.elementText("rcvMutiPacket"));
                    sIfStatistics.setRcvBroadPacket(child1.elementText("rcvBroadPacket"));
                    sIfStatistics.setSendUniPacket(child1.elementText("sendUniPacket"));
                    sIfStatistics.setSendMutiPacket(child1.elementText("sendMutiPacket"));
                    sIfStatistics.setSendBroadPacket(child1.elementText("sendBroadPacket"));
                    sIfStatistics.setRcvErrorPacket(child1.elementText("rcvErrorPacket"));
                    sIfStatistics.setRcvDropPacket(child1.elementText("rcvDropPacket"));
                    sIfStatistics.setSendErrorPacket(child1.elementText("sendErrorPacket"));
                    sIfStatistics.setSendDropPacket(child1.elementText("sendDropPacket"));
                }
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return sIfStatisticsList;
    }
    public static String getCpuInfoXml() {
        return  "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                   \n" +
                "  <filter type=\"subtree\">                                                               \n" +
                "    <devm:devm xmlns:devm=\"http://www.huawei.com/netconf/vrp/huawei-devm\">              \n" +
                "      <devm:cpuInfos>                                                                   \n" +
                "        <devm:cpuInfo/>                                                                 \n" +
                "      </devm:cpuInfos>                                                                  \n" +
                "    </devm:devm>                                                                        \n" +
                "  </filter>                                                                             \n" +
                "</get>                                                                                  \n" +
                "</rpc>";
    }

    public static String getMemoryInfoXml() {
        return  "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>                                                                                     \n" +
                "  <filter type=\"subtree\">                                                                 \n" +
                "    <devm:devm xmlns:devm=\"http://www.huawei.com/netconf/vrp/huawei-devm\">                \n" +
                "      <devm:memoryInfos>                                                                  \n" +
                "        <devm:memoryInfo/>                                                                \n" +
                "      </devm:memoryInfos>                                                                 \n" +
                "    </devm:devm>                                                                          \n" +
                "  </filter>                                                                               \n" +
                "</get>                                                                                    \n" +
                "</rpc>";
    }
}
