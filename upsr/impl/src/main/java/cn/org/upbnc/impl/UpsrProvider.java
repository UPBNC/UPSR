/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.impl;

import cn.org.upbnc.core.Session;
import cn.org.upbnc.odlapi.BgplsSessionODLApi;
import cn.org.upbnc.odlapi.TopoInfoODLApi;
import cn.org.upbnc.odlapi.TopoTestODLApi;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topo.rev181119.TopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrbgplssession.rev181120.UpsrBgplsSessionService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.UpsrTopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpsrProvider implements AutoCloseable{

    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    private final Session upsr = Session.getSession();
    private final RpcProviderRegistry rpcRegistry;
    private final DataBroker dataBroker;

    //ODL REST Service RpcRegistration start;
    private BindingAwareBroker.RpcRegistration<TopoService> topoServiceReg;
    private BindingAwareBroker.RpcRegistration<UpsrTopoService> topoInfoServiceReg;
    private BindingAwareBroker.RpcRegistration<UpsrBgplsSessionService> upsrBgplsSessionServiceRpcRegistration;
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
        this.upsr.init();

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

    private void registerServices(){
        this.topoServiceReg = this.rpcRegistry.addRpcImplementation(TopoService.class, new TopoTestODLApi(this.upsr));
        this.topoInfoServiceReg = this.rpcRegistry.addRpcImplementation(UpsrTopoService.class, new TopoInfoODLApi(this.upsr));
        this.upsrBgplsSessionServiceRpcRegistration = this.rpcRegistry.addRpcImplementation(UpsrBgplsSessionService.class,new BgplsSessionODLApi(this.upsr));
    }

    private void closeServices(){
        this.topoServiceReg.close();
        this.topoInfoServiceReg.close();
        this.upsrBgplsSessionServiceRpcRegistration.close();
    }

}