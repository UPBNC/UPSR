package cn.org.upbnc.util.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunningXml {
    private static final Logger LOG = LoggerFactory.getLogger(ExplicitPathXml.class);

    public static String getRunningXml() {
        return null;
    }
    public static String getRunningTunnelXml() {
        return null;
    }
    public static String getRunningSrLabelXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"" + GetMessageId.getId() + "\">\n" +
                "  <get-config>\n" +
                "    <source>\n" +
                "      <running/>\n" +
                "    </source>\n" +
                "    <filter>\n" +
                "      <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "        <ospfv2:ospfv2comm>\n" +
                "          <ospfv2:ospfSites>\n" +
                "            <ospfv2:ospfSite>\n" +
                "              <ospfv2:areas>\n" +
                "                <ospfv2:area>\n" +
                "                  <ospfv2:interfaces>\n" +
                "                    <ospfv2:interface>\n" +
                "                      <ospfv2:srInterface>\n" +
                "                        <ospfv2:prefixSidType/>\n" +
                "                        <ospfv2:prefixLabel/>\n" +
                "                      </ospfv2:srInterface>\n" +
                "                    </ospfv2:interface>\n" +
                "                  </ospfv2:interfaces>\n" +
                "                </ospfv2:area>\n" +
                "              </ospfv2:areas>\n" +
                "              <ospfv2:ospfSrgbs>\n" +
                "                <ospfv2:ospfSrgb/>\n" +
                "              </ospfv2:ospfSrgbs>\n" +
                "            </ospfv2:ospfSite>\n" +
                "          </ospfv2:ospfSites>\n" +
                "        </ospfv2:ospfv2comm>\n" +
                "      </ospfv2:ospfv2>\n" +
                "      <segr:segr xmlns:segr=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "        <segr:staticIpv4Adjs/>\n" +
                "      </segr:segr>\n" +
                "    </filter>\n" +
                "  </get-config>\n" +
                "</rpc>";
        return xml;
    }

}
