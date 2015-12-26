import java.lang.reflect.Constructor;

public class Test{
    
    public static void test(Class<? extends Classifier> classType) throws Exception{
        String[] filePath={"breast-cancer-assignment5.txt","german-assignment5.txt"};
        System.out.println(classType.getName());
        for(String f:filePath){
            DataProcess dataProcess=new DataProcess(f);
            int[][] dataset=dataProcess.getDataset();
            int[] label=dataProcess.getLabel();
            int instanceNum=dataset.length;
            randomize(dataset,label);
            int testNum=instanceNum/10;
            int trainNum=instanceNum-testNum;
            System.out.println(f);
            System.out.print("accuracy: ");
            double[] accuracy=new double[10];
            for(int i=0;i!=10;++i){
                Constructor<? extends Classifier> con=classType.getConstructor(int[][].class,int[].class,int.class);
                Classifier c=(Classifier)con.newInstance(dataset,label,trainNum);
                int right=0;
                for(int j=trainNum;j!=instanceNum;++j){
                    int predictedLabel=c.test(dataset[j]);
                    if(predictedLabel==label[j]){
                        ++right;
                    }
                }
                accuracy[i]=(double)right/testNum;
                System.out.printf("%#.4f ",accuracy[i]);
                swapTest(dataset,label,testNum);
            }
            System.out.println();
            double avg=0.0;
            for(double t:accuracy){
                avg+=t;
            }
            avg/=10;
            double variance=0.0;
            for(double t:accuracy){
            	double bias=t-avg;
            	variance+=bias*bias;
            }
            variance/=10;
            double deviation=Math.sqrt(variance);
            System.out.printf("average: %#.4f\nstandard deviation: %#.4f\n",avg,deviation);
        }
    }

    private static void randomize(int[][] dataset,int[] label){
        for(int i=0;i!=dataset.length-1;++i){
            int randomIndex=(int)(Math.random()*(dataset.length-i))+i;
            int[] tmpFeature=dataset[randomIndex];
            int tmpLabel=label[randomIndex];
            dataset[randomIndex]=dataset[i];
            label[randomIndex]=label[i];
            dataset[i]=tmpFeature;
            label[i]=tmpLabel;
        }
    }

    private static void swapTest(int[][] dataset,int[] label,int testNum){
        int totalNum=dataset.length;
        for(int i=0,j=totalNum-1;i<j;++i,--j){
            swap(dataset,i,j);
            swap(label,i,j);
        }
        for(int i=0,j=testNum-1;i<j;++i,--j){
            swap(dataset,i,j);
            swap(label,i,j);
        }
        for(int i=testNum,j=totalNum-1;i<j;++i,--j){
            swap(dataset,i,j);
            swap(label,i,j);
        }
    }

    private static void swap(int[][] a,int i,int j){
        int[] tmp=a[i];
        a[i]=a[j];
        a[j]=tmp;
    }

    private static void swap(int[] a,int i,int j){
        int tmp=a[i];
        a[i]=a[j];
        a[j]=tmp;
    }
}
