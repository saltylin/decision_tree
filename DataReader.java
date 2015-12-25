import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DataReader {

    public DataReader(String filePath) {
        Scanner s = null;
        try {
            s = new Scanner(new FileInputStream(new File(filePath)));
        } catch(IOException e) {
            e.printStackTrace();
        }
        String[] str = s.nextLine().split(",");
        int featureNum = str.length;
        int[] featureType = new int[featureNum];
        for (int i = 0; i != featureNum; ++i) {
            featureType[i] = str[i].equals("1")? 1 : 0;
        }
        int numFeatureNum = 0;
        for (int t: featureType) {
            if (t == 0) {
                ++numFeatureNum;
            }
        }
        int cateFeatureNum = featureNum - numFeatureNum;
        List<Double[]> numFeatureList = new ArrayList<Double[]>();
        List<Integer[]> cateFeatureList = new ArrayList<Integer[]>();
        List<Integer> labelList = new ArrayList<Integer>();
        while (s.hasNextLine()) {
            str=s.nextLine().split(",");
            Double[] numF = new Double[numFeatureNum];
            Integer[] cateF = new Integer[cateFeatureNum];
            int numI = 0;
            int cateI = 0;
            for (int i = 0; i != featureNum; ++i) {
                if (featureType[i] == 0) {
                    numF[numI++] = Double.valueOf(str[i]);
                } else {
                    cateF[cateI++] = Integer.valueOf(str[i]);
                }
            }
            if (str[featureNum].equals("1.0") || str[featureNum].equals("1")) {
                rawLabel.add(1);    
            } else {
                rawLabel.add(-1);
            }
            numFeatureList.add(numF);
            cateFeatureList.add(cateF);
        }
        s.close();
        int instanceNum = rawLabel.size();
        label = new int[instanceNum];
        for (int i = 0; i != instanceNum; ++i) {
            label[i] = rawLabel.get(i);
        }
        numFeature = new double[instanceNum][];
        for (int i = 0; i!= numFeatureNum; ++i) {
            Double[] tmp = numFeatureList.get(i);
            numFeature[i] = new double[numFeatureNum];
            for (int j = 0; j != numFeatureNum; ++j) {
                numFeature[i][j] = tmp[j];
            }
        }
        cateFeature = new int[instanceNum][];
        for (int i = 0; i!= cateFeatureNum; ++i) {
            Integer[] tmp = cateFeatureList.get(i);
            cateFeature[i] = new int[cateFeatureNum];
            for (int j = 0; j != cateFeatureNum; ++j) {
                cateFeature[i][j] = tmp[j];
            }
        }
    }

    public double[][] getNumFeature() {
        return numFeature;
    }

    public int[][] getCateFeature() {
        return cateFeature;
    }

    public int[] getLabel() {
        return label;
    }

    private double[][] numFeature;
    private int[][] cateFeature;
    private int[] label;

}
