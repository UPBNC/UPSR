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
import cn.org.upbnc.entity.*;
//import cn.org.upbnc.entity.Device;

import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.DeviceTypeEnum;
import cn.org.upbnc.enumtype.TopoStatusEnum;
import cn.org.upbnc.util.UtilInterface;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.ospf.node.attributes.ospf.node.attributes.Ted;
import org.opendaylight.yangtools.yang.binding.DataObject;
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

public class BGPManagerImpl implements BGPManager, DataChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(BGPManagerImpl.class);
    private static BGPManager instance = null;

    private static final InstanceIdentifier<Topology> II_TO_TOPOLOGY_DEFAULT = InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("example-linkstate-topology")));

    private UtilInterface utilInterface;
    private DataBroker dataBroker;
    private Topology odlTopology;
    private BgpTopoInfo bgpTopoInfo;
    private BgpTopoInfo bgpTopoInfoTotal;
    private TopoCallback tcb;
    private TopoStatusEnum topoStatusEnum;


    private BGPManagerImpl(){
        this.utilInterface = null;
        this.dataBroker = null;
        this.odlTopology = null;
        //this.bgpTopoInfo = null;
        this.bgpTopoInfoTotal = null;
        this.tcb = null;
        this.topoStatusEnum = TopoStatusEnum.INIT;
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
            this.dataBroker = this.getDataBroker();
            this.dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,II_TO_TOPOLOGY_DEFAULT,this, AsyncDataBroker.DataChangeScope.SUBTREE);
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
    public void getTopoInfo(){
        if(this.topoStatusEnum != TopoStatusEnum.INIT){
            return ;
        }
        try {
            this.topoStatusEnum=TopoStatusEnum.UPDATING;
            this.getDataBroker();
            ReadTransaction trx = this.dataBroker.newReadOnlyTransaction();
            CheckedFuture<Optional<Topology>, ReadFailedException> future = trx.read(LogicalDatastoreType.OPERATIONAL, II_TO_TOPOLOGY_DEFAULT);

            Futures.addCallback(future, new FutureCallback<Optional<Topology>>() {
                @Override
                public void onSuccess(@NullableDecl Optional<Topology> topologyOptional) {
                    if(null != topologyOptional) {
                        odlTopology = topologyOptional.get();
                        if (null != odlTopology) {
                            LOG.info(odlTopology.getKey().toString());
                            LOG.info("Read Topology from ODL Start...");
                            bgpTopoInfoTotal = updateBgpTopoInfoByODLTopo(odlTopology);
                            bgpTopoInfo = dealBgpTopoInfo(bgpTopoInfoTotal);
                            if(bgpTopoInfo == null) {
                                LOG.info("Bgp Topo is null!");
                            }
                            tcb.updateBgpTopoInfoCb(bgpTopoInfo);
                            topoStatusEnum = TopoStatusEnum.FINISH;
                            LOG.info("Read Topology from ODL End!");
                        } else {
                            LOG.info("Read Topology but NULL!");
                        }
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

    @Override
    public void test(){

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
     * Transfer ODL Topology to UPSR Bgp Topology
     */
    private BgpTopoInfo updateBgpTopoInfoByODLTopo(Topology odlTopology){
        // Create TopoInfo
        BgpTopoInfo bgpTopoInfo = new BgpTopoInfo();

        // Set BgpDevices
        List<Node> nodes = odlTopology.getNode();
        bgpTopoInfo.setBgpDeviceList(this.getDeveiceByODLNode(nodes));

        // Set BgpLinks
        List<Link> links = odlTopology.getLink();
        bgpTopoInfo.setBgpLinkList(this.getLinkByODLLink(links,bgpTopoInfo.getBgpDeviceList()));

        return bgpTopoInfo;
    }

    /*
     * private function
     * Transfer ODL Links to UPSR Links
     */
    private List<BgpLink> getLinkByODLLink(List<Link> links,List<BgpDevice> bgpDeviceList){
        List<BgpLink> bgpLinkList = new ArrayList<BgpLink>();
        Iterator<Link> linkIterator = links.iterator();
        int linkId = 0;
        while (linkIterator.hasNext()){
            // Find a link of ODL
            Link link = linkIterator.next();

            // Create a link of upsr
            BgpLink bgpLink = new BgpLink();
            bgpLink.setId(linkId);
            bgpLink.setName(link.getLinkId().getValue());

            // Add Source Interface
            Source source = link.getSource();
            String srcNode = source.getSourceNode().getValue();
            String srcTp = source.getSourceTp().getValue();
            BgpDeviceInterface sourceBgpDeviceInterface = this.findBgpDeviceInterfaceByNodeId(bgpDeviceList,srcNode,srcTp);
            if(null == sourceBgpDeviceInterface) {
                sourceBgpDeviceInterface = this.createNewBgpDeviceInterface(srcNode,srcTp);
            }

            // Add Destination Interface
            Destination destination = link.getDestination();
            String destNode = destination.getDestNode().getValue();
            String destTp = destination.getDestTp().getValue();
            BgpDeviceInterface destBgpDeviceInterface = this.findBgpDeviceInterfaceByNodeId(bgpDeviceList,destNode,destTp);
            if( null == destBgpDeviceInterface ){
                destBgpDeviceInterface =  this.createNewBgpDeviceInterface(destNode,destTp);
            }

            // Add Interface
            bgpLink.setBgpDeviceInterface1(sourceBgpDeviceInterface);
            bgpLink.setBgpDeviceInterface2(destBgpDeviceInterface);

            // Find a igp Link
            Link1 link1 = link.getAugmentation(Link1.class);
//            Link1 link1 = link.augmentation(Link1.class);
            IgpLinkAttributes igpLinkAttributes =link1.getIgpLinkAttributes();

            // Set metric
            bgpLink.setMetric(igpLinkAttributes.getMetric());
            igpLinkAttributes.getName();

            // Find a ospf Link
            //IgpLinkAttributes1 igpLinkAttributes1= igpLinkAttributes.getAugmentation(IgpLinkAttributes1.class);
            //IgpLinkAttributes1 igpLinkAttributes1= igpLinkAttributes.augmentation(IgpLinkAttributes1.class);
            //OspfLinkAttributes ospfLinkAttributes = igpLinkAttributes1.getOspfLinkAttributes();
            //LOG.info(ospfLinkAttributes.getTed().toString());

            bgpLinkList.add(bgpLink);
            linkId++;
        }
        return  bgpLinkList;
    }

    /*
     * private function
     * Transfer ODL Nodes to UPSR Devices
     */
    private List<BgpDevice> getDeveiceByODLNode(List<Node> nodes){
        Iterator<Node> nodeIterator = nodes.iterator();
        List<BgpDevice> bgpDeviceList = new ArrayList<BgpDevice>();
        int deviceId = 0;

        while (nodeIterator.hasNext()){
            // Find a node of ODL
            Node node = nodeIterator.next();

            // Create a code of upsr
            BgpDevice bgpDevice = new BgpDevice();
            bgpDevice.setId(deviceId);
            bgpDevice.setName(node.getNodeId().getValue());

            // Find a igp node
            Node1 node1 = node.getAugmentation(Node1.class);
//            Node1 node1 = node.augmentation(Node1.class);
            IgpNodeAttributes igpNodeAttributes = node1.getIgpNodeAttributes();
            this.setBgpDeviceRouterInfo(igpNodeAttributes,bgpDevice);

            // Add Prefix
            this.setBgpDevicePrefixList(igpNodeAttributes,bgpDevice);

            // Find a opsf Node & add address
            this.setBgpDeviceRouterIp(igpNodeAttributes,bgpDevice);

            // Find termination & add interface into device
            this.setBgpDeviceTerminationPoint(node,bgpDevice);

            deviceId++;
            bgpDeviceList.add(bgpDevice);
        }
        return bgpDeviceList;
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

    private void setBgpDeviceRouterInfo(IgpNodeAttributes igpNodeAttributes,BgpDevice bgpDevice){
        if(null != igpNodeAttributes ) {
            List<IpAddress> ipAddresses = igpNodeAttributes.getRouterId();
            if(null != ipAddresses && !ipAddresses.isEmpty()){
                bgpDevice.setDeviceTypeEnum(DeviceTypeEnum.ROUTER);
                bgpDevice.setRouterId(ipAddresses.get(0).getIpv4Address().getValue());
            }else{
                bgpDevice.setDeviceTypeEnum(DeviceTypeEnum.OTHER);
            }
        }
    }

    private void setBgpDevicePrefixList(IgpNodeAttributes igpNodeAttributes,BgpDevice bgpDevice){
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
                bgpDevice.setPrefixList(upsrPrefixList);
            }
        }
    }

    private void setBgpDeviceRouterIp(IgpNodeAttributes igpNodeAttributes,BgpDevice bgpDevice){
        IgpNodeAttributes1 igpNodeAttributes1 = igpNodeAttributes.getAugmentation(IgpNodeAttributes1.class);
//        IgpNodeAttributes1 igpNodeAttributes1 = igpNodeAttributes.augmentation(IgpNodeAttributes1.class);
        OspfNodeAttributes ospfNodeAttributes = igpNodeAttributes1.getOspfNodeAttributes();
        if(null != ospfNodeAttributes){
           Ted ted = ospfNodeAttributes.getTed();
           if(null != ted){
               Ipv4Address ipv4Address = ted.getTeRouterIdIpv4();
               if(null!=ipv4Address){
                   Address address = new Address(ipv4Address.getValue(),AddressTypeEnum.V4);
                   bgpDevice.setAddress(address);
               }
           }
        }
        //Address address = new Address(ospfNodeAttributes.getTed().getTeRouterIdIpv4().getValue(), AddressTypeEnum.V4);
    }

    private void setBgpDeviceTerminationPoint(Node node,BgpDevice bgpdevice){
        List<TerminationPoint> terminationPoints = node.getTerminationPoint();
        if(null != terminationPoints && !terminationPoints.isEmpty()) {
            Iterator<TerminationPoint> terminationPointIterator = terminationPoints.iterator();

            while (terminationPointIterator.hasNext()) {
                // Catch tp
                TerminationPoint tp = terminationPointIterator.next();

                // Create a new device interface
                BgpDeviceInterface bgpDeviceInterface = new BgpDeviceInterface();

                // Add interface name;
                bgpDeviceInterface.setName(tp.getTpId().getValue());
                bgpDeviceInterface.setBgpDeviceName(bgpdevice.getName());

                // Add interface ip;
                TerminationPoint1 tp1 = tp.getAugmentation(TerminationPoint1.class);
//                TerminationPoint1 tp1 = tp.augmentation(TerminationPoint1.class);
                if(null != tp1) {
                    IgpTerminationPointAttributes itpa = tp1.getIgpTerminationPointAttributes();
                    if(null != itpa) {
                        Ip ip = (Ip) itpa.getTerminationPointType();
                        List<IpAddress> ipAddresses = ip.getIpAddress();
                        if (null != ipAddresses && !ipAddresses.isEmpty()) {
                            Address ad = new Address(ipAddresses.get(0).getIpv4Address().getValue(), AddressTypeEnum.V4);
                            bgpDeviceInterface.setIp(ad);
                            LOG.info("IP:"+ad.getAddress());
                        }
                    }
                }

                // Add interface into device
                bgpdevice.addBgpDeviceInterface(bgpDeviceInterface);
            }
        }
    }

    private BgpDeviceInterface findBgpDeviceInterfaceByNodeId(List<BgpDevice> bgpDeviceList, String node, String tp){
        if(null != bgpDeviceList && !bgpDeviceList.isEmpty()){
            Iterator<BgpDevice> bgpDeviceIterator = bgpDeviceList.iterator();
            while(bgpDeviceIterator.hasNext()){
                BgpDevice bgpDevice = bgpDeviceIterator.next();
                if(node.equals(bgpDevice.getName())){
                    List<BgpDeviceInterface> bgpDeviceInterfaces = bgpDevice.getBgpDeviceInterfaceList();
                    Iterator<BgpDeviceInterface> bgpDeviceInterfaceIterator = bgpDeviceInterfaces.iterator();
                    while (bgpDeviceInterfaceIterator.hasNext()){
                        BgpDeviceInterface bgpDeviceInterface = bgpDeviceInterfaceIterator.next();
                        if(tp.equals(bgpDeviceInterface.getName())){
                            return bgpDeviceInterface;
                        }
                    }
                }
            }
        }
        return null;
    }

    private BgpDeviceInterface createNewBgpDeviceInterface(String node, String tp){
        BgpDeviceInterface bgpDeviceInterface = new BgpDeviceInterface();
        bgpDeviceInterface.setName(tp);
        bgpDeviceInterface.setBgpDeviceName(node);
        try {
            bgpDeviceInterface.setIp(this.getIpv4AddressTpName(bgpDeviceInterface.getName()));
        }catch (Exception e){
            LOG.info("Create deviceInterface failure!");
            LOG.info(e.getMessage());
        }
        return bgpDeviceInterface;
    }

    private BgpTopoInfo dealBgpTopoInfo(BgpTopoInfo bgpTopoInfoTotalToDeal){
        BgpTopoInfo bgpTopoInfoDealed = new BgpTopoInfo();
        // Deal bgp device
        bgpTopoInfoDealed.setBgpDeviceList(this.dealBgpDevice(bgpTopoInfoTotalToDeal.getBgpDeviceList()));
        // Deal bgp link
        bgpTopoInfoDealed.setBgpLinkList(this.dealBgpLink(bgpTopoInfoTotalToDeal.getBgpLinkList(),bgpTopoInfoDealed.getBgpDeviceList()));

        return bgpTopoInfoDealed;
    }

    private List<BgpDevice> dealBgpDevice(List<BgpDevice> bgpDeviceListToDeal){
        List<BgpDevice> bgpDeviceListDealed = null;
        if( null != bgpDeviceListToDeal && !bgpDeviceListToDeal.isEmpty()){
            bgpDeviceListDealed = new ArrayList<BgpDevice>();
            Iterator<BgpDevice> deviceIterator = bgpDeviceListToDeal.iterator();
            while (deviceIterator.hasNext()){
                BgpDevice bgpDevice= deviceIterator.next();
                if(null != bgpDevice.getRouterId()){
                    bgpDeviceListDealed.add(bgpDevice);
                }
            }
        }
        return bgpDeviceListDealed;
    }



    private List<BgpLink> dealBgpLink(List<BgpLink> bgpLinkListToDeal,List<BgpDevice> bgpDeviceList){
        List<BgpLink> bgpLinkListDealed = null;
        if( null != bgpLinkListToDeal && !bgpLinkListToDeal.isEmpty()){
            bgpLinkListDealed = new ArrayList<BgpLink>();
            Iterator<BgpLink> deviceIterator = bgpLinkListToDeal.iterator();
            while (deviceIterator.hasNext()){
                BgpLink bgpLink= deviceIterator.next();
                if(this.isBgpLinkInDeviceList(bgpLink,bgpDeviceList)){
                    bgpLinkListDealed.add(bgpLink);
                }
            }
        }
        return bgpLinkListDealed;
    }

    private boolean isBgpLinkInDeviceList(BgpLink bgpLink,List<BgpDevice> bgpDeviceList){
        boolean ret1 = this.isBgpDeviceInterfaceInDeviceList(bgpLink.getBgpDeviceInterface1(),bgpDeviceList);
        boolean ret2 = this.isBgpDeviceInterfaceInDeviceList(bgpLink.getBgpDeviceInterface2(),bgpDeviceList);
        return (ret1&&ret2);
    }

    private boolean isBgpDeviceInterfaceInDeviceList(BgpDeviceInterface bgpDeviceInterface,List<BgpDevice> bgpDeviceList){
        Iterator<BgpDevice> bgpDeviceIterator = bgpDeviceList.iterator();
        while(bgpDeviceIterator.hasNext()){
            BgpDevice bgpDevice = bgpDeviceIterator.next();
            if(bgpDevice.getBgpDeviceInterfaceList().contains(bgpDeviceInterface)){
                return true;
            }
        }
        return false;
    }

    //**********************************************************************
    /*
     * 暂时不用！！！
     */
    private BgpDevice copyBgpDevice(BgpDevice copy){
        BgpDevice ret = new BgpDevice();
        ret.setId(copy.getId());
        ret.setName(copy.getName());
        ret.setRouterId(copy.getRouterId());
        ret.setDeviceTypeEnum(copy.getDeviceTypeEnum());
        ret.setBgpDeviceInterfaceList(this.copyBgpDeviceInterface(copy.getBgpDeviceInterfaceList()));
        ret.setPrefixList(copy.getPrefixList());
        return ret;
    }

    private List<BgpDeviceInterface> copyBgpDeviceInterface(List<BgpDeviceInterface> copy){
        List<BgpDeviceInterface> ret = null;
        if(null != copy && !copy.isEmpty()){
            ret = new ArrayList<BgpDeviceInterface>();
            Iterator<BgpDeviceInterface> bgpDeviceInterfaceIterator = copy.iterator();
            while(bgpDeviceInterfaceIterator.hasNext()){
                BgpDeviceInterface copy1 = bgpDeviceInterfaceIterator.next();
                BgpDeviceInterface ret1 = new BgpDeviceInterface();
                ret1.setBgpDeviceName(copy1.getName());
                ret1.setId(copy1.getId());
                ret1.setName(copy1.getName());
//                Address retAddress = new Address();
//                retAddress.setType(copy1.getIp().getType());
//                retAddress.setAddress(copy1.getIp().getAddress());
                ret1.setIp(new Address(copy1.getIp().getAddress(),copy1.getIp().getType()));
                ret.add(ret1);
            }
        }
        return ret;
    }


    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        DataObject dataObject = change.getUpdatedSubtree();

        if(dataObject instanceof Topology){
            Topology odlTopology = (Topology) dataObject;
            LOG.info("Update Topology from ODL Start...");
            this.bgpTopoInfoTotal = updateBgpTopoInfoByODLTopo(odlTopology);
            this.bgpTopoInfo = dealBgpTopoInfo(this.bgpTopoInfoTotal);
            this.tcb.updateBgpTopoInfoCb(this.bgpTopoInfo);
            LOG.info("Update Topology from ODL End!");
        }else{

        }


    }
}
