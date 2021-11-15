// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.6.0;

import "./BalancedWeightTree.sol";

contract BalancedWeightTreeTest {
    
    BalancedWeightTree.Tree private tree;
    uint[12] private selectionCounts;
	
	function createTree() public returns (uint) {
		BalancedWeightTree.insert(tree, 1, 3);
		BalancedWeightTree.insert(tree, 2, 18);
		BalancedWeightTree.insert(tree, 3, 13);
		BalancedWeightTree.insert(tree, 15, 19);
		BalancedWeightTree.insert(tree, 5, 7);
		BalancedWeightTree.insert(tree, 6, 14);
		BalancedWeightTree.insert(tree, 7, 1);
		BalancedWeightTree.insert(tree, 8, 1);
		BalancedWeightTree.insert(tree, 9, 11);
		BalancedWeightTree.insert(tree, 10, 11);
		BalancedWeightTree.insert(tree, 11, 2);
		BalancedWeightTree.insert(tree, 12, 1);
		BalancedWeightTree.insert(tree, 13, 16);
		BalancedWeightTree.insert(tree, 14, 4);
		BalancedWeightTree.insert(tree, 4, 8);
		
		BalancedWeightTree.remove(tree, 15);
		BalancedWeightTree.remove(tree, 14);
		BalancedWeightTree.remove(tree, 13);
		
    	BalancedWeightTree.update(tree, 2, 1);
	
		return BalancedWeightTree.select(tree, 50);
	}
	
	function testTree() public returns (uint[12] memory) {
		uint[12] memory weights = [uint(3), 1, 13, 8, 7, 14, 1, 1, 11, 11, 2, 1];
		uint weightSum = 73;
		uint iterations = 50;
		
		require(BalancedWeightTree.weightSum(tree) == weightSum, "Weight sum do not match!");
		
		for (uint i = 0; i < iterations; i++) {
			uint weight = rand(i, weightSum);
			uint selection = BalancedWeightTree.select(tree, weight);
			selectionCounts[selection - 1] = selectionCounts[selection - 1] + 1;
		}
		
		return selectionCounts;
		
		uint deviationSum = 0;
		
		for (uint i = 0; i < weights.length; i++) {
			uint a = weights[i] * iterations;
			uint b = selectionCounts[i] * weightSum;
			
			uint error = ((a > b) ? (a - b) : (b - a)) * 10000 / (weights[i] * iterations);
			
			deviationSum += error;
		}
		
		uint error = deviationSum / weights.length;
		
		//return error;
	}
	
	function testRand() public view returns (uint8[24] memory) {
	    uint8[24] memory numbers;
	    for (uint i = 0; i < 24; i++) {
	        numbers[i] = uint8(rand(i, 73));
	    }
	    return numbers;
	}
	
	function rand(uint mixer, uint range) private view returns (uint) {
		uint seed = uint(keccak256(abi.encodePacked(block.difficulty, block.timestamp, mixer)));
		return seed % range;
	}
	
}