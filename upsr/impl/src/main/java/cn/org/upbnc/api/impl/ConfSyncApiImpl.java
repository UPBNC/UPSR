/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.api.impl;

import cn.org.upbnc.api.ConfSyncApi;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfSyncApiImpl implements ConfSyncApi {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfSessionApiImpl.class);
    private static ConfSyncApi ourInstance = new ConfSyncApiImpl();
    private ServiceInterface serviceInterface;

    public static ConfSyncApi getInstance() {
        return ourInstance;
    }

    @Override
    public boolean setServiceInterface(ServiceInterface serviceInterface) {
        if (null == serviceInterface) {
            return false;
        }
        this.serviceInterface = serviceInterface;
        return true;
    }

    @Override
    public String syncDeviceConf() {
        String ret = null;
        String result = null;
        result = "sync device configure start......";
        ret = this.serviceInterface.getInterfaceService().syncInterfaceConf() ? " success" : "failed";
        result += ret;
        result += "\n";
        result += "sync vpnInstance configure....";
        ret = this.serviceInterface.getVpnService().syncVpnInstanceConf() ? " success" : "failed";
        result += ret;
        result += "\n";
        result += this.serviceInterface.getSrLabelService().syncAllIntfLabel();
        result += this.serviceInterface.getSrLabelService().syncAllNodeLabel();
        result += this.serviceInterface.getTunnelService().syncTunnelInstanceConf();
        result += this.serviceInterface.getRoutePolicyService().syncRoutePolicyConf();
        result += this.serviceInterface.getTunnelPolicyService().syncTunnelPolicyConf();
        result += "\n";
        result += "sync device configure end.";
        LOG.info("sync ret : " + result);
        return CodeEnum.SUCCESS.getMessage();
    }
}
