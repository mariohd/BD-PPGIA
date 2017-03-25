package userInterface.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import db.modules.dataStructure.Table;

public class SelectedTable extends JPanel {
	private Table table;
	private JTabbedPane abas = new JTabbedPane();
	private JPanel structure;
	private JPanel content;
	private JPanel query;
	
	public SelectedTable(Table table) {
		try {
			this.table = table;
			this.table.load();
			this.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.structure = new TableStructurePanel(table);
		this.content = new TableContentPanel(table);
		this.query = new JPanel();
		abas.add("Estrutura", structure);
		abas.add("Conteúdo", content);
		abas.add("Consulta", query);
		this.add(abas, BorderLayout.CENTER);
	}
}
