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
        calLabelNum();
        calSelfEntropy();
        calMajorLabel();
        calUpperError();
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
        Integer[] ins = new Integer[instance.length];
        for (int i = 0; i != instance.length; ++i) {
            ins[i] = instance[i];
        }
        List<Integer> idList = Arrays.asList(ins);
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
            int[] ids = new int[idList.size()];
            for (int k = 0; k != ids.length; ++k) {
                ids[k] = idList.get(k);
            }
            Node child = new Node(ids, childrenFea);
            childrenMap.put(t, child);
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
        error = 1.0 - (double)maxNum / instance.length;
    }

    private void calUpperError() {
        upperError = upperLimit(error, instance.length);
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

    private void calLabelNum() {
        Set<Integer> labelSet = new HashSet<Integer>();
        for (int t: instance) {
            labelSet.add(label[t]);
        }
        restLabelNum = labelSet.size();
    }

    public void prune() {
        if (isLeaf) {
            return;
        }
        double childrenError = 0.0;
        for (int t: childrenMap.keySet()) {
            Node child = childrenMap.get(t);
            child.prune();
            childrenError += child.instance.length * child.upperError;
        }
        childrenError /= instance.length;
        if (childrenError > upperError) {
            childrenMap = null;
            isLeaf = true;
        } else {
            upperError = childrenError;
        }
    }
    private static double upperLimit(double f, double N){
        return (f + z * z / (2 * N) + z * Math.sqrt(f / N - f * f / N + z * z / (4 * N * N)))/(1 + z * z / N);
    }


    public static void setDataset(int[][] fea, int[] la) {
        feature = fea;
        label = la;
    }

    private int[] instance;
    private int[] restFeature;
    private double error;
    private double entropy;
    private int majorLabel;
    private int restLabelNum;
    private boolean isLeaf = true;
    private int bestFeature;
    private Map<Integer, Node> childrenMap;
    private double upperError;
    private static int[][] feature;
    private static int[] label;
    private final static double z=1.150349;

}
