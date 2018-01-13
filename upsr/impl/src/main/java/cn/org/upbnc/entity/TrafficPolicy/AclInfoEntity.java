package cn.org.upbnc.entity.TrafficPolicy;

import java.util.List;

public class AclInfoEntity {
    String aclName;

    List<AclRuleInfoEntity> aclRuleInfoEntityList;

    public List<AclRuleInfoEntity> getAclRuleInfoEntityList() {
        return aclRuleInfoEntityList;
    }

    public void setAclRuleInfoEntityList(List<AclRuleInfoEntity> aclRuleInfoEntityList) {
        this.aclRuleInfoEntityList = aclRuleInfoEntityList;
    }

    public String getAclName() {
        return aclName;
    }

    public void setAclName(String aclName) {
        this.aclName = aclName;
    }
}
