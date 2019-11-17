package cn.org.upbnc.service.entity.actionCfg;

import java.util.List;

public class CheckPointInfoServiceEntity {
    String commitId;
    String userLabel;
    String userName;
    String timeStamp;
    List<PointChangeInfoServiceEntity> sinceList;
    List<PointChangeInfoServiceEntity> curList;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(String userLabel) {
        this.userLabel = userLabel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<PointChangeInfoServiceEntity> getSinceList() {
        return sinceList;
    }

    public void setSinceList(List<PointChangeInfoServiceEntity> sinceList) {
        this.sinceList = sinceList;
    }

    public List<PointChangeInfoServiceEntity> getCurList() {
        return curList;
    }

    public void setCurList(List<PointChangeInfoServiceEntity> curList) {
        this.curList = curList;
    }
}
