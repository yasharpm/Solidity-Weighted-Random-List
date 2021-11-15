import java.util.HashMap;
import java.util.Map;

/**
 * BalancedWeightTree but unnecessary variables have been removed.
 * This helps to have smaller contract size and lesser gas spent on the functions.
 */
public class SimplifiedBalancedWeightTree {

    private static class Node {

        String id;
        int weight;

        Node parent;
        Node leftChild;
        Node rightChild;

        boolean isLeftChild;

        int weightSum;

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
            if (probe.leftChild != null && probe.leftChild.weightSum > weight) {
                probe = probe.leftChild;
                continue;
            }

            weight -= probe.leftChild == null ? 0 : probe.leftChild.weightSum;

            if (probe.weight > weight) {
                return probe.id;
            }

            weight -= probe.weight;

            if (probe.rightChild != null && probe.rightChild.weightSum > weight) {
                probe = probe.rightChild;
            }
            else {
                return null;
            }
        }
    }

    public void insert(String id, int weight) {
        Node node = new Node(id, weight);
        nodeMap.put(id, node);

        if (root == null) {
            root = node;
            return;
        }

        Node probe = root;

        while (true) {
            probe.weightSum += node.weightSum;

            if (probe.leftChild == null) {
                node.isLeftChild = true;
                node.parent = probe;

                probe.leftChild = node;

                promote(node);
                return;
            }
            else if (probe.rightChild == null) {
                node.isLeftChild = false;
                node.parent = probe;

                probe.rightChild = node;

                promote(node);
                return;
            }
            else if (probe.leftChild.weightSum > probe.rightChild.weightSum) {
                probe = probe.rightChild;
            }
            else {
                probe = probe.leftChild;
            }
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
                node.weightSum = node.rightChild.weightSum;

                remove(node.rightChild);
            }
        }
        else if (node.rightChild == null) {
            node.id = node.leftChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.leftChild.weight;
            node.weightSum = node.leftChild.weightSum;

            remove(node.leftChild);
        }
        else if (node.leftChild.weight > node.rightChild.weight) {
            node.id = node.leftChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.leftChild.weight;
            node.weightSum = node.leftChild.weightSum + node.rightChild.weightSum;

            remove(node.leftChild);
        }
        else {
            node.id = node.rightChild.id;
            nodeMap.put(node.id, node);

            node.weight = node.rightChild.weight;
            node.weightSum = node.leftChild.weightSum + node.rightChild.weightSum;

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

            nodeMap.put(node.id, node);
            nodeMap.put(node.rightChild.id, node.rightChild);

            demote(node.rightChild);
        }
    }

}
