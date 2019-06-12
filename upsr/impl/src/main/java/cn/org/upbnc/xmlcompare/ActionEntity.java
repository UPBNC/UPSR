package cn.org.upbnc.xmlcompare;

import java.util.ArrayList;
import java.util.List;

public class ActionEntity {
    private String path;
    private ActionTypeEnum action;
    private List<ModifyEntity> modifyEntities = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ActionTypeEnum getAction() {
        return action;
    }

    public void setAction(ActionTypeEnum action) {
        this.action = action;
    }

    public List<ModifyEntity> getModifyEntities() {
        return modifyEntities;
    }

    public void setModifyEntities(List<ModifyEntity> modifyEntities) {
        this.modifyEntities = modifyEntities;
    }

    @Override
    public String toString() {
        return "ActionEntity{" +
                "path='" + path + '\'' +
                ", action=" + action +
                ", modifyEntities=" + modifyEntities +
                '}';
    }
}
