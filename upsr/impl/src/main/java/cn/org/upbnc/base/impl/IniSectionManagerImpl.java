/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.IniSectionManager;
import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;

public class IniSectionManagerImpl implements IniSectionManager{
    private File file = new File("./sr_conf.ini");
    public static IniSectionManager ourInstance = new IniSectionManagerImpl();

    private Ini ini = new Ini();

    private IniSectionManagerImpl() {
        ini.setFile(file);
    }
    public static IniSectionManager getInstance() {
        if(null == ourInstance) {
            ourInstance = new IniSectionManagerImpl();
        }
        return  ourInstance;
    }

    @Override
    public String getValue(String section, String key, final String defaultvalue) {
        if((null == section)||(null == key)) {
            return null;
        }
        if(null != ini) {
            Profile.Section sectionCfg = ini.get(section);
            if( null == sectionCfg) {return null;}
            final  String value = sectionCfg.get(key);
            if (StringUtils.isBlank(value)) {    // if bank ,return default value
                return defaultvalue;
            }
            return value;
        }
        return null;
    }

    @Override
    public Boolean setValue(String section, String key, String value) {
        if((null == section)||(null == key)) {
            return false;
        }
        if(null != ini) {
            Profile.Section sectionCfg = ini.get(section);
            if(null == sectionCfg) {
                ini.add(section, key, value);
                return true;
            }
            sectionCfg.replace(key, value);
            return true;
        }
        return false;
    }

    @Override
    public String getValue(File file, String section, String key, final String defaultvalue) {
        if(null == file) {
            return null;
        }
        Ini tmpini = new Ini();
        tmpini.setFile(file);
        if(null != tmpini) {
            Profile.Section sectionCfg = tmpini.get(section);
            if( null == sectionCfg) {return null;}
            final  String value = sectionCfg.get(key);
            if (StringUtils.isBlank(value)) {    // if bank ,return default value
                return defaultvalue;
            }
            return value;
        }
        return null;
    }

    @Override
    public Boolean setValue(File file, String section, String key, String value) {
        if(null == file) {
            return false;
        }
        Ini tmpini = new Ini();
        tmpini.setFile(file);
        if(null != ini) {
            Profile.Section sectionCfg = ini.get(section);
            if(null == sectionCfg) {
                ini.add(section, key, value);
                return true;
            }
            sectionCfg.replace(key, value);
            return true;
        }
        return false;
    }
}
