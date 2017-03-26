package userInterface.gui;

import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import db.modules.dataStructure.Tuple;
import db.modules.descriptors.ColumnDescriptor;

public class TupleTableModel extends AbstractTableModel {

	private List<Tuple> elements;
	
	public TupleTableModel(List<Tuple> elements) {
		this.elements = elements;
	}
	
	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public int getColumnCount() {
		if(getRowCount() != 0)
			return elements.get(0).getColumns().size();
		else 
			return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Tuple e = elements.get(rowIndex);
		Collection<Object> o = e.values();
		return o.toArray()[columnIndex].toString();
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		ColumnDescriptor c = (ColumnDescriptor) elements.get(0).getColumns().toArray()[columnIndex];
		return c.getName();
		
	}

}
