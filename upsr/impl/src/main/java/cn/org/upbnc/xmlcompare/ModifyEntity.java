package cn.org.upbnc.xmlcompare;

public class ModifyEntity {
    private String path;
    private String label;
    private String odlValue;
    private String newValue;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOdlValue() {
        return odlValue;
    }

    public void setOdlValue(String odlValue) {
        this.odlValue = odlValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClicommand(String action) {
        if (this.label.equals("mplsTunnelEgressLSRId")) {
            return this.getTunnelDestCli("modify");
        }
        return "";
    }
    private String getTunnelDestCli(String action) {
        return " destination " + this.getNewValue() + "\n";
    }
    private String getTunnelBandwidthCli(String action) {
        return " mpls te bandwidth ct0 " + this.getNewValue() + "\n";
    }
    private String getTunnelPath(String action) {
        return " mpls te path explicit-path  " + this.getNewValue() + "\n";
    }
    private String getTunnelBfdCli(String action) {
        return " mpls te bfd enable  " + this.getNewValue() + "\n";
    }
    private String getTunnelServiceClassCli(String action) {
        return " mpls te service-class  " + this.getNewValue() + "\n";
    }

    @Override
    public String toString() {
        return "ModifyEntity{" +
                "path='" + path + '\'' +
                ", label='" + label + '\'' +
                ", odlValue='" + odlValue + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
