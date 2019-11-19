package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.TrafficPolicyManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.TrafficPolicy.*;
import cn.org.upbnc.service.TrafficPolicyService;
import cn.org.upbnc.service.entity.TrafficPolicy.*;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.xml.TrafficAclXml;
import sun.dc.pr.PRError;

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
    public boolean syncTrafficPolicyConf() {
        boolean flag = true;
        List<Device> devices = deviceManager.getDeviceList();
        for (Device device : devices) {
            flag = syncTrafficPolicyConf(device.getRouterId());
        }
        return flag;
    }

    @Override
    public Map<String,List<AclInfoServiceEntity>> getAclInfo(String routerId, String aclName) {
        Map<String,List<AclInfoServiceEntity>> aclMaps = new HashMap<>();
        Map<String, Map<String,AclInfoEntity>> aclInfoMaps = trafficPolicyManager.getAllAclInfoEntity();
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

    @Override
    public Map<String, List<TrafficClassServiceEntity>> getTrafficClassInfo(String routerId, String trafficClassName) {
        Map<String,List<TrafficClassServiceEntity>> trafficClassMaps = new HashMap<>();
        Map<String, Map<String,TrafficClassInfoEntity>> trafficClassInfoMaps = trafficPolicyManager.getAllTrafficClassInfoEntity();
        if (routerId == null) {
            for (String key : trafficClassInfoMaps.keySet()) {
                Collection<TrafficClassInfoEntity> collection = trafficClassInfoMaps.get(key).values();
                List<TrafficClassServiceEntity> trafficClassServiceEntityList = new ArrayList<>();
                for (TrafficClassInfoEntity trafficClassInfoEntity : collection) {
                    TrafficClassServiceEntity trafficClassServiceEntity = trafficClassInfoEntityToTrafficClassServiceEntity(trafficClassInfoEntity);
                    trafficClassServiceEntityList.add(trafficClassServiceEntity);
                }
                trafficClassMaps.put(key,trafficClassServiceEntityList);
            }
        } else {
            if (trafficClassName == null) {
                Collection<TrafficClassInfoEntity> collection = trafficClassInfoMaps.get(routerId).values();
                List<TrafficClassServiceEntity> trafficClassServiceEntityList = new ArrayList<>();
                for (TrafficClassInfoEntity trafficClassInfoEntity : collection) {
                    TrafficClassServiceEntity trafficClassServiceEntity = trafficClassInfoEntityToTrafficClassServiceEntity(trafficClassInfoEntity);
                    trafficClassServiceEntityList.add(trafficClassServiceEntity);
                }
                trafficClassMaps.put(routerId,trafficClassServiceEntityList);
            } else {
                Collection<TrafficClassInfoEntity> collection = trafficClassInfoMaps.get(routerId).values();
                List<TrafficClassServiceEntity> trafficClassServiceEntityList = new ArrayList<>();
                for (TrafficClassInfoEntity trafficClassInfoEntity : collection) {
                    if (trafficClassInfoEntity.getTrafficClassName().equals(trafficClassName)) {
                        TrafficClassServiceEntity trafficClassServiceEntity = trafficClassInfoEntityToTrafficClassServiceEntity(trafficClassInfoEntity);
                        trafficClassServiceEntityList.add(trafficClassServiceEntity);
                    }
                }
                trafficClassMaps.put(routerId,trafficClassServiceEntityList);
            }
        }
        return trafficClassMaps;
    }

    @Override
    public Map<String, List<TrafficBehaveServiceEntity>> getTrafficBehaveInfo(String routerId, String trafficBehaveName) {
        Map<String,List<TrafficBehaveServiceEntity>> trafficBehaveMaps = new HashMap<>();
        Map<String, Map<String,TrafficBehaveInfoEntity>> trafficBehaveInfoMaps = trafficPolicyManager.getAllTrafficBehaveInfoEntity();
        if (routerId == null) {
            for (String key : trafficBehaveInfoMaps.keySet()) {
                Collection<TrafficBehaveInfoEntity> collection = trafficBehaveInfoMaps.get(key).values();
                List<TrafficBehaveServiceEntity> trafficBehaveServiceEntityList = new ArrayList<>();
                for (TrafficBehaveInfoEntity trafficBehaveInfoEntity : collection) {
                    TrafficBehaveServiceEntity trafficBehaveServiceEntity = trafficBehaveInfoEntityToTrafficBehaveServiceEntity(trafficBehaveInfoEntity);
                    trafficBehaveServiceEntityList.add(trafficBehaveServiceEntity);
                }
                trafficBehaveMaps.put(key,trafficBehaveServiceEntityList);
            }
        } else {
            if (trafficBehaveName == null) {
                Collection<TrafficBehaveInfoEntity> collection = trafficBehaveInfoMaps.get(routerId).values();
                List<TrafficBehaveServiceEntity> trafficBehaveServiceEntityList = new ArrayList<>();
                for (TrafficBehaveInfoEntity trafficBehaveInfoEntity : collection) {
                    TrafficBehaveServiceEntity trafficBehaveServiceEntity = trafficBehaveInfoEntityToTrafficBehaveServiceEntity(trafficBehaveInfoEntity);
                    trafficBehaveServiceEntityList.add(trafficBehaveServiceEntity);
                }
                trafficBehaveMaps.put(routerId,trafficBehaveServiceEntityList );
            } else {
                Collection<TrafficBehaveInfoEntity> collection = trafficBehaveInfoMaps.get(routerId).values();
                List<TrafficBehaveServiceEntity> trafficBehaveServiceEntityList = new ArrayList<>();
                for (TrafficBehaveInfoEntity trafficBehaveInfoEntity : collection) {
                    if (trafficBehaveInfoEntity.getTrafficBehaveName().equals(trafficBehaveName)) {
                        TrafficBehaveServiceEntity trafficBehaveServiceEntity = trafficBehaveInfoEntityToTrafficBehaveServiceEntity(trafficBehaveInfoEntity);
                        trafficBehaveServiceEntityList.add(trafficBehaveServiceEntity);
                    }
                }
                trafficBehaveMaps.put(routerId,trafficBehaveServiceEntityList);
            }
        }
        return trafficBehaveMaps;
    }

    @Override
    public Map<String, List<TrafficPolicyServiceEntity>> getTrafficPolicyInfo(String routerId, String trafficPolicyName) {
        Map<String,List<TrafficPolicyServiceEntity>> trafficPolicyMaps = new HashMap<>();
        Map<String, Map<String,TrafficPolicyInfoEntity>> trafficPolicyInfoMaps = trafficPolicyManager.getAllTrafficPolicyInfoEntity();
        if (routerId == null) {
            for (String key : trafficPolicyInfoMaps.keySet()) {
                Collection<TrafficPolicyInfoEntity> collection = trafficPolicyInfoMaps.get(key).values();
                List<TrafficPolicyServiceEntity> trafficPolicyServiceEntityList = new ArrayList<>();
                for (TrafficPolicyInfoEntity trafficPolicyInfoEntity : collection) {
                    TrafficPolicyServiceEntity trafficPolicyServiceEntity = trafficPolicyInfoEntityToTrafficPolicyServiceEntity(trafficPolicyInfoEntity);
                    trafficPolicyServiceEntityList.add(trafficPolicyServiceEntity);
                }
                trafficPolicyMaps.put(key,trafficPolicyServiceEntityList);
            }
        } else {
            if (trafficPolicyName == null) {
                Collection<TrafficPolicyInfoEntity> collection = trafficPolicyInfoMaps.get(routerId).values();
                List<TrafficPolicyServiceEntity> trafficPolicyServiceEntityList = new ArrayList<>();
                for (TrafficPolicyInfoEntity trafficPolicyInfoEntity : collection) {
                    TrafficPolicyServiceEntity trafficPolicyServiceEntity = trafficPolicyInfoEntityToTrafficPolicyServiceEntity(trafficPolicyInfoEntity);
                    trafficPolicyServiceEntityList.add(trafficPolicyServiceEntity);
                }
                trafficPolicyMaps.put(routerId,trafficPolicyServiceEntityList );
            } else {
                Collection<TrafficPolicyInfoEntity> collection = trafficPolicyInfoMaps.get(routerId).values();
                List<TrafficPolicyServiceEntity> trafficPolicyServiceEntityList = new ArrayList<>();
                for (TrafficPolicyInfoEntity trafficPolicyInfoEntity : collection) {
                    if (trafficPolicyInfoEntity.getTrafficPolicyName().equals(trafficPolicyName)) {
                        TrafficPolicyServiceEntity trafficPolicyServiceEntity = trafficPolicyInfoEntityToTrafficPolicyServiceEntity(trafficPolicyInfoEntity);
                        trafficPolicyServiceEntityList.add(trafficPolicyServiceEntity);
                    }
                }
                trafficPolicyMaps.put(routerId,trafficPolicyServiceEntityList);
            }
        }
        return trafficPolicyMaps;
    }

    @Override
    public Map<String, List<TrafficIfPolicyServiceEntity>> getTrafficIfPolicyInfo(String routerId, String ifName) {
        Map<String,List<TrafficIfPolicyServiceEntity>> trafficIfPolicyMaps = new HashMap<>();
        Map<String, Map<String,TrafficIfPolicyInfoEntity>> trafficIfPolicyInfoMaps = trafficPolicyManager.getAllTrafficIfPolicyInfoEntity();
        if (routerId == null) {
            for (String key : trafficIfPolicyInfoMaps.keySet()) {
                Collection<TrafficIfPolicyInfoEntity> collection = trafficIfPolicyInfoMaps.get(key).values();
                List<TrafficIfPolicyServiceEntity> trafficIfPolicyServiceEntityList = new ArrayList<>();
                for (TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity : collection) {
                    TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity = trafficIfPolicyInfoEntityToTrafficIfPolicyServiceEntity(trafficIfPolicyInfoEntity);
                    trafficIfPolicyServiceEntityList.add(trafficIfPolicyServiceEntity);
                }
                trafficIfPolicyMaps.put(key,trafficIfPolicyServiceEntityList);
            }
        } else {
            if (ifName == null) {
                Collection<TrafficIfPolicyInfoEntity> collection = trafficIfPolicyInfoMaps.get(routerId).values();
                List<TrafficIfPolicyServiceEntity> trafficIfPolicyServiceEntityList = new ArrayList<>();
                for (TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity : collection) {
                    TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity = trafficIfPolicyInfoEntityToTrafficIfPolicyServiceEntity(trafficIfPolicyInfoEntity);
                    trafficIfPolicyServiceEntityList.add(trafficIfPolicyServiceEntity);
                }
                trafficIfPolicyMaps.put(routerId,trafficIfPolicyServiceEntityList );
            } else {
                Collection<TrafficIfPolicyInfoEntity> collection = trafficIfPolicyInfoMaps.get(routerId).values();
                List<TrafficIfPolicyServiceEntity> trafficIfPolicyServiceEntityList = new ArrayList<>();
                for (TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity : collection) {
                    if (trafficIfPolicyInfoEntity.getIfName().equals(ifName)) {
                        TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity = trafficIfPolicyInfoEntityToTrafficIfPolicyServiceEntity(trafficIfPolicyInfoEntity);
                        trafficIfPolicyServiceEntityList.add(trafficIfPolicyServiceEntity);
                    }
                }
                trafficIfPolicyMaps.put(routerId,trafficIfPolicyServiceEntityList);
            }
        }
        return trafficIfPolicyMaps;
    }

    private AclInfoServiceEntity aclInfoEntityToAclInfoServiceEntity(AclInfoEntity aclInfoEntity) {
        AclInfoServiceEntity aclInfoServiceEntity = new AclInfoServiceEntity();
        aclInfoServiceEntity.setAclName(aclInfoEntity.getAclName());
        List<AclRuleInfoServiceEntity> aclRuleInfoServiceEntityList = new ArrayList<>();
        for(AclRuleInfoEntity aclRuleInfoEntity:aclInfoEntity.getAclRuleInfoEntityList()){
            AclRuleInfoServiceEntity aclRuleInfoServiceEntity = aclRuleInfoEntityToAclRuleInfoServiceEntity(aclRuleInfoEntity);
            aclRuleInfoServiceEntityList.add(aclRuleInfoServiceEntity);
        }
        aclInfoServiceEntity.setAclRuleInfoServiceEntityList(aclRuleInfoServiceEntityList);
        return aclInfoServiceEntity;
    }

    private TrafficClassServiceEntity trafficClassInfoEntityToTrafficClassServiceEntity(TrafficClassInfoEntity trafficClassInfoEntity) {
        TrafficClassServiceEntity trafficClassServiceEntity = new TrafficClassServiceEntity();
        trafficClassServiceEntity.setTrafficClassName(trafficClassInfoEntity.getTrafficClassName());
        trafficClassServiceEntity.setOperator(trafficClassInfoEntity.getOperator());
        List<TrafficClassAclServiceEntity> trafficClassAclServiceEntityList = new ArrayList<>();
        for(TrafficClassAclInfoEntity trafficClassAclInfoEntity:trafficClassInfoEntity.getTrafficClassAclInfoEntityList()){
            TrafficClassAclServiceEntity trafficClassAclServiceEntity = trafficClassAclInfoEntityToTrafficClassAclServiceEntity(trafficClassAclInfoEntity);
            trafficClassAclServiceEntityList.add(trafficClassAclServiceEntity);
        }
        trafficClassServiceEntity.setTrafficClassAclServiceEntityList(trafficClassAclServiceEntityList);
        return trafficClassServiceEntity;
    }

    private TrafficBehaveServiceEntity trafficBehaveInfoEntityToTrafficBehaveServiceEntity(TrafficBehaveInfoEntity trafficBehaveInfoEntity) {
        TrafficBehaveServiceEntity trafficBehaveServiceEntity = new TrafficBehaveServiceEntity();
        trafficBehaveServiceEntity.setTrafficBehaveName(trafficBehaveInfoEntity.getTrafficBehaveName());
        trafficBehaveServiceEntity.setRedirectTunnelName(trafficBehaveInfoEntity.getRedirectTunnelName());
        return trafficBehaveServiceEntity;
    }

    private TrafficPolicyServiceEntity trafficPolicyInfoEntityToTrafficPolicyServiceEntity(TrafficPolicyInfoEntity trafficPolicyInfoEntity) {
        TrafficPolicyServiceEntity trafficPolicyServiceEntity = new TrafficPolicyServiceEntity();
        trafficPolicyServiceEntity.setTrafficPolicyName(trafficPolicyInfoEntity.getTrafficPolicyName());
        List<TrafficPolicyNodeServiceEntity> trafficPolicyNodeServiceEntityList = new ArrayList<>();
        for(TrafficPolicyNodeInfoEntity trafficPolicyNodeInfoEntity:trafficPolicyInfoEntity.getTrafficPolicyNodeInfoEntityList()){
            TrafficPolicyNodeServiceEntity trafficPolicyNodeServiceEntity = trafficPolicyNodeInfoEntityToTrafficPolicyNodeServiceEntity(trafficPolicyNodeInfoEntity);
            trafficPolicyNodeServiceEntityList.add(trafficPolicyNodeServiceEntity);
        }
        trafficPolicyServiceEntity.setTrafficPolicyNodeServiceEntityList(trafficPolicyNodeServiceEntityList);
        return trafficPolicyServiceEntity;
    }

    private TrafficIfPolicyServiceEntity trafficIfPolicyInfoEntityToTrafficIfPolicyServiceEntity(TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity) {
        TrafficIfPolicyServiceEntity trafficIfPolicyServiceEntity = new TrafficIfPolicyServiceEntity();
        trafficIfPolicyServiceEntity.setIfName(trafficIfPolicyInfoEntity.getIfName());
        trafficIfPolicyServiceEntity.setPolicyName(trafficIfPolicyInfoEntity.getPolicyName());
        trafficIfPolicyServiceEntity.setDirection(trafficIfPolicyInfoEntity.getDirection());
        return trafficIfPolicyServiceEntity;
    }

    private AclRuleInfoServiceEntity aclRuleInfoEntityToAclRuleInfoServiceEntity(AclRuleInfoEntity aclRuleInfoEntity) {
        AclRuleInfoServiceEntity aclRuleInfoServiceEntity = new AclRuleInfoServiceEntity();
        aclRuleInfoServiceEntity.setRuleId(aclRuleInfoEntity.getRuleId());
        aclRuleInfoServiceEntity.setRuleType(aclRuleInfoEntity.getRuleType());
        aclRuleInfoServiceEntity.setProtoType(aclRuleInfoEntity.getProtoType());
        aclRuleInfoServiceEntity.setSourcce(aclRuleInfoEntity.getSourcce());
        aclRuleInfoServiceEntity.setSourcceWild(aclRuleInfoEntity.getSourcceWild());
        aclRuleInfoServiceEntity.setSourcePortOp(aclRuleInfoEntity.getSourcePortOp());
        aclRuleInfoServiceEntity.setSourcePort(aclRuleInfoEntity.getSourcePort());
        aclRuleInfoServiceEntity.setDestination(aclRuleInfoEntity.getDestination());
        aclRuleInfoServiceEntity.setDestinationWild(aclRuleInfoEntity.getDestinationWild());
        aclRuleInfoServiceEntity.setDestinationPortOp(aclRuleInfoEntity.getDestinationPortOp());
        aclRuleInfoServiceEntity.setDestinationPort(aclRuleInfoEntity.getDestinationPort());
        return aclRuleInfoServiceEntity;
    }

    private TrafficClassAclServiceEntity trafficClassAclInfoEntityToTrafficClassAclServiceEntity(TrafficClassAclInfoEntity trafficClassAclInfoEntity) {
        TrafficClassAclServiceEntity trafficClassAclServiceEntity = new TrafficClassAclServiceEntity();
        trafficClassAclServiceEntity.setAclName(trafficClassAclInfoEntity.getAclName());
        return trafficClassAclServiceEntity;
    }

    private TrafficPolicyNodeServiceEntity trafficPolicyNodeInfoEntityToTrafficPolicyNodeServiceEntity(TrafficPolicyNodeInfoEntity trafficPolicyNodeInfoEntity){
        TrafficPolicyNodeServiceEntity trafficPolicyNodeServiceEntity = new TrafficPolicyNodeServiceEntity();
        trafficPolicyNodeServiceEntity.setClassName(trafficPolicyNodeInfoEntity.getClassName());
        trafficPolicyNodeServiceEntity.setBehaveName(trafficPolicyNodeInfoEntity.getBehaveName());
        return trafficPolicyNodeServiceEntity;
    }
}
