package cn.org.upbnc.util.netconf.TrafficPolicy;

import java.util.List;

public class SAclInfo {
    String aclNumOrName;
    List<SAclRuleInfo> sAclRuleInfoList;

    public List<SAclRuleInfo> getsAclRuleInfoList() {
        return sAclRuleInfoList;
    }

    public void setsAclRuleInfoList(List<SAclRuleInfo> sAclRuleInfoList) {
        this.sAclRuleInfoList = sAclRuleInfoList;
    }

    public String getAclNumOrName() {
        return aclNumOrName;
    }

    public void setAclNumOrName(String aclNumOrName) {
        this.aclNumOrName = aclNumOrName;
    }
}
