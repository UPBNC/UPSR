/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.enumtype;

public enum BgpTopoStatusEnum {
    INIT(0,"Init"),
    FINISH(1,"Finish"),
    UPDATING(2,"Updating"),
    UPDATED(3,"Updated");

    private int code;
    private String name;

    BgpTopoStatusEnum(int code , String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
