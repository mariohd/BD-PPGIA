package userInterface.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
		ColumnDescriptorTableModel cdtm = new ColumnDescriptorTableModel(table.getColumns());
		JTable jTable = new JTable(cdtm);
		this.add(new JScrollPane(jTable), BorderLayout.CENTER);
	}
}
