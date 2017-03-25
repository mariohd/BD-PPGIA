package db.modules.dataStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import db.Utils;
import db.modules.descriptors.ColumnDescriptor;

public class Tuple {
	private Map<ColumnDescriptor, Object> columns;

	public Tuple(Map<ColumnDescriptor, Object> columns) {
		this.columns = columns;
	}
	
	public Tuple() {}

	public int load(Table parent, int start) {
		RandomAccessFile file = parent.getContainerFile();
		this.columns = new LinkedHashMap<ColumnDescriptor, Object>();
		try {
			file.seek(start);
			byte[] tupleSizeBytes = new byte[4];
			file.read(tupleSizeBytes);
			int tupleSize = Utils.toInt(tupleSizeBytes, 0);
			int cursor = start + 4;
			for (ColumnDescriptor column: parent.getColumns()) {

				byte[] size = new byte[2];
				file.seek(cursor);
				file.read(size);
				cursor += 2;
				int columnSize = Utils.toInt(size, 0);

				byte[] valueByte = new byte[columnSize];
				file.seek(cursor);
				file.read(valueByte);
				Object value;
				if (column.getType() == Integer.class) {
					value = Utils.toInt(valueByte, 0);
				} else {
					value = Utils.asString(valueByte);
				}
				columns.put(column, value);

				cursor += columnSize;
			}
			return tupleSize;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public byte[] inBytes() {
		int tupleSize = size();
		byte[] tupleBytes = new byte[tupleSize];
		tupleBytes = Utils.putIn(tupleBytes, 0, Utils.toByteArray(tupleSize, 4));
		int location = 4;

		for (ColumnDescriptor key : columns.keySet()) {
			Object value = columns.get(key);
			byte[] columnBytes;

			if (key.getType() == Integer.class) {
				columnBytes = Utils.toByteArray((int) value);
			} else {
				columnBytes = ((String) value).getBytes();
			}

			byte[] columnSize = Utils.toByteArray(columnBytes.length, 2);
			tupleBytes = Utils.putIn(tupleBytes, location, columnSize);
			tupleBytes = Utils.putIn(tupleBytes, location + 2, columnBytes);
			location += (2 + columnBytes.length);
		}

		return tupleBytes;
	}

	public int size() {
		int size = 4;
		for (ColumnDescriptor key : columns.keySet()) {
			Object value = columns.get(key);
			size += 2 + (key.getType() == Integer.class ? 
							Utils.toByteArray((int) value).length : 
							((String) value).getBytes().length
						);
		}
		return size;
	}

	public void print() {
		for (ColumnDescriptor column : columns.keySet()) {
			System.out.println("\t" + column.getName());
			System.out.println("\t\t" + columns.get(column));
		}
		System.out.println("---------------------------------------------------------------");
		System.out.println();
	}
}
