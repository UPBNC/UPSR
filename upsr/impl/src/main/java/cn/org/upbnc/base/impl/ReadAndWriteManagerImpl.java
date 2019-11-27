package cn.org.upbnc.base.impl;

import cn.org.upbnc.base.ReadAndWriteManager;
import cn.org.upbnc.entity.statistics.Statistics;
import cn.org.upbnc.entity.statistics.IfStatisticsEntity;
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
    private String path;

    public ReadAndWriteManagerImpl() {
        this.path = "./statistics.txt";
        this.file = new File(path);
    }


    public static ReadAndWriteManager getInstance() {
        if (null == readAndWriteManager) {
            readAndWriteManager = new ReadAndWriteManagerImpl();
        }
        return readAndWriteManager;
    }

    @Override
    public void write(List<Statistics> statistics) {
        String userString = "";
        for (Statistics statistic : statistics) {
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
                fos = new FileOutputStream(path);
            } else {
                fos = new FileOutputStream(path, true);
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
    public List<Statistics> read(TimeEnum time) {

        List<Statistics> statistics = new ArrayList<>();
        File file = new File(path);
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
                    List<Statistics> list = (List<Statistics>) JSONArray.toList(jsonArray, Statistics.class);
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
