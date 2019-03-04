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
    public boolean createTunnelPolicy(TunnelPolicy tunnelPolicy,NetconfClient netconfClient){
        boolean isCreate = false;
        if(null != tunnelPolicy) {

            // create a list
            List<TunnelPolicy> tunnelPolicies = new ArrayList<TunnelPolicy>();
            tunnelPolicies.add(tunnelPolicy);

            isCreate = this.createTunnelPolicyListToDevice(tunnelPolicies,netconfClient);
            if(isCreate) {
                this.tunnelPolicyMap.put(tunnelPolicy.getTnlPolicyName(), tunnelPolicy);
            }
        }
        return isCreate;
    }

    @Override
    public boolean createTunnelPolicyList(List<TunnelPolicy> tunnelPolicies,NetconfClient netconfClient){
        boolean isCreate = false;
        if(null != tunnelPolicies && !tunnelPolicies.isEmpty()){
            isCreate = this.createTunnelPolicyListToDevice(tunnelPolicies,netconfClient);
            if(isCreate){
                for(TunnelPolicy tp : tunnelPolicies){
                    this.tunnelPolicyMap.put(tp.getTnlPolicyName(),tp);
                }
            }
        }

        return isCreate;
    };

    @Override
    public boolean deleteTunnelPolicyByName(String name,NetconfClient netconfClient){
        boolean isDelete = false;
        if(null!= name){

            // create a list
            List<String> names = new ArrayList<String>();
            names.add(name);

            isDelete = this.deleteTunnelPolicyFromDeviceByNameList(names,netconfClient);
            if(isDelete) {
                this.tunnelPolicyMap.remove(name);
            }
        }else{
            isDelete = true;
        }
        return isDelete;
    }

    @Override
    public boolean deleteTunnelPolicyByNameList(List<String> names,NetconfClient netconfClient){
        boolean isDelete = false;
        if( null !=  names && !names.isEmpty()){
            isDelete = this.deleteTunnelPolicyFromDeviceByNameList(names,netconfClient);
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
    public boolean syncTunnelPolicyConf(String routerID,NetconfClient netconfClient) {
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

    @Override
    public boolean isNameDuplicate(String name){
        if(null != name) {
            return this.tunnelPolicyMap.containsKey(name);
        }else{
            return false;
        }
    }

    private TunnelPolicy sTunnelPolicyToTunnelPolicy(STunnelPolicy sTunnelPolicy,String routerID){
        TunnelPolicy tunnelPolicy=new TunnelPolicy();
        tunnelPolicy.setRouterID(routerID);
        if(null==sTunnelPolicy.getTnlPolicyName()||sTunnelPolicy.getTnlPolicyName().isEmpty()){
            return null;
        }
        tunnelPolicy.setTnlPolicyName(sTunnelPolicy.getTnlPolicyName());
        tunnelPolicy.setDescription(sTunnelPolicy.getDescription());
        //tunnelPolicy.setTnlPolicyType(sTunnelPolicy.getTnlPolicyType());
        if(sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.Invalid.getName())){
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.Invalid.getCode());
        }else if(sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.TnlBinding.getName())){//解析tpNexthops
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlBinding.getCode());
            for(STpNexthop sTpNexthop:sTunnelPolicy.getSTpNexthops()){
                TpNexthop tpNexthop=sTpNexthopToTpNexthop(sTpNexthop,tunnelPolicy.getTnlPolicyName());
                if(null==tpNexthop){
                    break;
                }
                tunnelPolicy.getTpNexthops().add(tpNexthop);
            }
        }else if(sTunnelPolicy.getTnlPolicyType().equals(TnlPolicyTypeEnum.TnlSelectSeq.getName())){//解析tnlSelSeqs
            tunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlSelectSeq.getCode());
            for(STnlSelSeq sTnlSelSeq:sTunnelPolicy.getSTnlSelSeqls()){
                TnlSelSeq tnlSelSeq1=sTnlSelSeqToTnlSelSeq(sTnlSelSeq,tunnelPolicy.getTnlPolicyName());
                tunnelPolicy.getTnlSelSeqls().add(tnlSelSeq1);
            }
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

    private  List<STunnelPolicy> tunnelPolicyListToSTunnelPolicyList(List<TunnelPolicy> tunnelPolicies){
        List<STunnelPolicy> sTunnelPolicies = null;
        if(null != tunnelPolicies && !tunnelPolicies.isEmpty()){
            sTunnelPolicies = new ArrayList<STunnelPolicy>();

            for(TunnelPolicy t : tunnelPolicies){
                STunnelPolicy st = null;
                st = this.tunnelPolicyToSTunnelPolicy(t);
                if(st != null ){
                    sTunnelPolicies.add(st);
                }
            }
        }
        return sTunnelPolicies;
    }

    private STunnelPolicy tunnelPolicyToSTunnelPolicy(TunnelPolicy tunnelPolicy){
        STunnelPolicy sTunnelPolicy = null;

        if(null == tunnelPolicy || null==tunnelPolicy.getTnlPolicyName()|| tunnelPolicy.getTnlPolicyName().isEmpty()){
            return null;
        }

        sTunnelPolicy = new STunnelPolicy();

        sTunnelPolicy.setTnlPolicyName(tunnelPolicy.getTnlPolicyName());
        sTunnelPolicy.setDescription(tunnelPolicy.getDescription());
        //tunnelPolicy.setTnlPolicyType(sTunnelPolicy.getTnlPolicyType());
        if(tunnelPolicy.getTnlPolicyType() == TnlPolicyTypeEnum.Invalid.getCode()){
            sTunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.Invalid.getName());
        }else if(tunnelPolicy.getTnlPolicyType() == TnlPolicyTypeEnum.TnlBinding.getCode()){//解析tpNexthops
            sTunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlBinding.getName());

            for(TpNexthop tpNexthop:tunnelPolicy.getTpNexthops()){

                STpNexthop sTpNexthop= this.tpNexthopToSTpNexthop(tpNexthop);
                if(null==tpNexthop){
                    break;
                }
                sTunnelPolicy.getSTpNexthops().add(sTpNexthop);
            }
        }else if(tunnelPolicy.getTnlPolicyType() == TnlPolicyTypeEnum.TnlSelectSeq.getCode()){//解析tnlSelSeqs
            sTunnelPolicy.setTnlPolicyType(TnlPolicyTypeEnum.TnlSelectSeq.getName());

            for(TnlSelSeq tnlSelSeq:tunnelPolicy.getTnlSelSeqls()){

                STnlSelSeq stnlSelSeq= this.tnlSelSeqToSTnlSelSeq(tnlSelSeq);
                sTunnelPolicy.getSTnlSelSeqls().add(stnlSelSeq);
            }
        }

        return sTunnelPolicy;
    }

    private STpNexthop tpNexthopToSTpNexthop(TpNexthop tpNexthop){
        STpNexthop sTpNexthop = null;
        if(null != tpNexthop){
            sTpNexthop = new STpNexthop();
            sTpNexthop.setDownSwitch(tpNexthop.isDownSwitch());
            sTpNexthop.setIgnoreDestCheck(tpNexthop.isIgnoreDestCheck());
            sTpNexthop.setIncludeLdp(tpNexthop.isIncludeLdp());
            sTpNexthop.setNexthopIPaddr(tpNexthop.getNexthopIPaddr());
            sTpNexthop.setTpTunnels(tpNexthop.getTpTunnels());
        }
        return sTpNexthop;
    }

    private STnlSelSeq tnlSelSeqToSTnlSelSeq(TnlSelSeq tnlSelSeq){
        STnlSelSeq sTnlSelSeq = null;
        if(null != tnlSelSeq){
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

    private boolean createTunnelPolicyListToDevice(List<TunnelPolicy> tunnelPolicies,NetconfClient netconfClient){

        List<STunnelPolicy> sTunnelPolicies = this.tunnelPolicyListToSTunnelPolicyList(tunnelPolicies);

        String commandCreateTunnelPolicyXml = TunnelPolicyXml.createTunnelPolicyXml(sTunnelPolicies);
        LOG.info("CommandCreateTunnelPolicyXml: " + commandCreateTunnelPolicyXml);

        String outPutTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandCreateTunnelPolicyXml);
        LOG.info("OutPutTunnelPolicyXml: " + outPutTunnelPolicyXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutTunnelPolicyXml));
    }


    private boolean deleteTunnelPolicyFromDeviceByNameList(List<String> names,NetconfClient netconfClient){

        String commandDeleteTunnelPolicyXml = TunnelPolicyXml.deleteTunnelPolicyXml(names);
        LOG.info("CommandDeleteTunnelPolicyXml: " + commandDeleteTunnelPolicyXml);

        String outPutDeleteTunnelPolicyXml = netconfController.sendMessage(netconfClient, commandDeleteTunnelPolicyXml);
        LOG.info("OutPutDeleteTunnelPolicyXml: " + outPutDeleteTunnelPolicyXml);

        return CheckXml.RESULT_OK.equals(CheckXml.checkOk(outPutDeleteTunnelPolicyXml));
    }
}
