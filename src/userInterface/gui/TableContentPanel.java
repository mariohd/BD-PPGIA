package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;

public class TableContentPanel extends JPanel {

	private Table table;
	private List<Tuple> tuples;
	private TableModel tableModel;
	private JTable jTable;
	private Runnable callback;
	
	private JButton addTuple, loadTuples;
	private JScrollPane loadingPanel, tablePanel;
	
	public TableContentPanel(Table table, Runnable callback) {
		this.table = table;
		this.callback = callback;
		
		this.tuples = new ArrayList<Tuple>();
		this.tableModel = new TupleTableModel(this.tuples);
		this.jTable = new JTable(tableModel);
		
		this.addTuple = new JButton("Adicionar Linhas");
		this.loadTuples = new JButton("Carregar Tuplas");
		
		this.loadingPanel = new JScrollPane(new JLabel(Icons.getIcon("icons/loading.gif"), JLabel.CENTER));
		this.tablePanel = new JScrollPane();
		
		init();
		configActions();
		mount();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
	}
	
	private void mount() {
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		northPanel.add(loadTuples);	
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(addTuple);	
		
		this.tablePanel = new JScrollPane(jTable);
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(this.tablePanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void configActions() {
		loadTuples.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(tablePanel);
				add(loadingPanel);
				revalidate();
				repaint();
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							tuples = table.allTuples();
							tableModel = new TupleTableModel(tuples);
							jTable = new JTable(tableModel);
							tablePanel = new JScrollPane(jTable);
							remove(loadingPanel);
							
							add(tablePanel, BorderLayout.CENTER);
							revalidate();
							repaint();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				
				t.start();		
			}
		});
		
		addTuple.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Runnable callback = new Runnable() {
					
					@Override
					public void run() {
						try {
							update();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				};
				
				new AddTupleFrame(table, callback);
			}
		});
	}
	
	private void update() throws IOException {
		this.callback.run();
	}
}
