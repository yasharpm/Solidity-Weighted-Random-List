// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.6.0;

import "./BalancedWeightTree.sol";

library WeightedRandomList {
    
	struct Data {
		BalancedWeightTree.Tree tree;
	}
	
	/**
     * Inserts the id with the specified weight
     * _id the identifier. Can have any value. Preferably greater than zero.
     * _weight the weight. Can have any value
     * The uniqueness of _id is not being checked. Adding 2 entries with the same identifier causes an unexpected behavior.
     */
	function insert(Data storage data, uint _id, uint _weight) internal {
		BalancedWeightTree.insert(data.tree, _id, _weight);
	}
	
	/**
	 * Removes the specified identifier
	 * _id the identifier
	 * Returns true is the identifier is removed, false if it doesn't exist.
	 */
	function remove(Data storage data, uint _id) internal returns (bool) {
		return BalancedWeightTree.remove(data.tree, _id);
	}
	
	/**
	 * Updates the weight of an existing identifier
	 * _id the identifier
	 * _weight the new weight to be assigned
	 * Returns true if the weight is updated, false if the identifier doesn't exist.
	 */
	function update(Data storage data, uint _id, uint _weight) internal returns (bool) {
		return BalancedWeightTree.update(data.tree, _id, _weight);
	}
	
	/**
	 * Returns the weight sum
	 */
	function weightSum(Data storage data) public view returns (uint) {
		return BalancedWeightTree.weightSum(data.tree);
	}
	
	/**
	 * Performs a weighted selection
	 * _weight is greater or equal to zero and smaller than the weightSum
	 * Returns the selected id. If _weight is equal or greater than weight sum returns 0.
	 */
	function selectWithWeight(Data storage data, uint _weight) public view returns (uint) {
		return BalancedWeightTree.select(data.tree, _weight);
	}
	
	/**
	 * Performs a weighted selection using the provided seed
	 * _seed is a random number
	 * Returns the selected id
	 */
	function singleSelect(Data storage data, uint _seed) public view returns (uint) {
		return selectWithWeight(data, _seed % BalancedWeightTree.weightSum(data.tree));
	}
	
	/**
	 * Performs a weighted random selection
	 * Returns the selected id
	 */
	function singleSelect(Data storage data) public view returns (uint) {
		return singleSelect(data, rand(0));
	}
	
	/**
	 * Performs a weighted random selection a number of times
	 * _count the number of times to perform the selection
	 * Returns an array with the length of _count containing the selected identifieres
	 * The array can contain repeated values
	 */
	function multiSelect(Data storage data, uint _count) public view returns (uint[] memory) {
		uint[] memory result = new uint[](_count);
		
		for (uint i = 0; i < _count; i++) {
			result[i] = singleSelect(data, rand(i));
		}
		
		return result;
	}
	
	/**
	 * Returns a puedo-random integer
	 */
	function rand(uint mixer) public view returns (uint) {
		return uint(keccak256(abi.encodePacked(block.difficulty, block.timestamp, mixer)));
	}
	
}