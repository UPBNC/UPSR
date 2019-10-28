package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TrafficPolicyManager;
import cn.org.upbnc.entity.TrafficPolicy.AclInfoEntity;
import cn.org.upbnc.service.TrafficPolicyService;
import cn.org.upbnc.service.entity.TrafficPolicy.AclInfoServiceEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.TrafficAclXml;

import java.util.*;

public class TrafficPolicyServiceImpl implements TrafficPolicyService {
    private static TrafficPolicyServiceImpl ourInstance = new TrafficPolicyServiceImpl();
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;
    private BaseInterface baseInterface;
    private TrafficPolicyManager trafficPolicyManager;

    public static TrafficPolicyServiceImpl getInstance() {
        return ourInstance;
    }

    private TrafficPolicyServiceImpl() {
        this.netConfManager = null;
        this.deviceManager = null;
        this.baseInterface = null;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
            this.trafficPolicyManager = this.baseInterface.getTrafficPolicyManager();
        }
        return true;
    }

    @Override
    public boolean syncTrafficPolicyConf(String routerId) {
        NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
        if (netconfClient == null) {
            return false;
        }
        return trafficPolicyManager.syncTrafficPolicyConf(routerId, netconfClient);
    }

    @Override
    public Map<String,List<AclInfoServiceEntity>> getAclInfo(String routerId, String aclName) {
        Map<String,List<AclInfoServiceEntity>> aclMaps = new HashMap<>();
        Map<String, Map<String,AclInfoEntity>> aclInfoMaps = trafficPolicyManager.getAllTrafficPolicy();
        if (routerId == null) {
            for (String key : aclInfoMaps.keySet()) {
                Collection<AclInfoEntity> collection = aclInfoMaps.get(key).values();
                List<AclInfoServiceEntity> aclInfoServiceEntityList = new ArrayList<>();
                for (AclInfoEntity aclInfoEntity : collection) {
                    AclInfoServiceEntity aclInfoServiceEntity = aclInfoEntityToAclInfoServiceEntity(aclInfoEntity);
                    aclInfoServiceEntityList.add(aclInfoServiceEntity);
                }
                aclMaps.put(key,aclInfoServiceEntityList);
            }
        } else {
            if (aclName == null) {
                Collection<AclInfoEntity> collection = aclInfoMaps.get(routerId).values();
                List<AclInfoServiceEntity> aclInfoServiceEntityList = new ArrayList<>();
                for (AclInfoEntity aclInfoEntity : collection) {
                    AclInfoServiceEntity aclInfoServiceEntity = aclInfoEntityToAclInfoServiceEntity(aclInfoEntity);
                    aclInfoServiceEntityList.add(aclInfoServiceEntity);
                }
                aclMaps.put(routerId,aclInfoServiceEntityList);
            } else {
                Collection<AclInfoEntity> collection = aclInfoMaps.get(routerId).values();
                List<AclInfoServiceEntity> aclInfoServiceEntityList = new ArrayList<>();
                for (AclInfoEntity aclInfoEntity : collection) {
                    if (aclInfoEntity.getAclName().equals(aclName)) {
                        AclInfoServiceEntity aclInfoServiceEntity = aclInfoEntityToAclInfoServiceEntity(aclInfoEntity);
                        aclInfoServiceEntityList.add(aclInfoServiceEntity);
                    }
                }
                aclMaps.put(routerId,aclInfoServiceEntityList);
            }
        }
        return aclMaps;
    }



    private AclInfoServiceEntity aclInfoEntityToAclInfoServiceEntity(AclInfoEntity aclInfoEntity) {
        AclInfoServiceEntity aclInfoServiceEntity = new AclInfoServiceEntity();
        aclInfoServiceEntity.setAclName(aclInfoEntity.getAclName());
        return aclInfoServiceEntity;
    }
}
