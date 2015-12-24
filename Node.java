import java.util.*;

public class Node {
    
    public Node(int[] instance, int[] restFeature) {
        this.instance = instance;
        this.restFeature = restFeature;
        init();
    }

    public int predict(int[] testFeature) {
        if (isLeaf) {
            return majorLabel;
        } else {
            int featureVal = testFeature[bestFeature];
            if (!childrenMap.containsKey(featureVal)) {
                return majorLabel;
            } else {
                return childrenMap.get(featureVal).predict(testFeature);
            }
        }
    }

    private void init() {
        restLabelNum = labelNum();
        calSelfEntropy();
        calMajorLabel();
        if (restLabelNum == 1) {
            return;
        } else {
            bestFeature = calBestFeature();
            if (bestFeature != -1) {
                split();
            }
        }
    }

    private void calSelfEntropy() {
        List<Integer> idList = Arrays.asList(instance);
        entropy = calEntropy(idList);
    }

    private void split() {
        isLeaf = false;
        Map<Integer, List<Integer>> featureMap = new HashMap<Integer, List<Integer>>();
        for (int id: instance) {
            int featureVal = feature[id][bestFeature];
            if (!featureMap.containsKey(featureVal)) {
                List<Integer> idList = new ArrayList<Integer>();
                idList.add(id);
                featureMap.put(featureVal, idList);
            } else {
                featureMap.get(featureVal).add(id);
            }
        }
        childrenMap = new HashMap<Integer, Node>();
        int[] childrenFea = new int[restFeature.length - 1];
        int i = 0;
        for (int t: restFeature) {
            if (t != bestFeature) {
                childrenFea[i++] = t;
            }
        }
        for (int t: featureMap.keySet()) {
            List<Integer> idList = featureMap.get(t);
            int[] ids = idList.toArray(new int[idList.size()]);
            Node child = new Node(ids, childrenFea);
            childremMap.put(t, child);
        }
    }

    private int calBestFeature() {
        int bestFeatureIndex = -1;
        double maxInfoGain = 0.0;
        for (int feaIndex: restFeature) {
            Map<Integer, List<Integer>> featureMap = new HashMap<Integer, List<Integer>>();
            for (int id: instance) {
                int featureVal = feature[id][feaIndex];
                if (!featureMap.containsKey(featureVal)) {
                    List<Integer> idList = new ArrayList<Integer>();
                    idList.add(id);
                    featureMap.put(featureVal, idList);
                } else {
                    featureMap.get(featureVal).add(id);
                }
            }
            double splitEntropy = 0.0;
            List<Integer> numList = new ArrayList<Integer>();
            for (int t: featureMap.keySet()) {
                List<Integer> idList = featureMap.get(t);
                numList.add(idList.size());
                splitEntropy += idList.size() * calEntropy(idList);
            }
            splitEntropy /= instance.length;
            double splitInfo = calLogValue(numList);
            if (splitInfo < Double.MIN_VALUE) {
                continue;
            } else {
                double infoGain = (entropy - splitEntropy) / splitInfo;
                if (infoGain > maxInfoGain) {
                    maxInfoGain = infoGain;
                    bestFeatureIndex = feaIndex;
                }
            }
        }
        return bestFeatureIndex;
    }

    private void calMajorLabel() {
        Map<Integer, Integer> classMap = new HashMap<Integer, Integer>();
        for (int id: instance) {
            if (!classMap.containsKey(label[id])) {
                classMap.put(label[id], 1);
            } else {
                int preNum = classMap.get(label[id]);
                classMap.put(label[id], 1 + preNum);
            }
        }
        int maxNum = 0;
        int maxLabel = -1;
        for (int l: classMap.keySet()) {
            int tmpNum = classMap.get(l);
            if (tmpNum > maxNum) {
                maxNum = tmpNum;
                maxLabel = l;
            }
        }
        majorLabel = maxLabel;
        error = 1.0 - (double)MaxNum / instance.length;
    }

    private static double calEntropy(List<Integer> idList) {
        int num = idList.size();
        Map<Integer, Integer> classMap = new HashMap<Integer, Integer>();
        for (int id: idList) {
            if (!classMap.containsKey(label[id])) {
                classMap.put(label[id], 1);
            } else {
                int preNum = classMap.get(label[id]);
                classMap.put(label[id], 1 + preNum);
            }
        }
        List<Integer> numList = new ArrayList<Integer>();
        for (int t: classMap.keySet()) {
            numList.add(classMap.get(t));
        }
        return calLogValue(numList);
    }

    private static double calLogValue(List<Integer> numList) {
        int total = 0;
        double result = 0.0;
        for (int t: numList) {
            total += t;
            result -= t * Math.log(t);
        }
        result /= total;
        result += Math.log(total);
        return result;
    }

    private int labelNum() {
        Set<Integer> labelSet = new HashSet<Integer>();
        for (int t: instance) {
            labelSet.add(label[t]);
        }
        return labelSet.size();
    }

    public void prune() {

    }

    public static void setDataset(int[][] feature, int[] label) {
        this.feature = feature;
        this.label = label;
    }

    private int[] instance;
    private int[] restFeature;
    private double error;
    private int majorLabel;
    private int restLabelNum;
    private boolean isLeaf = true;
    private int bestFeature;
    private Map<Integer, Node> childrenMap;
    private static int[][] feature;
    private static int[] label;

}
