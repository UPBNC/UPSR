/*
 * Copyright © 2018 Copyright (c) 2018 BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base;

import cn.org.upbnc.entity.BgpLink;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.entity.Link;

import java.util.List;

public interface LinkManager {


    boolean addLink(Link link);

    void setLinkList(List<Link> linkList);

    // Get Link
    Link getLink(Link link);
//    Link getLinkByDevices(Device device1,Device device2);
//    Link getLinkByDevicesName(String deviceName1,String deviceName2);
    Link getLinkByInterfaces(DeviceInterface deviceInterface1,DeviceInterface deviceInterface2);
    List<Link> getLinkList();


    // Update Link
    List<Link> updateLinkListByBgpLinkList(List<Device> devices,List<BgpLink> bgpLinkList);
}
