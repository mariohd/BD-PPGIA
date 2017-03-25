package db.modules.dataStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import db.modules.fs.FileSystem;
import db.modules.metaStructure.DataBlockHeader;

public class DataBlock {
	private Table parent;
	private DataBlockHeader header;
	private List<Tuple> tuples;
	
	public DataBlock(Table parent, int blockId) {
		this.parent = parent;
		this.header = new DataBlockHeader(parent, blockId);
		this.tuples = new ArrayList<Tuple>();
	}
	
	public boolean availableSpaceFor(int tupleSize) {
		return this.header.getUsedSpace() + 8 + tupleSize <= FileSystem.pageSize;
	}

	public boolean save() throws IOException {
		return header.save();
	}
	
	public boolean load() throws IOException {
		return header.load();
	}
	
	public void printTuples() {
		for (Tuple tuple : tuples) {
			tuple.print();
		}
	}
	
	public boolean loadTuples() throws IOException {
		this.tuples = new ArrayList<Tuple>();
		
		int zero = this.header.readStart();
		int spaceToRead = this.header.getUsedSpace() + zero;
		int cursor = zero;

		while (cursor < spaceToRead) {
			cursor += readTuple(cursor);
		}
		
		return true;
	}
	
	private int readTuple(int cursor) throws IOException {
		Tuple tp = new Tuple();
		cursor = tp.load(parent, cursor);;
		tuples.add(tp);
		return cursor;
	}
	
	public boolean insert(Tuple tuple) throws IOException {
		int currentUsedSpace = this.header.writeStart();
		byte[] tpBytes = tuple.inBytes();
		
		RandomAccessFile file =  this.parent.getContainerFile();
		
		file.seek(currentUsedSpace);
		file.write(tpBytes);
		
		this.header.updateUsedSpace(this.header.getUsedSpace() + tuple.size());
		
		return true;
	}
}
