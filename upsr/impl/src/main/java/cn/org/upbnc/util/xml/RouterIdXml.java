package cn.org.upbnc.util.xml;

import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class RouterIdXml {
    private static final Logger LOG = LoggerFactory.getLogger(RouterIdXml.class);

    public static String getRouterIdXml() {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <ospfv2:ospfv2 xmlns:ospfv2=\"http://www.huawei.com/netconf/vrp/huawei-ospfv2\">\n" +
                "      <ospfv2:ospfv2comm>\n" +
                "        <ospfv2:ospfSites>\n" +
                "          <ospfv2:ospfSite>\n" +
                "            <ospfv2:routerId/>\n" +
                "          </ospfv2:ospfSite>\n" +
                "        </ospfv2:ospfSites>\n" +
                "      </ospfv2:ospfv2comm>\n" +
                "    </ospfv2:ospfv2>\n" +
                "  </filter>\n" +
                "</get>" +
                "</rpc>";
        return str;
    }

    public static String getRouterIdFromXml(String xml) {
        String routerId = "";
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                routerId = root.elements().get(0).elements().get(0).elements().get(0).elements().get(0).elements().get(0).elementText("routerId");
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return routerId;
    }

}
