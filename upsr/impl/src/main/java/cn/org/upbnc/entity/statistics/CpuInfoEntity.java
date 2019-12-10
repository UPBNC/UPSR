package cn.org.upbnc.entity.statistics;

public class CpuInfoEntity {
    private long date;
    private String position;
    private String entIndex;
    private String systemCpuUsage;
    private String ovloadThreshold;
    private String unovloadThreshold;
    private String interval;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEntIndex() {
        return entIndex;
    }

    public void setEntIndex(String entIndex) {
        this.entIndex = entIndex;
    }

    public String getSystemCpuUsage() {
        return systemCpuUsage;
    }

    public void setSystemCpuUsage(String systemCpuUsage) {
        this.systemCpuUsage = systemCpuUsage;
    }

    public String getOvloadThreshold() {
        return ovloadThreshold;
    }

    public void setOvloadThreshold(String ovloadThreshold) {
        this.ovloadThreshold = ovloadThreshold;
    }

    public String getUnovloadThreshold() {
        return unovloadThreshold;
    }

    public void setUnovloadThreshold(String unovloadThreshold) {
        this.unovloadThreshold = unovloadThreshold;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
