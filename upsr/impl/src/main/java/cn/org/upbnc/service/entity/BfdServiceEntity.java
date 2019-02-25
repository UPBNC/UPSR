package cn.org.upbnc.service.entity;

public class BfdServiceEntity {
    private Integer type;
    private String bfdId;
    private String minRecvTime;
    private String minSendTime;
    private Integer timeout;
    private Integer discriminatorLocal;
    private Integer discriminatorRemote;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getBfdId() {
        return bfdId;
    }

    public void setBfdId(String bfdId) {
        this.bfdId = bfdId;
    }

    public String getMinRecvTime() {
        return minRecvTime;
    }

    public void setMinRecvTime(String minRecvTime) {
        this.minRecvTime = minRecvTime;
    }

    public String getMinSendTime() {
        return minSendTime;
    }

    public void setMinSendTime(String minSendTime) {
        this.minSendTime = minSendTime;
    }

    public void setDiscriminatorRemote(Integer discriminatorRemote) {
        this.discriminatorRemote = discriminatorRemote;
    }

    public Integer getDiscriminatorRemote() {
        return discriminatorRemote;
    }

    public Integer getDiscriminatorLocal() {
        return discriminatorLocal;
    }

    public void setDiscriminatorLocal(Integer discriminatorLocal) {
        this.discriminatorLocal = discriminatorLocal;
    }
}
