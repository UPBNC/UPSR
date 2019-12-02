package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.ReadAndWriteManager;
import cn.org.upbnc.entity.statistics.IfClearedStatEntity;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
import cn.org.upbnc.entity.statistics.CpuInfoEntity;
import cn.org.upbnc.entity.statistics.MemoryInfoEntity;
import cn.org.upbnc.enumtype.TimeEnum;
import cn.org.upbnc.util.TimeUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadAndWriteManagerImpl implements ReadAndWriteManager {

    public static ReadAndWriteManagerImpl readAndWriteManager = new ReadAndWriteManagerImpl();
    private File file = null;
    private String pathIfClearedStat;
    private String pathIfStatistics;
    private String pathCpuInfo;
    private String pathMemoryInfo;

    public ReadAndWriteManagerImpl() {
        this.pathIfClearedStat = "./statistics.txt";
        this.file = new File(pathIfClearedStat);
    }


    public static ReadAndWriteManager getInstance() {
        if (null == readAndWriteManager) {
            readAndWriteManager = new ReadAndWriteManagerImpl();
        }
        return readAndWriteManager;
    }

    @Override
    public void writeIfClearedStat(List<IfClearedStatEntity> statistics) {
        String userString = "";
        for (IfClearedStatEntity statistic : statistics) {
            JSONObject jsonObject = JSONObject.fromObject(statistic);
            if (("").equals(userString)) {
                userString = jsonObject.toString();
            } else {
                userString = userString + "," + jsonObject.toString();
            }
        }
        try {
            FileOutputStream fos = null;
            OutputStreamWriter os;
            if (!file.exists()) {
                file.createNewFile();
                fos = new FileOutputStream(pathIfClearedStat);
            } else {
                fos = new FileOutputStream(pathIfClearedStat, true);
            }
            os = new OutputStreamWriter(
                    fos, "UTF-8");
            os.write(userString);
            os.append("\r\n");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeIfStatisticsEntity(List<IfStatisticsEntity> ifStatisticsEntityList) {
        return;
    }

    @Override
    public void writeCpuInfoEntity(List<CpuInfoEntity> cpuInfoEntityList) {
        return;
    }

    @Override
    public void writeMemoryInfoEntity(List<MemoryInfoEntity> memoryInfoEntityList) {
        return;
    }

    @Override
    public List<IfClearedStatEntity> readIfClearedStat(TimeEnum time) {

        List<IfClearedStatEntity> statistics = new ArrayList<>();
        File file = new File(pathIfClearedStat);
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(
                        new FileInputStream(file), "UTF-8"
                );
                BufferedReader bufferedReader = new BufferedReader(is);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String end = "[" + lineTxt + "]";
                    JSONArray jsonArray = JSONArray.fromObject(end);
                    List<IfClearedStatEntity> list = (List<IfClearedStatEntity>) JSONArray.toList(jsonArray, IfClearedStatEntity.class);
                    if (list.size() > 0) {
                        if(time.equals(TimeEnum.Day)){
                            if(TimeUtils.isToday(list.get(0).getDate())){
                                statistics.addAll(list);
                            }
                        }else if(time.equals(TimeEnum.Week)){
                            if(TimeUtils.isThisWeek(list.get(0).getDate())){
                                statistics.addAll(list);
                            }
                        }else if(time.equals(TimeEnum.Month)){
                            if(TimeUtils.isThisMonth(list.get(0).getDate())){
                                statistics.addAll(list);
                            }
                        }
                    }
                }
                is.close();
            } else {
                System.out.println("找不到指定文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statistics;
    }
}
