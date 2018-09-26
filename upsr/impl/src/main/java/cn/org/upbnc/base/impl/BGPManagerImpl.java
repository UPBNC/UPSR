/*
 * Copyright © 2018 Copyright (c) 2018 UP & BNC, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.BGPManager;
import cn.org.upbnc.callback.TopoCallback;
import cn.org.upbnc.entity.Address;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.DeviceInterface;
import cn.org.upbnc.entity.TopoInfo;
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.DeviceTypeEnum;
import cn.org.upbnc.util.UtilInterface;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Destination;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Source;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.node.TerminationPoint;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.Link1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.Node1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.TerminationPoint1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.igp.link.attributes.IgpLinkAttributes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.igp.node.attributes.IgpNodeAttributes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.igp.node.attributes.igp.node.attributes.Prefix;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.igp.termination.point.attributes.IgpTerminationPointAttributes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.nt.l3.unicast.igp.topology.rev131021.igp.termination.point.attributes.igp.termination.point.attributes.termination.point.type.Ip;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.IgpLinkAttributes1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.IgpNodeAttributes1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.ospf.link.attributes.OspfLinkAttributes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.ospf.node.attributes.OspfNodeAttributes;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.opendaylight.mdsal.binding.api.DataBroker;
//import org.opendaylight.mdsal.binding.api.ReadTransaction;
//import org.opendaylight.mdsal.common.api.LogicalDatastoreType;

//import java.util.ArrayList;
//import java.util.List;

public class BGPManagerImpl implements BGPManager {
    private static final Logger LOG = LoggerFactory.getLogger(BGPManagerImpl.class);
    private static BGPManager instance = null;

    private static final InstanceIdentifier<Topology> II_TO_TOPOLOGY_DEFAULT = InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("example-linkstate-topology")));

    private UtilInterface utilInterface;
    private DataBroker dataBroker;
    private Topology odlTopology;
    private TopoInfo topoInfo;
    private TopoCallback tcb;

    //private Optional<Topology> topologyOptional;

//    private List<BGPConnect> bgpConnectList;

    private BGPManagerImpl(){
        this.utilInterface = null;
        this.dataBroker = null;
        this.odlTopology = null;
        this.topoInfo = null;
        this.tcb = null;
        //this.topologyOptional = null;
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
    public void setTopoCallback(TopoCallback tcb){
        this.tcb = tcb;
    }

    @Override
    public TopoInfo getTopoInfo(){
        try {
            this.getDataBroker();
            ReadTransaction trx = this.dataBroker.newReadOnlyTransaction();
            CheckedFuture<Optional<Topology>, ReadFailedException> future = trx.read(LogicalDatastoreType.OPERATIONAL, II_TO_TOPOLOGY_DEFAULT);

            Futures.addCallback(future, new FutureCallback<Optional<Topology>>() {
                @Override
                public void onSuccess(@NullableDecl Optional<Topology> topologyOptional) {
                    odlTopology=  topologyOptional.get();
                    if( null != odlTopology) {
                        LOG.info(odlTopology.getKey().toString());
                        LOG.info("Read Topology from ODL Start...");
                        topoInfo = getTopoInfoByODLTopo(odlTopology);
                        tcb.setTopoInfoCb(topoInfo);
                        LOG.info("Read Topology from ODL End!");

                    }else {
                        LOG.info("Read Topology but NULL!");
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
        return null;
    }

    @Override
    public void test(){
//        try {
//            this.getDataBroker();
//            ReadTransaction trx = this.dataBroker.newReadOnlyTransaction();
//            CheckedFuture<Optional<Topology>, ReadFailedException> future = trx.read(LogicalDatastoreType.OPERATIONAL, II_TO_TOPOLOGY_DEFAULT);
//
//            Futures.addCallback(future, new FutureCallback<Optional<Topology>>() {
//                @Override
//                public void onSuccess(@NullableDecl Optional<Topology> topologyOptional) {
//                    odlTopology= topologyOptional.get();
//                    if( null != odlTopology) {
//                        LOG.info(odlTopology.getKey().toString());
//                    }else {
//                        LOG.info("Read but NULL!");
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//                    LOG.info("Failed");
//                }
//            });
//        }catch (Exception e){
//            LOG.info(e.getMessage());
//        }

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

    /*
     * private function
     * Transfer ODL Topology to UPSR Topology
     */
    private TopoInfo getTopoInfoByODLTopo(Topology odlTopology){
        // Create TopoInfo
        TopoInfo topoInfo = new TopoInfo();

        // Set Device
        List<Node> nodes = odlTopology.getNode();
        topoInfo.setDeviceList(this.getDeveiceByODLNode(nodes));

        // Set Links
        List<Link> links = odlTopology.getLink();
        topoInfo.setLinkList(this.getLinkByODLLink(links,topoInfo.getDeviceList()));

        return topoInfo;
    }

    /*
     * private function
     * Transfer ODL Links to UPSR Links
     */
    private List<cn.org.upbnc.entity.Link> getLinkByODLLink(List<Link> links,List<Device> deviceList){
        List<cn.org.upbnc.entity.Link> linkList = new ArrayList<cn.org.upbnc.entity.Link>();
        Iterator<Link> linkIterator = links.iterator();
        int linkId = 0;
        while (linkIterator.hasNext()){
            // Find a link of ODL
            Link link = linkIterator.next();

            // Create a link of upsr
            cn.org.upbnc.entity.Link upsrLink = new cn.org.upbnc.entity.Link();
            upsrLink.setId(linkId);
            upsrLink.setName(link.getLinkId().getValue());

            // Add Source Interface
            Source source = link.getSource();
            String srcNode = source.getSourceNode().getValue();
            String srcTp = source.getSourceTp().getValue();
            DeviceInterface sourceDeviceInterface = this.findDeviceInterfaceByNodeId(deviceList,srcNode,srcTp);
            if(null == sourceDeviceInterface) {
                sourceDeviceInterface = createNewDeviceInterface(srcNode,srcTp);
            }

            // Add Destination Interface
            Destination destination = link.getDestination();
            String destNode = destination.getDestNode().getValue();
            String destTp = destination.getDestTp().getValue();
            DeviceInterface destinationDeviceInterface = this.findDeviceInterfaceByNodeId(deviceList,destNode,destTp);
            if( null == destinationDeviceInterface ){
                destinationDeviceInterface =  createNewDeviceInterface(destNode,destTp);
            }

            // Add Interface
            upsrLink.setDeviceInterface1(sourceDeviceInterface);
            upsrLink.setDeviceInterface2(destinationDeviceInterface);

            // Find a igp Link
            Link1 link1 = link.getAugmentation(Link1.class);
//            Link1 link1 = link.augmentation(Link1.class);
            IgpLinkAttributes igpLinkAttributes =link1.getIgpLinkAttributes();

            // Set metric
            upsrLink.setMetric(igpLinkAttributes.getMetric());
            igpLinkAttributes.getName();

            // Find a ospf Link
            IgpLinkAttributes1 igpLinkAttributes1= igpLinkAttributes.getAugmentation(IgpLinkAttributes1.class);
//            IgpLinkAttributes1 igpLinkAttributes1= igpLinkAttributes.augmentation(IgpLinkAttributes1.class);
            OspfLinkAttributes ospfLinkAttributes = igpLinkAttributes1.getOspfLinkAttributes();
            LOG.info(ospfLinkAttributes.getTed().toString());

            linkList.add(upsrLink);
            linkId++;
        }
        return  linkList;
    }

    /*
     * private function
     * Transfer ODL Nodes to UPSR Devices
     */
    private List<Device> getDeveiceByODLNode(List<Node> nodes){
        Iterator<Node> nodeIterator = nodes.iterator();
        List<Device> deviceList = new ArrayList<Device>();
        int deviceId = 0;

        while (nodeIterator.hasNext()){
            // Find a node of ODL
            Node node = nodeIterator.next();

            // Create a code of upsr
            Device device = new Device();
            device.setId(deviceId);
            device.setName(node.getNodeId().getValue());

            // Find a igp node
            Node1 node1 = node.getAugmentation(Node1.class);
//            Node1 node1 = node.augmentation(Node1.class);
            IgpNodeAttributes igpNodeAttributes = node1.getIgpNodeAttributes();
            this.setDeviceRouterInfo(igpNodeAttributes,device);

            // Add Prefix
            this.setDevicePrefixList(igpNodeAttributes,device);

            // Find a opsf Node & add address
            this.setDeviceRouterIp(igpNodeAttributes,device);

            // Find termination & add interface into device
            this.setDeviceTerminationPoint(node,device);

            deviceId++;
            deviceList.add(device);
        }
        return deviceList;
    }

    private Address getIpv4AddressTpName(String tpName){
        String tag = "ipv4";
        int index = tpName.indexOf(tag);
        String ipv4 = tpName.substring(index+tag.length()+1);

        Address address = new Address();
        address.setType(AddressTypeEnum.V4);
        address.setAddress(ipv4);
        return address;
    }

    private void setDeviceRouterInfo(IgpNodeAttributes igpNodeAttributes,Device device){
        if(null != igpNodeAttributes ) {
            List<IpAddress> ipAddresses = igpNodeAttributes.getRouterId();
            if(null != ipAddresses && !ipAddresses.isEmpty()){
                device.setDeviceTypeEnum(DeviceTypeEnum.ROUTER);
                device.setRouterId(ipAddresses.get(0).getIpv4Address().getValue());
            }else{
                device.setDeviceTypeEnum(DeviceTypeEnum.OTHER);
            }
        }
    }

    private void setDevicePrefixList(IgpNodeAttributes igpNodeAttributes,Device device){
        if(null != igpNodeAttributes) {
            List<cn.org.upbnc.entity.Prefix> upsrPrefixList = new ArrayList<cn.org.upbnc.entity.Prefix>();
            List<Prefix> prefixs = igpNodeAttributes.getPrefix();
            if(null != prefixs && !prefixs.isEmpty()) {
                Iterator<Prefix> prefixIterator = prefixs.iterator();
                while (prefixIterator.hasNext()) {
                    Prefix prefix = prefixIterator.next();
                    cn.org.upbnc.entity.Prefix upsrPrefix = new cn.org.upbnc.entity.Prefix();
                    upsrPrefix.setPrefix(prefix.getPrefix().getIpv4Prefix().getValue());
                    upsrPrefix.setMetric(prefix.getMetric().intValue());
                    upsrPrefixList.add(upsrPrefix);
                }
                device.setPrefixList(upsrPrefixList);
            }
        }
    }

    private void setDeviceRouterIp(IgpNodeAttributes igpNodeAttributes,Device device){
        IgpNodeAttributes1 igpNodeAttributes1 = igpNodeAttributes.getAugmentation(IgpNodeAttributes1.class);
//        IgpNodeAttributes1 igpNodeAttributes1 = igpNodeAttributes.augmentation(IgpNodeAttributes1.class);
        OspfNodeAttributes ospfNodeAttributes = igpNodeAttributes1.getOspfNodeAttributes();
        Address address = new Address(ospfNodeAttributes.getTed().getTeRouterIdIpv4().getValue(), AddressTypeEnum.V4);
        device.setAddress(address);
    }

    private void setDeviceTerminationPoint(Node node,Device device){
        List<TerminationPoint> terminationPoints = node.getTerminationPoint();
        if(null != terminationPoints && !terminationPoints.isEmpty()) {
            Iterator<TerminationPoint> terminationPointIterator = terminationPoints.iterator();

            while (terminationPointIterator.hasNext()) {
                // Catch tp
                TerminationPoint tp = terminationPointIterator.next();

                // Create a new device interface
                DeviceInterface deviceInterface = new DeviceInterface();

                // Add interface name;
                deviceInterface.setName(tp.getTpId().getValue());
                deviceInterface.setDeviceName(device.getDeviceName());

                // Add interface ip;
//                TerminationPoint1 tp1 = tp.getAugmentation(TerminationPoint1.class);
////                TerminationPoint1 tp1 = tp.augmentation(TerminationPoint1.class);
//                if(null != tp1) {
//                    IgpTerminationPointAttributes itpa = tp1.getIgpTerminationPointAttributes();
//                    if(null != itpa) {
//                        Ip ip = (Ip) itpa.getTerminationPointType();
//                        List<IpAddress> ipAddresses = ip.getIpAddress();
//                        if (null != ipAddresses && !ipAddresses.isEmpty()) {
//                            Address ad = new Address(ipAddresses.get(0).getIpv4Address().getValue(), AddressTypeEnum.V4);
//                            deviceInterface.setIp(ad);
//                        }
//                    }
//                }

                // Add interface into device
                deviceInterface.setDevice(device);
                device.addDeviceInterface(deviceInterface);
            }
        }
    }

    private DeviceInterface findDeviceInterfaceByNodeId(List<Device> deviceList, String node, String tp){
        if(null != deviceList && deviceList.isEmpty()){
            Iterator<Device> deviceIterator = deviceList.iterator();
            while(deviceIterator.hasNext()){
                Device device = deviceIterator.next();
                if(node.equals(device.getName())){
                    List<DeviceInterface> deviceInterfaces = device.getDeviceInterfaceList();
                    Iterator<DeviceInterface> deviceInterfaceIterator = deviceInterfaces.iterator();
                    while (deviceInterfaceIterator.hasNext()){
                        DeviceInterface deviceInterface = deviceInterfaceIterator.next();
                        if(tp.equals(deviceInterface.getName())){
                            return deviceInterface;
                        }
                    }
                }
            }
        }
        return null;
    }

    private DeviceInterface createNewDeviceInterface(String node, String tp){
        DeviceInterface deviceInterface = new DeviceInterface();
        deviceInterface.setName(tp);
        deviceInterface.setDeviceName(node);
        try {
            deviceInterface.setIp(this.getIpv4AddressTpName(deviceInterface.getName()));
        }catch (Exception e){
            LOG.info("Create deviceInterface failure!");
            LOG.info(e.getMessage());
        }
        return deviceInterface;
    }

}
