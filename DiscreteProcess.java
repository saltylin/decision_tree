import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DiscreteProcess {

    public discreteProcess(double[][] numFeature) {
        instanceNum = numFeature.length;
        disFeature = new int[instance][];
        featureNum = numFeature[0].length;
        for (int i = 0; i != disFeature.length; ++i) {
            disFeature[i] = new int[featureNum];
        }
        int intervalNum = (int)(Math.log(instanceNum) / Math.log(2));
        for (int i = 0; i != featureNum; ++i) {
            double[] value = new double[instanceNum];
            for(int j = 0; j != instanceNum; ++j){
                value[j] = numFeature[j][i];
            }
            Arrays.sort(value);
            List<Double> th = new ArrayList<Double>();
            th.add(-Double.MAX_VALUE);
            for (int j = intervalNum-1; j < instanceNum; j += intervalNum) {
                th.add(value[j]);
            }
            th.add(Double.MAX_VALUE);
            discreteBorderList.add(th);
            for (int j = 0; j != instanceNum; ++j) {
                for (int k = 0; k < th.size() - 1; ++k) {
                    if (numFeature[j][i] > th.get(k) && numFeature[j][i] <= th.get(k + 1)) {
                        disFeature[j][i] = k;
                        break;
                    }
                }
            }
        }
        }
    }

    public int[][] getDisFeature() {
        return disFeature;
    }

    public int[] discrete(double[] numFeature) {
        int[] result = new int[featureNum];
        for (int i = 0; i != featureNum; ++i) {
            List<Double> th = discreteBorderList.get(i);
            for (int j = 0; j < th.size() -1; ++j) {
                if (numFeature[i] > th.get(j) && numFeature[i] < th.get(j + 1)) {
                    result[i] = j;
                    break;
                }
            }
        }
        return result;
    }

    private int featureNum;
    private int[][] disFeature;
    private List<List<Double>> discreteBorderList = new ArrayList<List<Double>>();

}
