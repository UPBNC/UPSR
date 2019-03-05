package cn.org.upbnc.util.path;

public class NodeNeighbor {
    int weight;
    String node;
    String port;
    String targetNode;
    String targetPort;

    public NodeNeighbor() {
        this.weight = 0;
        this.node = null;
        this.port = null;
        this.targetNode = null;
        this.targetPort = null;
    }

    public NodeNeighbor(String node, String port, String targetNode, String targetPort,int weight) {
        this.weight = weight;
        this.node = node;
        this.port = port;
        this.targetNode = targetNode;
        this.targetPort = targetPort;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(String targetNode) {
        this.targetNode = targetNode;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }
}
