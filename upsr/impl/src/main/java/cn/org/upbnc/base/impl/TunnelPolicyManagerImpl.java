package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.*;
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

import java.util.ArrayList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelPolicyManagerImpl implements TunnelPolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(VpnInstanceManagerImpl.class);
    private static TunnelPolicyManager instance = null;
    private List<TunnelPolicy> tunnelPolicyList = null;

    public TunnelPolicyManagerImpl() {
        this.tunnelPolicyList = new ArrayList<TunnelPolicy>();
    }

    public TunnelPolicyManagerImpl(List<TunnelPolicy> tunnelPolicyList) {
        this.tunnelPolicyList = tunnelPolicyList;
    }

    public static TunnelPolicyManager getInstance() {
        if (null == instance) {
            instance = new TunnelPolicyManagerImpl();
        }
        return instance;
    }

    @Override
    public List<TunnelPolicy> getAllTunnelPolicys(String routerID) {
        List<TunnelPolicy> tunnelPolicyList=new ArrayList<TunnelPolicy>();
        if(null==this.tunnelPolicyList){
            return tunnelPolicyList;
        }
        for(TunnelPolicy t :this.tunnelPolicyList){
            if(t.getRouterID().equals(routerID)){
                tunnelPolicyList.add(t);
            }
        }
        return tunnelPolicyList;
    }

    public List<TunnelPolicy> getAllTunnelPolicys() {
        List<TunnelPolicy> tunnelPolicyList=new ArrayList<TunnelPolicy>();
        if(null==this.tunnelPolicyList){
            return tunnelPolicyList;
        }
        return this.tunnelPolicyList;

    }

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
            this.tunnelPolicyList.add(tunnelPolicy);
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
}
