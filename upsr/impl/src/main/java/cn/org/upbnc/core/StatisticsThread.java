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
        this.threadInstance.interrupt();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000 * 60 * 1);
            LOG.info("first statistics thread " + Thread.currentThread() + " stopped " + stopMe);
            statisticsApi.setStatistics();
        } catch (InterruptedException e) {
            if (stopMe == true) {
                LOG.info("Interrupted first statistics thread " + Thread.currentThread() + " stopped");
                return;
            }
        }

        while (stopMe != true) {
            try {
                Thread.sleep(statisticInterval);
                LOG.info("timer statistics thread " + Thread.currentThread() + " stopped " + stopMe);
                if (switchValve.equals("1")) {
                    statisticsApi.setStatistics();
                }
            } catch (InterruptedException e) {
                if (stopMe == true) {
                    LOG.info("Interrupted timer statistics thread " + Thread.currentThread() + " stopped");
                    return;
                }
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
        this.statisticInterval = statisticInterval * 1000 * 60;
    }
}