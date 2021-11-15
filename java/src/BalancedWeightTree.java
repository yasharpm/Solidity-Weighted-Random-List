import java.util.HashMap;
import java.util.Map;

/**
 * This is the original algorithm which contains debug functions like print.
 */
public class BalancedWeightTree {

    private static class Node {

        String id;
        int weight;

        Node parent;
        Node leftChild;
        Node rightChild;

        boolean isLeftChild;

        int weightSum;

        int leftWeightSum = 0;
        int rightWeightSum = 0;

        int depth;

        private Node(String id, int weight) {
            this.id = id;
            this.weight = weight;

            weightSum = weight;
        }

    }

    private Node root = null;

    private Map<String, Node> nodeMap = new HashMap<>();

    public int weightSum() {
        return root == null ? 0 : root.weightSum;
    }

    public String select(int weight) {
        if (root == null) {
            return null;
        }

        Node probe = root;

        while (true) {
            if (probe.leftWeightSum > weight) {
                probe = probe.leftChild;
                continue;
            }

            weight -= probe.leftWeightSum;

            if (probe.weight > weight) {
                return probe.id;
            }

            weight -= probe.weight;

            if (probe.rightWeightSum > weight) {
                probe = probe.rightChild;
            }
            else {
                return null;
            }
        }
    }

    public int depth(String id) {
        return nodeMap.get(id).depth;
    }

    public void insert(String id, int weight) {
        Node node = new Node(id, weight);
        nodeMap.put(id, node);

        if (root == null) {
            root = node;
            root.depth = 0;
            return;
        }

        int depth = 1;

        Node probe = root;

        while (true) {
            probe.weightSum += node.weightSum;

            if (probe.leftChild == null) {
                node.isLeftChild = true;
                node.parent = probe;

                probe.leftChild = node;
                probe.leftWeightSum += node.weightSum;

                node.depth = depth;

                promote(node);
                return;
            }
            else if (probe.rightChild == null) {
                node.isLeftChild = false;
                node.parent = probe;

                probe.rightChild = node;
                probe.rightWeightSum += node.weightSum;

                node.depth = depth;

                promote(node);
                return;
            }
            else if (probe.leftWeightSum > probe.rightWeightSum) {
                probe.rightWeightSum += node.weightSum;
                probe = probe.rightChild;
            }
            else {
                probe.leftWeightSum += node.weightSum;
                probe = probe.leftChild;
            }

            depth++;
        }
    }

    private void promote(Node node) {
        if (node.parent != null && node.weight > node.parent.weight) {
            String id = node.id;
            node.id = node.parent.id;
            node.parent.id = id;

            int weight = node.weight;
            node.weight = node.parent.weight;
            node.parent.weight = weight;

            node.weightSum += node.weight - weight;

            if (node.isLeftChild) {
                node.parent.leftWeightSum += node.weight - weight;
            }
            else {
                node.parent.rightWeightSum += node.weight - weight;
            }

            nodeMap.put(node.id, node);
            nodeMap.put(node.parent.id, node.parent);

            promote(node.parent);
        }
    }

    public boolean remove(String id) {
        Node node = nodeMap.remove(id);

        if (node == null) {
            return false;
        }

        Node probe = node;

        while (probe.parent != null) {
            probe.parent.weightSum -= node.weight;

            if (probe.isLeftChild) {
                probe.parent.leftWeightSum -= node.weight;
            }
            else {
                probe.parent.rightWeightSum -= node.weight;
            }

            probe = probe.parent;
        }

        remove(node);

        return true;
    }

    private void remove(Node node) {
        if (node.leftChild == null) {
            if (node.rightChild == null) {
                if (node.parent == null) {
                    root = null;
                }
                else if (node.isLeftChild) {
                    node.parent.leftChild = null;
                }
                else {
                    node.parent.rightChild = null;
                }
            }
            else {
                node.id = node.rightChild.id;
                nodeMap.put(node.id, node);

                node.weight = node.rightChild.weight;
                node.rightWeightSum -= node.weight;
                node.weightSum = node.leftWeightSum + node.weight + node.rightWeightSum;

                remove(node.rightChild);
            }
        }
        else if (node.rightChild == null) {
            node.id = node.leftChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.leftChild.weight;
            node.leftWeightSum -= node.weight;
            node.weightSum = node.leftWeightSum + node.weight + node.rightWeightSum;

            remove(node.leftChild);
        }
        else if (node.leftChild.weight > node.rightChild.weight) {
            node.id = node.leftChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.leftChild.weight;
            node.leftWeightSum -= node.weight;
            node.weightSum = node.leftWeightSum + node.weight + node.rightWeightSum;

            remove(node.leftChild);
        }
        else {
            node.id = node.rightChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.rightChild.weight;
            node.rightWeightSum -= node.weight;
            node.weightSum = node.leftWeightSum + node.weight + node.rightWeightSum;

            remove(node.rightChild);
        }
    }

    public boolean update(String id, int weight) {
        Node node = nodeMap.get(id);

        if (node == null) {
            return false;
        }

        int weightDiff = weight - node.weight;

        node.weight = weight;
        node.weightSum += weightDiff;

        Node probe = node;

        while (probe.parent != null) {
            probe.parent.weightSum += weightDiff;

            if (probe.isLeftChild) {
                probe.parent.leftWeightSum += weightDiff;
            }
            else {
                probe.parent.rightWeightSum += weightDiff;
            }

            probe = probe.parent;
        }

        if (weightDiff > 0) {
            promote(node);
        }
        else {
            demote(node);
        }

        return true;
    }

    private void demote(Node node) {
        if (node.leftChild != null) {
            if (node.rightChild != null) {
                if (node.leftChild.weight > node.rightChild.weight) {
                    if (node.leftChild.weight > node.weight) {
                        String id = node.id;
                        node.id = node.leftChild.id;
                        node.leftChild.id = id;

                        int weight = node.weight;
                        node.weight = node.leftChild.weight;
                        node.leftChild.weight = weight;

                        node.leftChild.weightSum += weight - node.weight;
                        node.leftWeightSum = node.leftChild.weightSum;

                        nodeMap.put(node.id, node);
                        nodeMap.put(node.leftChild.id, node.leftChild);

                        demote(node.leftChild);
                    }
                }
                else if (node.rightChild.weight > node.weight) {
                    String id = node.id;
                    node.id = node.rightChild.id;
                    node.rightChild.id = id;

                    int weight = node.weight;
                    node.weight = node.rightChild.weight;
                    node.rightChild.weight = weight;

                    node.rightChild.weightSum += weight - node.weight;
                    node.rightWeightSum = node.rightChild.weightSum;

                    nodeMap.put(node.id, node);
                    nodeMap.put(node.rightChild.id, node.rightChild);

                    demote(node.rightChild);
                }
            }
            else if (node.leftChild.weight > node.weight) {
                String id = node.id;
                node.id = node.leftChild.id;
                node.leftChild.id = id;

                int weight = node.weight;
                node.weight = node.leftChild.weight;
                node.leftChild.weight = weight;

                node.leftChild.weightSum += weight - node.weight;
                node.leftWeightSum = node.leftChild.weightSum;

                nodeMap.put(node.id, node);
                nodeMap.put(node.leftChild.id, node.leftChild);

                demote(node.leftChild);
            }
        }
        else if (node.rightChild != null && node.rightChild.weight > node.weight) {
            String id = node.id;
            node.id = node.rightChild.id;
            node.rightChild.id = id;

            int weight = node.weight;
            node.weight = node.rightChild.weight;
            node.rightChild.weight = weight;

            node.rightChild.weightSum += weight - node.weight;
            node.rightWeightSum = node.rightChild.weightSum;

            nodeMap.put(node.id, node);
            nodeMap.put(node.rightChild.id, node.rightChild);

            demote(node.rightChild);
        }
    }

    public void verify() {
        if (root != null) {
            verify(root);
        }
    }

    private void verify(Node node) {
        if (node.weightSum != node.leftWeightSum + node.weight + node.rightWeightSum) {
            throw new RuntimeException("Weight sum did not match with weights in " + node.id);
        }

        if (node.leftChild != null) {
            if (node.leftWeightSum != node.leftChild.weightSum) {
                throw new RuntimeException("Left weights did not match in " + node.id);
            }

            verify(node.leftChild);
        }
        else if (node.leftWeightSum != 0) {
            throw new RuntimeException("Left child is null but weight sum is not zero.");
        }

        if (node.rightChild != null) {
            if (node.rightWeightSum != node.rightChild.weightSum) {
                throw new RuntimeException("Right weights did not match in " + node.id);
            }

            verify(node.rightChild);
        }
        else if (node.rightWeightSum != 0) {
            throw new RuntimeException("Right child is null but weight sum is not zero.");
        }
    }

    public void printRoute(String id) {
        Node node = nodeMap.get(id);

        while (node != null) {
            System.out.println(node.id);

            node = node.parent;
        }
    }

    public void print(boolean summarized) {
        System.out.println("<<<<< START >>>>>");

        if (root != null) {
            printNode(root, summarized);
        }

        System.out.println("#################");
    }

    private void printNode(Node node, boolean summarized) {
        if (node != root) {
            StringBuilder sb = new StringBuilder();

            Node probe = node;

            boolean firstTimeSkip = true;

            while (probe.parent != null && probe.parent != root) {
                if (probe.isLeftChild || firstTimeSkip) {
                    sb.insert(0, "  |");

                    firstTimeSkip = false;
                }
                else {
                    sb.insert(0, "   ");
                }

                probe = probe.parent;
            }

            if (probe.isLeftChild || node.parent == root) {
                sb.insert(0, '|');
            }
            else {
                sb.insert(0, ' ');
            }

            System.out.print(sb);
            System.out.print('_');
        }

        if (summarized) {
            System.out.println(node.weight);
        }
        else {
            System.out.print(node.id);
            System.out.print(' ');
            System.out.print(node.weightSum);
            System.out.print(" (");
            System.out.print(node.leftWeightSum);
            System.out.print('-');
            System.out.print(node.weight);
            System.out.print('-');
            System.out.print(node.rightWeightSum);
            System.out.println(")");
        }

        if (node.leftChild != null) {
            printNode(node.leftChild, summarized);
        }

        if (node.rightChild != null) {
            printNode(node.rightChild, summarized);
        }
    }

}
