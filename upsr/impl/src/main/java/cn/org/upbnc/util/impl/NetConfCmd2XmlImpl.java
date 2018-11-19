/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.impl;

import cn.org.upbnc.enumtype.ManufactureEnum;
import cn.org.upbnc.util.NetConfCmd2Xml;
import cn.org.upbnc.util.xml.EditConfig;
import cn.org.upbnc.util.xml.SRXml;

import java.util.HashMap;
import java.util.Map;

public class NetConfCmd2XmlImpl implements NetConfCmd2Xml {
    private static NetConfCmd2Xml instance = new NetConfCmd2XmlImpl();
    public static NetConfCmd2Xml getInstance() {
        return instance;
    }

    private String path = ""; //可以考虑读配置文件
    private Map<String,SRXml> hwCmdMap;
    private Map<String,SRXml> ciscoCmdMap;

    private NetConfCmd2XmlImpl() {
        this.hwCmdMap = new HashMap<String,SRXml>();
        this.ciscoCmdMap = new HashMap<String,SRXml>();
    }

    // Read all XML File in path
    @Override
    public void initXml(){
        SRXml editConfig = new EditConfig();
        this.hwCmdMap.put("EditConfig",editConfig);
    }

    // Get XML object by Manufacture
    @Override
    public SRXml getXMLByManufacture(ManufactureEnum manufacture,String cmd){
        SRXml ret = null;
        if(ManufactureEnum.HW == manufacture){
            ret = this.hwCmdMap.get(cmd);
        }else if(ManufactureEnum.CISCO == manufacture){
            ret = this.ciscoCmdMap.get(cmd);
        }else{
            ///
        }

        //如果为null，再去文件中找找看
        if(null == ret){
            ret = null; //这是为了编译通过
        }
        return ret;
    }

    //可以删除
    public String getPath() {
        return path;
    }
}
