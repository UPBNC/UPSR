/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.IniSectionManager;

import java.io.File;

public class IniSectionManagerImpl implements IniSectionManager{
    private File file = new File("./sr_conf.ini");
    public static IniSectionManager ourInstance = new IniSectionManagerImpl();

    private IniSectionManagerImpl() {

    }
    public static IniSectionManager getInstance() {
        if(null == ourInstance) {
            ourInstance = new IniSectionManagerImpl();
        }
        return  ourInstance;
    }

    @Override
    public String getValue(String section, String key) {
        return null;
    }

    @Override
    public Boolean setValue(String section, String key, String value) {
        return null;
    }

    @Override
    public String getValue(File file, String section, String key) {
        return null;
    }

    @Override
    public Boolean setValue(File file, String section, String key, String value) {
        return null;
    }
}
