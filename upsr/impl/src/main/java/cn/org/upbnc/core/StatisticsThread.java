package cn.org.upbnc.core;

import cn.org.upbnc.api.StatisticsApi;
import cn.org.upbnc.api.impl.StatisticsApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsThread.class);
    StatisticsApi statisticsApi = StatisticsApiImpl.getInstance();
    boolean stopMe = true;

    public void stopMe() {
        this.stopMe = false;
    }

    @Override
    public void run() {
        while (stopMe) {
            try {
                Thread.sleep(1000 * 60 * 5);
                statisticsApi.setStatistics();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}