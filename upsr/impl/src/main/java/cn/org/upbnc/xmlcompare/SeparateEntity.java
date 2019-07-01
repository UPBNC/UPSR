package cn.org.upbnc.xmlcompare;

import java.util.ArrayList;
import java.util.List;

public class SeparateEntity {
    private int num;
    private String path;
    private String nextPath;
    private List<ActionEntity> actionEntities = new ArrayList<>();

    public List<ActionEntity> getActionEntities() {
        return actionEntities;
    }

    public void setActionEntities(List<ActionEntity> actionEntities) {
        this.actionEntities = actionEntities;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SeparateEntity{" +
                "num=" + num +
                ", path='" + path + '\'' +
                ", nextPath='" + nextPath + '\'' +
                ", actionEntities=" + actionEntities +
                '}';
    }

    public String getNextPath() {
        return nextPath;
    }

    public void setNextPath(String nextPath) {
        this.nextPath = nextPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
