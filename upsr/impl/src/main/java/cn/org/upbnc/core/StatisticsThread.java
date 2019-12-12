package cn.org.upbnc.core;

import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.api.impl.StatisticsApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsThread.class);
    StatisticsApi statisticsApi = StatisticsApiImpl.getInstance();
    String switchValve = "1"; //1 : 开， 2：关
    private static StatisticsThread threadInstance = null;
    long statisticInterval = 1000 * 60 * 5;
    boolean stopMe = false;

    public static StatisticsThread getInstance() {
        if (threadInstance == null) {
            threadInstance = new StatisticsThread();
        }
        return threadInstance;
    }

    public void stopMe() {
        this.stopMe = true;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000 * 60 * 1);
            statisticsApi.setStatistics();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (stopMe != true) {
            try {
                Thread.sleep(statisticInterval);
                if (switchValve.equals("1")) {
                    statisticsApi.setStatistics();
                }
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
        return statisticInterval /(1000);
    }

    public void setStatisticInterval(long statisticInterval) {
        this.statisticInterval = statisticInterval * 1000;
    }
}