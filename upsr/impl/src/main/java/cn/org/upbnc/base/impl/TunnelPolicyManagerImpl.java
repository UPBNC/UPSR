package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.*;
import cn.org.upbnc.entity.Tunnel;
import cn.org.upbnc.entity.TunnelPolicy.TnlSelSeq;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TunnelPolicy.STnlSelSeq;
import cn.org.upbnc.util.netconf.TunnelPolicy.STpNexthop;
import cn.org.upbnc.util.netconf.TunnelPolicy.STunnelPolicy;
import cn.org.upbnc.util.xml.TunnelPolicyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelPolicyManagerImpl implements TunnelPolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceManagerImpl.class);
    private static TunnelPolicyManager instance = null;
//    private List<TunnelPolicy> tunnelPolicyList = null;
    private Map<String,TunnelPolicy> tunnelPolicyMap;

    private TunnelPolicyManagerImpl() {
        this.tunnelPolicyMap = new HashMap<String,TunnelPolicy>();
    }

//    public TunnelPolicyManagerImpl(List<TunnelPolicy> tunnelPolicyList) {
//        this.tunnelPolicyList = tunnelPolicyList;
//    }

    public static TunnelPolicyManager getInstance() {
        if (null == instance) {
            instance = new TunnelPolicyManagerImpl();
        }
        return instance;
    }

    @Override
    public List<TunnelPolicy> getAllTunnelPolicys(String routerID) {
        if(this.tunnelPolicyMap.isEmpty()){
            return null;
        }
        List<TunnelPolicy> tunnelPolicyList=new ArrayList<TunnelPolicy>();
        Collection<TunnelPolicy> collection = this.tunnelPolicyMap.values();
        for(TunnelPolicy t : collection){
            if(t.getRouterID().equals(routerID)){
                tunnelPolicyList.add(t);
            }
        }
        return tunnelPolicyList;
    }

    @Override
    public List<TunnelPolicy> getAllTunnelPolicys() {
        return new ArrayList<>(this.tunnelPolicyMap.values());

    }

    @Override
    public TunnelPolicy getTunnelPolicy(String name){
//        TunnelPolicy tunnelPolicyList
        if(null != name) {
            return this.tunnelPolicyMap.get(name);
        }
        return null;
    }

    @Override
    public boolean createTunnelPolicy(TunnelPolicy tunnelPolicy){
        boolean isCreate = false;
        if(null != tunnelPolicy) {
            isCreate = this.createTunnelPolicyToDevice(tunnelPolicy);
            if(isCreate) {
                this.tunnelPolicyMap.put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
            }
        }
        return isCreate;
    }

    @Override
    public boolean createTunnelPolicyList(List<TunnelPolicy> tunnelPolicies){
        boolean isCreate = false;
        if(null != tunnelPolicies && !tunnelPolicies.isEmpty()){
            isCreate = this.createTunnelPolicyListToDevice(tunnelPolicies);
            if(isCreate){
                for(TunnelPolicy tp : tunnelPolicies){
                    this.tunnelPolicyMap.put(tp.getTnlPolicyName(),tp);
                }
            }
        }

        return isCreate;
    };

    @Override
    public boolean deleteTunnelPolicyByName(String name){
        boolean isDelete = false;
        if(null!= name){
            isDelete = deleteTunnelPolicyFromDeviceByName(name);
            if(isDelete) {
                this.tunnelPolicyMap.remove(name);
            }
        }else{
            isDelete = true;
        }
        return isDelete;
    }

    @Override
    public boolean deleteTunnelPolicyByNameList(List<String> names){
        boolean isDelete = false;
        if( null !=  names && !names.isEmpty()){
            isDelete = this.deleteTunnelPolicyFromDeviceByNameList(names);
            if(isDelete){
                for (String name:names){
                    this.tunnelPolicyMap.remove(name);
                }
            }

        }else{
            isDelete = true;
        }

        return isDelete;
    }

    @Override
    public boolean deleteTunnelPolicy(TunnelPolicy tunnelPolicy){
        boolean isDelete = false;
        if(null!= tunnelPolicy){
            isDelete = deleteTunnelPolicyFromDevice(tunnelPolicy);
            if(isDelete) {
                this.tunnelPolicyMap.remove(tunnelPolicy.getTnlPolicyName());
            }
        }
        return isDelete;
    }

    @Override
    public boolean syncTunnelPolicyConf(NetconfClient netconfClient,String routerID) {
        if(null==netconfClient){
            return  false;
        }
        String commandGetTunnelPolicyXml = TunnelPolicyXml.getTunnelPolicyXml();
        LOG.info("commandGetTunnelPolicyXml: " + commandGetTunnelPolicyXml);
        String outPutTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandGetTunnelPolicyXml);
        LOG.info("outPutSrNodeLabelXml: " + outPutTunnelPolicyXml);
        List<STunnelPolicy> sTunnelPolicyList = TunnelPolicyXml.getSTunnelPolicyFromXml(outPutTunnelPolicyXml);

        if (null==sTunnelPolicyList) {
            LOG.info("can not get TunnelPolicy info from device: routerID="+routerID);
            return false;
        }
        for(STunnelPolicy sTunnelPolicy : sTunnelPolicyList){
            TunnelPolicy tunnelPolicy = sTunnelPolicyToTunnelPolicy(sTunnelPolicy,routerID);
            if(null==tunnelPolicy){
                break;
            }
            //this.tunnelPolicyList.add(tunnelPolicy);
            this.tunnelPolicyMap.put(tunnelPolicy.getTnlPolicyName(),tunnelPolicy);
        }
        return true;
    }

    private TunnelPolicy sTunnelPolicyToTunnelPolicy(STunnelPolicy sTunnelPolicy,String routerID){
        TunnelPolicy tunnelPolicy=new TunnelPolicy();
        tunnelPolicy.setRouterID(routerID);
        if(null==sTunnelPolicy.getTnlPolicyName()||sTunnelPolicy.getTnlPolicyName().isEmpty()){
            return null;
        }
        tunnelPolicy.setTnlPolicyName(sTunnelPolicy.getTnlPolicyName());
        tunnelPolicy.setDescription(sTunnelPolicy.getDescription());
        tunnelPolicy.setTnlPolicyType(sTunnelPolicy.getTnlPolicyType());
        if(tunnelPolicy.getTnlPolicyType().equals("invalid")){
            return tunnelPolicy;
        }
        if(tunnelPolicy.getTnlPolicyType().equals("tnlBinding")){//解析tpNexthops
            for(STpNexthop sTpNexthop:sTunnelPolicy.getSTpNexthops()){
                TpNexthop tpNexthop=sTpNexthopToTpNexthop(sTpNexthop,tunnelPolicy.getTnlPolicyName());
                if(null==tpNexthop){
                    break;
                }
                tunnelPolicy.getTpNexthops().add(tpNexthop);
            }
            return tunnelPolicy;
        }
        if(tunnelPolicy.getTnlPolicyType().equals("tnlSelectSeq")){//解析tnlSelSeqs
            for(STnlSelSeq sTnlSelSeq:sTunnelPolicy.getSTnlSelSeqls()){
                TnlSelSeq tnlSelSeq1=sTnlSelSeqToTnlSelSeq(sTnlSelSeq,tunnelPolicy.getTnlPolicyName());
                tunnelPolicy.getTnlSelSeqls().add(tnlSelSeq1);
            }
            return tunnelPolicy;
        }
        return tunnelPolicy;
    }

    private TpNexthop sTpNexthopToTpNexthop(STpNexthop sTpNexthop,String tnlPolicyName){
        TpNexthop tpNexthop=new TpNexthop();
        if(null==sTpNexthop.getNexthopIPaddr()||sTpNexthop.getNexthopIPaddr().isEmpty()){
            return null;
        }
        tpNexthop.setTnlPolicyName(tnlPolicyName);
        tpNexthop.setNexthopIPaddr(sTpNexthop.getNexthopIPaddr());
        tpNexthop.setDownSwitch(sTpNexthop.isDownSwitch());
        tpNexthop.setIgnoreDestCheck(sTpNexthop.isIgnoreDestCheck());
        tpNexthop.setIncludeLdp(sTpNexthop.isIncludeLdp());
        tpNexthop.setTpTunnels(sTpNexthop.getTpTunnels());

        return  tpNexthop;
    }

    private TnlSelSeq sTnlSelSeqToTnlSelSeq(STnlSelSeq sTnlSelSeq,String tnlPolicyName){
        TnlSelSeq tnlSelSeq=new TnlSelSeq();
        tnlSelSeq.setTnlPolicyName(tnlPolicyName);
        tnlSelSeq.setSelTnlType1(sTnlSelSeq.getSelTnlType1());
        tnlSelSeq.setSelTnlType2(sTnlSelSeq.getSelTnlType2());
        tnlSelSeq.setSelTnlType3(sTnlSelSeq.getSelTnlType3());
        tnlSelSeq.setSelTnlType4(sTnlSelSeq.getSelTnlType4());
        tnlSelSeq.setSelTnlType5(sTnlSelSeq.getSelTnlType5());
        tnlSelSeq.setLoadBalanceNum(sTnlSelSeq.getLoadBalanceNum());
        tnlSelSeq.setUnmix(sTnlSelSeq.isUnmix());
        return  tnlSelSeq;
    }

    private boolean createTunnelPolicyToDevice(TunnelPolicy tunnelPolicy){
        boolean ret = false;
        return ret;
    }

    private boolean createTunnelPolicyListToDevice(List<TunnelPolicy> tunnelPolicies){
        boolean ret = false;

        return ret;
    }

    private boolean deleteTunnelPolicyFromDeviceByName(String name){
        boolean ret = false;
        return ret;
    }

    private boolean deleteTunnelPolicyFromDevice(TunnelPolicy tunnelPolicy){
        boolean ret = false;
        return ret;
    }

    private boolean deleteTunnelPolicyFromDeviceByNameList(List<String> names){
        boolean ret = false;
        return ret;
    }
}
