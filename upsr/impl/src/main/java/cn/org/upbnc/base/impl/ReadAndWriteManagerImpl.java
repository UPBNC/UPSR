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
import net.sf.json.JsonConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadAndWriteManagerImpl implements ReadAndWriteManager {

    private static ReadAndWriteManagerImpl readAndWriteManager ;
    private File file = null;
    private String pathIfClearedStat;
    private String pathIfStatistics;
    private String pathCpuInfo;
    private String pathMemoryInfo;

    private ReadAndWriteManagerImpl() {
        File folder = new File("./statistics/");
        //文件夹路径不存在，以后使用log4j来做每天一个文件夹的优化
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        } else {
        }
        this.pathIfClearedStat = folder + "/ifClearedStat.txt";
        this.pathIfStatistics = folder + "/ifStatistics.txt";
        this.pathCpuInfo = folder + "/cpuInfo.txt";
        this.pathMemoryInfo = folder + "/memoryInfo.txt";
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
    public void writeIfClearedStatMap(Map<String, List<IfClearedStatEntity>> ifClearedStatMap) {
        if (ifClearedStatMap.size() == 0) {
            return;
        }
        try {
            this.writeStatisticsToFile(pathIfClearedStat,JSONObject.fromObject(ifClearedStatMap));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void writeIfStatisticsMap(Map<String, List<IfStatisticsEntity>> ifStatisticsMap) {
        if (ifStatisticsMap.size() == 0) {
            return;
        }
        try {
            this.writeStatisticsToFile(pathIfStatistics,JSONObject.fromObject(ifStatisticsMap));
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void writeCpuInfoMap(Map<String, List<CpuInfoEntity>> cpuInfoMap) {
        if (cpuInfoMap.size() == 0) {
            return;
        }
        try {
            this.writeStatisticsToFile(pathCpuInfo,JSONObject.fromObject(cpuInfoMap));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void writeMemoryInfoMap(Map<String, List<MemoryInfoEntity>> memoryInfoMap) {
        if (memoryInfoMap.size() == 0) {
            return;
        }
        try {
            this.writeStatisticsToFile(pathMemoryInfo,JSONObject.fromObject(memoryInfoMap));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void writeStatisticsToFile(String filePath, JSONObject jsonObject) throws Exception{
        File file = new File(filePath);
        FileOutputStream fos ;
        OutputStreamWriter os;
        if (!file.exists()) {
            file.createNewFile();
            fos = new FileOutputStream(filePath);
        } else {
            fos = new FileOutputStream(filePath, true);
        }
        os = new OutputStreamWriter(fos, "UTF-8");
        os.write(jsonObject.toString());
        os.append("\r\n");
        os.close();
        return;
    }

    @Override
    public List<Map<String, List<IfClearedStatEntity>>> getIfClearedStatMap(int rows) {
        List<Map<String, List<IfClearedStatEntity>>> mapList = new ArrayList<>();
        File file = new File(pathIfClearedStat);
        try {
            if (file.isFile() && file.exists()) {
                byte[] tempbytes = this.readLastRows(pathIfClearedStat,rows);
                if (tempbytes != null) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tempbytes);
                    InputStream inputStream = byteArrayInputStream;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String lineTxt;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        Map<String, List<IfClearedStatEntity>> tempMap = new HashMap<>();
                        JSONObject jsonObject = JSONObject.fromObject(lineTxt);
                        Map<String, JSONArray> map = (Map<String, JSONArray>) jsonObject;
                        for (String key : map.keySet()) {
                            List<IfClearedStatEntity> ifClearedStatEntityList =
                                    JSONArray.toList(map.get(key), new IfClearedStatEntity(), new JsonConfig());
                            tempMap.put(key, ifClearedStatEntityList);
                        }
                        mapList.add(tempMap);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapList;
    }

    @Override
    public List<Map<String, List<IfStatisticsEntity>>> getIfStatisticsMap(int rows) {
        Map<String, List<IfStatisticsEntity>> retMap = new HashMap<>();
        File file = new File(pathIfStatistics);
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(is);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = JSONObject.fromObject(lineTxt);
                    Map<String, JSONArray> map = (Map<String, JSONArray>)jsonObject;
                    for (String key:map.keySet()) {
                        List<IfStatisticsEntity> ifStatisticsEntityList =
                                JSONArray.toList( map.get(key), new IfStatisticsEntity(),new JsonConfig());
                        retMap.put(key,ifStatisticsEntityList);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public List<Map<String, List<CpuInfoEntity>>> getCpuInfoMap(int rows) {
        Map<String, List<CpuInfoEntity>> retMap = new HashMap<>();
        File file = new File(pathCpuInfo);
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(is);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = JSONObject.fromObject(lineTxt);
                    Map<String, JSONArray> map = (Map<String, JSONArray>)jsonObject;
                    for (String key:map.keySet()) {
                        List<CpuInfoEntity> cpuInfoEntityList =
                                JSONArray.toList( map.get(key), new CpuInfoEntity(),new JsonConfig());
                        retMap.put(key,cpuInfoEntityList);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public List<Map<String, List<MemoryInfoEntity>>> getMemoryInfoMap(int rows) {
        Map<String, List<MemoryInfoEntity>> retMap = new HashMap<>();
        File file = new File(pathMemoryInfo);
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(is);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = JSONObject.fromObject(lineTxt);
                    Map<String, JSONArray> map = (Map<String, JSONArray>)jsonObject;
                    for (String key:map.keySet()) {
                        List<MemoryInfoEntity> memoryInfoEntityList =
                                JSONArray.toList( map.get(key), new MemoryInfoEntity(),new JsonConfig());
                        retMap.put(key,memoryInfoEntityList);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return retMap;
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

    private byte[] readLastRows(String filename, int rows) {
        String lineSeparator = "\r\n";
        try (RandomAccessFile rf = new RandomAccessFile(filename, "r")) {
            // 每次读取的字节数要和系统换行符大小一致
            byte[] c = new byte[lineSeparator.getBytes().length];
            // 在获取到指定行数和读完文档之前,从文档末尾向前移动指针,遍历文档每一个字节
            for (long pointer = rf.length(), lineSeparatorNum = 0; pointer >= 0 && lineSeparatorNum < rows; ) {
                // 移动指针
                rf.seek(pointer--);
                // 读取数据
                int readLength = rf.read(c);
                if (readLength != -1 && new String(c, 0, readLength).equals(lineSeparator)) {
                    lineSeparatorNum++;
                }
                //扫描完依然没有找到足够的行数,将指针归0
                if (pointer == -1 && lineSeparatorNum < rows) {
                    rf.seek(0);
                }
            }
            byte[] tempbytes = new byte[(int) (rf.length() - rf.getFilePointer())];
            rf.readFully(tempbytes);
            rf.close();
            return tempbytes;
        } catch (IOException e) {

        }
        return null;
    }
}
