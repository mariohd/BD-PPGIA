package userInterface.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import db.modules.descriptors.ColumnDescriptor;

public class ColumnDescriptorTableModel extends AbstractTableModel {
	
	private List<ColumnDescriptor> elements;
	
	public ColumnDescriptorTableModel(List<ColumnDescriptor> elements) {
		this.elements = elements;
	}

	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ColumnDescriptor e = elements.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return e.getName();
		case 1:
			return e.getType().getSimpleName();
		case 2:
			return e.getSize();
		default:
			return e.isSsystemColumn() ? "Definido pelo sistema" : "Definido pelo usuário" ;
		}
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Campo";
		case 1: 
			return "Tipo";
		case 2: 
			return "Tamanho";
		case 3:
			return "Extra";
		}
		return null;
		
	}

}
