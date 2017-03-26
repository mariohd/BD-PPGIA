package userInterface.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import db.modules.dataStructure.Table;

public class SelectedTable extends JPanel {
	private Table table;
	private JTabbedPane tabs = new JTabbedPane();
	private JPanel structure;
	private JPanel content;
	private JPanel query;
	
	public SelectedTable(Table table) {
		try {
			this.table = table;
			this.table.load();
			this.structure = new TableStructurePanel(table);
			this.content = new TableContentPanel(table);
			this.query = new JPanel();
			this.init();
			this.mount();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		this.setLayout(new BorderLayout());
	}
	
	private void mount() {
		ImageIcon structureIcon = Icons.getIcon("icons/structure.png");
		ImageIcon contentIcon = Icons.getIcon("icons/folder.png");
		ImageIcon queryIcon = Icons.getIcon("icons/search.png");
		
		tabs.addTab("Estrutura",structureIcon, structure);
		tabs.addTab("Conteúdo", contentIcon, content);
		tabs.addTab("Consulta", queryIcon, query);
		this.add(tabs, BorderLayout.CENTER);
	}
}
