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
import cn.org.upbnc.enumtype.AddressTypeEnum;
import cn.org.upbnc.enumtype.DeviceTypeEnum;
import cn.org.upbnc.enumtype.BgpTopoStatusEnum;
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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.IgpNodeAttributes1;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.ospf.node.attributes.OspfNodeAttributes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.ospf.topology.rev131021.ospf.node.attributes.ospf.node.attributes.Ted;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//import cn.org.upbnc.entity.Device;

//import org.opendaylight.mdsal.binding.api.DataBroker;
//import org.opendaylight.mdsal.binding.api.ReadTransaction;
//import org.opendaylight.mdsal.common.api.LogicalDatastoreType;

//import java.util.ArrayList;
//import java.util.List;

public class BGPManagerImpl implements BGPManager, DataChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(BGPManagerImpl.class);
    private static BGPManager instance = null;
    private String DOMAIN="domain";

    private static final InstanceIdentifier<Topology> II_TO_TOPOLOGY_DEFAULT = InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("example-linkstate-topology")));

    private UtilInterface utilInterface;
    private DataBroker dataBroker;
    private Topology odlTopology;
    private BgpTopoInfo bgpTopoInfo;
    private BgpTopoInfo bgpTopoInfoTotal;
    private TopoCallback tcb;
    private BgpTopoStatusEnum bgpTopoStatusEnum;
    private Map<String,BgpTopoInfo> bgpTopoInfoMap;
    private Map<String,BgpTopoInfo> bgpTopoInfoTotalMap;


    private BGPManagerImpl(){
        this.utilInterface = null;
        this.dataBroker = null;
        this.odlTopology = null;
        this.bgpTopoInfoTotal = null;
        this.tcb = null;
        this.bgpTopoInfoMap = null;
        this.bgpTopoInfoTotalMap = null;
        this.bgpTopoStatusEnum = BgpTopoStatusEnum.INIT;
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
    public BgpTopoInfo getBgpTopoInfo(){
        if(this.bgpTopoStatusEnum != BgpTopoStatusEnum.INIT){
            return this.bgpTopoInfo;
        }
        try {
            this.bgpTopoStatusEnum = BgpTopoStatusEnum.UPDATING;
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
//                            bgpTopoInfoTotal = updateBgpTopoInfoByODLTopo(odlTopology);
//                            bgpTopoInfo = dealBgpTopoInfo(bgpTopoInfoTotal);

                            bgpTopoInfoTotalMap = updateBgpTopoInfoByODLTopoDomain(odlTopology);
                            bgpTopoInfoMap = dealBgpTopoInfoMapDomain(bgpTopoInfoTotalMap);
                            bgpTopoInfo = dealBgpTopoInfoDomain(bgpTopoInfoMap);

                            if(bgpTopoInfo == null) {
                                LOG.info("Bgp Topo is null!");
                            }
                            tcb.updateBgpTopoInfoCb(bgpTopoInfo);
                            bgpTopoStatusEnum = BgpTopoStatusEnum.FINISH;
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
        return this.bgpTopoInfo;
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
     * Set into map
     */
    private Map<String,BgpTopoInfo> updateBgpTopoInfoByODLTopoDomain(Topology odlTopology){
        // Create map
        Map<String,BgpTopoInfo> map = new HashMap<String,BgpTopoInfo>();

        // Check odlTopology
        if( null == odlTopology){
            return map;
        }
        // Set BgpDevices
        List<Node> nodes = odlTopology.getNode();
        // Get domain and node list
        Map<String,List<Node> > nodeMap = this.getODLDomainNodeMap(odlTopology);

        // Get domain and link list
        //Map<String,List<Link>> linkMap = this.getODLDomainLinkMap(odlTopology,nodeMap);

        // Get bgp topo map
        map = this.getDomainBgpTopoMap(nodeMap,odlTopology);

        return map;
    }

    private Map<String,BgpTopoInfo> dealBgpTopoInfoMapDomain(Map<String,BgpTopoInfo> totalMap){
        Map<String,BgpTopoInfo> map = new HashMap<String,BgpTopoInfo>();
        Set<Map.Entry<String,BgpTopoInfo>> set =  totalMap.entrySet();
        Iterator<Map.Entry<String,BgpTopoInfo>> iterator = set.iterator();
        while (iterator.hasNext()){
            Map.Entry<String,BgpTopoInfo> entry = iterator.next();
            BgpTopoInfo temp = this.dealBgpTopoInfo(entry.getValue());
            map.put(entry.getKey(),temp);
        }
        return map;
    }

    private BgpTopoInfo dealBgpTopoInfoDomain(Map<String,BgpTopoInfo> map){
        BgpTopoInfo bgpTopoInfo = new BgpTopoInfo();
        Set<Map.Entry<String,BgpTopoInfo>> set =  map.entrySet();
        Iterator<Map.Entry<String,BgpTopoInfo>> iterator = set.iterator();
        while (iterator.hasNext()){
            Map.Entry<String,BgpTopoInfo> entry = iterator.next();
            this.addDomainInfo(bgpTopoInfo,entry.getValue());
        }
        return bgpTopoInfo;
    }

    private void addDomainInfo(BgpTopoInfo target, BgpTopoInfo source){
        List<BgpDevice> tDeveice = target.getBgpDeviceList();
        if(null == tDeveice){
            tDeveice = new ArrayList<BgpDevice>();
            if(null != source.getBgpDeviceList()) {
                tDeveice.addAll(source.getBgpDeviceList());
            }
            target.setBgpDeviceList(tDeveice);
        }else{
            if(null != source.getBgpDeviceList()) {
                addBgpDeviceIntoTargetListDomain(tDeveice, source.getBgpDeviceList());
            }
        }

        List<BgpLink> tLink = target.getBgpLinkList();
        if(null == tLink){
            tLink = new ArrayList<BgpLink>();
            if(null != source.getBgpLinkList()) {
                tLink.addAll(source.getBgpLinkList());
            }
            target.setBgpLinkList(tLink);
        }else{
            if(null != source.getBgpLinkList()) {
                addBgpLinkIntoTargetListDomain(tLink, source.getBgpLinkList());
            }
        }

        return;
    }

    private void addBgpDeviceIntoTargetListDomain(List<BgpDevice> target,List<BgpDevice> source){
        if(null != target && !target.isEmpty() && null != source && !source.isEmpty()){
            for(BgpDevice bgpDevice : source){
                if(!isBgpDeviceInListDomain(bgpDevice,target)){
                    target.add(bgpDevice);
                }
            }
        }
        return;
    }

    private boolean isBgpDeviceInListDomain(BgpDevice bgpDevice,List<BgpDevice> list){
        for(BgpDevice t : list){
            if(t.getRouterId().equals(bgpDevice.getRouterId())){
                return true;
            }
        }
        return false;
    }

    private void addBgpLinkIntoTargetListDomain(List<BgpLink> target,List<BgpLink> source){
        if(null != target && !target.isEmpty() && null != source && !source.isEmpty()){
            for(BgpLink bgpLink : source){
                if(!isBgpLinkiInListDomain(bgpLink,target)){
                    target.add(bgpLink);
                }
            }
        }
        return;
    }
    private boolean isBgpLinkiInListDomain(BgpLink bgpLink,List<BgpLink> list){
        for(BgpLink t : list){
            if(t.getBgpDeviceInterface1().getIp().getAddress().equals(bgpLink.getBgpDeviceInterface1().getIp().getAddress())&&
                    t.getBgpDeviceInterface2().getIp().getAddress().equals(bgpLink.getBgpDeviceInterface2().getIp().getAddress())
            ){
                return true;
            }
        }
        return false;
    }
    /*
     * private function
     * Transfer ODL Topology to UPSR Bgp Topology
     */
    private BgpTopoInfo updateBgpTopoInfoByODLTopo(Topology odlTopology){
        // Create TopoInfo
        BgpTopoInfo bgpTopoInfo = new BgpTopoInfo();

        // Check odlTopology
        if( null == odlTopology){
            return bgpTopoInfo;
        }

//        // 方案一：所有域，node取并集，link取交集
//        // Set BgpDevices
//        List<Node> nodes = odlTopology.getNode();
//        List<BgpDevice> bgpDevices = this.getDeviceByODLNode(nodes);
//        //bgpTopoInfo.setBgpDeviceList(this.getCombineDevice(bgpDevices));
//        bgpTopoInfo.setBgpDeviceList(bgpDevices);
//
//        // Set BgpLinks
//        List<Link> links = odlTopology.getLink();
//        bgpTopoInfo.setBgpLinkList(this.getLinkByODLLink(links,bgpTopoInfo.getBgpDeviceList()));


        // 方案二，取第一个domain处理
        // Get domain topo
        String domain = this.getDomain(odlTopology);

        // Set BgpDevices
        List<Node> nodes = this.getODLDomainNode(odlTopology.getNode(),DOMAIN+domain);
        bgpTopoInfo.setBgpDeviceList(this.getDeviceByODLNode(nodes));

        // Set BgpLinks
        List<Link> links = this.getODLDomainLink(odlTopology.getLink(),DOMAIN+domain);
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
    private List<BgpDevice> getDeviceByODLNode(List<Node> nodes){
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
        List<BgpLink> ret = null;
        if(null == bgpDeviceList || bgpDeviceList.isEmpty()){
            return ret;
        }
        if(null == bgpLinkListToDeal || bgpLinkListToDeal.isEmpty()){
            return ret;
        }

        ret = new ArrayList<BgpLink>();
        Iterator<BgpDevice> bgpDeviceIterator = bgpDeviceList.iterator();
        while (bgpDeviceIterator.hasNext()){
            BgpDevice bgpDevice = bgpDeviceIterator.next();
            List<BgpDeviceInterface> bgpDeviceInterfaces = bgpDevice.getBgpDeviceInterfaceList();
            if(null != bgpDeviceInterfaces && !bgpDeviceInterfaces.isEmpty()){
                Iterator<BgpDeviceInterface> bgpDeviceInterfaceIterator = bgpDeviceInterfaces.iterator();
                while(bgpDeviceInterfaceIterator.hasNext()){
                    BgpDeviceInterface bgpDeviceInterface = bgpDeviceInterfaceIterator.next();
                    ret.addAll(this.getLinkByInterface(bgpDeviceInterface,bgpLinkListToDeal));
                }
            }
        }
        return ret;
    }

    private List<BgpLink> getLinkByInterface(BgpDeviceInterface bdi,List<BgpLink> lbl){
        List<BgpLink> ret = new ArrayList<BgpLink>();
        //通过Interface找到以Interface为Src的Link
        List<BgpLink> srcBgpLinkList = this.findSrcLinkBySrcInterface(bdi,lbl);
        for(BgpLink bgpLink : srcBgpLinkList){
            // 通过Link的Dest Interface 找到 LinkList，排除Interface是自己的Link
            ret.addAll(this.findDstLinkByMideInterfaceWithoutSrcInterface(bgpLink.getBgpDeviceInterface2(),lbl,bdi));
        }
        return ret;
    }

    private List<BgpLink> findDstLinkByMideInterfaceWithoutSrcInterface(BgpDeviceInterface bdi,List<BgpLink> lbl,BgpDeviceInterface outbdi){
        List<BgpLink> ret = new ArrayList<BgpLink>();
        for(BgpLink bgpLink : lbl){
            if(bgpLink.getBgpDeviceInterface1().getBgpDeviceName().equals(bdi.getBgpDeviceName()) && bgpLink.getBgpDeviceInterface2() != outbdi){
                BgpLink temp = new BgpLink();
                // 设置src Interface
                temp.setBgpDeviceInterface1(outbdi);
                // 设置 dst Interface
                temp.setBgpDeviceInterface2(bgpLink.getBgpDeviceInterface2());
                ret.add(temp);
            }
        }
        return ret;
    }


    private List<BgpLink> findSrcLinkBySrcInterface(BgpDeviceInterface bdi,List<BgpLink> lbl){
        List<BgpLink> ret = new ArrayList<BgpLink>();
        Iterator<BgpLink> ibl = lbl.iterator();
        while(ibl.hasNext()){
            BgpLink bgpLink = ibl.next();
            if(bgpLink.getBgpDeviceInterface1() == bdi){
                ret.add(bgpLink);
            }
        }
        return ret;
    }

    private List<BgpLink> dealBgpLink1(List<BgpLink> bgpLinkListToDeal,List<BgpDevice> bgpDeviceList){
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
    //**********************************************************************

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

    private List<BgpDevice> getCombineDevice(List<BgpDevice> bgpDevices){
        List<BgpDevice> ret = new ArrayList<BgpDevice>();
        Iterator<BgpDevice> iterator = bgpDevices.iterator();
        while (iterator.hasNext()){
            BgpDevice bgpDevice = iterator.next();
            if(!this.isBgpDeviceInList(bgpDevice,ret)){
                ret.add(bgpDevice);
            }
        }

        return ret;
    }

    private boolean isBgpDeviceInList(BgpDevice bgpDevice,List<BgpDevice> bgpDevices){
        Iterator<BgpDevice> iterator = bgpDevices.iterator();
        while(iterator.hasNext()){
            BgpDevice temp = iterator.next();
            if(bgpDevice.getRouterId() != null && bgpDevice.getRouterId().equals(temp.getRouterId())){
                return true;
            }
        }
        return false;
    }

    //**********************************************************************
    //        // Get domain and node list
    //        Map<String,List<Node> > nodeMap = this.getODLDomainNodeMap(odlTopology);
    //
    //        // Get domain and link list
    //        Map<String,List<Link>> linkMap = this.getODLDomainLinkMap(odlTopology,nodeMap);
    //
    //        // Combine nodes
    //
    //
    //        // Get bgp topo map
    //        //Map<String,BgpTopoInfo> bgpTopoInfoMap = this.getDomainBgpTopoMap(nodeMap,odlTopology);
    //**********************************************************************
    /*
     * 暂时不用！！！
     */
    private Map<String,List<Node>> getODLDomainNodeMap(Topology topology){
        Map<String,List<Node>> domainMap = new HashMap<String,List<Node>>();

        List<Node> nodes = topology.getNode();
        Iterator<Node> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            // Find a node of ODL
            Node node = nodeIterator.next();
            String domain = isNodeInDomain(node,domainMap.keySet());
            if(null != domain ){
                List<Node> list = domainMap.get(domain);
                list.add(node);
            }else{
                List<Node> list = new ArrayList<Node>();
                list.add(node);
                String domain2 = this.getDomainByNode(node);
                domainMap.put(domain2,list);
            }
        }
        return domainMap;
    }

    private Map<String,List<Link>> getODLDomainLinkMap(Topology topology,Map nodeMap){
        Map<String,List<Link>> domainMap = new HashMap<String,List<Link>>();

        Set<String> domainSet = nodeMap.keySet();
        Iterator<String> iterator = domainSet.iterator();
        while(iterator.hasNext()){

            String domain = iterator.next();
            List<Link> links = this.getODLDomainLink(topology.getLink(),DOMAIN+domain);
            domainMap.put(domain,links);
        }
        return domainMap;
    }

    private Map<String,BgpTopoInfo> getDomainBgpTopoMap(Map nodeMap,Topology topology){
        Map<String,BgpTopoInfo> map = new  HashMap<String,BgpTopoInfo>();
        Set<String> domainSet = nodeMap.keySet();
        Iterator<String> iterator = domainSet.iterator();
        while(iterator.hasNext()){
            String domain = iterator.next();
            BgpTopoInfo bgpTopoInfo = new BgpTopoInfo();

            // Set BgpDevices
            List<Node> nodes = (List<Node>)nodeMap.get(domain);
            bgpTopoInfo.setBgpDeviceList(this.getDeviceByODLNode(nodes));

            // Set BgpLinks
            List<Link> links = this.getODLDomainLink(topology.getLink(),DOMAIN+domain);
            bgpTopoInfo.setBgpLinkList(this.getLinkByODLLink(links,bgpTopoInfo.getBgpDeviceList()));

            // add to map
            map.put(domain,bgpTopoInfo);
        }
        return map;
    }

    private String isNodeInDomain(Node node,Set<String> domainSet){
        Iterator<String> iterator = domainSet.iterator();
        while (iterator.hasNext()){
            String domain = iterator.next();
            if(node.getNodeId().getValue().contains(domain)){
                return domain;
            }
        }
        return null;
    }

    private String getDomainByNode(Node node){
        String nodeId = node.getNodeId().getValue();
        String[] first = nodeId.split(DOMAIN);
        if(first.length > 1) {
            String[] second = first[1].split("&");
            if(second.length >1){
                return second[0];
            }
        }
        return null;
    }
    //**********************************************************************

    //**********************************************************************
    /*
     * 暂时不用！！！
     */

    private String getDomain(Topology topology){
        List<Node> nodes = topology.getNode();
        if(nodes != null && !nodes.isEmpty()){
            Node node = nodes.get(0);
            String nodeId = node.getNodeId().getValue();
            String[] temp = nodeId.split(DOMAIN);
            if(temp.length > 1) {
                String[] temp2 = temp[1].split("&");
                if(temp2.length >1){
                    return temp2[0];
                }
            }
        }
        return null;
    }

    private List<Node> getODLDomainNode(List<Node> nodes,String domain) {
        List<Node> ret = new ArrayList<Node>();
        Iterator<Node> nodeIterator = nodes.iterator();

        while (nodeIterator.hasNext()) {
            // Find a node of ODL
            Node node = nodeIterator.next();
            String nodeId = node.getNodeId().getValue();
            if(nodeId.contains(domain)){
                ret.add(node);
            }
        }
        return ret;
    }

    private List<Link> getODLDomainLink(List<Link> links,String domain) {
        List<Link> ret = new ArrayList<Link>();
        Iterator<Link> linkIterator = links.iterator();

        while (linkIterator.hasNext()) {
            // Find a link of ODL
            Link link = linkIterator.next();
            String linkId = link.getLinkId().getValue();
            if(linkId.contains(domain)){
                ret.add(link);
            }
        }
        return ret;
    }
    //**********************************************************************

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        DataObject dataObject = change.getUpdatedSubtree();

        if(dataObject instanceof Topology){
            Topology odlTopology = (Topology) dataObject;
            LOG.info("Update Topology from ODL Start...");
            this.bgpTopoStatusEnum = BgpTopoStatusEnum.UPDATING;
//            this.bgpTopoInfoTotal = updateBgpTopoInfoByODLTopo(odlTopology);
//            this.bgpTopoInfo = dealBgpTopoInfo(this.bgpTopoInfoTotal);
            this.bgpTopoInfoTotalMap = updateBgpTopoInfoByODLTopoDomain(odlTopology);
            this.bgpTopoInfoMap = dealBgpTopoInfoMapDomain(bgpTopoInfoTotalMap);
            this.bgpTopoInfo = dealBgpTopoInfoDomain(bgpTopoInfoMap);
            this.tcb.updateBgpTopoInfoCb(this.bgpTopoInfo);
            this.bgpTopoStatusEnum = BgpTopoStatusEnum.UPDATED;
            LOG.info("Update Topology from ODL End!");
        }else{

        }


    }
}
