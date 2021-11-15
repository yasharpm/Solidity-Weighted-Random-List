// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.6.0;

import "./WeightedRandomList.sol";

contract WeightedRandomListTest {
    
	WeightedRandomList.Data data;
	
	function create() public {
		WeightedRandomList.insert(data, 1, 4);
		WeightedRandomList.insert(data, 2, 1);
		WeightedRandomList.insert(data, 3, 2);
		WeightedRandomList.insert(data, 4, 10);
		
		WeightedRandomList.remove(data, 4);
		
		WeightedRandomList.update(data, 2, 4);
	}
	
	function test() public view returns (uint[] memory) {
		return WeightedRandomList.multiSelect(data, 10);
	}
	
}