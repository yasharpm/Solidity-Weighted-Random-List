pragma solidity ^0.6.0;

library BalancedWeightTree {

	struct Node {
		uint id;
		uint weight;
		
		uint parent;
		uint leftChild;
		uint rightChild;
		
		bool isLeftChild;
		
		uint weightSum;
	}
	
	struct Tree {
		uint counter;
		mapping(uint => Node) nodes;
		
		uint root;
		
		mapping(uint => uint) nodeMap;
	}
	
	/**
	 * Returns the weight sum of the tree
	 */
	function weightSum(Tree storage tree) public view returns (uint) {
		return tree.nodes[tree.root].weightSum;
	}
	
	/**
	 * Performs a weighted selection among the stored ids
	 * _weight is greater or equal to zero and smaller than weightSum
	 * Returns the selected id. If _weight is equal or greater than weight sum returns 0.
	 */
	function select(Tree storage tree, uint _weight) public view returns (uint) {
		uint probe = tree.root;
		
		while (true) {
			if (tree.nodes[tree.nodes[probe].leftChild].weightSum > _weight) {
				probe = tree.nodes[probe].leftChild;
				continue;
			}
			
			_weight -= tree.nodes[tree.nodes[probe].leftChild].weightSum;
			
			if (tree.nodes[probe].weight > _weight) {
				return tree.nodes[probe].id;
			}
			
			_weight -= tree.nodes[probe].weight;
			
			if (tree.nodes[tree.nodes[probe].rightChild].weightSum > _weight) {
				probe = tree.nodes[probe].rightChild;
			}
			else {
				return 0;
			}
		}
	}

    /**
     * Inserts the id with the specified weight inside the tree.
     * _id the identifier. Can have any value
     * _weight the weight. Can have any value
     * The uniqueness of _id is not being checked. Adding 2 entries with the same identifier causes an unexpected behavior.
     */
	function insert(Tree storage tree, uint _id, uint _weight) internal {
		Node memory node = Node({id: _id, weight: _weight, parent: 0, leftChild: 0, rightChild: 0, isLeftChild: true, weightSum: _weight});
		
		tree.counter++;
		tree.nodes[tree.counter] = node;
		
		tree.nodeMap[_id] = tree.counter;
		
		if (tree.root == 0) {
			tree.root = tree.counter;
			return;
		}
		
		uint probe = tree.root;
		
		while (true) {
			tree.nodes[probe].weightSum += tree.nodes[tree.counter].weightSum;
			
			if (tree.nodes[probe].leftChild == 0) {
				tree.nodes[tree.counter].parent = probe;
				
				tree.nodes[probe].leftChild = tree.counter;
				
				promote(tree, tree.counter);
				return;
			}
			else if (tree.nodes[probe].rightChild == 0) {
				tree.nodes[tree.counter].isLeftChild = false;
				tree.nodes[tree.counter].parent = probe;
				
				tree.nodes[probe].rightChild = tree.counter;
				
				promote(tree, tree.counter);
				return;
			}
			else if (tree.nodes[tree.nodes[probe].leftChild].weightSum > tree.nodes[tree.nodes[probe].rightChild].weightSum) {
				probe = tree.nodes[probe].rightChild;
			}
			else {
				probe = tree.nodes[probe].leftChild;
			}
		}
	}
	
	function promote(Tree storage tree, uint probe) private {
		while (tree.nodes[probe].parent != 0 && tree.nodes[probe].weight > tree.nodes[tree.nodes[probe].parent].weight) {
			uint temp = tree.nodes[probe].id;
			tree.nodes[probe].id = tree.nodes[tree.nodes[probe].parent].id;
			tree.nodes[tree.nodes[probe].parent].id = temp;
			
			temp = tree.nodes[probe].weight;
			tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].parent].weight;
			tree.nodes[tree.nodes[probe].parent].weight = temp;
			
			tree.nodes[probe].weightSum += tree.nodes[probe].weight;
			tree.nodes[probe].weightSum -= temp;
			
			tree.nodeMap[tree.nodes[probe].id] = probe;
			tree.nodeMap[tree.nodes[tree.nodes[probe].parent].id] = tree.nodes[probe].parent;
			
			probe = tree.nodes[probe].parent;
		}
	}
	
	/**
	 * Removes the specified identifier from the tree
	 * _id the identifier
	 * Returns true is the identifier is removed, false if it doesn't exist in the tree.
	 */
	function remove(Tree storage tree, uint _id) internal returns (bool) {
		uint node = tree.nodeMap[_id];
		
		if (node == 0) {
			return false;
		}
		
		delete tree.nodeMap[_id];
		
		uint probe = node;
		
		while (tree.nodes[probe].parent != 0) {
			tree.nodes[tree.nodes[probe].parent].weightSum -= tree.nodes[node].weight;
			
			probe = tree.nodes[probe].parent;
		}
		
		pullUp(tree, node);
		
		return true;
	}
	
	function pullUp(Tree storage tree, uint probe) private {
		while (true) {
			if (tree.nodes[probe].leftChild == 0) {
				if (tree.nodes[probe].rightChild == 0) {
					if (tree.nodes[probe].parent == 0) {
						tree.root = 0;
					}
					else if (tree.nodes[probe].isLeftChild) {
						tree.nodes[tree.nodes[probe].parent].leftChild = 0;
					}
					else {
						tree.nodes[tree.nodes[probe].parent].rightChild = 0;
					}
					
					delete tree.nodes[probe];
					
					return;
				}
				else {
					tree.nodes[probe].id = tree.nodes[tree.nodes[probe].rightChild].id;
					tree.nodeMap[tree.nodes[probe].id] = probe;
					
					tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].rightChild].weight;
					tree.nodes[probe].weightSum = tree.nodes[tree.nodes[probe].rightChild].weightSum;
					
					probe = tree.nodes[probe].rightChild;
				}
			}
			else if (tree.nodes[probe].rightChild == 0) {
				tree.nodes[probe].id = tree.nodes[tree.nodes[probe].leftChild].id;
				tree.nodeMap[tree.nodes[probe].id] = probe;
				
				tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].leftChild].weight;
				tree.nodes[probe].weightSum = tree.nodes[tree.nodes[probe].leftChild].weightSum;
				
				probe = tree.nodes[probe].leftChild;
			}
			else if (tree.nodes[tree.nodes[probe].leftChild].weight > tree.nodes[tree.nodes[probe].rightChild].weight) {
				tree.nodes[probe].id = tree.nodes[tree.nodes[probe].leftChild].id;
				tree.nodeMap[tree.nodes[probe].id] = probe;
				
				tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].leftChild].weight;
				tree.nodes[probe].weightSum = tree.nodes[tree.nodes[probe].leftChild].weightSum + tree.nodes[tree.nodes[probe].rightChild].weightSum;
				
				probe = tree.nodes[probe].leftChild;
			}
			else {
				tree.nodes[probe].id = tree.nodes[tree.nodes[probe].rightChild].id;
				tree.nodeMap[tree.nodes[probe].id] = probe;
				
				tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].rightChild].weight;
				tree.nodes[probe].weightSum = tree.nodes[tree.nodes[probe].leftChild].weightSum + tree.nodes[tree.nodes[probe].rightChild].weightSum;
				
				probe = tree.nodes[probe].rightChild;
			}
		}
	}
	
	/**
	 * Updates the weight of an existing identifier
	 * _id the identifier
	 * _weight the new weight to be assigned
	 * Returns true if the weight is updated, false if the identifier doesn't exist.
	 */
	function update(Tree storage tree, uint _id, uint _weight) internal returns (bool) {
		uint node = tree.nodeMap[_id];
		
		if (node == 0) {
			return false;
		}
		
		uint oldWeight = tree.nodes[node].weight;
		
		tree.nodes[node].weight = _weight;
		
		tree.nodes[node].weightSum += _weight;
		tree.nodes[node].weightSum -= oldWeight;
		
		uint probe = node;
		
		while (tree.nodes[probe].parent != 0) {
			tree.nodes[tree.nodes[probe].parent].weightSum += _weight;
			tree.nodes[tree.nodes[probe].parent].weightSum -= oldWeight;
			
			probe = tree.nodes[probe].parent;
		}
		
		if (_weight > oldWeight) {
			promote(tree, node);
		}
		else {
			demote(tree, node);
		}
		
		return true;
	}
	
	function demote(Tree storage tree, uint probe) private {
		while (true) {
			if (tree.nodes[probe].leftChild != 0) {
				if (tree.nodes[probe].rightChild != 0) {
					if (tree.nodes[tree.nodes[probe].leftChild].weight > tree.nodes[tree.nodes[probe].rightChild].weight) {
						if (tree.nodes[tree.nodes[probe].leftChild].weight > tree.nodes[probe].weight) {
							uint temp = tree.nodes[probe].id;
							tree.nodes[probe].id = tree.nodes[tree.nodes[probe].leftChild].id;
							tree.nodes[tree.nodes[probe].leftChild].id = temp;
							
							temp = tree.nodes[probe].weight;
							tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].leftChild].weight;
							tree.nodes[tree.nodes[probe].leftChild].weight = temp;
							
							tree.nodes[tree.nodes[probe].leftChild].weightSum += temp;
							tree.nodes[tree.nodes[probe].leftChild].weightSum -= tree.nodes[probe].weight;
							
							tree.nodeMap[tree.nodes[probe].id] = probe;
							tree.nodeMap[tree.nodes[tree.nodes[probe].leftChild].id] = tree.nodes[probe].leftChild;
							
							probe = tree.nodes[probe].leftChild;
							continue;
						}
						
						return;
					}
					else if (tree.nodes[tree.nodes[probe].rightChild].weight > tree.nodes[probe].weight) {
						uint temp = tree.nodes[probe].id;
						tree.nodes[probe].id = tree.nodes[tree.nodes[probe].rightChild].id;
						tree.nodes[tree.nodes[probe].rightChild].id = temp;
						
						temp = tree.nodes[probe].weight;
						tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].rightChild].weight;
						tree.nodes[tree.nodes[probe].rightChild].weight = temp;
						
						tree.nodes[tree.nodes[probe].rightChild].weightSum += temp;
						tree.nodes[tree.nodes[probe].rightChild].weightSum -= tree.nodes[probe].weight;
						
						tree.nodeMap[tree.nodes[probe].id] = probe;
						tree.nodeMap[tree.nodes[tree.nodes[probe].rightChild].id] = tree.nodes[probe].rightChild;
						
						probe = tree.nodes[probe].rightChild;
						continue;
					}
					
					return;
				}
				else if (tree.nodes[tree.nodes[probe].leftChild].weight > tree.nodes[probe].weight) {
					uint temp = tree.nodes[probe].id;
					tree.nodes[probe].id = tree.nodes[tree.nodes[probe].leftChild].id;
					tree.nodes[tree.nodes[probe].leftChild].id = temp;
					
					temp = tree.nodes[probe].weight;
					tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].leftChild].weight;
					tree.nodes[tree.nodes[probe].leftChild].weight = temp;
					
					tree.nodes[tree.nodes[probe].leftChild].weightSum += temp;
					tree.nodes[tree.nodes[probe].leftChild].weightSum -= tree.nodes[probe].weight;
					
					tree.nodeMap[tree.nodes[probe].id] = probe;
					tree.nodeMap[tree.nodes[tree.nodes[probe].leftChild].id] = tree.nodes[probe].leftChild;
					
					probe = tree.nodes[probe].leftChild;
					continue;
				}
				
				return;
			}
			else if (tree.nodes[tree.nodes[probe].rightChild].weight > tree.nodes[probe].weight) {
				uint temp = tree.nodes[probe].id;
				tree.nodes[probe].id = tree.nodes[tree.nodes[probe].rightChild].id;
				tree.nodes[tree.nodes[probe].rightChild].id = temp;
				
				temp = tree.nodes[probe].weight;
				tree.nodes[probe].weight = tree.nodes[tree.nodes[probe].rightChild].weight;
				tree.nodes[tree.nodes[probe].rightChild].weight = temp;
				
				tree.nodes[tree.nodes[probe].rightChild].weightSum += temp;
				tree.nodes[tree.nodes[probe].rightChild].weightSum -= tree.nodes[probe].weight;
				
				tree.nodeMap[tree.nodes[probe].id] = probe;
				tree.nodeMap[tree.nodes[tree.nodes[probe].rightChild].id] = tree.nodes[probe].rightChild;
				
				probe = tree.nodes[probe].rightChild;
				continue;
			}
			
			return;
		}
	}

}