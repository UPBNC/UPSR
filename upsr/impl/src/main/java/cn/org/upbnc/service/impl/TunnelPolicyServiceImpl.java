package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TunnelPolicyManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.TunnelPolicy.TnlSelSeq;
import cn.org.upbnc.entity.TunnelPolicy.TpNexthop;
import cn.org.upbnc.entity.TunnelPolicy.TunnelPolicy;
import cn.org.upbnc.enumtype.CodeEnum;
import cn.org.upbnc.enumtype.ResponseEnum;
import cn.org.upbnc.enumtype.TnlPolicyTypeEnum;
import cn.org.upbnc.service.TunnelPolicyService;
import cn.org.upbnc.service.entity.TunnelPolicy.TnlSelSeqEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TpNexthopEntity;
import cn.org.upbnc.service.entity.TunnelPolicy.TunnelPolicyEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TunnelPolicyServiceImpl implements TunnelPolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static TunnelPolicyService ourInstance = null;
    private BaseInterface baseInterface;
    private TunnelPolicyManager tunnelPolicyManager;
    private NetConfManager netConfManager;
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
        this.deviceManager = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.tunnelPolicyManager = this.baseInterface.getTunnelPolicyManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public boolean syncTunnelPolicyConf() {
        boolean ret = true;
        for (Device device : deviceManager.getDeviceList()) {
            if (!syncTunnelPolicyConf(device.getRouterId())) {
                LOG.info("syncTunnelPolicyConf failed,routerId:" + device.getRouterId());
                ret = false;
            }
        }
        return ret;
    }

    @Override
    public Map<String, Object> createTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities) {
        LOG.info("createTunnelPolicys :" + tunnelPolicyEntities.toString());
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (tunnelPolicyEntities != null && tunnelPolicyEntities.size() > 0) {
            String routerId = tunnelPolicyEntities.get(0).getRouterID();
            NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
            if (null == netconfClient) {
                map.put(ResponseEnum.MESSAGE.getName(), "this device(" + routerId + ") netconfClient is null.");
                return map;
            }
            List<TunnelPolicy> tunnelPolicies = TunnelPolicyEntityMapToTunnelPolicies(tunnelPolicyEntities);
            if (tunnelPolicies.size() > 0) {
                boolean flag = tunnelPolicyManager.createTunnelPolicyList(tunnelPolicies, netconfClient);
                if (flag) {
                    map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
                    map.put(ResponseEnum.MESSAGE.getName(), "success");
                    return map;
                }
            }
        }
        return map;
    }

    private List<TunnelPolicy> TunnelPolicyEntityMapToTunnelPolicies(List<TunnelPolicyEntity> tunnelPolicyEntities) {
        List<TunnelPolicy> tunnelPolicies = new ArrayList<>();
        TunnelPolicy tunnelPolicy;
        List<TpNexthop> tpNexthops;
        TpNexthop tpNexthop;
        for (TunnelPolicyEntity entity : tunnelPolicyEntities) {
            tunnelPolicy = new TunnelPolicy();
            tunnelPolicy.setRouterID(entity.getRouterID());
            tunnelPolicy.setDescription(entity.getDescription());
            tunnelPolicy.setTnlPolicyName(entity.getTnlPolicyName());
            tunnelPolicy.setTnlPolicyType(entity.getTnlPolicyType());
            tpNexthops = new ArrayList<>();
            tpNexthop = new TpNexthop();
            if (entity.getTpNexthopEntities() != null && entity.getTpNexthopEntities().size() > 0) {
                for (TpNexthopEntity tpNexthopEntity : entity.getTpNexthopEntities()) {
                    tpNexthop.setNexthopIPaddr(tpNexthopEntity.getNexthopIPaddr());
                    tpNexthop.setTpTunnels(tpNexthopEntity.getTpTunnels());
                }
            }
            tpNexthops.add(tpNexthop);
            tunnelPolicy.setTpNexthops(tpNexthops);
            tunnelPolicies.add(tunnelPolicy);
        }
        return tunnelPolicies;
    }

    @Override
    public Map<String, Object> deleteTunnelPolicys(List<TunnelPolicyEntity> tunnelPolicyEntities) {
        LOG.info("deleteTunnelPolicys :" + tunnelPolicyEntities.toString());
        Map<String, Object> map = new HashMap<>();
        map.put(ResponseEnum.CODE.getName(), CodeEnum.ERROR.getName());
        if (tunnelPolicyEntities != null && tunnelPolicyEntities.size() > 0) {
            String routerId = tunnelPolicyEntities.get(0).getRouterID();
            NetconfClient netconfClient = netConfManager.getNetconClient(routerId);
            if (null == netconfClient) {
                map.put(ResponseEnum.MESSAGE.getName(), "this device(" + routerId + ") netconfClient is null.");
                return map;
            }
            List<String> names = new ArrayList<>();
            for(TunnelPolicyEntity entity:tunnelPolicyEntities){
                names.add(entity.getTnlPolicyName());
            }
            if (names.size() > 0) {
                boolean flag = tunnelPolicyManager.deleteTunnelPolicyByNameList(names, netconfClient);
                if (flag) {
                    map.put(ResponseEnum.CODE.getName(), CodeEnum.SUCCESS.getName());
                    map.put(ResponseEnum.MESSAGE.getName(), "success");
                    return map;
                }
            }
        }
        return map;
    }

    public boolean syncTunnelPolicyConf(String routerID) {
        NetconfClient netconfClient = netConfManager.getNetconClient(routerID);
        if (null == netconfClient) {
            return false;
        }
        return tunnelPolicyManager.syncTunnelPolicyConf(routerID, netconfClient);
    }

    public List<TunnelPolicyEntity> getTunnelPolicys(String routerId) {
        List<TunnelPolicyEntity> tunnelPolicyEntityList = new ArrayList<TunnelPolicyEntity>();
        List<TunnelPolicy> tunnelPolicyList = new ArrayList<TunnelPolicy>();
        if (null == routerId || routerId.isEmpty()) {
            tunnelPolicyList = tunnelPolicyManager.getAllTunnelPolicys();
        } else {
            tunnelPolicyList = tunnelPolicyManager.getAllTunnelPolicys(routerId);
        }
        if (null == tunnelPolicyList) {
            return tunnelPolicyEntityList;
        }
        for (TunnelPolicy tunnelPolicy : tunnelPolicyList) {
            TunnelPolicyEntity tunnelPolicyEntity = tunnelPolicyToTunnelPolicyEntity(tunnelPolicy);
            if (null != tunnelPolicyEntity) {
                tunnelPolicyEntityList.add(tunnelPolicyEntity);
            }
        }
        return tunnelPolicyEntityList;
    }


    private TunnelPolicyEntity tunnelPolicyToTunnelPolicyEntity(TunnelPolicy tunnelPolicy) {
        TunnelPolicyEntity tunnelPolicyEntity = new TunnelPolicyEntity();

        if (null == tunnelPolicy.getTnlPolicyName() || tunnelPolicy.getTnlPolicyName().isEmpty()) {
            return null;
        }
        if (null == tunnelPolicy.getRouterID() || tunnelPolicy.getRouterID().isEmpty()) {
            return null;
        }
        tunnelPolicyEntity.setRouterID(tunnelPolicy.getRouterID());
        tunnelPolicyEntity.setTnlPolicyName(tunnelPolicy.getTnlPolicyName());
        tunnelPolicyEntity.setDescription(tunnelPolicy.getDescription());
        tunnelPolicyEntity.setTnlPolicyType(tunnelPolicy.getTnlPolicyType());
//        if (tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.Invalid.getCode()) {
//            return tunnelPolicyEntity;
//        }
        if (null != tunnelPolicyEntity.getTnlPolicyType()) {
        if (tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.TnlBinding.getCode()) {//解析tpNexthops
            for (TpNexthop tpNexthop : tunnelPolicy.getTpNexthops()) {
                TpNexthopEntity tpNexthopEntity = tpNexthopToTpNexthopEntity(tpNexthop);
                if (null == tpNexthopEntity) {
                    break;
                }
                tunnelPolicyEntity.getTpNexthopEntities().add(tpNexthopEntity);
            }
            return tunnelPolicyEntity;
        }
        if (tunnelPolicyEntity.getTnlPolicyType() == TnlPolicyTypeEnum.TnlSelectSeq.getCode()) {//解析tnlSelSeqs
            for (TnlSelSeq tnlSelSeq : tunnelPolicy.getTnlSelSeqls()) {
                TnlSelSeqEntity tnlSelSeqEntity = tnlSelSeqToTnlSelSeqEntity(tnlSelSeq);
                tunnelPolicyEntity.getTnlSelSeqlEntities().add(tnlSelSeqEntity);
            }
            return tunnelPolicyEntity;
        }
    }
        return tunnelPolicyEntity;
    }

    private TpNexthopEntity tpNexthopToTpNexthopEntity(TpNexthop tpNexthop) {
        TpNexthopEntity tpNexthopEntity = new TpNexthopEntity();
        if (null == tpNexthop.getNexthopIPaddr() || tpNexthop.getNexthopIPaddr().isEmpty()) {
            return null;
        }
        tpNexthopEntity.setTnlPolicyName(tpNexthop.getTnlPolicyName());
        tpNexthopEntity.setNexthopIPaddr(tpNexthop.getNexthopIPaddr());
        tpNexthopEntity.setDownSwitch(tpNexthop.isDownSwitch());
        tpNexthopEntity.setIgnoreDestCheck(tpNexthop.isIgnoreDestCheck());
        tpNexthopEntity.setIncludeLdp(tpNexthop.isIncludeLdp());
        tpNexthopEntity.setTpTunnels(tpNexthop.getTpTunnels());

        return tpNexthopEntity;
    }

    private TnlSelSeqEntity tnlSelSeqToTnlSelSeqEntity(TnlSelSeq tnlSelSeq) {
        TnlSelSeqEntity tnlSelSeqEntity = new TnlSelSeqEntity();
        tnlSelSeqEntity.setTnlPolicyName(tnlSelSeq.getTnlPolicyName());
        tnlSelSeqEntity.setSelTnlType1(tnlSelSeq.getSelTnlType1());
        tnlSelSeqEntity.setSelTnlType2(tnlSelSeq.getSelTnlType2());
        tnlSelSeqEntity.setSelTnlType3(tnlSelSeq.getSelTnlType3());
        tnlSelSeqEntity.setSelTnlType4(tnlSelSeq.getSelTnlType4());
        tnlSelSeqEntity.setSelTnlType5(tnlSelSeq.getSelTnlType5());
        tnlSelSeqEntity.setLoadBalanceNum(tnlSelSeq.getLoadBalanceNum());
        tnlSelSeqEntity.setUnmix(tnlSelSeq.isUnmix());
        return tnlSelSeqEntity;
    }
}
