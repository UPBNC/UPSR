package cn.org.upbnc.service.entity;

public class BfdServiceEntity {
    private Integer type;
    private String bfdId;
    private String minRecvTime;
    private String minSendTime;
    private String multiplier;
    private String discriminatorLocal;
    private String discriminatorRemote;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
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

    public void setDiscriminatorRemote(String discriminatorRemote) {
        this.discriminatorRemote = discriminatorRemote;
    }

    public String getDiscriminatorRemote() {
        return discriminatorRemote;
    }

    public String getDiscriminatorLocal() {
        return discriminatorLocal;
    }

    public void setDiscriminatorLocal(String discriminatorLocal) {
        this.discriminatorLocal = discriminatorLocal;
    }

}
