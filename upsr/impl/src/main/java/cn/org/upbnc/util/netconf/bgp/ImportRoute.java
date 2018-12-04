/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.util.netconf.bgp;

public class ImportRoute {
    private String importProtocol;
    private String importProcessId;

    public ImportRoute(String importProtocol, String importProcessId){
        this.importProcessId=importProcessId;
        this.importProtocol=importProtocol;
    }

    public String getImportProcessId() {
        return importProcessId;
    }

    public String getImportProtocol() {
        return importProtocol;
    }

    public void setImportProcessId(String importProcessId) {
        this.importProcessId = importProcessId;
    }

    public void setImportProtocol(String importProtocol) {
        this.importProtocol = importProtocol;
    }
}
