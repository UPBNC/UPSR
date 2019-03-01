package cn.org.upbnc.service.entity;

public class RoutePolicyNodeEntity {
    private String nodeSequence;

    public String getNodeSequence() {
        return nodeSequence;
    }

    public void setNodeSequence(String nodeSequence) {
        this.nodeSequence = nodeSequence;
    }

    @Override
    public String toString() {
        return "RoutePolicyNodeEntity{" +
                "nodeSequence='" + nodeSequence + '\'' +
                '}';
    }
}
