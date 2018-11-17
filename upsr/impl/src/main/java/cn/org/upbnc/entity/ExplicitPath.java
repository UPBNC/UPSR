/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.entity;
import java.util.ArrayList;
import java.util.List;
public class ExplicitPath {
    // Local
    private Integer id;
    private Device device;

    private String pathName;
    private List<Label> labelList;

    public ExplicitPath(){
        this.id = 0;
        this.device = null;
        this.pathName = null;
        this.labelList = new ArrayList<Label>();
    }


    public ExplicitPath(Integer id, Device device, String pathName, List<Label> labelList) {
        this.id = id;
        this.device = device;
        this.pathName = pathName;
        // Important
        this.labelList = new ArrayList<Label>();
        this.labelList.addAll(labelList);
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

    public List<Label> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<Label> labelList) {
        this.labelList = labelList;
    }
}
