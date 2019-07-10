package cn.org.upbnc.xmlcompare;

public class SrSRGBUtils {
    public static String running() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <srSRGBs>\n" +
                "      <srSRGB>\n" +
                "        <lowerSid>321536</lowerSid>\n" +
                "        <upperSid>331775</upperSid>\n" +
                "        <total>10240</total>\n" +
                "      </srSRGB>\n" +
                "    </srSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }

    public static String add() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <srSRGBs>\n" +
                "      <srSRGB>\n" +
                "        <lowerSid>321536</lowerSid>\n" +
                "        <upperSid>331775</upperSid>\n" +
                "        <total>10240</total>\n" +
                "      </srSRGB>\n" +
                "      <srSRGB>\n" +
                "        <lowerSid>321538</lowerSid>\n" +
                "        <upperSid>331778</upperSid>\n" +
                "        <total>10248</total>\n" +
                "      </srSRGB>\n" +
                "    </srSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }

    public static String modify() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <srSRGBs>\n" +
                "      <srSRGB>\n" +
                "        <lowerSid>321534</lowerSid>\n" +
                "        <upperSid>331774</upperSid>\n" +
                "        <total>10244</total>\n" +
                "      </srSRGB>\n" +
                "    </srSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }
}
