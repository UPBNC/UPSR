package cn.org.upbnc.util.path;

import java.util.ArrayList;
import java.util.List;

public class NodeNeighbors {
    String node;
    List<NodeNeighbor> neighborList;

    public NodeNeighbors(String node) {
        this.node = node;
        this.neighborList = new ArrayList<>();
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public List<NodeNeighbor> getNeighborList() {
        return neighborList;
    }

    public void setNeighborList(List<NodeNeighbor> neighborList) {
        this.neighborList = neighborList;
    }

    public void addNeighbor(String target,int weight){
        if(weight > 0 && null != target) {
            this.addNeighbor(null, target, null, weight);
        }
    }

    public void addNeighbor(String port,String target,String targetPort,int weight){
        if(weight > 0 && null != target) {
            NodeNeighbor neighbor = new NodeNeighbor(this.node, port, target, targetPort, weight);
            this.neighborList.add(neighbor);
        }
        return;
    }

    public void addNeighbor(NodeNeighbor neighbor){
        if(null != neighbor) {
            this.neighborList.add(neighbor);
        }
        return;
    }
}
