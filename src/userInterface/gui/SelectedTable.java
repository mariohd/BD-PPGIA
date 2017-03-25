package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
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
		ImageIcon structureIcon = new ImageIcon( this.getClass().getResource("icons/structure-plan.png"));
		ImageIcon contentIcon = new ImageIcon( this.getClass().getResource("icons/folder.png"));
		ImageIcon queryIcon = new ImageIcon( this.getClass().getResource("icons/file.png"));
		abas.addTab("Estrutura",structureIcon, structure);
		abas.addTab("Conteúdo", contentIcon, content);
		abas.addTab("Consulta", queryIcon, query);
		this.add(abas, BorderLayout.CENTER);
	}
}
