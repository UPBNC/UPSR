/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc  and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.odlapi;

import cn.org.upbnc.api.APIInterface;
import cn.org.upbnc.api.TopoInfoApi;
import cn.org.upbnc.core.Session;
import cn.org.upbnc.entity.*;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.Links;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.LinksBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.DestBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.linkinfo.links.SourceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.nodeinfo.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.nodeinfo.NodesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.nodeinfo.nodes.DeviceInterfaces;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrtopo.rev181119.nodeinfo.nodes.DeviceInterfacesBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

public class TopoInfoODLApi implements UpsrTopoService {
    private static final Logger LOG = LoggerFactory.getLogger(UpsrProvider.class);
    Session session;
    TopoInfoApi topoInfoApi;

    public TopoInfoODLApi(Session session) {
        this.session = session;
    }

    private TopoInfoApi getTopoInfoApi() {
        if (this.topoInfoApi == null) {
            APIInterface apiInterface = session.getApiInterface();
            if (apiInterface != null) {
                topoInfoApi = apiInterface.getTopoInfoApi();
            }
        }
        return this.topoInfoApi;
    }

    private String getRouterId(TopoInfo topoInfo, String deviceName) {
        List<Device> deviceList = topoInfo.getDeviceList();
        Iterator<Device> deviceIterator = deviceList.iterator();
        while (deviceIterator.hasNext()) {
            Device device = deviceIterator.next();
            if (deviceName.equals(device.getName()) != true) {
                continue;
            }
            return device.getRouterId();
        }
        return "";
    }

    private List<Links> getLinks(TopoInfo topoInfo, GetLinksInput filter){
        List<Links> linksList = new ArrayList<>();
        List<Link> linkList = topoInfo.getLinkList();
        if (linkList == null){
            LOG.info("linkList is null");
            return linksList;
        }
        Iterator<Link> linkIterator = linkList.iterator();
        while (linkIterator.hasNext()){
            Link link = linkIterator.next();
            if(filter != null && filter.getLinkId().equals(link.getId().toString()) != true) {
                continue;
            }
            boolean linkExist = false;
            for (Links links:linksList){
                if((links.getDest().equals(link.getDeviceInterface1().getIp().getAddress())) &&
                    links.getSource().equals(link.getDeviceInterface2().getIp().getAddress())){
                    linkExist = true;
                    break;
                }
            }
            if(linkExist == true){
                continue;
            }
            LinksBuilder linksBuilder = new LinksBuilder();
            linksBuilder.setLinkId(link.getId().toString());
            SourceBuilder sourceBuilder = new SourceBuilder();
            //sourceBuilder.setRouterId(link.getDeviceInterface1().getDevice().getRouterId());
            sourceBuilder.setRouterId(this.getRouterId(topoInfo,link.getDeviceInterface1().getDeviceName()));
            sourceBuilder.setIfAddress(link.getDeviceInterface1().getIp().getAddress());
            linksBuilder.setSource(sourceBuilder.build());
            DestBuilder destBuilder = new DestBuilder();
            //destBuilder.setRouterId(link.getDeviceInterface2().getDevice().getRouterId());
            destBuilder.setRouterId(this.getRouterId(topoInfo,link.getDeviceInterface2().getDeviceName()));
            destBuilder.setIfAddress(link.getDeviceInterface2().getIp().getAddress());
            linksBuilder.setDest(destBuilder.build());
            linksList.add(linksBuilder.build());
        }
        return linksList;
    }
    private List<Nodes> getNodes(TopoInfo topoInfo, GetNodesInput filter){
        List<Nodes> nodesList = new ArrayList<>();
        List<Device> deviceList = topoInfo.getDeviceList();
        if (deviceList == null){
            return nodesList;
        }
        Iterator<Device> deviceIterator = deviceList.iterator();
        while (deviceIterator.hasNext()){
            Device device = deviceIterator.next();
            if(filter != null && filter.getRouterId().equals(device.getRouterId()) != true){
                continue;
            }
            NodesBuilder nodesBuilder = new NodesBuilder();
            nodesBuilder.setRouterId(device.getRouterId());
            List<DeviceInterfaces> deviceInterfaces = new ArrayList<>();
            List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
            Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaceList.iterator();
            while (deviceInterfaceIterator.hasNext()){
                DeviceInterface deviceInterface = deviceInterfaceIterator.next();
                if (filter != null && filter.getIfAddress() != null && filter.getIfAddress().equals(deviceInterface.getIp().getAddress()) != true){
                    continue;
                }
                DeviceInterfacesBuilder deviceInterfacesBuilder = new DeviceInterfacesBuilder();
                deviceInterfacesBuilder.setIfAddress(deviceInterface.getIp().getAddress());
                deviceInterfaces.add(deviceInterfacesBuilder.build());
            }
            nodesBuilder.setDeviceInterfaces(deviceInterfaces);
            nodesList.add(nodesBuilder.build());
        }
        return nodesList;
    }
    @Override
    public Future<RpcResult<GetNodesOutput>> getNodes(GetNodesInput input) {
        /*
        http://localhost:8181/restconf/operations/upsrTopo:getNodes
        {"input": { "routerId":"sunxa"}}
         */
        GetNodesOutputBuilder getNodesOutputBuilder = new GetNodesOutputBuilder();
        LOG.info("getNodes begin");
        if(SystemStatusEnum.ON != this.session.getStatus()){
            return RpcResultBuilder.success(getNodesOutputBuilder.build()).buildFuture();
        }else{
            this.getTopoInfoApi();
        }
        TopoInfo topoInfo = topoInfoApi.getTopoInfo();
        List<Nodes> nodesList = new ArrayList<>();

        if (topoInfo != null){
            getNodesOutputBuilder.setNodes(this.getNodes(topoInfo,input));
        }
        LOG.info("getNodes end");
        return RpcResultBuilder.success(getNodesOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetTopoOutput>> getTopo() {
        /*
        http://localhost:8181/restconf/operations/upsrTopo:getTopo
         */
        GetTopoOutputBuilder getTopoOutputBuilder = new GetTopoOutputBuilder();
        LOG.info("getTopo begin");
        if(SystemStatusEnum.ON != this.session.getStatus()){
            return RpcResultBuilder.success(getTopoOutputBuilder.build()).buildFuture();
        }else{
            this.getTopoInfoApi();
        }
        TopoInfo topoInfo = topoInfoApi.getTopoInfo();
        if (topoInfo != null) {
            getTopoOutputBuilder.setLinks(this.getLinks(topoInfo, null));
            getTopoOutputBuilder.setNodes(this.getNodes(topoInfo, null));
        }
        LOG.info("getTopo end");
        return RpcResultBuilder.success(getTopoOutputBuilder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetLinksOutput>> getLinks(GetLinksInput input) {
        /*
        http://localhost:8181/restconf/operations/upsrTopo:getLinks  :  {"input": { "linkId":"sunxasss"}}
         */
        GetLinksOutputBuilder getLinksOutputBuilder = new GetLinksOutputBuilder();
        LOG.info("getLinks begin");
        if(SystemStatusEnum.ON != this.session.getStatus()){
            return RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
        }else{
            this.getTopoInfoApi();
        }
        TopoInfo topoInfo = topoInfoApi.getTopoInfo();
        if (topoInfo != null) {
            getLinksOutputBuilder.setLinks(this.getLinks(topoInfo, input));
        }

        LOG.info("getLinks end");
        return  RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
    }
}
