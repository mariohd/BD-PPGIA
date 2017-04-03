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
import javax.swing.JScrollPane;

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
	
	private JMenu bufferMenu;
	private JMenuItem viewBuffer;
	private JMenu bufferAlgorithms;
	private JMenuItem lruAlgorithm;
	
	private JScrollPane loadingPanel;
	
	public Window(FileSystem fs) {
		this.fs = fs;
		this.comboTables = new JComboBox<Table>();
		
		this.confirmTable = new JButton("Confirmar");
		
		this.menuBar = new JMenuBar();
		this.tableMenu = new JMenu("Tabelas");
		this.addTable = new JMenuItem("Adicionar");
		this.refreshTables = new JMenuItem("Atualizar");
		
		this.bufferMenu = new JMenu("Buffer");
		this.viewBuffer = new JMenuItem("Ver Buffer");
		this.bufferAlgorithms = new JMenu("Algoritmos");
		this.lruAlgorithm = new JMenuItem("LRU");
		
		this.loadingPanel = new JScrollPane();
		
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
		this.setTitle("PPGIA_Db");
		this.setIconImage(Icons.getIcon("icons/database.png").getImage());
	}
	
	private void config() {		
		tableMenu.add(addTable);
		addTable.setIcon(Icons.getIcon("icons/plus.png", new Dimension(24, 24)));
		tableMenu.addSeparator();
		tableMenu.add(refreshTables);
		refreshTables.setIcon(Icons.getIcon("icons/refresh.png", new Dimension(24, 24)));
		
		bufferMenu.add(viewBuffer);
		viewBuffer.setIcon(Icons.getIcon("icons/buffer.png", new Dimension(24, 24)));
		bufferAlgorithms.setIcon(Icons.getIcon("icons/algorithm.png", new Dimension(24, 24)));
		bufferAlgorithms.add(lruAlgorithm);
		bufferMenu.add(bufferAlgorithms);
		
		menuBar.add(tableMenu);
		menuBar.add(bufferMenu);
		
		for (Table t : fs.getTables().values()) {
			comboTables.addItem(t);
		}
		configActions();
	}
	
	private void configActions() {
		viewBuffer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new BufferFrame();
			}
		});
		addTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable callback = new Runnable() {
					@Override
					public void run() {
						refreshTables();
					}
				};
				
				new AddTableFrame(callback);
			}
		});
		
		refreshTables.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTables();
			}
		});
		
		confirmTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (center.isShowing()) remove(center);
				add(loadingPanel, BorderLayout.CENTER);
				validate();
				repaint();
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						center = new SelectedTable((Table) comboTables.getSelectedItem());
						if (loadingPanel.isShowing()) remove(loadingPanel);
						add(center, BorderLayout.CENTER);
						validate();
						repaint();
					}
				});
				t.start();
			}
		});
	}
	
	private void refreshTables() {
		comboTables.removeAllItems();
		fs = new FileSystem();
		for (Table t : fs.getTables().values()) {
			comboTables.addItem(t);
		}
		validate();
		repaint();
	}
		
	private void mount() {		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		northPanel.add(new JLabel("Tabela: "));
		northPanel.add(comboTables);
		northPanel.add(confirmTable);
		
		this.loadingPanel = new JScrollPane(new JLabel(Icons.getIcon("icons/loading.gif"), JLabel.CENTER));
		
		this.setJMenuBar(menuBar);
		this.add(northPanel, BorderLayout.NORTH);
	}
}
