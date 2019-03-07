/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;


import cn.org.upbnc.api.VpnInstanceApi;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.service.ServiceInterface;
import cn.org.upbnc.service.VPNService;
import cn.org.upbnc.service.entity.UpdateVpnInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VpnInstanceApiImpl implements VpnInstanceApi {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceApiImpl.class);
    private static VpnInstanceApi ourInstance = new VpnInstanceApiImpl();
    private ServiceInterface serviceInterface;
    private VPNService vpnService;

    public static VpnInstanceApi getInstance() {
        return ourInstance;
    }

    private VpnInstanceApiImpl() {
        this.serviceInterface = null;
    }

    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (null != serviceInterface) {
            vpnService = this.serviceInterface.getVpnService();
        }
        return true;
    }

    public Map<String, Object> updateVpnInstance(UpdateVpnInstance updateVpnInstance) {
        String vpnName = updateVpnInstance.getVpnName();
        String routerId = updateVpnInstance.getRouterId();
        String rd = updateVpnInstance.getRd();
        List<DeviceInterface> deviceInterfaceList = updateVpnInstance.getDeviceInterfaceList();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.BODY.getName(), false);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());

        if (null == this.vpnService) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnService is null.");
            return resultMap;
        }
        if ((null == routerId) || (routerId.isEmpty()) || (null == vpnName) || (vpnName.isEmpty())
                || (null == rd) || (rd.isEmpty())) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "routerId , vpnName or rd is null.");
            return resultMap;
        }
        if (null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return resultMap;
        }
        for (DeviceInterface deviceInterface : deviceInterfaceList) {
            if ((null == deviceInterface.getIp()) && (null != deviceInterface.getMask())) {
                resultMap.put(ResponseEnum.MESSAGE.getName(), "ip is null,but mask is not.");
                return resultMap;
            }
            if ((null == deviceInterface.getMask()) && (null != deviceInterface.getIp())) {
                resultMap.put(ResponseEnum.MESSAGE.getName(), "mask is null,but ip is not.");
                return resultMap;
            }
        }
        return this.vpnService.updateVpnInstance(updateVpnInstance);


    }

    public boolean delVpnInstance(Integer id) {
        return (null == this.vpnService) ? false : this.vpnService.delVpnInstance(id);
    }

    public Map<String, Object> delVpnInstance(String routerId, String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseEnum.BODY.getName(), false);
        resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if ((null == vpnName) || (vpnName.isEmpty())) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnName is null.");
            return resultMap;
        }
        if (null == this.vpnService) {
            return resultMap;
        }
        return this.vpnService.delVpnInstance(routerId, vpnName);
    }

    public Map<String, Object> getVpnInstance(String routerId, String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        if ((null == routerId) || routerId.isEmpty() || (null == vpnName) || vpnName.isEmpty()) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "routerId or vpnName is null.");
            return resultMap;
        }

        if (null == serviceInterface.getNetconfSessionService().getNetconfClient(routerId)) {
            resultMap.put(ResponseEnum.MESSAGE.getName(), "netconfClient is null");
            return resultMap;
        }
        return (null == this.vpnService) ? null : this.vpnService.getVpnInstance(routerId, vpnName);
    }

    public Map<String, Object> getVpnInstanceList(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == vpnName) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnName is null.");
            return resultMap;
        }

        return (null == this.vpnService) ? null : this.vpnService.getVpnInstanceList(vpnName);
    }

    public Map<String, Object> getVpnInstanceMap(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == vpnName) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), null);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnName is null.");
            return resultMap;
        }
        return (null == this.vpnService) ? null : this.vpnService.getVpnInstanceMap(vpnName);
    }

    public String getTest() {
        return null;
    }

    public Map<String, Object> isContainVpnName(String vpnName) {
        Map<String, Object> resultMap = new HashMap<>();
        if (vpnName.equals("")) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), false);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "vpnName is \"\".");
            return resultMap;
        }
        return (null == this.vpnService) ? null : this.vpnService.isContainVpnName(vpnName);
    }

    public Map<String, Object> isContainRd(String routerId, String rd) {
        Map<String, Object> resultMap = new HashMap<>();
        if (routerId.equals("") || rd.equals("")) {
            resultMap.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
            resultMap.put(ResponseEnum.BODY.getName(), false);
            resultMap.put(ResponseEnum.MESSAGE.getName(), "routerId or rd is \"\".");
            return resultMap;
        }
        return (null == this.vpnService) ? null : this.vpnService.isContainRd(routerId, rd);
    }

    public Map<String, Object> createTunnelsByVpnTemplate(String vpnName) {
        return (null == this.vpnService) ? null : this.vpnService.createTunnelsByVpnTemplate(vpnName);
    }
}
