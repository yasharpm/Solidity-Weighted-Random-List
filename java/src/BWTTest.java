import java.util.*;

public class BWTTest {

    private static final int SELECTION_TRY_COUNT = 1_000_000;

    private static final Random RANDOM = new Random();

    public static void main(String... args) {
//        basicTest();
        randomTest();
    }

    private static void basicTest() {
        Map<String, Integer> weightMap = new HashMap<>();

        weightMap.put("A01", 3);
        weightMap.put("A02", 18);
        weightMap.put("A03", 13);
        weightMap.put("A05", 7);
        weightMap.put("A06", 14);
        weightMap.put("A07", 1);
        weightMap.put("A08", 1);
        weightMap.put("A09", 11);
        weightMap.put("A10", 11);
        weightMap.put("A11", 2);
        weightMap.put("A12", 1);
        weightMap.put("A15", 8);
        BalancedWeightTree tree = new BalancedWeightTree();

        tree.insert("A01", 3);
        tree.insert("A02", 18);
        tree.insert("A03", 13);
        tree.insert("A04", 19);
        tree.insert("A05", 7);
        tree.insert("A06", 14);
        tree.insert("A07", 1);
        tree.insert("A08", 1);
        tree.insert("A09", 11);
        tree.insert("A10", 11);
        tree.insert("A11", 2);
        tree.insert("A12", 1);
        tree.insert("A13", 16);
        tree.insert("A14", 4);
        tree.insert("A15", 8);

        tree.remove("A04");

        tree.remove("A14");

        tree.remove("A13");

        weightMap.put("A02", 1);
        tree.update("A02", 1);

        tree.print(false);

        int weightSum = tree.weightSum();

        testTree(weightMap, weightSum, tree);

        System.out.println(tree.select(50));
    }

    private static void randomTest() {
        Map<String, Integer> weightMap = new HashMap<>();
        List<String> ids = new ArrayList<>(200);
        BalancedWeightTree tree = new BalancedWeightTree();

        int weightSum = 0;

        String id = UUID.randomUUID().toString().substring(0, 4);
        int weight = RANDOM.nextInt(100) + 1;
        weightSum += weight;

        weightMap.put(id, weight);
        ids.add(id);
        tree.insert(id, weight);

        int nodeCount = 1;

        for (int i = 0; i < 800; i++) {
            int rand = RANDOM.nextInt(4);
            if (rand > 1) {
                id = UUID.randomUUID().toString().substring(0, 4);

                if (ids.contains(id)) {
                    continue;
                }

                weight = RANDOM.nextInt(100) + 1;
                weightSum += weight;

                weightMap.put(id, weight);
                ids.add(id);
                tree.insert(id, weight);

                nodeCount++;
            }
            else if (rand == 1 && nodeCount > 0) {
                id = ids.get(RANDOM.nextInt(ids.size()));
                weight = RANDOM.nextInt(100) + 1;

                weightSum += weight - weightMap.get(id);

                weightMap.put(id, weight);
                tree.update(id, weight);
            } else if (nodeCount > 0) {
                id = ids.remove(RANDOM.nextInt(ids.size()));
                weightSum -= weightMap.remove(id);
                tree.remove(id);

                nodeCount--;
            }
        }

        tree.print(true);

        System.out.println(nodeCount + " nodes with weight sum " + weightSum + " and depth is " + depth(weightMap, tree));

        tree.verify();

        testTree(weightMap, weightSum, tree);
    }

    private static void testTree(Map<String, Integer> weightMap, int weightSum, BalancedWeightTree tree) {
        if (weightSum != tree.weightSum()) {
            throw new RuntimeException("Weight sum do not match!");
        }

        Map<String, Integer> selectionCounts = new HashMap<>();

        for (int i = 0; i < SELECTION_TRY_COUNT; i++) {
            int r = RANDOM.nextInt(weightSum);

            String selection = tree.select(r);

            selectionCounts.put(selection, selectionCounts.getOrDefault(selection, 0) + 1);
        }

        float deviationSum = 0;

        for (String id: weightMap.keySet()) {
            float expected = weightMap.get(id).floatValue() / weightSum;
            float actual = selectionCounts.getOrDefault(id, 0).floatValue() / SELECTION_TRY_COUNT;

            float error = Math.abs(expected - actual) / expected;

            deviationSum += error;
        }

        double error = 100 * deviationSum / weightMap.size();

        if (error > 2) {
            throw new RuntimeException("Error is too much!");
        }

        System.out.println("Random selection test passed with error " + error + "%");
    }

    private static int depth(Map<String, Integer> weightMap, BalancedWeightTree tree) {
        int depth = 0;

        String deepest = null;

        for (String id: weightMap.keySet()) {
            if (tree.depth(id) > depth) {
                deepest = id;
            }

            depth = Math.max(depth, tree.depth(id));
        }

//        tree.printRoute(deepest);

        return depth + 1;
    }

}
