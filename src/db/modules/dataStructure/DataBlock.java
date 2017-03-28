package db.modules.dataStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import db.modules.fs.FileSystem;
import db.modules.metaStructure.DataBlockHeader;
import db.modules.metaStructure.PageBlock;

public class DataBlock implements PageBlock {
	private Table parent;
	private DataBlockHeader header;
	private List<Tuple> tuples;
	
	public DataBlock(Table parent, int blockId) {
		this.parent = parent;
		this.header = new DataBlockHeader(parent, blockId);
		this.tuples = new ArrayList<Tuple>();
	}
	
	public void setParent(Table t) {
		this.parent = t;
	}
	
	public boolean availableSpaceFor(int tupleSize) {
		return this.header.getUsedSpace() + 8 + tupleSize <= FileSystem.pageSize;
	}
	
	public List<Tuple> getTuples() {
		return this.tuples;
	}

	public boolean save() throws IOException {
		return header.save();
	}
	
	public boolean load() throws IOException {
		return header.load();
	}
	
	public String printTuples() {
		String tuplesS = "";
		for (Tuple tuple : tuples) {
			tuplesS += tuple.print();
		}
		return tuplesS;
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
		cursor = tp.load(parent, cursor);
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

	public String generateNewRowid() {
		return this.header.generateNewRowid();
	}

	public Tuple getTuple(Integer address) {
		Tuple tp = new Tuple();
		tp.load(parent, address);
		return tp;
	}

	@Override
	public Object get() {
		return this;
	}

	@Override
	public int type() {
		return this.header.getType();
	}
	
	public String toString() {
		return "{ " + this.parent.getContainer() + "." + this.header.getBlockId() +  " }";
	}

	public Table getParent() {
		return parent;
	}
}
