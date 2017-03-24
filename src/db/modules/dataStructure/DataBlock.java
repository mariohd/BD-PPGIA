package db.modules.dataStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;

import db.Utils;
import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;
import db.modules.metaStructure.DataBlockHeader;

public class DataBlock {
	private Table parent;
	private DataBlockHeader header;
	private Iterator<ColumnDescriptor> columns;
	
	public DataBlock(Table parent, int blockId) {
		this.parent = parent;
		this.header = new DataBlockHeader(parent, blockId);
		this.columns = this.parent.getColumns().iterator();
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
	
	public boolean read() throws IOException {
		int spaceToRead = this.header.getUsedSpace();
		int zero = this.header.readStart();
		int cursor = 0;
		
		RandomAccessFile file =  this.parent.getContainerFile();
		
		while (cursor + zero < zero + spaceToRead) {
			byte[] tupleSizeBytes = new byte[4];
			file.seek(zero + cursor);
			file.read(tupleSizeBytes);
			cursor += 4;
			
			int tupleSize = Utils.toInt(tupleSizeBytes, 0);
			readTuple(zero + cursor, tupleSize, 4);
			cursor += tupleSize - 4;
		}
		
		return false;
	}
	
	private int readTuple(int cursor, int tupleSize, int acc) throws IOException {
		if (acc == tupleSize) {
			return cursor;
		} else {
		
			RandomAccessFile file =  this.parent.getContainerFile();
			
			file.seek(cursor);
			byte[] columnSizeBytes = new byte[2];
			file.read(columnSizeBytes);
			int columnSize = Utils.toInt(columnSizeBytes, 0);
			cursor += 2;
			
			cursor = readColumn(columnSize, cursor);
			acc += columnSize;
			return readTuple(cursor, tupleSize, acc + 2);
		}
		
	}
	
	private int readColumn(int columnSize, int cursor) throws IOException {
		byte[] columnBytes = new byte[columnSize];
		
		RandomAccessFile file =  this.parent.getContainerFile();

		file.seek(cursor);
		file.read(columnBytes);
		
		if (! columns.hasNext() ) {
			columns = this.parent.getColumns().iterator();
		}
		
		ColumnDescriptor column = columns.next();
			
		if (column.getType() == Integer.class) {
			System.out.println(Utils.toInt(columnBytes, 0));
		} else {
			System.out.println(Utils.asString(columnBytes));
		}
		
		return cursor + columnSize;
	}
	
	public boolean insert(Map<ColumnDescriptor, Object> tuple) throws IOException {

		int currentUsedSpace = this.header.writeStart();
		int tupleSize = 4;
		int location = currentUsedSpace + tupleSize;
		
		for (ColumnDescriptor column : tuple.keySet()) {
			Object value = tuple.get(column);
			tupleSize += writeColumn(column, value, location);
			location = currentUsedSpace + tupleSize;
		}
		
		RandomAccessFile file =  this.parent.getContainerFile();
		
		file.seek(currentUsedSpace);
		file.write(Utils.toByteArray(tupleSize, 4));
		
		this.header.updateUsedSpace(this.header.getUsedSpace() + tupleSize);
		
		return true;
	}


	private int writeColumn(ColumnDescriptor column, Object value, int location) throws IOException {
		
		int columnSize = 0;
		byte[] byteValue;
		if (column.getType() == Integer.class) {
			int intValue = (int) value;
			byteValue = Utils.toByteArray(intValue);
			columnSize = byteValue.length;
		} else {
			String stringValue = (String) value;
			byteValue = stringValue.getBytes();
			columnSize = byteValue.length;
		}
		
		RandomAccessFile file =  this.parent.getContainerFile();
		
		file.seek(location);
		file.write(Utils.toByteArray(columnSize, 2));
		
		file.seek(location + 2);
		file.write(byteValue);
		
		return columnSize + 2;
	}
}
