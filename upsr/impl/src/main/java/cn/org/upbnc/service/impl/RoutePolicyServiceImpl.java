package cn.org.upbnc.service.impl;

import cn.org.upbnc.base.BaseInterface;
import cn.org.upbnc.base.DeviceManager;
import cn.org.upbnc.base.NetConfManager;
import cn.org.upbnc.base.RoutePolicyManager;
import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.RoutePolicy;
import cn.org.upbnc.entity.RoutePolicyNode;
import cn.org.upbnc.service.RoutePolicyService;
import cn.org.upbnc.service.entity.RoutePolicyEntity;
import cn.org.upbnc.service.entity.RoutePolicyNodeEntity;
import cn.org.upbnc.util.netconf.NetconfClient;
import cn.org.upbnc.util.netconf.SRoutePolicy;
import cn.org.upbnc.util.netconf.SRoutePolicyNode;
import cn.org.upbnc.util.xml.CheckXml;
import cn.org.upbnc.util.xml.RoutePolicyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static cn.org.upbnc.base.impl.NetConfManagerImpl.netconfController;

public class RoutePolicyServiceImpl implements RoutePolicyService {
    private static final Logger LOG = LoggerFactory.getLogger(VPNServiceImpl.class);
    private static RoutePolicyService instance = null;
    private BaseInterface baseInterface;
    private RoutePolicyManager routePolicyManager;
    private NetConfManager netConfManager;
    private DeviceManager deviceManager;

    public static RoutePolicyService getInstance() {
        if (null == instance) {
            instance = new RoutePolicyServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean setBaseInterface(BaseInterface baseInterface) {
        this.baseInterface = baseInterface;
        if (null != baseInterface) {
            this.netConfManager = this.baseInterface.getNetConfManager();
            this.routePolicyManager = this.baseInterface.getRoutePolicyManager();
            this.deviceManager = this.baseInterface.getDeviceManager();
        }
        return true;
    }

    @Override
    public boolean syncRoutePolicyConf() {
        List<RoutePolicy> routePolicies;
        RoutePolicy routePolicy;
        String routerId;
        List<RoutePolicyNode> routePolicyNodes;
        RoutePolicyNode routePolicyNode;
        for (Device device : deviceManager.getDeviceList()) {
            routePolicies = new ArrayList<>();
            routerId = device.getRouterId();
            NetconfClient netconfClient = this.netConfManager.getNetconClient(routerId);
            if (null != netconfClient) {
                String sendMsg = RoutePolicyXml.getRoutePolicyXml("");
                LOG.info("getRoutePolicyXml sendMsg={}", new Object[]{sendMsg});
                String result = netconfController.sendMessage(netconfClient, sendMsg);
                LOG.info("getRoutePolicyXml result={}", new Object[]{result});
                List<SRoutePolicy> sRoutePolicies = RoutePolicyXml.getRoutePolicyFromXml(result);
                for (SRoutePolicy sroutePolicy : sRoutePolicies) {
                    routePolicy = new RoutePolicy();
                    routePolicy.setRouterId(routerId);
                    routePolicy.setPolicyName(sroutePolicy.getName());
                    routePolicyNodes = new ArrayList<>();
                    for (SRoutePolicyNode sRoutePolicyNode : sroutePolicy.getRoutePolicyNodes()) {
                        routePolicyNode = new RoutePolicyNode();
                        routePolicyNode.setNodeSequence(sRoutePolicyNode.getNodeSequence());
                        routePolicyNodes.add(routePolicyNode);
                    }
                    routePolicy.setRoutePolicyNodes(routePolicyNodes);
                    routePolicies.add(routePolicy);
                }
                routePolicyManager.updateRoutePolicys(routePolicies);
            }
        }
        return true;
    }

    @Override
    public boolean createRoutePolicys(List<RoutePolicyEntity> routePolicyEntities) {
        boolean flag = false;
        if (routePolicyEntities.size() > 0) {
            List<SRoutePolicy> routePolicies = routePolicyEntityMapToSRoutePolicy(routePolicyEntities);
            NetconfClient netconfClient = this.netConfManager.getNetconClient(routePolicyEntities.get(0).getRouterId());
            String sendMsg = RoutePolicyXml.getCreateRoutePolicyXml(routePolicies);
            LOG.info("getRoutePolicyXml sendMsg={}", new Object[]{sendMsg});
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
                flag = true;
            }
            if (flag) {
                List<RoutePolicy> routePolicy = routePolicyEntityMapToRoutePolicy(routePolicyEntities);
                routePolicyManager.updateRoutePolicys(routePolicy);
            }
        }
        return flag;
    }

    private List<RoutePolicy> routePolicyEntityMapToRoutePolicy(List<RoutePolicyEntity> routePolicyEntities) {
        List<RoutePolicy> routePolicies = new ArrayList<>();
        RoutePolicy routePolicy;
        List<RoutePolicyNode> routePolicyNodes;
        RoutePolicyNode routePolicyNode;
        for (RoutePolicyEntity routePolicyEntity : routePolicyEntities) {
            routePolicy = new RoutePolicy();
            routePolicyNodes = new ArrayList<>();
            routePolicy.setPolicyName(routePolicyEntity.getPolicyName());
            routePolicy.setRouterId(routePolicyEntity.getRouterId());
            for (RoutePolicyNodeEntity entity : routePolicyEntity.getRoutePolicyNodes()) {
                routePolicyNode = new RoutePolicyNode();
                routePolicyNode.setNodeSequence(entity.getNodeSequence());
                routePolicyNodes.add(routePolicyNode);
            }
            routePolicy.setRoutePolicyNodes(routePolicyNodes);
            routePolicies.add(routePolicy);
        }
        return routePolicies;
    }


    private List<SRoutePolicy> routePolicyEntityMapToSRoutePolicy(List<RoutePolicyEntity> routePolicyEntities) {
        List<SRoutePolicy> routePolicies = new ArrayList<>();
        SRoutePolicy routePolicy;
        List<SRoutePolicyNode> routePolicyNodes;
        SRoutePolicyNode routePolicyNode;
        for (RoutePolicyEntity routePolicyEntity : routePolicyEntities) {
            routePolicy = new SRoutePolicy();
            routePolicyNodes = new ArrayList<>();
            routePolicy.setName(routePolicyEntity.getPolicyName());
            for (RoutePolicyNodeEntity entity : routePolicyEntity.getRoutePolicyNodes()) {
                routePolicyNode = new SRoutePolicyNode();
                routePolicyNode.setNodeSequence(entity.getNodeSequence());
                routePolicyNodes.add(routePolicyNode);
            }
            routePolicy.setRoutePolicyNodes(routePolicyNodes);
            routePolicies.add(routePolicy);
        }
        return routePolicies;
    }


    @Override
    public boolean deleteRoutePolicys(List<RoutePolicyEntity> routePolicyEntities) {
        boolean flag = false;
        if (routePolicyEntities.size() > 0) {
            List<SRoutePolicy> routePolicies = routePolicyEntityMapToSRoutePolicy(routePolicyEntities);
            NetconfClient netconfClient = this.netConfManager.getNetconClient(routePolicyEntities.get(0).getRouterId());
            String sendMsg = RoutePolicyXml.getDeleteRoutePolicyXml(routePolicies);
            LOG.info("getRoutePolicyXml sendMsg={}", new Object[]{sendMsg});
            String result = netconfController.sendMessage(netconfClient, sendMsg);
            if (CheckXml.RESULT_OK.equals(CheckXml.checkOk(result))) {
                flag = true;
            }
            if (flag) {
                List<RoutePolicy> routePolicy = routePolicyEntityMapToRoutePolicy(routePolicyEntities);
                routePolicyManager.deletePolicys(routePolicy);
            }
        }
        return flag;
    }

    @Override
    public List<RoutePolicyEntity> getRoutePolicys(String routerId, String policyName) {
        List<RoutePolicyEntity> routePolicyEntities = new ArrayList<>();
        RoutePolicyEntity routePolicyEntity;
        List<RoutePolicyNodeEntity> routePolicyNodeEntities;
        RoutePolicyNodeEntity routePolicyNodeEntity;
        List<RoutePolicy> routePolicyList;
        if ("".equals(routerId)) {
            routePolicyList = routePolicyManager.getAllRoutePolicys();
        } else {
            routePolicyList = routePolicyManager.getRoutePolicys(routerId, policyName);
        }
        for (RoutePolicy routePolicy : routePolicyList) {
            routePolicyEntity = new RoutePolicyEntity();
            routePolicyEntity.setRouterId(routePolicy.getRouterId());
            routePolicyEntity.setPolicyName(routePolicy.getPolicyName());
            routePolicyNodeEntities = new ArrayList<>();
            for (RoutePolicyNode routePolicyNode : routePolicy.getRoutePolicyNodes()) {
                routePolicyNodeEntity = new RoutePolicyNodeEntity();
                routePolicyNodeEntity.setNodeSequence(routePolicyNode.getNodeSequence());
                routePolicyNodeEntities.add(routePolicyNodeEntity);
            }
            routePolicyEntity.setRoutePolicyNodes(routePolicyNodeEntities);
            routePolicyEntities.add(routePolicyEntity);
        }
        return routePolicyEntities;
    }
}
