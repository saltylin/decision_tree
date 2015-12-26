import java.util.Map;

public class Main{

    public static void main(String[] args) {
        //String[] filePath = {"breast-cancer-assignment5.txt", "german-assignment5.txt"};
        String[] filePath = {"german-assignment5.txt"};
        for (String f: filePath) {
            DataReader dr = new DataReader(f);
            double[][] numFeature = dr.getNumFeature();
            int[][] cateFeature = dr.getCateFeature();
            int[] label = dr.getLabel();
            int instanceNum = label.length;
            int testNum = instanceNum / 10;
            int trainNum = instanceNum - testNum;
            int numFeatureNum = numFeature[0].length;
            int cateFeatureNum = cateFeature[0].length;
            randomize(numFeature, cateFeature, label);
            for (int i = 0; i != 10; ++i) {
                double[][] trainNumFeature = new double[trainNum][];
                int[][] trainCateFeature = new int[trainNum][];
                int[] trainLabel = new int[trainNum];
                for (int j = 0; j != trainNum; ++j) {
                    trainNumFeature[j] = numFeature[j];
                    trainCateFeature[j] = cateFeature[j];
                    trainLabel[j] = label[j];
                }
                double[][] testNumFeature = new double[testNum][];
                int[][] testCateFeature = new int[testNum][];
                int[] testLabel = new int[testNum];
                for (int j = 0; j != testNum; ++j) {
                    testNumFeature[j] = numFeature[trainNum + j];
                    testCateFeature[j] = cateFeature[trainNum + j];
                    testLabel[j] = label[trainNum + j];
                }
                Cluster cluster = new Cluster(trainNumFeature, trainCateFeature, trainLabel);
                int[] clusterLabel = cluster.getClusterLabel();
                Map<Integer, Integer> clusterLabelMap = cluster.getClusterLabelMap();
                DiscreteProcess disPro = new DiscreteProcess(trainNumFeature);
                int[][] trainDisFeature = disPro.getDisFeature();
                int[][] trainFeature = new int[trainNum][];
                for (int j = 0; j != trainNum; ++j) {
                    trainFeature[j] = new int[numFeatureNum + cateFeatureNum];
                    for (int k = 0; k != numFeatureNum; ++k) {
                        trainFeature[j][k] = trainDisFeature[j][k];
                    }
                    for (int k = 0; k != cateFeatureNum; ++k) {
                        trainFeature[j][k + numFeatureNum] = trainCateFeature[j][k];
                    }
                }
                DecisionTree tree1 = new DecisionTree(trainFeature, clusterLabel);
                DecisionTree tree2 = new DecisionTree(trainFeature, trainLabel);
                int right1 = 0;
                int right2 = 0;
                for (int j = 0; j != testNum; ++j) {
                    int[] testDisFeature = disPro.discrete(testNumFeature[j]);
                    int[] testFeature = new int[numFeatureNum + cateFeatureNum];
                    for (int k = 0; k != numFeatureNum; ++k) {
                        testFeature[k] = testDisFeature[k];
                    }
                    for (int k = 0; k != cateFeatureNum; ++k) {
                        testFeature[k + numFeatureNum] = testCateFeature[j][k];
                    }
                    int predictClusterId = tree1.predict(testFeature);
                    int predictLabel = clusterLabelMap.get(predictClusterId);
                    if (predictLabel == testLabel[j]) {
                        ++right1;
                    }
                    int pre2 = tree2.predict(testFeature);
                    if (pre2 == testLabel[j]) {
                        ++right2;
                    }
                }
                double accuracy = (double)right1 / testNum;
                double acc2 = (double)right2 / testNum;
                System.out.println(accuracy + "  " + acc2);
                swapTest(numFeature, cateFeature, label, testNum);
            }
        }
    }

    private static void randomize(double[][] numFeature, int[][] cateFeature, int[] label){
        for (int i = 0; i != label.length-1; ++i){
            int randomIndex = (int)(Math.random() * (label.length - i)) + i;
            double[] tmpNumFeature = numFeature[randomIndex];
            int[] tmpCateFeature = cateFeature[randomIndex];
            int tmpLabel = label[randomIndex];
            numFeature[randomIndex] = numFeature[i];
            cateFeature[randomIndex] = cateFeature[i];
            label[randomIndex] = label[i];
            numFeature[i] = tmpNumFeature;
            cateFeature[i] = tmpCateFeature;
            label[i] = tmpLabel;
        }
    }

    private static void swapTest(double[][] numFeature, int[][] cateFeature, int[] label, int testNum){
        int totalNum = label.length;
        for(int i = 0, j = totalNum - 1; i < j; ++i, --j){
            swap(numFeature, i, j);
            swap(cateFeature, i, j);
            swap(label, i, j);
        }
        for(int i = 0, j = testNum - 1; i < j; ++i, --j){
            swap(numFeature, i, j);
            swap(cateFeature, i, j);
            swap(label, i, j);
        }
        for(int i = testNum, j = totalNum-1; i < j; ++i, --j){
            swap(numFeature, i, j);
            swap(cateFeature, i, j);
            swap(label, i, j);
        }
    }

    private static void swap(double[][] a, int i, int j){
        double[] tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    private static void swap(int[][] a, int i, int j){
        int[] tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    private static void swap(int[] a, int i, int j){
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
