/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.entity;

public class Tunnel {
    // Local
    private Integer ID;
    private Device device;
    private Integer tunnelID;
    private String description;
    private Address destIP;
    private Integer bandWidth;
    private ExplicitPath masterPath;
    private ExplicitPath slavePath;
    private boolean bfdEnable;
    private BFDSession bfdSession;

}
