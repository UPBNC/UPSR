package cn.org.upbnc.entity;

public class RoutePolicyNode {
    private String nodeSequence;


    public String getNodeSequence() {
        return nodeSequence;
    }

    public void setNodeSequence(String nodeSequence) {
        this.nodeSequence = nodeSequence;
    }

    @Override
    public String toString() {
        return "RoutePolicyNode{" +
                "nodeSequence='" + nodeSequence + '\'' +
                '}';
    }

}
