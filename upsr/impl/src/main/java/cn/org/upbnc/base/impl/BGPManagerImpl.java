/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.BGPManager;
import cn.org.upbnc.entity.BGPConnect;
import cn.org.upbnc.util.UtilInterface;
//import com.google.common.base.Optional;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
//import org.opendaylight.mdsal.binding.api.DataBroker;
//import org.opendaylight.mdsal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
//import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BGPManagerImpl implements BGPManager {
    private static final Logger LOG = LoggerFactory.getLogger(BGPManagerImpl.class);
    private static BGPManager instance = null;

    private static final InstanceIdentifier<Topology> II_TO_TOPOLOGY_DEFAULT = InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("example-linkstate-topology")));

    private UtilInterface utilInterface;
    private DataBroker dataBroker;
    private Topology odlTopology;
    private Optional<Topology> topologyOptional;

//    private List<BGPConnect> bgpConnectList;

    private BGPManagerImpl(){
        this.utilInterface = null;
        this.dataBroker = null;
        this.odlTopology = null;
        this.topologyOptional = null;
        //this.bgpConnectList = new ArrayList<BGPConnect>();
        return;
    }
    public static BGPManager getInstance(){
        if(null == instance) {
            instance = new BGPManagerImpl();
        }
        return instance;
    }

    @Override
    public boolean setUtilInterface(UtilInterface utilInterface) {
        boolean ret =false;
        try {
            this.utilInterface = utilInterface;
            ret = true;
        }catch (Exception e){
            ret = false;
            LOG.info(e.getMessage());
        }
        return ret;
    }

    @Override
    public void test(){
        try {
            this.getDataBroker();
            ReadTransaction trx = this.dataBroker.newReadOnlyTransaction();
            CheckedFuture<Optional<Topology>, ReadFailedException> future = trx.read(LogicalDatastoreType.OPERATIONAL, II_TO_TOPOLOGY_DEFAULT);

            Futures.addCallback(future, new FutureCallback<Optional<Topology>>() {
                @Override
                public void onSuccess(@NullableDecl Optional<Topology> topologyOptional) {
                    odlTopology= topologyOptional.get();
                    if( null != odlTopology) {
                        LOG.info(odlTopology.getKey().toString());
                    }else {
                        LOG.info("Read but NULL!");
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    LOG.info("Failed");
                }
            });
        }catch (Exception e){
            LOG.info(e.getMessage());
        }

        return;
    }


    /*
     *  private function
     *  Get Data Broker
     */
    private DataBroker getDataBroker(){
        if(this.dataBroker == null){
            if(null != this.utilInterface){
                this.dataBroker = this.utilInterface.getDataBroker();
            }
        }
        return this.dataBroker;
    }
}
