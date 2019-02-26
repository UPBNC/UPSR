package cn.org.upbnc.util.netconf;

public class SPeerAF {
    private String importRtPolicyName;
    private String exportRtPolicyName;

    public String getExportRtPolicyName() {
        return exportRtPolicyName;
    }

    public void setExportRtPolicyName(String exportRtPolicyName) {
        this.exportRtPolicyName = exportRtPolicyName;
    }

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
                ", exportRtPolicyName='" + exportRtPolicyName + '\'' +
                '}';
    }
}
