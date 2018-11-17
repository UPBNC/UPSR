/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.enumtype;

public enum  SystemStatusEnum {
    OFF(0,"系统关闭"),
    ON(1,"系统正常运转"),
    STARTING(2,"系统启动中"),
    CLOSING(3,"系统关闭中"),
    EXCPTION(4,"系统异常");

    private int code;
    private String name;

    SystemStatusEnum(int code ,String name){
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
