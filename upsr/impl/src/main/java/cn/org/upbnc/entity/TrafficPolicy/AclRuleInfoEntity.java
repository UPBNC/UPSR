package cn.org.upbnc.entity.TrafficPolicy;

public class AclRuleInfoEntity {
    String ruleId;
    String ruleType;
    String protoType;
    String sourcce;
    String sourcceWild;
    String sourcePortOp;
    String sourcePort;
    String destination;
    String destinationWild;
    String destinationPortOp;
    String destinationPort;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getProtoType() {
        return protoType;
    }

    public void setProtoType(String protoType) {
        this.protoType = protoType;
    }

    public String getSourcce() {
        return sourcce;
    }

    public void setSourcce(String sourcce) {
        this.sourcce = sourcce;
    }

    public String getSourcceWild() {
        return sourcceWild;
    }

    public void setSourcceWild(String sourcceWild) {
        this.sourcceWild = sourcceWild;
    }

    public String getSourcePortOp() {
        return sourcePortOp;
    }

    public void setSourcePortOp(String sourcePortOp) {
        this.sourcePortOp = sourcePortOp;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationWild() {
        return destinationWild;
    }

    public void setDestinationWild(String destinationWild) {
        this.destinationWild = destinationWild;
    }

    public String getDestinationPortOp() {
        return destinationPortOp;
    }

    public void setDestinationPortOp(String destinationPortOp) {
        this.destinationPortOp = destinationPortOp;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }
}
