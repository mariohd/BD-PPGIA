package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import db.modules.dataStructure.Table;
import db.modules.fs.FileSystem;

public class Window extends JFrame {
	
	private FileSystem fs;
	private JPanel center = new JPanel();
	private JComboBox<Table> comboTables;
	
	private JButton confirmTable;
	
	private JMenuBar menuBar;
	private JMenu tableMenu;
	private JMenuItem addTable;
	private JMenuItem refreshTables;
	
	public Window(FileSystem fs) {
		this.fs = fs;
		this.comboTables = new JComboBox<Table>();
		
		this.confirmTable = new JButton("Confirmar");
		
		this.menuBar = new JMenuBar();
		this.tableMenu = new JMenu("Tabelas");
		this.addTable = new JMenuItem("Adicionar");
		this.refreshTables = new JMenuItem("Atualizar");
		
		init();
		config();
		mount();
		this.setVisible(true);
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		Dimension resolution = Utils.screenResolution();
		resolution.setSize(resolution.getWidth() * 0.7, resolution.getHeight() * 0.7);
		this.setSize(resolution);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
	
	private void config() {
		tableMenu.add(addTable);
		addTable.setIcon(Icons.getIcon("icons/plus.png", new Dimension(16, 16)));
		tableMenu.addSeparator();
		tableMenu.add(refreshTables);
		refreshTables.setIcon(Icons.getIcon("icons/refresh.png", new Dimension(16, 16)));
		menuBar.add(tableMenu);
		setJMenuBar(menuBar);
		configActions();
	}
	
	private void configActions() {
		addTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		refreshTables.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		confirmTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (center.isShowing()) remove(center);
				center = new SelectedTable((Table) comboTables.getSelectedItem());
				add(center, BorderLayout.CENTER);
				repaint();
				revalidate();
			}
		});
	}
	
	private void mount() {
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		for (Table t : fs.getTables()) {
			comboTables.addItem(t);
		}
		
		northPanel.add(new JLabel("Tabela: "));
		northPanel.add(comboTables);
		northPanel.add(confirmTable);
		
		this.add(northPanel, BorderLayout.NORTH);
	}
}
