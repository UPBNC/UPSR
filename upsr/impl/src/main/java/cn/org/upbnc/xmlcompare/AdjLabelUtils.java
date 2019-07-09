package cn.org.upbnc.xmlcompare;

public class AdjLabelUtils {
    public static String running(){
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <staticIpv4Adjs>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.1.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.1.2</remoteIpAddress>\n" +
                "        <segmentId>322211</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.2.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.2.2</remoteIpAddress>\n" +
                "        <segmentId>322212</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>11.1.2.1</localIpAddress>\n" +
                "        <remoteIpAddress>11.1.2.2</remoteIpAddress>\n" +
                "        <segmentId>322101</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "    </staticIpv4Adjs>\n" +
                "  </segr>\n" +
                "</data>";
    }
    public static String add(){
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <staticIpv4Adjs>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.1.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.1.2</remoteIpAddress>\n" +
                "        <segmentId>322211</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.2.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.2.2</remoteIpAddress>\n" +
                "        <segmentId>322212</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>11.1.2.1</localIpAddress>\n" +
                "        <remoteIpAddress>11.1.2.2</remoteIpAddress>\n" +
                "        <segmentId>322101</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "\t        <staticIpv4Adj>\n" +
                "        <localIpAddress>11.1.2.3</localIpAddress>\n" +
                "        <remoteIpAddress>11.1.2.3</remoteIpAddress>\n" +
                "        <segmentId>322103</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "    </staticIpv4Adjs>\n" +
                "  </segr>\n" +
                "</data>";
    }
    public static String modify(){
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
                "  <segr xmlns=\"http://www.huawei.com/netconf/vrp/huawei-segr\">\n" +
                "    <staticIpv4Adjs>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.1.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.1.2</remoteIpAddress>\n" +
                "        <segmentId>322211</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>10.1.2.1</localIpAddress>\n" +
                "        <remoteIpAddress>10.1.2.2</remoteIpAddress>\n" +
                "        <segmentId>322212</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "      <staticIpv4Adj>\n" +
                "        <localIpAddress>11.1.2.4</localIpAddress>\n" +
                "        <remoteIpAddress>11.1.2.4</remoteIpAddress>\n" +
                "        <segmentId>322104</segmentId>\n" +
                "      </staticIpv4Adj>\n" +
                "    </staticIpv4Adjs>\n" +
                "  </segr>\n" +
                "</data>";
    }
}
