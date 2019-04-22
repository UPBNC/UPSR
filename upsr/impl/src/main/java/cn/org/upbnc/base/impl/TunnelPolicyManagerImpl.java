package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.*;
import cn.org.upbnc.entity.Tunnel;
import cn.org.upbnc.entity.TunnelPolicy.TnlSelSeq;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.enumtype.TnlPolicyTypeEnum;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TunnelPolicy.STnlSelSeq;
import cn.org.upbnc.util.netconf.TunnelPolicy.STpNexthop;
import cn.org.upbnc.util.netconf.TunnelPolicy.STunnelPolicy;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.TunnelPolicyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelPolicyManagerImpl implements TunnelPolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceManagerImpl.class);
    private static TunnelPolicyManager instance = null;
    private Map<String, TunnelPolicy> tunnelPolicyMap;
    private Map<String, Map<String, TunnelPolicy>> tunnelPolicyMaps;

    private TunnelPolicyManagerImpl() {
        this.tunnelPolicyMaps = new HashMap<>();
    }


    public static TunnelPolicyManager getInstance() {
        if (null == instance) {
            instance = new TunnelPolicyManagerImpl();
        }
        return instance;
    }

    @Override
    public List<TunnelPolicy> getAllTunnelPolicys(String routerID) {
        if (tunnelPolicyMaps.containsKey(routerID)) {
            List<TunnelPolicy> tunnelPolicyList = new ArrayList<TunnelPolicy>();
            Collection<TunnelPolicy> collection = this.tunnelPolicyMaps.get(routerID).values();
            for (TunnelPolicy t : collection) {
                tunnelPolicyList.add(t);
            }
            return tunnelPolicyList;
        } else {
            return null;
        }

    }

    @Override
    public List<TunnelPolicy> getAllTunnelPolicys() {
        List<TunnelPolicy> tunnelPolicyList = new ArrayList<TunnelPolicy>();
        for (String routerId : tunnelPolicyMaps.keySet()) {
            Collection<TunnelPolicy> collection = this.tunnelPolicyMaps.get(routerId).values();
            for (TunnelPolicy t : collection) {
                tunnelPolicyList.add(t);
            }
        }
        return tunnelPolicyList;
    }

    @Override
    public TunnelPolicy getTunnelPolicy(String name) {
        if (null != name) {
            return this.tunnelPolicyMap.get(name);
        }
        return null;
    }

    @Override
    public boolean createTunnelPolicy(TunnelPolicy tunnelPolicy, NetconfClient netconfClient) {
        boolean isCreate = false;
        if (null != tunnelPolicy) {
            List<TunnelPolicy> tunnelPolicies = new ArrayList<TunnelPolicy>();
            tunnelPolicies.add(tunnelPolicy);
            isCreate = this.createTunnelPolicyListToDevice(tunnelPolicies, netconfClient);
            if (isCreate) {
                if (tunnelPolicyMaps.containsKey(tunnelPolicy.getRouterID())) {
                    tunnelPolicyMaps.get(tunnelPolicy.getRouterID()).put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
                } else {
                    tunnelPolicyMap = new HashMap<>();
                    tunnelPolicyMap.put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
                    tunnelPolicyMaps.put(tunnelPolicy.getRouterID(), tunnelPolicyMap);
                }
            }
        }
        return isCreate;
    }

    @Override
    public boolean createTunnelPolicyList(List<TunnelPolicy> tunnelPolicies, NetconfClient netconfClient) {
        boolean isCreate = false;
        if (null != tunnelPolicies && !tunnelPolicies.isEmpty()) {
            isCreate = this.createTunnelPolicyListToDevice(tunnelPolicies, netconfClient);
            if (isCreate) {
                for (TunnelPolicy tp : tunnelPolicies) {
                    if (tunnelPolicyMaps.containsKey(tp.getRouterID())) {
                        tunnelPolicyMaps.get(tp.getRouterID()).put(tp.getTnlPolicyName(), tp);
                    } else {
                        tunnelPolicyMap = new HashMap<>();
                        tunnelPolicyMap.put(tp.getTnlPolicyName(), tp);
                        tunnelPolicyMaps.put(tp.getRouterID(), tunnelPolicyMap);
                    }
                }
            }
        }
        return isCreate;
    }

    @Override
    public boolean deleteTunnelPolicyByName(String name, NetconfClient netconfClient) {
        boolean isDelete = false;
        if (null != name) {

            // create a list
            List<String> names = new ArrayList<String>();
            names.add(name);

            isDelete = this.deleteTunnelPolicyFromDeviceByNameList(names, netconfClient);
            if (isDelete) {
                this.tunnelPolicyMap.remove(name);
            }
        } else {
            isDelete = true;
        }
        return isDelete;
    }

    @Override
    public boolean deleteTunnelPolicyByNameList(List<String> names, NetconfClient netconfClient, String routerId) {
        boolean isDelete = false;
        if (null != names && !names.isEmpty()) {
            isDelete = this.deleteTunnelPolicyFromDeviceByNameList(names, netconfClient);
            if (isDelete) {
                for (String name : names) {
                    if (tunnelPolicyMaps.containsKey(routerId)) {
                        tunnelPolicyMaps.get(routerId).remove(name);
                    }
                }
            }
        } else {
            isDelete = true;
        }
        return isDelete;
    }

    @Override
    public boolean syncTunnelPolicyConf(String routerID, NetconfClient netconfClient) {
        if (null == netconfClient) {
            return false;
        }
        String commandGetTunnelPolicyXml = TunnelPolicyXml.getTunnelPolicyXml();
        LOG.info("commandGetTunnelPolicyXml: " + commandGetTunnelPolicyXml);
        String outPutTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandGetTunnelPolicyXml);
        LOG.info("outPutSrNodeLabelXml: " + outPutTunnelPolicyXml);
        List<STunnelPolicy> sTunnelPolicyList = TunnelPolicyXml.getSTunnelPolicyFromXml(outPutTunnelPolicyXml);

        if (null == sTunnelPolicyList) {
            LOG.info("can not get TunnelPolicy info from device: routerID=" + routerID);
            return false;
        }
        for (STunnelPolicy sTunnelPolicy : sTunnelPolicyList) {
            TunnelPolicy tunnelPolicy = sTunnelPolicyToTunnelPolicy(sTunnelPolicy, routerID);
            if (null == tunnelPolicy) {
                break;
            }
            if (tunnelPolicyMaps.containsKey(routerID)) {
                tunnelPolicyMaps.get(routerID).put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
            } else {
                tunnelPolicyMap = new HashMap<>();
                tunnelPolicyMap.put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
                tunnelPolicyMaps.put(routerID, tunnelPolicyMap);
            }

        }
        return true;
    }

    @Override
    public boolean isNameDuplicate(String name) {
        if (null != name) {

            return this.tunnelPolicyMap.containsKey(name);
        } else {
            return false;
        }
    }

    private TunnelPolicy sTunnelPolicyToTunnelPolicy(STunnelPolicy sTunnelPolicy, String routerID) {
        TunnelPolicy tunnelPolicy = new TunnelPolicy();
        tunnelPolicy.setRouterID(routerID);
        if (null == sTunnelPolicy.getTnlPolicyName() || sTunnelPolicy.getTnlPolicyName().isEmpty()) {
            return null;
        }
        tunnelPolicy.setTnlPolicyName(sTunnelPolicy.getTnlPolicyName());
        tunnelPolicy.setDescription(sTunnelPolicy.getDescription());
        //tunnelPolicy.setTnlPolicyType(sTunnelPolicy.getTnlPolicyType());
        if (sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.Invalid.getName())) {
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.Invalid.getCode());
        } else if (sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.TnlBinding.getName())) {//解析tpNexthops
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlBinding.getCode());
            for (STpNexthop sTpNexthop : sTunnelPolicy.getSTpNexthops()) {
                TpNexthop tpNexthop = sTpNexthopToTpNexthop(sTpNexthop, tunnelPolicy.getTnlPolicyName());
                if (null == tpNexthop) {
                    break;
                }
                tunnelPolicy.getTpNexthops().add(tpNexthop);
            }
        } else if (sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.TnlSelectSeq.getName())) {//解析tnlSelSeqs
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlSelectSeq.getCode());
            for (STnlSelSeq sTnlSelSeq : sTunnelPolicy.getSTnlSelSeqls()) {
                TnlSelSeq tnlSelSeq1 = sTnlSelSeqToTnlSelSeq(sTnlSelSeq, tunnelPolicy.getTnlPolicyName());
                tunnelPolicy.getTnlSelSeqls().add(tnlSelSeq1);
            }
        }

        return tunnelPolicy;
    }

    private TpNexthop sTpNexthopToTpNexthop(STpNexthop sTpNexthop, String tnlPolicyName) {
        TpNexthop tpNexthop = new TpNexthop();
        if (null == sTpNexthop.getNexthopIPaddr() || sTpNexthop.getNexthopIPaddr().isEmpty()) {
            return null;
        }
        tpNexthop.setTnlPolicyName(tnlPolicyName);
        tpNexthop.setNexthopIPaddr(sTpNexthop.getNexthopIPaddr());
        tpNexthop.setDownSwitch(sTpNexthop.isDownSwitch());
        tpNexthop.setIgnoreDestCheck(sTpNexthop.isIgnoreDestCheck());
        tpNexthop.setIncludeLdp(sTpNexthop.isIncludeLdp());
        tpNexthop.setTpTunnels(sTpNexthop.getTpTunnels());

        return tpNexthop;
    }

    private TnlSelSeq sTnlSelSeqToTnlSelSeq(STnlSelSeq sTnlSelSeq, String tnlPolicyName) {
        TnlSelSeq tnlSelSeq = new TnlSelSeq();
        tnlSelSeq.setTnlPolicyName(tnlPolicyName);
        tnlSelSeq.setSelTnlType1(sTnlSelSeq.getSelTnlType1());
        tnlSelSeq.setSelTnlType2(sTnlSelSeq.getSelTnlType2());
        tnlSelSeq.setSelTnlType3(sTnlSelSeq.getSelTnlType3());
        tnlSelSeq.setSelTnlType4(sTnlSelSeq.getSelTnlType4());
        tnlSelSeq.setSelTnlType5(sTnlSelSeq.getSelTnlType5());
        tnlSelSeq.setLoadBalanceNum(sTnlSelSeq.getLoadBalanceNum());
        tnlSelSeq.setUnmix(sTnlSelSeq.isUnmix());
        return tnlSelSeq;
    }

    private List<STunnelPolicy> tunnelPolicyListToSTunnelPolicyList(List<TunnelPolicy> tunnelPolicies) {
        List<STunnelPolicy> sTunnelPolicies = null;
        if (null != tunnelPolicies && !tunnelPolicies.isEmpty()) {
            sTunnelPolicies = new ArrayList<STunnelPolicy>();

            for (TunnelPolicy t : tunnelPolicies) {
                STunnelPolicy st = null;
                st = this.tunnelPolicyToSTunnelPolicy(t);
                if (st != null) {
                    sTunnelPolicies.add(st);
                }
            }
        }
        return sTunnelPolicies;
    }

    private STunnelPolicy tunnelPolicyToSTunnelPolicy(TunnelPolicy tunnelPolicy) {
        STunnelPolicy sTunnelPolicy = null;

        if (null == tunnelPolicy || null == tunnelPolicy.getTnlPolicyName() || tunnelPolicy.getTnlPolicyName().isEmpty()) {
            return null;
        }

        sTunnelPolicy = new STunnelPolicy();

        sTunnelPolicy.setTnlPolicyName(tunnelPolicy.getTnlPolicyName());
        sTunnelPolicy.setDescription(tunnelPolicy.getDescription());
        for (TpNexthop tpNexthop : tunnelPolicy.getTpNexthops()) {

            STpNexthop sTpNexthop = this.tpNexthopToSTpNexthop(tpNexthop);
            if (null == tpNexthop) {
                break;
            }
            sTunnelPolicy.getSTpNexthops().add(sTpNexthop);
        }
        return sTunnelPolicy;
    }

    private STpNexthop tpNexthopToSTpNexthop(TpNexthop tpNexthop) {
        STpNexthop sTpNexthop = null;
        if (null != tpNexthop) {
            sTpNexthop = new STpNexthop();
            sTpNexthop.setDownSwitch(tpNexthop.isDownSwitch());
            sTpNexthop.setIgnoreDestCheck(tpNexthop.isIgnoreDestCheck());
            sTpNexthop.setIncludeLdp(tpNexthop.isIncludeLdp());
            sTpNexthop.setNexthopIPaddr(tpNexthop.getNexthopIPaddr());
            sTpNexthop.setTpTunnels(tpNexthop.getTpTunnels());
        }
        return sTpNexthop;
    }

    private STnlSelSeq tnlSelSeqToSTnlSelSeq(TnlSelSeq tnlSelSeq) {
        STnlSelSeq sTnlSelSeq = null;
        if (null != tnlSelSeq) {
            sTnlSelSeq = new STnlSelSeq();
            sTnlSelSeq.setSelTnlType1(tnlSelSeq.getSelTnlType1());
            sTnlSelSeq.setSelTnlType2(tnlSelSeq.getSelTnlType2());
            sTnlSelSeq.setSelTnlType3(tnlSelSeq.getSelTnlType3());
            sTnlSelSeq.setSelTnlType4(tnlSelSeq.getSelTnlType4());
            sTnlSelSeq.setSelTnlType5(tnlSelSeq.getSelTnlType5());
            sTnlSelSeq.setUnmix(tnlSelSeq.isUnmix());
            sTnlSelSeq.setLoadBalanceNum(tnlSelSeq.getLoadBalanceNum());
        }
        return sTnlSelSeq;
    }

    private boolean createTunnelPolicyListToDevice(List<TunnelPolicy> tunnelPolicies, NetconfClient netconfClient) {

        Map<String, List<String>> map;
        Map<String, Map<String, List<String>>> maps = new HashMap<>();
        Map<String, List<String>> nexthopIPaddrMap = new HashMap<>();
        TunnelPolicy tp;
        List<String> stringList;
        List<String> nexthopIPaddrList;
        boolean flag = false;
        List<STunnelPolicy> sTunnelPolicies = this.tunnelPolicyListToSTunnelPolicyList(tunnelPolicies);
        for (TunnelPolicy tunnelPolicy : tunnelPolicies) {
            if (tunnelPolicyMaps.containsKey(tunnelPolicy.getRouterID())) {
                if (tunnelPolicyMaps.get(tunnelPolicy.getRouterID()).containsKey(tunnelPolicy.getTnlPolicyName())) {
                    tp = tunnelPolicyMaps.get(tunnelPolicy.getRouterID()).get(tunnelPolicy.getTnlPolicyName());
                    if (tp.getTpNexthops().size() > 0) {
                        for (TpNexthop tpNexthop : tp.getTpNexthops()) {
                            nexthopIPaddrList = new ArrayList<>();
                            if (tunnelPolicy.getTpNexthops().size() > 0) {
                                for (TpNexthop tpNexthop1 : tunnelPolicy.getTpNexthops()) {
                                    flag = false;
                                    if (tpNexthop1.getNexthopIPaddr().equals(tpNexthop.getNexthopIPaddr())) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    if (nexthopIPaddrMap.containsKey(tp.getTnlPolicyName())) {
                                        nexthopIPaddrMap.get(tp.getTnlPolicyName()).add(tpNexthop.getNexthopIPaddr());
                                    } else {
                                        nexthopIPaddrList.add(tpNexthop.getNexthopIPaddr());
                                        nexthopIPaddrMap.put(tp.getTnlPolicyName(), nexthopIPaddrList);
                                    }
                                    flag = false;
                                } else {
                                    for (String string : tpNexthop.getTpTunnels()) {
                                        stringList = new ArrayList<>();
                                        for (TpNexthop tpNexthop1 : tunnelPolicy.getTpNexthops()) {
                                            if (!(tpNexthop1.getTpTunnels().contains(string))) {
                                                stringList.add(string);
                                            }
                                        }
                                        if (stringList.size() > 0) {
                                            if (maps.containsKey(tpNexthop.getTnlPolicyName())) {
                                                maps.get(tpNexthop.getTnlPolicyName()).put(tpNexthop.getNexthopIPaddr(), stringList);
                                            } else {
                                                map = new HashMap<>();
                                                map.put(tpNexthop.getNexthopIPaddr(), stringList);
                                                maps.put(tpNexthop.getTnlPolicyName(), map);
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (nexthopIPaddrMap.containsKey(tp.getTnlPolicyName())) {
                                    nexthopIPaddrMap.get(tp.getTnlPolicyName()).add(tpNexthop.getNexthopIPaddr());
                                } else {
                                    nexthopIPaddrList.add(tpNexthop.getNexthopIPaddr());
                                    nexthopIPaddrMap.put(tp.getTnlPolicyName(), nexthopIPaddrList);
                                }
                            }
                            LOG.info("nexthopIPaddrMap.toString()  :" + nexthopIPaddrMap.toString());

                        }
                    }
                }
            }
        }
        String commandCreateTunnelPolicyXml = TunnelPolicyXml.createTunnelPolicyXml(sTunnelPolicies, nexthopIPaddrMap, maps);
        LOG.info("CommandCreateTunnelPolicyXml: " + commandCreateTunnelPolicyXml);

        String outPutTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandCreateTunnelPolicyXml);
        LOG.info("OutPutTunnelPolicyXml: " + outPutTunnelPolicyXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutTunnelPolicyXml));
    }


    private boolean deleteTunnelPolicyFromDeviceByNameList(List<String> names, NetconfClient netconfClient) {

        String commandDeleteTunnelPolicyXml = TunnelPolicyXml.deleteTunnelPolicyXml(names);
        LOG.info("CommandDeleteTunnelPolicyXml: " + commandDeleteTunnelPolicyXml);

        String outPutDeleteTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandDeleteTunnelPolicyXml);
        LOG.info("OutPutDeleteTunnelPolicyXml: " + outPutDeleteTunnelPolicyXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutDeleteTunnelPolicyXml));
    }
}
