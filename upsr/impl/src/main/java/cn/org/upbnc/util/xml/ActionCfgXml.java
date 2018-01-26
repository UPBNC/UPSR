package cn.org.upbnc.util.xml;

import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCfgXml {
    private static final Logger LOG = LoggerFactory.getLogger(ActionCfgXml.class);
    public static String getCommitCfgXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                + GetMessageId.getId() + "\">\n" +
                "    <commit/>\n" +
                "</rpc>";
        return xml;
    }

    public static String getCancelCfgXml() {
        String xml = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\""
                + GetMessageId.getId() + "\">\n" +
                "    <discard-changes/>\n" +
                "</rpc>";
        return xml;
    }
}
