package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.*;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.TunnelPolicy.TnlSelSeq;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.service.TunnelPolicyService;
import cn.org.upbnc.service.VPNService;
import cn.org.upbnc.service.entity.TunnelPolicy.TnlSelSeqEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TpNexthopEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TunnelPolicy.STunnelPolicy;
import cn.org.upbnc.util.xml.TunnelPolicyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TunnelPolicyServiceImpl implements TunnelPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static TunnelPolicyService ourInstance = null;
    private BaseInterface baseInterface;
    private TunnelPolicyManager tunnelPolicyManager;
    private NetConfManager netConfManager ;
    private DeviceManager deviceManager;

    public static TunnelPolicyService getInstance() {
        if (null == ourInstance) {
            ourInstance = new TunnelPolicyServiceImpl();
        }
        return ourInstance;
    }

    private TunnelPolicyServiceImpl() {
        this.tunnelPolicyManager = null;
        this.netConfManager = null;
        this.baseInterface = null;
        this.deviceManager=null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.tunnelPolicyManager = this.baseInterface.getTunnelPolicyManager();
            this.deviceManager=this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public boolean syncTunnelPolicyConf(){
        boolean ret=true;
        for(Device device:deviceManager.getDeviceList()){
            if(!syncTunnelPolicyConf(device.getRouterId())){
                LOG.info("syncTunnelPolicyConf failed,routerId:"+device.getRouterId());
                ret=false;
            }
        }
        return ret;
    }

    public boolean syncTunnelPolicyConf(String routerID) {
        NetconfClient netconfClient = netConfManager.getNetconClient(routerID);
        if(null==netconfClient){
            return  false;
        }
        return tunnelPolicyManager.syncTunnelPolicyConf(netconfClient,routerID);
    }

    public  List<TunnelPolicyEntity> getTunnelPolicys(String routerId){
        List<TunnelPolicyEntity> tunnelPolicyEntityList =new ArrayList<TunnelPolicyEntity>();
        List<TunnelPolicy> tunnelPolicyList=new ArrayList<TunnelPolicy>();
        if(null==routerId||routerId.isEmpty()){
            tunnelPolicyList=tunnelPolicyManager.getAllTunnelPolicys();
        }else{
            tunnelPolicyList=tunnelPolicyManager.getAllTunnelPolicys(routerId);
        }
        if(null==tunnelPolicyList){
            return tunnelPolicyEntityList;
        }
        for(TunnelPolicy tunnelPolicy : tunnelPolicyList){
            TunnelPolicyEntity tunnelPolicyEntity=tunnelPolicyToTunnelPolicyEntity(tunnelPolicy);
            if(null!=tunnelPolicyEntity){
                tunnelPolicyEntityList.add(tunnelPolicyEntity);
            }
        }
        return tunnelPolicyEntityList;
    }



    private TunnelPolicyEntity tunnelPolicyToTunnelPolicyEntity(TunnelPolicy tunnelPolicy){
        TunnelPolicyEntity tunnelPolicyEntity=new TunnelPolicyEntity();

        if(null==tunnelPolicy.getTnlPolicyName()||tunnelPolicy.getTnlPolicyName().isEmpty()){
            return null;
        }
        if(null==tunnelPolicy.getRouterID()||tunnelPolicy.getRouterID().isEmpty()){
            return null;
        }
        tunnelPolicyEntity.setRouterID(tunnelPolicy.getRouterID());
        tunnelPolicyEntity.setTnlPolicyName(tunnelPolicy.getTnlPolicyName());
        tunnelPolicyEntity.setDescription(tunnelPolicy.getDescription());
        tunnelPolicyEntity.setTnlPolicyType(tunnelPolicy.getTnlPolicyType());
        if(tunnelPolicyEntity.getTnlPolicyType().equals("invalid")){
            return tunnelPolicyEntity;
        }
        if(tunnelPolicyEntity.getTnlPolicyType().equals("tnlBinding")){//解析tpNexthops
            for(TpNexthop tpNexthop:tunnelPolicy.getTpNexthops()){
                TpNexthopEntity tpNexthopEntity=tpNexthopToTpNexthopEntity(tpNexthop);
                if(null==tpNexthopEntity){
                    break;
                }
                tunnelPolicyEntity.getTpNexthopEntities().add(tpNexthopEntity);
            }
            return tunnelPolicyEntity;
        }
        if(tunnelPolicyEntity.getTnlPolicyType().equals("tnlSelectSeq")){//解析tnlSelSeqs
            for(TnlSelSeq tnlSelSeq:tunnelPolicy.getTnlSelSeqls()){
                TnlSelSeqEntity tnlSelSeqEntity=tnlSelSeqToTnlSelSeqEntity(tnlSelSeq);
                tunnelPolicyEntity.getTnlSelSeqlEntities().add(tnlSelSeqEntity);
            }
            return tunnelPolicyEntity;
        }
        return tunnelPolicyEntity;
    }

    private TpNexthopEntity tpNexthopToTpNexthopEntity(TpNexthop tpNexthop){
        TpNexthopEntity tpNexthopEntity=new TpNexthopEntity();
        if(null==tpNexthop.getNexthopIPaddr()||tpNexthop.getNexthopIPaddr().isEmpty()){
            return null;
        }
        tpNexthopEntity.setTnlPolicyName(tpNexthop.getTnlPolicyName());
        tpNexthopEntity.setNexthopIPaddr(tpNexthop.getNexthopIPaddr());
        tpNexthopEntity.setDownSwitch(tpNexthop.isDownSwitch());
        tpNexthopEntity.setIgnoreDestCheck(tpNexthop.isIgnoreDestCheck());
        tpNexthopEntity.setIncludeLdp(tpNexthop.isIncludeLdp());
        tpNexthopEntity.setTpTunnels(tpNexthop.getTpTunnels());

        return  tpNexthopEntity;
    }

    private TnlSelSeqEntity tnlSelSeqToTnlSelSeqEntity(TnlSelSeq tnlSelSeq){
        TnlSelSeqEntity tnlSelSeqEntity=new TnlSelSeqEntity();
        tnlSelSeqEntity.setTnlPolicyName(tnlSelSeq.getTnlPolicyName());
        tnlSelSeqEntity.setSelTnlType1(tnlSelSeq.getSelTnlType1());
        tnlSelSeqEntity.setSelTnlType2(tnlSelSeq.getSelTnlType2());
        tnlSelSeqEntity.setSelTnlType3(tnlSelSeq.getSelTnlType3());
        tnlSelSeqEntity.setSelTnlType4(tnlSelSeq.getSelTnlType4());
        tnlSelSeqEntity.setSelTnlType5(tnlSelSeq.getSelTnlType5());
        tnlSelSeqEntity.setLoadBalanceNum(tnlSelSeq.getLoadBalanceNum());
        tnlSelSeqEntity.setUnmix(tnlSelSeq.isUnmix());
        return  tnlSelSeqEntity;
    }
}
