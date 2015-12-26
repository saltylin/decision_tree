
public class DecisionTree {

    public DecisionTree(int[][] feature, int[] label) {
        Node.setDataset(feature, label);
        int[] instance = new int[label.length];
        for (int i = 0; i != instance.length; ++i) {
            instance[i] = i;
        }
        int[] restFeature = new int[feature[0].length];
        for (int i = 0; i != restFeature.length; ++i) {
            restFeature[i] = i;
        }
        root = new Node(instance, restFeature);
        root.prune();
    }

    public int predict(int[] feature) {
        return root.predict(feature);
    }

    private Node root;

}
