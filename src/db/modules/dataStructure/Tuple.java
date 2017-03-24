package db.modules.dataStructure;

import java.util.Map;

import db.Utils;
import db.modules.descriptors.ColumnDescriptor;

public class Tuple {
	private Map<ColumnDescriptor, Object> columns;
	
	public Tuple(Map<ColumnDescriptor, Object> columns) {
		this.columns = columns;
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
