/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.impl;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.odlapi.*;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrbgplssession.rev181120.UpsrBgplsSessionService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrinterface.rev181119.UpsrInterfaceService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpsrNetconfSessionService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.UpsrSrLabelService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsyncconf.rev181129.UpsrSyncConfService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.UpsrTopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.UpsrTunnelService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.UpsrVpnInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpsrProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    private final Session upsr = Session.getSession();
    private final RpcProviderRegistry rpcRegistry;
    private final DataBroker dataBroker;
    private NetconfSessionODLApi netconfSessionODLApi;

    //ODL REST Service RpcRegistration start;
    private BindingAwareBroker.RpcRegistration<UpsrTopoService> topoInfoServiceReg;
    private BindingAwareBroker.RpcRegistration<UpsrBgplsSessionService> upsrBgplsSessionServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrVpnInstanceService> vpnInstanceServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrNetconfSessionService> upsrNetconfSessionServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrSrLabelService> upsrSrLabelServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrInterfaceService> upsrInterfaceServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrSyncConfService> upsrSyncConfServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrTunnelService> upsrTunnelServiceRpcRegistration;
    //...
    //ODL REST Service RpcRegistration end;

    public UpsrProvider(RpcProviderRegistry rpcRegistry, final DataBroker dataBroker) {
        this.rpcRegistry = rpcRegistry;
        this.dataBroker = dataBroker;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("Upsr Session Initiated");
        // Init upsr system
        this.upsr.init(this.dataBroker);

        // Register service
        this.registerServices();
        LOG.info("Upsr Session Initiated End!");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        // Close Register Service
        this.closeServices();
        LOG.info("UpsrProvider Closed");
    }

    private void registerServices() {
        netconfSessionODLApi = new NetconfSessionODLApi(this.upsr);
        this.topoInfoServiceReg = this.rpcRegistry.addRpcImplementation(UpsrTopoService.class, new TopoInfoODLApi(this.upsr));
        this.upsrBgplsSessionServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrBgplsSessionService.class, new BgplsSessionODLApi(this.upsr));
        this.vpnInstanceServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrVpnInstanceService.class, new VpnInstanceODLApi(this.upsr));
        this.upsrNetconfSessionServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrNetconfSessionService.class, netconfSessionODLApi);
        this.upsrSrLabelServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrSrLabelService.class, new SrLabelODLApi(this.upsr));
        this.upsrInterfaceServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrInterfaceService.class, new InterfaceODLApi(this.upsr));
        this.upsrSyncConfServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrSyncConfService.class, new ConfSyncODLApi(this.upsr));
        this.upsrTunnelServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrTunnelService.class, new TunnelODLApi(this.upsr));
    }

    private void closeServices() {
        this.topoInfoServiceReg.close();
        this.upsrBgplsSessionServiceRpcRegistration.close();
        this.vpnInstanceServiceRpcRegistration.close();
        netconfSessionODLApi.close();
        this.upsrNetconfSessionServiceRpcRegistration.close();
        this.upsrSrLabelServiceRpcRegistration.close();
        this.upsrInterfaceServiceRpcRegistration.close();
        this.upsrSyncConfServiceRpcRegistration.close();
        this.upsrTunnelServiceRpcRegistration.close();
    }

}