package userInterface.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import db.modules.dataStructure.Table;

public class TableStructurePanel extends JPanel {
	
	private Table table;
	
	public TableStructurePanel(Table t) {
		this.table = t;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		ColumnDescriptorTableModel cdtm = new ColumnDescriptorTableModel(table.getColumns());
		JTable jtb = new JTable(cdtm);
		JScrollPane jsp = new JScrollPane(jtb);
		add(jsp, BorderLayout.CENTER);
	}
}
