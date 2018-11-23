package cn.org.upbnc.entity;

public class Prefix {
    private String prefix;
    private Integer metric;

    public Prefix(){
        this.metric = 0;
        this.prefix = null;
    }

    public Prefix(String prefix,Integer metric){
        this.prefix = prefix;
        this.metric = metric;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getMetric() {
        return metric;
    }

    public void setMetric(Integer metric) {
        this.metric = metric;
    }
}
