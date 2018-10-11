/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.service;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.entity.Device;

public interface SrLabelService {
    // Set BaseInterface
    boolean setBaseInterface(BaseInterface baseInterface);
    String updateNodeLabel(String routerId, String labelVal);
    String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd);
    String syncNodeLabel(String routerId);
    String updateIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);
    String syncIntfLabel();
    String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);
    Device getDevice(String routerId);
}
