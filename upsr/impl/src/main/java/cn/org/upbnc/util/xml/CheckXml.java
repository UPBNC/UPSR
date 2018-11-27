/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.xml;

public class CheckXml {
    public static String checkOk(String result) {
        String str;
        if (result.contains("ok")) {
            str = "ok";
        } else {
            str = result;
        }
        return str;
    }
}
