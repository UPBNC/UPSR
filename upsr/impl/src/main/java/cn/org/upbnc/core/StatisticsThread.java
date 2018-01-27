package cn.org.upbnc.core;

import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.api.impl.StatisticsApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsThread.class);
    StatisticsApi statisticsApi = StatisticsApiImpl.getInstance();
    String switchValve = "2"; //1 : 开， 2：关
    private static StatisticsThread threadInstance = null;
    long statisticInterval = 1000 * 60 * 5;

    public static StatisticsThread getInstance() {
        if (threadInstance == null) {
            threadInstance = new StatisticsThread();
        }
        return threadInstance;
    }

    public void stopMe() {
        this.switchValve = "2";
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (switchValve.equals("1")) {
                    statisticsApi.setStatistics();
                }
                Thread.sleep(statisticInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void setSwitchValve(String switchValve) {
        this.switchValve = switchValve;
    }

    public String getSwitchValve() {
        return switchValve;
    }

    public long getStatisticInterval() {
        return statisticInterval /(1000 * 60);
    }

    public void setStatisticInterval(long statisticInterval) {
        this.statisticInterval = statisticInterval * 1000 * 60 ;
    }
}