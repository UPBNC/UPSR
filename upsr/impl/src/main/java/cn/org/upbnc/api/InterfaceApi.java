/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api;


import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.entity.DevInterfaceInfo;

import java.util.List;

public interface InterfaceApi {
    // Set ServiceInterface
    boolean setServiceInterface(ServiceInterface serviceInterface);
    List<DevInterfaceInfo> getDeviceInterfaceList(String routerId);
}
