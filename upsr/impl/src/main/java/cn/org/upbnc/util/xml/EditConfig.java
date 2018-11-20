/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class EditConfig implements SRXml{
    private Document doc;

    public EditConfig(){
    }

    @Override
    public void initDocument(){
        this.doc = DocumentHelper.createDocument();
        Element root = this.doc.addElement("edit-config");

        // target
        Element target = root.addElement("target");
        target.addElement("candidate");

        // config
//        Element config = root.addElement("config");
//        Element system = config.addElement("system:system");
//        system.addAttribute("xmlns:system","http://www.huawei.com/netconf/vrp/huawei-system");
//        Element systemInfo = system.addElement("system:systemInfo");
//        Element sysName = systemInfo.addElement("system:sysName");
//        sysName.addText("SH_Router_PE1");
    }

    @Override
    public void initDocumentByXmlFile(String path){
        this.doc = DocumentHelper.createDocument();
        return;
    }
    @Override
    public void setParameter(Map<String, String> map) {

        return;
    }

    @Override
    public Document getXml() {
        return this.doc;
    }
}
