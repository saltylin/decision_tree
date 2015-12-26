import java.util.*;

public class Cluster {

    public Cluster(double[][] nf, int[][] cf, int[] la) {
        trainNum = nf.length;
        numFeature = new double[trainNum][];
        for (int i = 0; i != numFeature.length; ++i) {
            numFeature[i] = new double[nf[0].length];
            for (int j = 0; j != numFeature[i].length; ++j) {
                numFeature[i][j] = nf[i][j];
            }
        }
        cateFeature = new int[trainNum][];
        for (int i = 0; i != cateFeature.length; ++i) {
            cateFeature[i] = new int[cf[0].length];
            for (int j = 0; j != cateFeature[i].length; ++j) {
                cateFeature[i][j] = cf[i][j];
            }
        }
        label = new int[trainNum];
        for (int i = 0; i != label.length; ++i) {
            label[i] = la[i];
        }
        clusterLabel = new int[trainNum];
        init();
    }

    public int[] getClusterLabel() {
        return clusterLabel;
    }

    public Map<Integer, Integer> getClusterLabelMap() {
        return clusterLabelMap;
    }

    private void init() {
        calLabelMap();
        int start = 0;
        for (int labelId: labelMap.keySet()) {
            List<Integer> idList = labelMap.get(labelId);
            normalizeEachClass(idList);
            calCateValueFreq(labelId, idList);
            cluster(labelId, idList, start);
            start += clusterNum;
        }
    }

    private void calLabelMap() {
        for (int i = 0; i != trainNum; ++i) {
            int c = label[i];
            if (!labelMap.containsKey(c)) {
                List<Integer> idList = new ArrayList<Integer>();
                idList.add(i);
                labelMap.put(c, idList);
            } else {
                labelMap.get(c).add(i);
            }
        }
    }

    private void normalizeEachClass(List<Integer> idList) {
        for (int i = 0; i != numFeature[0].length; ++i) {
            double avg = 0.0;
            for (int id: idList) {
                avg += numFeature[id][i];
            }
            avg /= idList.size();
            double dev = 0.0;
            for (int id: idList) {
                numFeature[id][i] -= avg;
                dev += numFeature[id][i] * numFeature[id][i];
            }
            dev /= idList.size();
            dev = Math.sqrt(dev);
            for (int id: idList) {
                numFeature[id][i] /= dev;
            }
        }
    }

    private void calCateValueFreq(int labelId, List<Integer> idList) {
        List<Map<Integer, Integer>> freqList = new ArrayList<Map<Integer, Integer>>();
        for (int i = 0; i != cateFeature[0].length; ++i) {
            Map<Integer, Integer> tmpFreqMap = new HashMap<Integer, Integer>();
            for (int id: idList) {
                int val = cateFeature[id][i];
                if (!tmpFreqMap.containsKey(val)) {
                    tmpFreqMap.put(val, 1);
                } else {
                    int pre = tmpFreqMap.get(val);
                    tmpFreqMap.put(val, pre + 1);
                }
            }
            freqList.add(tmpFreqMap);
        }
        cateValueFreqMap.put(labelId, freqList);
    }

    private void cluster(int labelId, List<Integer> idList, int start) {
        final int iterateNum = 20;
        int instanceNum = idList.size();
        List<Map<Integer, Integer>> freqMap = cateValueFreqMap.get(labelId);
        double[][] centerNumFeature = new double[clusterNum][];
        int[][] centerCateFeature = new int[clusterNum][];
        for (int i = 0; i != clusterNum; ++i) {
            int randomIndex = (int)(Math.random() * instanceNum);
            centerNumFeature[i] = new double[numFeature[0].length];
            for (int j = 0; j != centerNumFeature[i].length; ++j) {
                centerNumFeature[i][j] = numFeature[idList.get(randomIndex)][j];
            }
            centerCateFeature[i] = new int[cateFeature[0].length];
            for (int j = 0; j != centerCateFeature[i].length; ++j) {
                centerCateFeature[i][j] = cateFeature[idList.get(randomIndex)][j];
            }
        }
        List<List<Integer>> clusterResult = new ArrayList<List<Integer>>();
        for (int i = 0; i != clusterNum; ++i) {
            clusterResult.add(new ArrayList<Integer>());
        }
        for (int it = 0; it != iterateNum; ++it) {
            for (List<Integer> clusterIdList: clusterResult) {
                clusterIdList.clear();
            }
            for (int id: idList) {
                double maxSim = -1.0;
                int maxClusterIndex = -1;
                for (int j = 0; j != clusterNum; ++j) {
                    double tmpSim = 0.0;
                    for (int k = 0; k != numFeature[0].length; ++k) {
                        double sub = numFeature[id][k] - centerNumFeature[j][k];
                        tmpSim += Math.exp(- sub * sub);
                    }
                    for (int k = 0; k != cateFeature[0].length; ++k) {
                        if (cateFeature[id][k] != centerCateFeature[j][k]) {
                            continue;
                        }
                        int cateVal = cateFeature[id][k];
                        double p = (double)freqMap.get(k).get(cateVal) / instanceNum;
                        tmpSim += 1 - p * p;
                    }
                    if (tmpSim > maxSim) {
                        maxSim = tmpSim;
                        maxClusterIndex = j;
                    }
                }
                clusterResult.get(maxClusterIndex).add(id);
            }
            for (int i = 0; i != clusterNum; ++i) {
                for (int j = 0; j != numFeature[0].length; ++j) {
                    double avg = 0.0;
                    for (int id: clusterResult.get(i)) {
                        avg += numFeature[id][j];
                    }
                    avg /= clusterResult.get(i).size();
                    centerNumFeature[i][j] = avg;
                }
                for (int j = 0; j != cateFeature[0].length; ++j) {
                    Map<Integer, Integer> tmpValFreqMap = new HashMap<Integer, Integer>();
                    for (int id: clusterResult.get(i)) {
                        int tmpVal = cateFeature[id][j];
                        if (!tmpValFreqMap.containsKey(tmpVal)) {
                            tmpValFreqMap.put(tmpVal, 1);
                        } else {
                            int preFreq = tmpValFreqMap.get(tmpVal);
                            tmpValFreqMap.put(tmpVal, 1 + preFreq);
                        }
                    }
                    int maxFreq = 0;
                    int maxVal = 0;
                    for (int val: tmpValFreqMap.keySet()) {
                        int tmpFreq = tmpValFreqMap.get(val);
                        if (tmpFreq > maxFreq) {
                            maxFreq = tmpFreq;
                            maxVal = val;
                        }
                    }
                    centerCateFeature[i][j] = maxVal;
                }
            }
        }
        for (int i = 0; i != clusterNum; ++i) {
            for (int id: clusterResult.get(i)) {
                clusterLabel[id] = i + start;
            }
            clusterLabelMap.put(i + start, labelId);
        }
    }

    private double[][] numFeature;
    private int[][] cateFeature;
    private int[] label;
    private int[] clusterLabel;
    private Map<Integer, Integer> clusterLabelMap = new HashMap<Integer, Integer>();
    private int trainNum;
    private Map<Integer, List<Integer>> labelMap = new HashMap<Integer, List<Integer>>();
    private Map<Integer, List<Map<Integer, Integer>>> cateValueFreqMap = new HashMap<Integer, List<Map<Integer, Integer>>>();
    private final static int clusterNum = 5;

}
