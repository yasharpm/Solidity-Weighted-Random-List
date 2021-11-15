# Solidity Weighted Random List Selector

### Algorithm

A self-balancing tree (balancing the weights) holds the keys and their weights and the weight sum of each child. A random integer smaller than the weight sum is taken and the tree is traversed to find the matching key.

### Self-balancing weight tree pseudo-code

Keys with higher weights are kept on the higher levels of the tree to reduce the average number of traverses required since these keys are more likely to be randomly selected.

##### Insert

- Start from the root
- Go through the nodes with the lowest weight sum until reaching a node with a null child
- At the new node to the target node
- Perform a promote on the new node

##### remove

- Subtract the weight of the removed node from the weight sum of all the parent nodes
- Perform a pull up on the removed node

##### update

- Apply the weight sum change to all the parent nodes
- If the node's weight has been increased perform a promote on the node otherwise perform a demote on it

##### promote

- If the weight of the target node is greater than its parent do the following otherwise return
- Switch the node with its parent
- Perform a promote on the parent node

##### demote

- If the weight of the target node is smaller than its child with highest weight do the following otherwise return
- Switch the node with its target child
- Perform a demote on the target child

##### pull up

- If the node does not have any children, remove the node from the tree

- Find the child with the highest weight
- Copy the information of this child into the node
- Perform a pull up on the child

### Efficiency

This tree algorithm is my own invention. I was not able to find a paper or document describing a tree for this purpose.

The tree grows in a logarithmic manner. The updates only reorder the nodes and do not change the structure of the tree. This tree does not do rotations to self balance when a node is removed. Hence specific remove calls can keep reducing the balance in the tree. But when new nodes are added, the tree either remains the same depth or grows logarithmic.

Apart from deformations caused by removals, all operations on the tree (insert, remove, update, select) are performed in logarithmic time.
