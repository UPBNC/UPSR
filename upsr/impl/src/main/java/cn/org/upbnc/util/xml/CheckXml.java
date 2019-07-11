/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class CheckXml {
    public static final String RESULT_OK = "ok";

    public static String checkOk(String result) {
        String str = "";
        if (null != result) {
            if (result.contains(RESULT_OK)) {
                str = RESULT_OK;
            } else {
                str = result;
            }
        }
        return str;
    }
    public static int getErrorInfoCode(String xml) {
        int errorInfoCode = 0;
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            Element errorInfo = root.element("rpc-error").element("error-info");
            errorInfoCode = Integer.parseInt(errorInfo.elementText("error-info-code"));
        } catch (Exception e) {
        }
        return errorInfoCode;
    }

    public static String getErrorMessage(String xml) {
        String ret = "";
        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            String errorMessage = root.element("rpc-error").elementText("error-message");
            ret = ret + "common error";
        } catch (Exception e) {
        }
        return ret;
    }
}
