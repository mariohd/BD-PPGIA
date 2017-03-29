package db.modules.buffer;

import db.modules.buffer.algorithm.BufferAlgorithm;
import db.modules.buffer.algorithm.LRU;
import db.modules.metaStructure.PageBlock;

public class BufferController {
	
	private static BufferAlgorithm<PageBlock> sharedBuffer;
	
	public static BufferAlgorithm<PageBlock> getLRUAlgorithm() {
		if (sharedBuffer == null)
			sharedBuffer = new LRU<PageBlock>(3000);
		return sharedBuffer;
	}
	
	public static BufferAlgorithm<PageBlock> createLRUAlgorithm(int size) {
		sharedBuffer = new LRU<PageBlock>(size);
		return sharedBuffer;
	}
}
