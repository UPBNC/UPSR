/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;

import java.util.Map;

public class ExplicitPath {
    // Local
    private Integer id;
    private Device device;
    private String pathName;
    private Map<String, Label> labelList;

    public ExplicitPath() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Map<String, Label> getLabelList() {
        return labelList;
    }

    public void setLabelList(Map<String, Label> labelList) {
        this.labelList = labelList;
    }
}
