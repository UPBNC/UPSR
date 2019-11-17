package cn.org.upbnc.util.netconf.actionCfg;

import java.util.List;

public class SCheckPointInfo {
    String commitId;
    String userLabel;
    String userName;
    String timeStamp;
    List<SPointChangeInfo> sinceList;
    List<SPointChangeInfo> currList;

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

    public List<SPointChangeInfo> getSinceList() {
        return sinceList;
    }

    public void setSinceList(List<SPointChangeInfo> sinceList) {
        this.sinceList = sinceList;
    }

    public List<SPointChangeInfo> getCurrList() {
        return currList;
    }

    public void setCurrList(List<SPointChangeInfo> currList) {
        this.currList = currList;
    }
}
