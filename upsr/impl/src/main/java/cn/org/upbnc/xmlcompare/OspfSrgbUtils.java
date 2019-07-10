package cn.org.upbnc.xmlcompare;

public class OspfSrgbUtils {
    public static String running() {
        return "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "    <ospfv2comm>\n" +
                "      <ospfSites>\n" +
                "        <ospfSite>\n" +
                "          <processId>65100</processId>\n" +
                "          <ospfSrgbs>\n" +
                "            <ospfSrgb>\n" +
                "              <srgbBegin>230000</srgbBegin>\n" +
                "              <srgbEnd>240000</srgbEnd>\n" +
                "            </ospfSrgb>\n" +
                "          </ospfSrgbs>\n" +
                "        </ospfSite>\n" +
                "      </ospfSites>\n" +
                "    </ospfv2comm>\n" +
                "  </ospfv2>\n" +
                "</data>";
    }

    public static String add() {
        return "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "    <ospfv2comm>\n" +
                "      <ospfSites>\n" +
                "        <ospfSite>\n" +
                "          <processId>65100</processId>\n" +
                "          <ospfSrgbs>\n" +
                "            <ospfSrgb>\n" +
                "              <srgbBegin>230000</srgbBegin>\n" +
                "              <srgbEnd>240000</srgbEnd>\n" +
                "            </ospfSrgb>\n" +
                "            <ospfSrgb>\n" +
                "              <srgbBegin>230001</srgbBegin>\n" +
                "              <srgbEnd>240001</srgbEnd>\n" +
                "            </ospfSrgb>\n" +
                "          </ospfSrgbs>\n" +
                "        </ospfSite>\n" +
                "      </ospfSites>\n" +
                "    </ospfv2comm>\n" +
                "  </ospfv2>\n" +
                "</data>";
    }

    public static String modify() {
        return "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <ospfv2 xmlns=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "    <ospfv2comm>\n" +
                "      <ospfSites>\n" +
                "        <ospfSite>\n" +
                "          <processId>65100</processId>\n" +
                "          <ospfSrgbs>\n" +
                "            <ospfSrgb>\n" +
                "              <srgbBegin>230006</srgbBegin>\n" +
                "              <srgbEnd>240006</srgbEnd>\n" +
                "            </ospfSrgb>\n" +
                "          </ospfSrgbs>\n" +
                "        </ospfSite>\n" +
                "      </ospfSites>\n" +
                "    </ospfv2comm>\n" +
                "  </ospfv2>\n" +
                "</data>";
    }
}
