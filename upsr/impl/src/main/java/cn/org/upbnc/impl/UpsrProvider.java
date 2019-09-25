/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.impl;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.core.StatisticsThread;
import cn.org.upbnc.odlapi.*;
import cn.org.upbnc.xmlcompare.XMLCompareTest;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrauth.rev170830.UpsrAuthService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsractioncfg.rev190509.UpsrActionCfgService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrbgplssession.rev181120.UpsrBgplsSessionService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrinterface.rev181119.UpsrInterfaceService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrmonitor.rev190923.UpsrMonitorService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrnetconfsession.rev181119.UpsrNetconfSessionService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrrouterpolicy.rev120222.UpsrRouterPolicyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.UpsrSrLabelService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrstatistic.rev181227.UpsrStatisticService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsyncconf.rev181129.UpsrSyncConfService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.UpsrTopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtrafficpolicy.rev190923.UpsrTrafficPolicyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnel.rev181227.UpsrTunnelService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtunnelpolicy.rev120222.UpsrTunnelPolicyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrvpninstance.rev181119.UpsrVpnInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpsrProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    private final Session upsr = Session.getSession();
    private final RpcProviderRegistry rpcRegistry;
    private final DataBroker dataBroker;
    private NetconfSessionODLApi netconfSessionODLApi;
    private StatisticsThread statisticsThread;
    //ODL REST Service RpcRegistration start;
    private BindingAwareBroker.RpcRegistration<UpsrTopoService> topoInfoServiceReg;
    private BindingAwareBroker.RpcRegistration<UpsrBgplsSessionService> upsrBgplsSessionServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrVpnInstanceService> vpnInstanceServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrNetconfSessionService> upsrNetconfSessionServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrSrLabelService> upsrSrLabelServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrInterfaceService> upsrInterfaceServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrSyncConfService> upsrSyncConfServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrTunnelService> upsrTunnelServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrTunnelPolicyService> upsrTunnelPolicyServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrRouterPolicyService> upsrRouterPolicyServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrStatisticService> upsrStatisticServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrActionCfgService> actionCfgServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrAuthService> upsrAuthServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrMonitorService> monitorServiceRpcRegistration;
    private BindingAwareBroker.RpcRegistration<UpsrTrafficPolicyService> trafficPolicyServiceRpcRegistration;
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
//        statisticsThread = new StatisticsThread();
//        statisticsThread.start();
//        XMLCompareTest.test();
//        statisticsThread = new StatisticsThread();
//        statisticsThread.start();
        LOG.info("Upsr Session Initiated End!");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        // Close Register Service
        this.closeServices();
//        statisticsThread.stopMe();
        LOG.info("UpsrProvider Closed");
    }

    private void registerServices() {
        this.topoInfoServiceReg = this.rpcRegistry.addRpcImplementation(UpsrTopoService.class, new TopoInfoODLApi(this.upsr));
        this.upsrBgplsSessionServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrBgplsSessionService.class, new BgplsSessionODLApi(this.upsr));
        this.vpnInstanceServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrVpnInstanceService.class, new VpnInstanceODLApi(this.upsr));
        netconfSessionODLApi = new NetconfSessionODLApi(this.upsr);
        this.upsrNetconfSessionServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrNetconfSessionService.class, netconfSessionODLApi);
        this.upsrSrLabelServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrSrLabelService.class, new SrLabelODLApi(this.upsr));
        this.upsrInterfaceServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrInterfaceService.class, new InterfaceODLApi(this.upsr));
        this.upsrSyncConfServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrSyncConfService.class, new ConfSyncODLApi(this.upsr));
        this.upsrTunnelServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrTunnelService.class, new TunnelODLApi(this.upsr));
        this.upsrTunnelPolicyServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrTunnelPolicyService.class, new TunnelPolicyODLApi(this.upsr));
        this.upsrRouterPolicyServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrRouterPolicyService.class, new RouterPolicyODLApi(this.upsr));
        this.upsrStatisticServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrStatisticService.class, new StatisticODLApi(this.upsr));
        this.actionCfgServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrActionCfgService.class, new ActionCfgODLApi(this.upsr));
        this.upsrAuthServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrAuthService.class, new UpsrAuthODLAPI(this.upsr));
        this.monitorServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrMonitorService.class, new MonitorInterfaseODLApi(this.upsr));
        this.trafficPolicyServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrTrafficPolicyService.class, new TrafficPolicyODLApi(this.upsr));
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
        this.upsrTunnelPolicyServiceRpcRegistration.close();
        this.upsrRouterPolicyServiceRpcRegistration.close();
        this.upsrStatisticServiceRpcRegistration.close();
        this.actionCfgServiceRpcRegistration.close();
        this.upsrAuthServiceRpcRegistration.close();
        this.monitorServiceRpcRegistration.close();
        this.trafficPolicyServiceRpcRegistration.close();
    }

}