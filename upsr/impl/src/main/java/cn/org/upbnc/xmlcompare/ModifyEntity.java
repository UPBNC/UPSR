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
