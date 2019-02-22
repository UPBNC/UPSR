package cn.org.upbnc.util.netconf;

public class SPeerAF {
    private String importRtPolicyName;

    public String getImportRtPolicyName() {
        return importRtPolicyName;
    }

    public void setImportRtPolicyName(String importRtPolicyName) {
        this.importRtPolicyName = importRtPolicyName;
    }

    @Override
    public String toString() {
        return "SPeerAF{" +
                "importRtPolicyName='" + importRtPolicyName + '\'' +
                '}';
    }
}
