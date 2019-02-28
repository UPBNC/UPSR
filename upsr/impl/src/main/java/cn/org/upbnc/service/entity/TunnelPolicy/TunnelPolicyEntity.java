package cn.org.upbnc.service.entity.TunnelPolicy;

import java.util.ArrayList;
import java.util.List;

public class TunnelPolicyEntity {

    private String routerID;
    private String tnlPolicyName;//length 1..39  case-sensitive characters, spaces not supported

    private String description;//length 1..80

    private String tnlPolicyType;//"Tunnel policy type. The available options are sel-seq, binding, and invalid. A
    // tunnel policy can be configured with only one policy type."

    private List<TpNexthopEntity> tpNexthopEntities;//List of tunnel binding configurations;tnlPolicyType='tnlBinding' and count
    // (tpNexthop)>=1

    private List<TnlSelSeqEntity> tnlSelSeqlEntities;//Sequence in which different types of tunnels are selected. If the value is
    // INVALID, no tunnel type has been configured.tnlPolicyType='tnlSelectSeq' and count(tnlSelSeq)>=1


    public TunnelPolicyEntity() {
        this.routerID=null;
        this.tnlPolicyName = null;
        this.description = null;
        this.tnlPolicyType = null;
        this.tpNexthopEntities = new ArrayList<TpNexthopEntity>();
        this.tnlSelSeqlEntities = new ArrayList<TnlSelSeqEntity>();
    }

    public TunnelPolicyEntity(String routerID,String tnlPolicyName, String description, String tnlPolicyType,
                              List<TpNexthopEntity> tpNexthopEntities,
                              List<TnlSelSeqEntity> tnlSelSeqlEntities) {
        this.routerID=routerID;
        this.tnlPolicyName = tnlPolicyName;
        this.description = description;
        this.tnlPolicyType = tnlPolicyType;
        this.tpNexthopEntities = tpNexthopEntities;
        this.tnlSelSeqlEntities = tnlSelSeqlEntities;
    }

    public String getRouterID() {
        return routerID;
    }

    public void setRouterID(String routerID) {
        this.routerID = routerID;
    }

    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTnlPolicyType() {
        return tnlPolicyType;
    }

    public void setTnlPolicyType(String tnlPolicyType) {
        this.tnlPolicyType = tnlPolicyType;
    }

    public List<TpNexthopEntity> getTpNexthopEntities() {
        return tpNexthopEntities;
    }

    public void setTpNexthopEntities(List<TpNexthopEntity> tpNexthopEntities) {
        this.tpNexthopEntities = tpNexthopEntities;
    }

    public List<TnlSelSeqEntity> getTnlSelSeqlEntities() {
        return tnlSelSeqlEntities;
    }

    public void setTnlSelSeqlEntities(List<TnlSelSeqEntity> tnlSelSeqlEntities) {
        this.tnlSelSeqlEntities = tnlSelSeqlEntities;
    }
}
