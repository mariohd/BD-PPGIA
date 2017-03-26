package userInterface.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import db.modules.dataStructure.Table;

public class TableStructurePanel extends JPanel {
	
	private Table table;
	
	public TableStructurePanel(Table table) {
		this.table = table;
		
		this.init();
		this.mount();
	}
	
	private void init() {
		setLayout(new BorderLayout());
	}
	
	private void mount() {
		TableModel tableModel = new ColumnDescriptorTableModel(table.getColumns());
		JTable jTable = new JTable(tableModel);
		
		this.add(new JScrollPane(jTable), BorderLayout.CENTER);
	}
}
