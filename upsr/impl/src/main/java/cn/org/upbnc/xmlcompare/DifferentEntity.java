package cn.org.upbnc.xmlcompare;

public class DifferentEntity {
    private int id;
    private String description;
    private String controlValue;
    private String controlXpathLocation;
    private String controlNode;
    private String testValue;
    private String testXpathLocation;
    private String testNode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getControlValue() {
        return controlValue;
    }

    public void setControlValue(String controlValue) {
        this.controlValue = controlValue;
    }

    public String getControlXpathLocation() {
        return controlXpathLocation;
    }

    public void setControlXpathLocation(String controlXpathLocation) {
        this.controlXpathLocation = controlXpathLocation;
    }

    public String getControlNode() {
        return controlNode;
    }

    public void setControlNode(String controlNode) {
        this.controlNode = controlNode;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    public String getTestXpathLocation() {
        return testXpathLocation;
    }

    public void setTestXpathLocation(String testXpathLocation) {
        this.testXpathLocation = testXpathLocation;
    }

    public String getTestNode() {
        return testNode;
    }

    public void setTestNode(String testNode) {
        this.testNode = testNode;
    }

    @Override
    public String toString() {
        return "DifferentEntity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", controlValue='" + controlValue + '\'' +
                ", controlXpathLocation='" + controlXpathLocation + '\'' +
                ", controlNode='" + controlNode + '\'' +
                ", testValue='" + testValue + '\'' +
                ", testXpathLocation='" + testXpathLocation + '\'' +
                ", testNode='" + testNode + '\'' +
                '}';
    }
}
