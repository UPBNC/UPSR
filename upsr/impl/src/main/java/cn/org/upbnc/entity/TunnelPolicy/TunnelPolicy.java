package cn.org.upbnc.entity.TunnelPolicy;

import java.util.ArrayList;
import java.util.List;

public class TunnelPolicy {
    private String routerID;

    private String tnlPolicyName;//length 1..39  case-sensitive characters, spaces not supported

    private String description;//length 1..80

    private String tnlPolicyType;//"Tunnel policy type. The available options are sel-seq, binding, and invalid. A
    // tunnel policy can be configured with only one policy type."

    private List<TpNexthop> TpNexthops;//List of tunnel binding configurations;tnlPolicyType='tnlBinding' and count
    // (tpNexthop)>=1

    private List<TnlSelSeq> TnlSelSeqls;//Sequence in which different types of tunnels are selected. If the value is
    // INVALID, no tunnel type has been configured.tnlPolicyType='tnlSelectSeq' and count(tnlSelSeq)>=1


    public TunnelPolicy() {
        this.routerID=null;
        this.tnlPolicyName = null;
        this.description = null;
        this.tnlPolicyType = null;
        this.TpNexthops = new ArrayList<TpNexthop>();
        this.TnlSelSeqls = new ArrayList<TnlSelSeq>();
    }

    public TunnelPolicy(String routerID,String tnlPolicyName, String description, String tnlPolicyType,
                        List<TpNexthop> TpNexthops,
                        List<TnlSelSeq> TnlSelSeqls) {
        this.routerID=routerID;
        this.tnlPolicyName = tnlPolicyName;
        this.description = description;
        this.tnlPolicyType = tnlPolicyType;
        this.TpNexthops = TpNexthops;
        this.TnlSelSeqls = TnlSelSeqls;
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

    public List<TpNexthop> getTpNexthops() {
        return TpNexthops;
    }

    public void setTpNexthops(List<TpNexthop> tpNexthops) {
        this.TpNexthops = tpNexthops;
    }

    public List<TnlSelSeq> getTnlSelSeqls() {
        return TnlSelSeqls;
    }

    public void setTnlSelSeqls(List<TnlSelSeq> tnlSelSeqls) {
        this.TnlSelSeqls = tnlSelSeqls;
    }
}
