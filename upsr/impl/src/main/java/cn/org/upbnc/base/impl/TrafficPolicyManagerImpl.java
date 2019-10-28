package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.TrafficPolicyManager;
import cn.org.upbnc.entity.TrafficPolicy.AclInfoEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.TrafficPolicy.SAclInfo;
import cn.org.upbnc.util.xml.TrafficAclXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class TrafficPolicyManagerImpl implements TrafficPolicyManager {
    private static final Logger LOG = LoggerFactory.getLogger(TrafficPolicyManagerImpl.class);
    private Map<String,AclInfoEntity> aclInfoEntityMap;
    private static TrafficPolicyManager instance;
    private Map<String,Map<String,AclInfoEntity>> aclInfoEntityMaps;

    public TrafficPolicyManagerImpl() {
        this.aclInfoEntityMaps = new HashMap<>();
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

    @Override
    public Map<String, Map<String, AclInfoEntity>> getAllTrafficPolicy() {
        return this.aclInfoEntityMaps;
    }

    private AclInfoEntity sAclInfoToAclInfoEntity(SAclInfo sAclInfo) {
        AclInfoEntity aclInfoEntity = new AclInfoEntity();
        aclInfoEntity.setAclName(sAclInfo.getAclNumOrName());

        return aclInfoEntity;
    }
}
