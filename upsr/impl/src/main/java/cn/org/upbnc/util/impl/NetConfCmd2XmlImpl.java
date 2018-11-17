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

import java.util.HashMap;
import java.util.Map;

public class NetConfCmd2XmlImpl implements NetConfCmd2Xml {

    private final String path = ""; //可以考虑读配置文件
    private Map<String,Object> hwCmdMap;
    private Map<String,Object> ciscoCmdMap;

    private NetConfCmd2XmlImpl(){
        this.hwCmdMap = new HashMap<String,Object>();
        this.ciscoCmdMap = new HashMap<String,Object>();
    }

    // Read all XML File in path
    public void init(){
    }

    // Get XML object by Manufacture
    @Override
    public Object getXMLByManufacture(ManufactureEnum manufacture,String cmd){
        Object ret = null;
        if(ManufactureEnum.HW == manufacture){
            ret = this.hwCmdMap.get(cmd);
        }else if(ManufactureEnum.CISCO == manufacture){
            ret = this.ciscoCmdMap.get(cmd);
        }else{
            ///
        }

        //
        if(null == ret){

        }
        return ret;
    }

}
