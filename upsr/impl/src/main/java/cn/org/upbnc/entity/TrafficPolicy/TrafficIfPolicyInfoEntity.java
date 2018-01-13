package cn.org.upbnc.entity.TrafficPolicy;

public class TrafficIfPolicyInfoEntity {
    String ifName;

    String direction;

    String policyName;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }
}
