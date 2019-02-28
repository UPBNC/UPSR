package cn.org.upbnc.util.netconf;

import cn.org.upbnc.enumtype.VpnEnum.VpnAdvertiseCommunityEnum;

public class SPeerAF {
    private static String notSet = "未设置";
    private String importRtPolicyName;
    private String exportRtPolicyName;
    private String advertiseCommunity;
    private String remoteAddress;

    public String getExportRtPolicyName() {
        return exportRtPolicyName;
    }

    public void setExportRtPolicyName(String exportRtPolicyName) {
        this.exportRtPolicyName = ((exportRtPolicyName == null) || exportRtPolicyName.equals("") ||
                exportRtPolicyName.equals(notSet))?null:exportRtPolicyName;;
    }

    public String getImportRtPolicyName() {
        return importRtPolicyName;
    }

    public void setImportRtPolicyName(String importRtPolicyName) {
        this.importRtPolicyName = ((importRtPolicyName == null) || importRtPolicyName.equals("") ||
                importRtPolicyName.equals(notSet))?null:importRtPolicyName;
    }

    public String getAdvertiseCommunity() {
        return advertiseCommunity;
    }

    public void setAdvertiseCommunity(String advertiseCommunity) {
        this.advertiseCommunity = (VpnAdvertiseCommunityEnum.ENABLED.getName().equals(advertiseCommunity) ||
                "true".equals(advertiseCommunity))?"true":"false";
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String toString() {
        return "SPeerAF{" +
                "importRtPolicyName='" + importRtPolicyName + '\'' +
                ", exportRtPolicyName='" + exportRtPolicyName + '\'' +
                ", advertiseCommunity='" + advertiseCommunity + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                '}';
    }
}
