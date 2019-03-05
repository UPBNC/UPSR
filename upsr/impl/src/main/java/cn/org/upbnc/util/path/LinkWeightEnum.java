package cn.org.upbnc.util.path;

public enum LinkWeightEnum {
    TagetArea(1),
    InnerArea(2),
    OuterArea(3);

    private int index;

    LinkWeightEnum(int idx) {
        this.index = idx;
    }

    public int getIndex() {
        return index;
    }
}
