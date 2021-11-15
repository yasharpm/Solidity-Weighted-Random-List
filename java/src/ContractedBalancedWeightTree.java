import java.util.HashMap;
import java.util.Map;

/**
 * NonRecursiveBalancedWeightTree but the object references are replaced with id references.
 * This is an auxiliary modification to help in writing the contract code.
 */
public class ContractedBalancedWeightTree {

    private static class Node {

        String id;
        int weight;

        int parent;
        int leftChild;
        int rightChild;

        boolean isLeftChild;

        int weightSum;

        private Node(String id, int weight) {
            this.id = id;
            this.weight = weight;

            weightSum = weight;
        }

    }

    private int counter = 0;
    private Map<Integer, Node> nodes = new HashMap<>();

    private int root = 0;

    private Map<String, Integer> nodeMap = new HashMap<>();

    public int weightSum() {
        return root == 0 ? 0 : nodes.get(root).weightSum;
    }

    public String select(int weight) {
        if (root == 0) {
            return null;
        }

        Node probe = nodes.get(root);

        while (true) {
            if (probe.leftChild != 0 && nodes.get(probe.leftChild).weightSum > weight) {
                probe = nodes.get(probe.leftChild);
                continue;
            }

            weight -= probe.leftChild == 0 ? 0 : nodes.get(probe.leftChild).weightSum;

            if (probe.weight > weight) {
                return probe.id;
            }

            weight -= probe.weight;

            if (probe.rightChild != 0 && nodes.get(probe.rightChild).weightSum > weight) {
                probe = nodes.get(probe.rightChild);
            }
            else {
                return null;
            }
        }
    }

    public void insert(String id, int weight) {
        Node node = new Node(id, weight);
        nodes.put(++counter, node);

        nodeMap.put(id, counter);

        if (root == 0) {
            root = counter;
            return;
        }

        int probe = root;

        while (true) {
            nodes.get(probe).weightSum += node.weightSum;

            if (nodes.get(probe).leftChild == 0) {
                node.isLeftChild = true;
                node.parent = probe;

                nodes.get(probe).leftChild = counter;

                promote(counter);
                return;
            }
            else if (nodes.get(probe).rightChild == 0) {
                node.isLeftChild = false;
                node.parent = probe;

                nodes.get(probe).rightChild = counter;

                promote(counter);
                return;
            }
            else if (nodes.get(nodes.get(probe).leftChild).weightSum > nodes.get(nodes.get(probe).rightChild).weightSum) {
                probe = nodes.get(probe).rightChild;
            }
            else {
                probe = nodes.get(probe).leftChild;
            }
        }
    }

    private void promote(int node) {
        int probe = node;

        while (nodes.get(probe).parent != 0 && nodes.get(probe).weight > nodes.get(nodes.get(probe).parent).weight) {
            String id = nodes.get(probe).id;
            nodes.get(probe).id = nodes.get(nodes.get(probe).parent).id;
            nodes.get(nodes.get(probe).parent).id = id;

            int weight = nodes.get(probe).weight;
            nodes.get(probe).weight = nodes.get(nodes.get(probe).parent).weight;
            nodes.get(nodes.get(probe).parent).weight = weight;

            nodes.get(probe).weightSum += nodes.get(probe).weight;
            nodes.get(probe).weightSum -= weight;

            nodeMap.put(nodes.get(probe).id, probe);
            nodeMap.put(nodes.get(nodes.get(probe).parent).id, nodes.get(probe).parent);

            probe = nodes.get(probe).parent;
        }
    }

    public boolean remove(String id) {
        int node = nodeMap.remove(id);

        if (node == 0) {
            return false;
        }

            int probe = node;

        while (nodes.get(probe).parent != 0) {
            nodes.get(nodes.get(probe).parent).weightSum -= nodes.get(node).weight;

            probe = nodes.get(probe).parent;
        }

        pullUp(node);

        return true;
    }

    private void pullUp(int node) {
        int probe = node;

        while (true) {
            if (nodes.get(probe).leftChild == 0) {
                if (nodes.get(probe).rightChild == 0) {
                    if (nodes.get(probe).parent == 0) {
                        root = 0;
                    }
                    else if (nodes.get(probe).isLeftChild) {
                        nodes.get(nodes.get(probe).parent).leftChild = 0;
                    }
                    else {
                        nodes.get(nodes.get(probe).parent).rightChild = 0;
                    }

                    nodes.remove(probe);

                    return;
                }
                else {
                    nodes.get(probe).id = nodes.get(nodes.get(probe).rightChild).id;
                    nodeMap.put(nodes.get(probe).id, probe);

                    nodes.get(probe).weight = nodes.get(nodes.get(probe).rightChild).weight;
                    nodes.get(probe).weightSum = nodes.get(nodes.get(probe).rightChild).weightSum;

                    probe = nodes.get(probe).rightChild;
                }
            }
            else if (nodes.get(probe).rightChild == 0) {
                nodes.get(probe).id = nodes.get(nodes.get(probe).leftChild).id;
                nodeMap.put(nodes.get(probe).id, probe);

                nodes.get(probe).weight = nodes.get(nodes.get(probe).leftChild).weight;
                nodes.get(probe).weightSum = nodes.get(nodes.get(probe).leftChild).weightSum;

                probe = nodes.get(probe).leftChild;
            }
            else if (nodes.get(nodes.get(probe).leftChild).weight > nodes.get(nodes.get(probe).rightChild).weight) {
                nodes.get(probe).id = nodes.get(nodes.get(probe).leftChild).id;
                nodeMap.put(nodes.get(probe).id, probe);

                nodes.get(probe).weight = nodes.get(nodes.get(probe).leftChild).weight;
                nodes.get(probe).weightSum = nodes.get(nodes.get(probe).leftChild).weightSum + nodes.get(nodes.get(probe).rightChild).weightSum;

                probe = nodes.get(probe).leftChild;
            }
            else {
                nodes.get(probe).id = nodes.get(nodes.get(probe).rightChild).id;
                nodeMap.put(nodes.get(probe).id, probe);

                nodes.get(probe).weight = nodes.get(nodes.get(probe).rightChild).weight;
                nodes.get(probe).weightSum = nodes.get(nodes.get(probe).leftChild).weightSum + nodes.get(nodes.get(probe).rightChild).weightSum;

                probe = nodes.get(probe).rightChild;
            }
        }
    }

    public boolean update(String id, int weight) {
        int node = nodeMap.get(id);

        if (node == 0) {
            return false;
        }

        int oldWeight = nodes.get(node).weight;

        nodes.get(node).weight = weight;

        nodes.get(node).weightSum += weight;
        nodes.get(node).weightSum -= oldWeight;

        int probe = node;

        while (nodes.get(probe).parent != 0) {
            nodes.get(nodes.get(probe).parent).weightSum += weight;
            nodes.get(nodes.get(probe).parent).weightSum -= oldWeight;

            probe = nodes.get(probe).parent;
        }

        if (weight > oldWeight) {
            promote(node);
        }
        else {
            demote(node);
        }

        return true;
    }

    private void demote(int node) {
        int probe = node;

        while (true) {
            if (nodes.get(probe).leftChild != 0) {
                if (nodes.get(probe).rightChild != 0) {
                    if (nodes.get(nodes.get(probe).leftChild).weight > nodes.get(nodes.get(probe).rightChild).weight) {
                        if (nodes.get(nodes.get(probe).leftChild).weight > nodes.get(probe).weight) {
                            String id = nodes.get(probe).id;
                            nodes.get(probe).id = nodes.get(nodes.get(probe).leftChild).id;
                            nodes.get(nodes.get(probe).leftChild).id = id;

                            int weight = nodes.get(probe).weight;
                            nodes.get(probe).weight = nodes.get(nodes.get(probe).leftChild).weight;
                            nodes.get(nodes.get(probe).leftChild).weight = weight;

                            nodes.get(nodes.get(probe).leftChild).weightSum += weight;
                            nodes.get(nodes.get(probe).leftChild).weightSum -= nodes.get(probe).weight;

                            nodeMap.put(nodes.get(probe).id, probe);
                            nodeMap.put(nodes.get(nodes.get(probe).leftChild).id, nodes.get(probe).leftChild);

                            probe = nodes.get(probe).leftChild;
                            continue;
                        }

                        return;
                    } else if (nodes.get(nodes.get(probe).rightChild).weight > nodes.get(probe).weight) {
                        String id = nodes.get(probe).id;
                        nodes.get(probe).id = nodes.get(nodes.get(probe).rightChild).id;
                        nodes.get(nodes.get(probe).rightChild).id = id;

                        int weight = nodes.get(probe).weight;
                        nodes.get(probe).weight = nodes.get(nodes.get(probe).rightChild).weight;
                        nodes.get(nodes.get(probe).rightChild).weight = weight;

                        nodes.get(nodes.get(probe).rightChild).weightSum += weight;
                        nodes.get(nodes.get(probe).rightChild).weightSum -= nodes.get(probe).weight;

                        nodeMap.put(nodes.get(probe).id, probe);
                        nodeMap.put(nodes.get(nodes.get(probe).rightChild).id, nodes.get(probe).rightChild);

                        probe = nodes.get(probe).rightChild;
                        continue;
                    }

                    return;
                } else if (nodes.get(nodes.get(probe).leftChild).weight > nodes.get(probe).weight) {
                    String id = nodes.get(probe).id;
                    nodes.get(probe).id = nodes.get(nodes.get(probe).leftChild).id;
                    nodes.get(nodes.get(probe).leftChild).id = id;

                    int weight = nodes.get(probe).weight;
                    nodes.get(probe).weight = nodes.get(nodes.get(probe).leftChild).weight;
                    nodes.get(nodes.get(probe).leftChild).weight = weight;

                    nodes.get(nodes.get(probe).leftChild).weightSum += weight;
                    nodes.get(nodes.get(probe).leftChild).weightSum -= nodes.get(probe).weight;

                    nodeMap.put(nodes.get(probe).id, probe);
                    nodeMap.put(nodes.get(nodes.get(probe).leftChild).id, nodes.get(probe).leftChild);

                    probe = nodes.get(probe).leftChild;
                    continue;
                }

                return;
            } else if (nodes.get(probe).rightChild != 0 && nodes.get(nodes.get(probe).rightChild).weight > nodes.get(probe).weight) {
                String id = nodes.get(probe).id;
                nodes.get(probe).id = nodes.get(nodes.get(probe).rightChild).id;
                nodes.get(nodes.get(probe).rightChild).id = id;

                int weight = nodes.get(probe).weight;
                nodes.get(probe).weight = nodes.get(nodes.get(probe).rightChild).weight;
                nodes.get(nodes.get(probe).rightChild).weight = weight;

                nodes.get(nodes.get(probe).rightChild).weightSum += weight;
                nodes.get(nodes.get(probe).rightChild).weightSum -= nodes.get(probe).weight;

                nodeMap.put(nodes.get(probe).id, probe);
                nodeMap.put(nodes.get(nodes.get(probe).rightChild).id, nodes.get(probe).rightChild);

                probe = nodes.get(probe).rightChild;
                continue;
            }

            return;
        }
    }

}
