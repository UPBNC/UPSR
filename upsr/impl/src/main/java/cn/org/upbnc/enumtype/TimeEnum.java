package cn.org.upbnc.enumtype;

public enum TimeEnum {
    Day("1", "Day"), Week("2", "Week"), Month("3", "Month");
    String id;
    String date;

    TimeEnum(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static TimeEnum getEnum(String id) {
        for (TimeEnum timeEnum : TimeEnum.values()) {
            if (timeEnum.getId().equals(id)) {
                return timeEnum;
            }
        }
        return null;
    }
}
