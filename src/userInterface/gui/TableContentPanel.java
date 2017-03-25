package userInterface.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;

public class TableContentPanel extends JPanel {

	private Table table;
	
	public TableContentPanel(Table t) {
		this.table = t;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		try {
			List<Tuple> tuples = table.allTuples();
			TupleTableModel cdtm = new TupleTableModel(tuples);
			JTable jtb = new JTable(cdtm);
			JScrollPane jsp = new JScrollPane(jtb);
			add(jsp, BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
