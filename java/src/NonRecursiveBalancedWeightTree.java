import java.util.HashMap;
import java.util.Map;

/**
 * SimplifiedBalancedWeightTree but the recursive functions have been replaced with loops.
 * This helps to reduce the back stack size and reduce the probability of contract call fails.
 */
public class NonRecursiveBalancedWeightTree {

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
        Node probe = node;

        while (probe.parent != null && probe.weight > probe.parent.weight) {
            String id = probe.id;
            probe.id = probe.parent.id;
            probe.parent.id = id;

            int weight = probe.weight;
            probe.weight = probe.parent.weight;
            probe.parent.weight = weight;

            probe.weightSum += probe.weight - weight;

            nodeMap.put(probe.id, probe);
            nodeMap.put(probe.parent.id, probe.parent);

            probe = probe.parent;
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

        pullUp(node);

        return true;
    }

    private void pullUp(Node node) {
        Node probe = node;

        while (true) {
            if (probe.leftChild == null) {
                if (probe.rightChild == null) {
                    if (probe.parent == null) {
                        root = null;
                    }
                    else if (probe.isLeftChild) {
                        probe.parent.leftChild = null;
                    }
                    else {
                        probe.parent.rightChild = null;
                    }

                    return;
                }
                else {
                    probe.id = probe.rightChild.id;
                    nodeMap.put(probe.id, probe);

                    probe.weight = probe.rightChild.weight;
                    probe.weightSum = probe.rightChild.weightSum;

                    probe = probe.rightChild;
                }
            }
            else if (probe.rightChild == null) {
                probe.id = probe.leftChild.id;
                nodeMap.put(probe.id, probe);

                probe.weight = probe.leftChild.weight;
                probe.weightSum = probe.leftChild.weightSum;

                probe = probe.leftChild;
            }
            else if (probe.leftChild.weight > probe.rightChild.weight) {
                probe.id = probe.leftChild.id;
                nodeMap.put(probe.id, probe);

                probe.weight = probe.leftChild.weight;
                probe.weightSum = probe.leftChild.weightSum + probe.rightChild.weightSum;

                probe = probe.leftChild;
            }
            else {
                probe.id = probe.rightChild.id;
                nodeMap.put(probe.id, probe);

                probe.weight = probe.rightChild.weight;
                probe.weightSum = probe.leftChild.weightSum + probe.rightChild.weightSum;

                probe = probe.rightChild;
            }
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
        Node probe = node;

        while (true) {
            if (probe.leftChild != null) {
                if (probe.rightChild != null) {
                    if (probe.leftChild.weight > probe.rightChild.weight) {
                        if (probe.leftChild.weight > probe.weight) {
                            String id = probe.id;
                            probe.id = probe.leftChild.id;
                            probe.leftChild.id = id;

                            int weight = probe.weight;
                            probe.weight = probe.leftChild.weight;
                            probe.leftChild.weight = weight;

                            probe.leftChild.weightSum += weight - probe.weight;

                            nodeMap.put(probe.id, probe);
                            nodeMap.put(probe.leftChild.id, probe.leftChild);

                            probe = probe.leftChild;
                            continue;
                        }

                        return;
                    } else if (probe.rightChild.weight > probe.weight) {
                        String id = probe.id;
                        probe.id = probe.rightChild.id;
                        probe.rightChild.id = id;

                        int weight = probe.weight;
                        probe.weight = probe.rightChild.weight;
                        probe.rightChild.weight = weight;

                        probe.rightChild.weightSum += weight - probe.weight;

                        nodeMap.put(probe.id, probe);
                        nodeMap.put(probe.rightChild.id, probe.rightChild);

                        probe = probe.rightChild;
                        continue;
                    }

                    return;
                } else if (probe.leftChild.weight > probe.weight) {
                    String id = probe.id;
                    probe.id = probe.leftChild.id;
                    probe.leftChild.id = id;

                    int weight = probe.weight;
                    probe.weight = probe.leftChild.weight;
                    probe.leftChild.weight = weight;

                    probe.leftChild.weightSum += weight - probe.weight;

                    nodeMap.put(probe.id, probe);
                    nodeMap.put(probe.leftChild.id, probe.leftChild);

                    probe = probe.leftChild;
                    continue;
                }

                return;
            } else if (probe.rightChild != null && probe.rightChild.weight > probe.weight) {
                String id = probe.id;
                probe.id = probe.rightChild.id;
                probe.rightChild.id = id;

                int weight = probe.weight;
                probe.weight = probe.rightChild.weight;
                probe.rightChild.weight = weight;

                probe.rightChild.weightSum += weight - probe.weight;

                nodeMap.put(probe.id, probe);
                nodeMap.put(probe.rightChild.id, probe.rightChild);

                probe = probe.rightChild;
                continue;
            }

            return;
        }
    }

}
