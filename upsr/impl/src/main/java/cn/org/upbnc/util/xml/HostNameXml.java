/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class HostNameXml {
    private static final Logger LOG = LoggerFactory.getLogger(HostNameXml.class);

    public static String getHostNameXml() {
        String str = "<rpc message-id =\"" + GetMessageId.getId() + "\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" >\n" +
                "<get>\n" +
                "  <filter type=\"subtree\">\n" +
                "    <sys:system xmlns:sys=\"urn:ietf:params:xml:ns:yang:ietf-system\">\n" +
                "      <sys:hostname/>\n" +
                "    </sys:system>\n" +
                "  </filter>\n" +
                "</get>" +
                "</rpc>";
        return str;
    }

    public static String getHostNameFromXml(String xml) {
        String hostName = "";
        if (!("".equals(xml))) {
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
                org.dom4j.Element root = document.getRootElement();
                hostName = root.elements().get(0).elements().get(0).elementText("hostname");
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
        return hostName;
    }
}
