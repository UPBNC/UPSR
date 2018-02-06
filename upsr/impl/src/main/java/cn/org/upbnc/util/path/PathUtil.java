package cn.org.upbnc.util.path;

import java.util.ArrayList;
import java.util.List;

public class PathUtil {
    String src;
    String dst;
    List<String> path;

    int firstWeight;
    int totalWeight;
    int step;

    public PathUtil() {
        this.firstWeight = 0;
        this.totalWeight = 0;
        this.src = null;
        this.dst = null;
        this.path = new ArrayList<String>();
    }

    public PathUtil(String src, String dst, List<String> path, int firstWeight, int totalWeight, int step) {
        this.src = src;
        this.dst = dst;
        this.path = new ArrayList<>(path);
        this.firstWeight = firstWeight;
        this.totalWeight = totalWeight;
        this.step = step;
    }

    public PathUtil(PathUtil p){
        this.src = p.src;
        this.dst = p.dst;
        this.firstWeight = p.firstWeight;
        this.totalWeight = p.totalWeight;
        this.step = p.step;
        this.path = new ArrayList<>(p.path);
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public void addNode(String node){
        this.path.add(node);
    }

    public int getFirstWeight() {
        return firstWeight;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public boolean addNode(String node,int weight){
        if( null != node && weight > 0) {
            this.firstWeight = this.firstWeight == 0 ? weight : this.firstWeight;
            this.totalWeight += weight;
            this.step++;
            this.path.add(node);
            this.dst = node;
            return true;
        }else {
            return false;
        }
    }

    // 判断路径是否存在节点
    public boolean isContainsNode(String node){
        return this.path.contains(node);
    }
}
