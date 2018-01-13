package cn.org.upbnc.service.entity.TrafficPolicy;

import java.util.List;

public class AclInfoServiceEntity {
    String routerId;

    String aclName;

    List<AclRuleInfoServiceEntity> aclRuleInfoServiceEntityList;

    public List<AclRuleInfoServiceEntity> getAclRuleInfoServiceEntityList() {
        return aclRuleInfoServiceEntityList;
    }

    public void setAclRuleInfoServiceEntityList(List<AclRuleInfoServiceEntity> aclRuleInfoServiceEntityList) {
        this.aclRuleInfoServiceEntityList = aclRuleInfoServiceEntityList;
    }

    public String getRouterId() {
        return routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getAclName() {
        return aclName;
    }

    public void setAclName(String aclName) {
        this.aclName = aclName;
    }
}
