package cn.org.upbnc.util.path;

import cn.org.upbnc.entity.Device;
import cn.org.upbnc.entity.Link;

import java.util.*;

public class CaculaterPaths {
    private final static int MAX_PATH = 2;

    public static List<PathUtil> caculatePathsByDevicesLinks(Map<String,List<Device>> area,
                                                             List<Link> links){
        List<PathUtil> pathUtils = new ArrayList<PathUtil>();
        Map<String,NodeNeighbors> nodeNeighborMap = new HashMap<String,NodeNeighbors>();

        for(Link link : links){
            Device src = link.getDeviceInterface1().getDevice();
            Device dst = link.getDeviceInterface2().getDevice();
            int weight = getLinkWeightByArea(area,src,dst);

            String srcRouterId = src.getRouterId();
            String dstRouterId = dst.getRouterId();

            NodeNeighbors neighbors = nodeNeighborMap.get(srcRouterId);
            if(neighbors == null){
                neighbors = new NodeNeighbors(srcRouterId);
                nodeNeighborMap.put(srcRouterId,neighbors);
            }

            neighbors.addNeighbor(dstRouterId,weight);
        }

//        Set<String> areaKeys =  area.keySet();
//        for(String sourceKey : areaKeys){
//            // create target keys
//            List<String> targetKeys = new ArrayList<>(areaKeys);
//            targetKeys.remove(sourceKey);
//
//            // get source area devices
//            List<Device> sourceDevices = new ArrayList<Device>(area.get(sourceKey));
//
//            // get target area devices
//            List<Device> targetDevices = new ArrayList<Device>();
//            for(String targetKey : targetKeys){
//                targetDevices.addAll(area.get(targetKey));
//            }
//
//            // caculater the path
//            for(Device sourceDevice : sourceDevices){
//                for(Device targetDevice : targetDevices){
//                    pathUtils.addAll(caculatePathsBySrcAndDst(sourceDevice.getRouterId(),targetDevice.getRouterId(),nodeNeighborMap));
//                }
//            }
//        }

        for(List<Device> sourceDevices : area.values()){

            List<List<Device>> areaDevices = new ArrayList<>(area.values());
            areaDevices.remove(sourceDevices);
            List<Device> targetDevices = new ArrayList<>();

            for(List<Device> list : areaDevices){
                targetDevices.addAll(list);
            }


            for(Device sourceDevice : sourceDevices){
                for(Device targetDevice : targetDevices){
                    pathUtils.addAll(caculatePathsBySrcAndDst(sourceDevice.getRouterId(),targetDevice.getRouterId(),nodeNeighborMap));
                }
            }
        }

        return pathUtils;
    }

    private static List<PathUtil> caculatePathsBySrcAndDst(String src,String dst,Map<String,NodeNeighbors> nodeNeighborMap){
        int MaxStep = nodeNeighborMap.keySet().size();
        int currentStep = 0;
        int currentPathNum = 0;
        List<PathUtil> ret = new ArrayList<PathUtil>();
        // init path
        List<PathUtil> currentPaths = new ArrayList<PathUtil>();
        List<PathUtil> tempCurrentPaths = new ArrayList<PathUtil>();
        PathUtil currentPath = new PathUtil();
        currentPath.setSrc(src);
        currentPath.setDst(src);
        currentPaths.add(currentPath);

        while( currentPathNum < MAX_PATH && currentStep < MaxStep && currentPaths.isEmpty()){

            // circle the current
            for(PathUtil p : currentPaths){
                String target = p.getDst();
                NodeNeighbors neighbors = nodeNeighborMap.get(target);
                List<NodeNeighbor> list = neighbors.getNeighborList();
                for(NodeNeighbor n : list){
                    PathUtil tempP = createPaths(p,n);
                    if(tempP.getDst().equals(dst)){
                        ret.add(tempP);
                        currentPathNum++;
                    }else{
                        tempCurrentPaths.add(tempP);
                    }
                }
            }

            currentPaths.clear();
            currentPaths.addAll(tempCurrentPaths);
            tempCurrentPaths.clear();
            currentStep++;
        }

        return ret;
    }

    private static PathUtil createPaths(PathUtil p,NodeNeighbor n){

        if(!p.isContainsNode(n.getTargetNode())){
            PathUtil np = new PathUtil(p);
            np.addNode(n.getTargetNode(),n.getWeight());
            return np;
        }else{
            return null;
        }
    }


    private static int getLinkWeightByArea(Map<String,List<Device>> area,Device src,Device dst){
        Iterator<List<Device>> iterator = area.values().iterator();
        while (iterator.hasNext()){
            List<Device> list = iterator.next();
            if(list.contains(src) && list.contains(dst)){
                return LinkWeightEnum.InnerArea.getIndex();
            }
        }

        return LinkWeightEnum.TagetArea.getIndex();
    }

}
