package cn.org.upbnc.util.netconf;

public class SRoutePolicyNode {
    private String nodeSequence;

    public String getNodeSequence() {
        return nodeSequence;
    }

    public void setNodeSequence(String nodeSequence) {
        this.nodeSequence = nodeSequence;
    }

    @Override
    public String toString() {
        return "SRoutePolicyNode{" +
                "nodeSequence='" + nodeSequence + '\'' +
                '}';
    }
}
