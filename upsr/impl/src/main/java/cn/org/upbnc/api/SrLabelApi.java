/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;

import cn.org.upbnc.entity.Device;
import cn.org.upbnc.service.ServiceInterface;

public interface SrLabelApi {
    boolean setServiceInterface(ServiceInterface serviceInterface);

    String updateNodeLabel(String routerId, String labelBegin, String labelValAbs, String action);

    String updateNodeLabelRange(String routerId, String labelBegin, String labelEnd, String action);

    String updateIntfLabel(String routerId, String ifAddress, String labelVal, String action);

    String delIntfLabel(String routerId, String localAddress, String remoteAddress, String labelVal);

    Device getDevice(String routerId);
}
