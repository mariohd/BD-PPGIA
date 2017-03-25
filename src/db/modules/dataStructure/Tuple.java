package db.modules.dataStructure;

import java.util.Map;

import db.Utils;
import db.modules.descriptors.ColumnDescriptor;

public class Tuple {
	private Map<ColumnDescriptor, Object> columns;
	
	public Tuple(Map<ColumnDescriptor, Object> columns) {
		this.columns = columns;
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
}
