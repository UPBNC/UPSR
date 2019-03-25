/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import cn.org.upbnc.entity.NetConfMessage;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

public class CheckXml {
    public static final String RESULT_OK = "ok";
    private static final String XML_RPC_ERROR = "rpc-error";
    private static final String XML_ERROR_TYPE = "error-type";
    private static final String XML_ERROR_TAG = "error-tag";
    private static final String XML_ERROR_MSG = "error-message";
    private static final String XML_ERROR_PATH = "error-path";
    private static final String XML_ERROR_INFO = "error-info";
    private static final String XML_ERROR_INFO_CODE = "nc-ext:error-info-code";
    private static final String XML_ERROR_INFO_PARAS = "nc-ext:error-paras";
    private static final String XML_ERROR_INFO_PARA = "nc-ext:error-para";

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
            ret = ret + errorMessage;
        } catch (Exception e) {
        }
        return ret;
    }

    public static NetConfMessage getNetConfMessage(String xml){
        NetConfMessage ret = new NetConfMessage();
        if (xml.contains(RESULT_OK)) {
            ret.setOK(true);
            return ret;
        }else{
            ret.setOK(false);
        }

        try {
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new InputSource(new StringReader(xml)));
            Element root = document.getRootElement();
            Element error = root.element(XML_RPC_ERROR);

            ret.setPath(root.elementText(XML_ERROR_PATH));
            ret.setMessage(error.elementText(XML_ERROR_MSG));
            ret.setTag(root.elementText(XML_ERROR_TAG));
            ret.setType(root.elementText(XML_ERROR_TYPE));

            Element info = root.element(XML_ERROR_INFO);

            ret.setCode(info.elementText(XML_ERROR_INFO_CODE));
            Element paras = info.element(XML_ERROR_INFO_PARAS);
            if(paras != null ) {
                List<Element> list = paras.elements();
                for(Element e : list){
                    ret.addPara(e.getText());
                }
            }

        } catch (Exception e) {
        }
        return ret;
    }
}
