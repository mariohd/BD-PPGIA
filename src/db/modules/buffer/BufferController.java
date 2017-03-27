package db.modules.buffer;

import db.modules.dataStructure.DataBlock;

public class BufferController {
	
	public static BufferAlgorithm<DataBlock> getLRUAlgorithm(int size) {
		return new LRU<DataBlock>(size);
	}
}
