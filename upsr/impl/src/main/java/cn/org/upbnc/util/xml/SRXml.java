/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import org.dom4j.Document;

import java.util.Map;

public interface SRXml {

    void initDocument();

    void initDocumentByXmlFile(String path);
    //替换Xml对应的值
    void setParameter(Map<String,String> map);

    //获取Document
    Document getXml();
}
