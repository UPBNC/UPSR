package cn.org.upbnc.xmlcompare;

public class DynSrSRGBUtils {
    public static String running() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <dynSrSRGBs>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>190143</beginSid>\n" +
                "        <endSid>212657</endSid>\n" +
                "        <rangeCount>22515</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>212979</beginSid>\n" +
                "        <endSid>321535</endSid>\n" +
                "        <rangeCount>108557</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "    </dynSrSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }

    public static String add() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <dynSrSRGBs>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>190143</beginSid>\n" +
                "        <endSid>212657</endSid>\n" +
                "        <rangeCount>22515</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>212979</beginSid>\n" +
                "        <endSid>321535</endSid>\n" +
                "        <rangeCount>108557</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>212978</beginSid>\n" +
                "        <endSid>321538</endSid>\n" +
                "        <rangeCount>108558</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "    </dynSrSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }

    public static String modify() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <dynSrSRGBs>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>190143</beginSid>\n" +
                "        <endSid>212657</endSid>\n" +
                "        <rangeCount>22515</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "      <dynSrSRGB>\n" +
                "        <beginSid>212978</beginSid>\n" +
                "        <endSid>321538</endSid>\n" +
                "        <rangeCount>108558</rangeCount>\n" +
                "      </dynSrSRGB>\n" +
                "    </dynSrSRGBs>\n" +
                "  </segr>\n" +
                "</data>";
    }
}
