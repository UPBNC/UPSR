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
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.SrStatus;
import cn.org.upbnc.enumtype.SystemStatusEnum;
import cn.org.upbnc.impl.UpsrProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.upsrsrlabel.rev181126.srglobal.SrgbPrefixSidBuilder;
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
    private static final Logger LOG = LoggerFactory.getLogger(TopoInfoODLApi.class);
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

    private boolean existLinks(List<Links> linksList, Link link) {
        for (Links links : linksList) {
            if ((links.getDest().getIfAddress().equals(link.getDeviceInterface1().getIp().getAddress())) &&
                    links.getSource().getIfAddress().equals(link.getDeviceInterface2().getIp().getAddress())) {
                return true;
            }
        }
        return false;
    }

    //组装链路信息
    private List<Links> getLinks(TopoInfo topoInfo, GetLinksInput filter) {
        List<Links> linksList = new ArrayList<>();
        List<Link> linkList = topoInfo.getLinkList();
        if (linkList == null) {
            LOG.info("linkList is null");
            return linksList;
        }
        Iterator<Link> linkIterator = linkList.iterator();
        while (linkIterator.hasNext()) {
            Link link = linkIterator.next();
            if ((filter != null) && (filter.getLinkId().equals(link.getId().toString()) != true)) {
                continue;
            }

            if (existLinks(linksList, link) == true) {
                continue;
            }
            LinksBuilder linksBuilder = new LinksBuilder();
            linksBuilder.setLinkId(link.getId().toString());
            SourceBuilder sourceBuilder = new SourceBuilder();
            sourceBuilder.setRouterId(link.getDeviceInterface1().getDevice().getRouterId());
            sourceBuilder.setIfAddress(link.getDeviceInterface1().getIp().getAddress());
            if (link.getDeviceInterface1().getAdjLabel() != null) {
                sourceBuilder.setAdjlabel(link.getDeviceInterface1().getAdjLabel().getValue().toString());
            }
            linksBuilder.setSource(sourceBuilder.build());
            DestBuilder destBuilder = new DestBuilder();
            destBuilder.setRouterId(link.getDeviceInterface2().getDevice().getRouterId());
            destBuilder.setIfAddress(link.getDeviceInterface2().getIp().getAddress());
            if (link.getDeviceInterface2().getAdjLabel() != null) {
                destBuilder.setAdjlabel(link.getDeviceInterface2().getAdjLabel().getValue().toString());
            }
            linksBuilder.setDest(destBuilder.build());
            linksList.add(linksBuilder.build());
        }
        return linksList;
    }

    //组装接口信息
    private List<DeviceInterfaces> deviceInterfacesCreate(Device device, GetNodesInput filter) {
        List<DeviceInterfaces> deviceInterfaces = new ArrayList<>();
        List<DeviceInterface> deviceInterfaceList = device.getDeviceInterfaceList();
        Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaceList.iterator();
        while (deviceInterfaceIterator.hasNext()) {
            DeviceInterface deviceInterface = deviceInterfaceIterator.next();
            if (filter != null && filter.getIfAddress() != null &&
                    filter.getIfAddress().equals(deviceInterface.getIp().getAddress()) != true) {
                continue;
            }
            DeviceInterfacesBuilder deviceInterfacesBuilder = new DeviceInterfacesBuilder();
            deviceInterfacesBuilder.setIfName(deviceInterface.getName());
            if ((deviceInterface.getBgpStatus() != null) && (deviceInterface.getBgpStatus() == 1)) {
                deviceInterfacesBuilder.setIsSrDomain(SrStatus.ENABLED.getName());
            } else {
                deviceInterfacesBuilder.setIsSrDomain(SrStatus.DISENABLED.getName());
            }
            deviceInterfacesBuilder.setIfAddress(deviceInterface.getIp().getAddress());
            if (deviceInterface.getMask() != null) {
                deviceInterfacesBuilder.setMask(deviceInterface.getMask().getAddress());
            }
            deviceInterfacesBuilder.setSrEnabled(deviceInterface.getSrStatus());
            if (deviceInterface.getAdjLabel() != null) {
                deviceInterfacesBuilder.setAdjlabel(deviceInterface.getAdjLabel().getValue().toString());
            }
            if (deviceInterface.getIfPhyStatus() != null) {
                deviceInterfacesBuilder.setPhyStatus(deviceInterface.getIfPhyStatus());
            }
            deviceInterfaces.add(deviceInterfacesBuilder.build());
        }
        return deviceInterfaces;
    }

    private SrgbPrefixSidBuilder srgbPrefixSidBuilderCreate(Device device) {
        SrgbPrefixSidBuilder srgbPrefixSidBuilder = new SrgbPrefixSidBuilder();
        if ((device.getSrStatus() != null) && device.getSrStatus().equals(SrStatus.ENABLED.getName()) &&
                device.getNodeLabel() != null) {
            srgbPrefixSidBuilder.setPrefixId(String.valueOf(new Integer(device.getNodeLabel().getValue() + device.getMinNodeSID())));
            srgbPrefixSidBuilder.setSrgbBegin(device.getMinNodeSID().toString());
            srgbPrefixSidBuilder.setSrgbEnd(device.getMaxNodeSID().toString());
            if (device.getMinAdjSID() != null) {
                srgbPrefixSidBuilder.setAdjBegin(device.getMinAdjSID().toString());
                srgbPrefixSidBuilder.setAdjEnd(device.getMaxAdjSID().toString());
            }
        }
        return srgbPrefixSidBuilder;
    }

    //组装节点信息
    private List<Nodes> getNodes(TopoInfo topoInfo, GetNodesInput filter) {
        List<Nodes> nodesList = new ArrayList<>();
        List<Device> deviceList = topoInfo.getDeviceList();
        if (deviceList == null) {
            LOG.info("getNodes deviceList is null");
            return nodesList;
        }
        Iterator<Device> deviceIterator = deviceList.iterator();
        while (deviceIterator.hasNext()) {
            Device device = deviceIterator.next();
            if (filter != null && filter.getRouterId().equals(device.getRouterId()) != true) {
                continue;
            }
            NodesBuilder nodesBuilder = new NodesBuilder();
            nodesBuilder.setRouterId(device.getRouterId());
            nodesBuilder.setSrEnabled(device.getSrStatus());
            nodesBuilder.setDeviceName(device.getDeviceName());
            nodesBuilder.setSrgbPrefixSid(srgbPrefixSidBuilderCreate(device).build());
            nodesBuilder.setDeviceInterfaces(deviceInterfacesCreate(device, filter));
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
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getNodesOutputBuilder.build()).buildFuture();
        } else {
            this.getTopoInfoApi();
        }
        if ((input != null) && (input.getIfAddress() != null) && (input.getRouterId() == null)) {
            LOG.info("getNodes err");
            return RpcResultBuilder.success(getNodesOutputBuilder.build()).buildFuture();
        }
        TopoInfo topoInfo = (TopoInfo) topoInfoApi.getTopoInfo().get(ResponseEnum.BODY.getName());
        if (topoInfo != null) {
            getNodesOutputBuilder.setNodes(this.getNodes(topoInfo, input));
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
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getTopoOutputBuilder.build()).buildFuture();
        } else {
            this.getTopoInfoApi();
        }
        TopoInfo topoInfo = (TopoInfo) topoInfoApi.getTopoInfo().get(ResponseEnum.BODY.getName());
        if (topoInfo != null) {
            getTopoOutputBuilder.setLinks(this.getLinks(topoInfo, null));
            getTopoOutputBuilder.setNodes(this.getNodes(topoInfo, null));
        }
        getTopoOutputBuilder.setResult("success");
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
        if (SystemStatusEnum.ON != this.session.getStatus()) {
            return RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
        } else {
            this.getTopoInfoApi();
        }
        TopoInfo topoInfo = (TopoInfo) topoInfoApi.getTopoInfo().get(ResponseEnum.BODY.getName());
        if (topoInfo != null) {
            getLinksOutputBuilder.setLinks(this.getLinks(topoInfo, null));
        }

        LOG.info("getLinks end");
        return RpcResultBuilder.success(getLinksOutputBuilder.build()).buildFuture();
    }
}
