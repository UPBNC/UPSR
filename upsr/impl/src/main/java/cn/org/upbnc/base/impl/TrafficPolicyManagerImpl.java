package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.TrafficPolicyManager;
import cn.org.upbnc.entity.TrafficPolicy.*;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TrafficPolicy.*;
import cn.org.upbnc.util.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TrafficPolicyManagerImpl implements TrafficPolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(TrafficPolicyManagerImpl.class);
    private Map<String,AclInfoEntity> aclInfoEntityMap;
    private Map<String,TrafficClassInfoEntity> trafficClassInfoEntityMap;
    private Map<String,TrafficBehaveInfoEntity> trafficBehaveInfoEntityMap;
    private Map<String,TrafficPolicyInfoEntity> trafficPolicyInfoEntityMap;
    private Map<String,TrafficIfPolicyInfoEntity> trafficIfPolicyInfoEntityMap;
    private static TrafficPolicyManager instance;
    private Map<String,Map<String,AclInfoEntity>> aclInfoEntityMaps;
    private Map<String,Map<String,TrafficClassInfoEntity>> trafficClassInfoEntityMaps;
    private Map<String,Map<String,TrafficBehaveInfoEntity>> trafficBehaveInfoEntityMaps;
    private Map<String,Map<String,TrafficPolicyInfoEntity>> trafficPolicyInfoEntityMaps;
    private Map<String,Map<String,TrafficIfPolicyInfoEntity>> trafficIfPolicyInfoEntityMaps;

    public TrafficPolicyManagerImpl() {
        this.aclInfoEntityMaps = new HashMap<>();
        this.trafficClassInfoEntityMaps = new HashMap<>();
        this.trafficBehaveInfoEntityMaps = new HashMap<>();
        this.trafficPolicyInfoEntityMaps = new HashMap<>();
        this.trafficIfPolicyInfoEntityMaps = new HashMap<>();
    }

    public static TrafficPolicyManager getInstance() {
        if (null == instance) {
            instance = new TrafficPolicyManagerImpl();
        }
        return instance;
    }
    @Override
    public List<AclInfoEntity> getAllTunnelPolicys(String routerID, String aclName) {
        if (routerID == null) {
            for (String id : aclInfoEntityMaps.keySet()) {

            }
        } else {
            if (aclName == null) {

            } else {

            }
        }
        return null;
    }

    @Override
    public boolean syncTrafficPolicyConf(String routerID, NetconfClient netconfClient) {
        this.syncTrafficPolicyAclConf(routerID,netconfClient);
        this.syncTrafficPolicyTrafficClassConf(routerID,netconfClient);
        this.syncTrafficPolicyTrafficBehaveConf(routerID,netconfClient);
        this.syncTrafficPolicyTrafficPolicyConf(routerID,netconfClient);
        this.syncTrafficPolicyTrafficIfPolicyConf(routerID,netconfClient);
        return true;
    }

    public boolean syncTrafficPolicyAclConf(String routerID, NetconfClient netconfClient) {
        String commandTrafficAclXml = TrafficAclXml.getSTrafficAclXml();
        LOG.info("commandTrafficAclXml : " + commandTrafficAclXml);
        String outPutTrafficAclXml = netconfController.sendMessage(netconfClient,commandTrafficAclXml);
        LOG.info("outPutTrafficAclXml : " + outPutTrafficAclXml);

        List<SAclInfo> sAclInfoList = TrafficAclXml.getSTrafficAclFromXml(outPutTrafficAclXml);

        for (SAclInfo sAclInfo : sAclInfoList) {
            AclInfoEntity aclInfoEntity = this.sAclInfoToAclInfoEntity(sAclInfo);
            if (aclInfoEntityMaps.containsKey(routerID)) {
                aclInfoEntityMaps.get(routerID).put(aclInfoEntity.getAclName(),aclInfoEntity);
            } else {
                aclInfoEntityMap = new HashMap<>();
                aclInfoEntityMap.put(aclInfoEntity.getAclName(),aclInfoEntity);
                aclInfoEntityMaps.put(routerID,aclInfoEntityMap);
            }
        }
        return true;
    }

    public boolean syncTrafficPolicyTrafficClassConf(String routerID, NetconfClient netconfClient) {
        String commandTrafficClassifierXml = TrafficClassifier.getTrafficClassifierXml();
        LOG.info("commandTrafficClassifierXml : " + commandTrafficClassifierXml);
        String outPutTrafficClassifierXml = netconfController.sendMessage(netconfClient,commandTrafficClassifierXml);
        LOG.info("outPutTrafficClassifierXml : " + outPutTrafficClassifierXml);

        List<STrafficClassInfo> sTrafficClassInfoList = TrafficClassifier.getSTrafficClassFromXml(outPutTrafficClassifierXml);

        for (STrafficClassInfo sTrafficClassInfo : sTrafficClassInfoList) {
            TrafficClassInfoEntity trafficClassInfoEntity = this.sTrafficClassInfoToTrafficClassInfoEntity(sTrafficClassInfo);
            if (trafficClassInfoEntityMaps.containsKey(routerID)) {
                trafficClassInfoEntityMaps.get(routerID).put(sTrafficClassInfo.getTrafficClassName(),trafficClassInfoEntity);
            } else {
                trafficClassInfoEntityMap = new HashMap<>();
                trafficClassInfoEntityMap.put(trafficClassInfoEntity.getTrafficClassName(),trafficClassInfoEntity);
                trafficClassInfoEntityMaps.put(routerID,trafficClassInfoEntityMap);
            }
        }
        return true;
    }

    public boolean syncTrafficPolicyTrafficBehaveConf(String routerID, NetconfClient netconfClient) {
        String commandTrafficBehaveXml = TrafficBehaviorXml.getTrafficBehaviorXml();
        LOG.info("commandTrafficBehaveXml : " + commandTrafficBehaveXml);
        String outPutTrafficBehaveXml = netconfController.sendMessage(netconfClient,commandTrafficBehaveXml);
        LOG.info("outPutTrafficBehaveXml : " + outPutTrafficBehaveXml);

        List<STrafficBehaveInfo> sTrafficBehaveInfoList = TrafficBehaviorXml.getSTrafficBehaveFromXml(outPutTrafficBehaveXml);

        for (STrafficBehaveInfo sTrafficBehaveInfo : sTrafficBehaveInfoList) {
            TrafficBehaveInfoEntity trafficBehaveInfoEntity = this.sTrafficBehaveInfoToTrafficBehaveInfoEntity(sTrafficBehaveInfo);
            if (trafficBehaveInfoEntityMaps.containsKey(routerID)) {
                trafficBehaveInfoEntityMaps.get(routerID).put(sTrafficBehaveInfo.getTrafficBehaveName(),trafficBehaveInfoEntity);
            } else {
                trafficBehaveInfoEntityMap = new HashMap<>();
                trafficBehaveInfoEntityMap.put(trafficBehaveInfoEntity.getTrafficBehaveName(),trafficBehaveInfoEntity);
                trafficBehaveInfoEntityMaps.put(routerID,trafficBehaveInfoEntityMap);
            }
        }
        return true;
    }

    public boolean syncTrafficPolicyTrafficPolicyConf(String routerID, NetconfClient netconfClient) {
        String commandTrafficPolicyXml = TrafficPolicyXml.getTrafficPolicyXml();
        LOG.info("commandTrafficPolicyXml : " + commandTrafficPolicyXml);
        String outPutTrafficPolicyXml = netconfController.sendMessage(netconfClient,commandTrafficPolicyXml);
        LOG.info("outPutTrafficPolicyXml : " + outPutTrafficPolicyXml);

        List<STrafficPolicyInfo> sTrafficPolicyInfoList = TrafficPolicyXml.getSTrafficPolicyFromXml(outPutTrafficPolicyXml);

        for (STrafficPolicyInfo sTrafficPolicyInfo : sTrafficPolicyInfoList) {
            TrafficPolicyInfoEntity trafficPolicyInfoEntity = this.sTrafficPolicyInfoToTrafficPolicyInfoEntity(sTrafficPolicyInfo);
            if (trafficPolicyInfoEntityMaps.containsKey(routerID)) {
                trafficPolicyInfoEntityMaps.get(routerID).put(sTrafficPolicyInfo.getTrafficPolicyName(),trafficPolicyInfoEntity);
            } else {
                trafficPolicyInfoEntityMap = new HashMap<>();
                trafficPolicyInfoEntityMap.put(trafficPolicyInfoEntity.getTrafficPolicyName(),trafficPolicyInfoEntity);
                trafficPolicyInfoEntityMaps.put(routerID,trafficPolicyInfoEntityMap);
            }
        }
        return true;
    }

    public boolean syncTrafficPolicyTrafficIfPolicyConf(String routerID, NetconfClient netconfClient) {
        String commandTrafficIfPolicyXml = TrafficPolicyApplyXml.getTrafficPolicyXml();
        LOG.info("commandTrafficIfPolicyXml : " + commandTrafficIfPolicyXml);
        String outPutTrafficIfPolicyXml = netconfController.sendMessage(netconfClient,commandTrafficIfPolicyXml);
        LOG.info("outPutTrafficIfPolicyXml : " + outPutTrafficIfPolicyXml);

        List<STrafficIfPolicyInfo> sTrafficIfPolicyInfoList = TrafficPolicyApplyXml.getSTrafficIfPolicyFromXml(outPutTrafficIfPolicyXml);

        for (STrafficIfPolicyInfo sTrafficIfPolicyInfo : sTrafficIfPolicyInfoList) {
            TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity = this.sTrafficIfPolicyInfoToTrafficIfPolicyInfoEntity(sTrafficIfPolicyInfo);
            if (trafficIfPolicyInfoEntityMaps.containsKey(routerID)) {
                trafficIfPolicyInfoEntityMaps.get(routerID).put(sTrafficIfPolicyInfo.getIfName(),trafficIfPolicyInfoEntity);
            } else {
                trafficIfPolicyInfoEntityMap = new HashMap<>();
                trafficIfPolicyInfoEntityMap.put(trafficIfPolicyInfoEntity.getIfName(),trafficIfPolicyInfoEntity);
                trafficIfPolicyInfoEntityMaps.put(routerID,trafficIfPolicyInfoEntityMap);
            }
        }
        return true;
    }


    @Override
    public Map<String, Map<String, AclInfoEntity>> getAllAclInfoEntity() {
        return this.aclInfoEntityMaps;
    }

    @Override
    public Map<String, Map<String, TrafficClassInfoEntity>> getAllTrafficClassInfoEntity() {
        return this.trafficClassInfoEntityMaps;
    }

    @Override
    public Map<String, Map<String, TrafficBehaveInfoEntity>> getAllTrafficBehaveInfoEntity() {
        return this.trafficBehaveInfoEntityMaps;
    }

    @Override
    public Map<String, Map<String, TrafficPolicyInfoEntity>> getAllTrafficPolicyInfoEntity() {
        return this.trafficPolicyInfoEntityMaps;
    }

    @Override
    public Map<String, Map<String, TrafficIfPolicyInfoEntity>> getAllTrafficIfPolicyInfoEntity() {
        return this.trafficIfPolicyInfoEntityMaps;
    }

    private AclInfoEntity sAclInfoToAclInfoEntity(SAclInfo sAclInfo) {
        AclInfoEntity aclInfoEntity = new AclInfoEntity();
        aclInfoEntity.setAclName(sAclInfo.getAclNumOrName());

        return aclInfoEntity;
    }

    private TrafficClassInfoEntity sTrafficClassInfoToTrafficClassInfoEntity(STrafficClassInfo sTrafficClassInfo) {
        TrafficClassInfoEntity trafficClassInfoEntity = new TrafficClassInfoEntity();
        trafficClassInfoEntity.setTrafficClassName(sTrafficClassInfo.getTrafficClassName());
        return trafficClassInfoEntity;
    }

    private TrafficBehaveInfoEntity sTrafficBehaveInfoToTrafficBehaveInfoEntity(STrafficBehaveInfo sTrafficBehaveInfo) {
        TrafficBehaveInfoEntity trafficBehaveInfoEntity = new TrafficBehaveInfoEntity();
        trafficBehaveInfoEntity.setTrafficBehaveName(sTrafficBehaveInfo.getTrafficBehaveName());
        return trafficBehaveInfoEntity;
    }

    private TrafficPolicyInfoEntity sTrafficPolicyInfoToTrafficPolicyInfoEntity(STrafficPolicyInfo sTrafficPolicyInfo) {
        TrafficPolicyInfoEntity trafficPolicyInfoEntity = new TrafficPolicyInfoEntity();
        trafficPolicyInfoEntity.setTrafficPolicyName(sTrafficPolicyInfo.getTrafficPolicyName());
        return trafficPolicyInfoEntity;
    }

    private TrafficIfPolicyInfoEntity sTrafficIfPolicyInfoToTrafficIfPolicyInfoEntity(STrafficIfPolicyInfo sTrafficIfPolicyInfo) {
        TrafficIfPolicyInfoEntity trafficIfPolicyInfoEntity = new TrafficIfPolicyInfoEntity();
        trafficIfPolicyInfoEntity.setIfName(sTrafficIfPolicyInfo.getIfName());
        return trafficIfPolicyInfoEntity;
    }
}
