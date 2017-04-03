package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import db.modules.dataStructure.DataBlock;
import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;

public class TableContentPanel extends JPanel {

	private Table table;
	private List<Tuple> tuples;
	private TableModel tableModel;
	private JTable jTable;
	private Runnable callback;
	
	private JButton addTuple, loadTuples, loadAllTuples;
	private JScrollPane loadingPanel, tablePanel;
	private JButton next, previous;
	private int dataBlockId = 1;
	private JLabel dataBlockLabel;
	
	public TableContentPanel(Table table, Runnable callback) {
		this.table = table;
		this.callback = callback;
		
		this.tuples = new ArrayList<Tuple>();
		this.tableModel = new TupleTableModel(this.tuples);
		this.jTable = new JTable(tableModel);
		
		this.addTuple = new JButton("Adicionar Tuplas");
		this.loadTuples = new JButton("Carregar Tuplas");
		this.loadAllTuples = new JButton("Carregar todas as Tuplas");
		
		this.loadingPanel = new JScrollPane(new JLabel(Icons.getIcon("icons/loading.gif"), JLabel.CENTER));
		this.tablePanel = new JScrollPane();
		
		this.next = new JButton(">");
		this.previous = new JButton("<");
		this.dataBlockLabel = new JLabel("" + dataBlockId);
		this.dataBlockLabel = new JLabel("");
		
		init();
		configActions();
		mount();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		this.updatePageLabel();
	}
	
	private void mount() {
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		northPanel.add(previous);
		northPanel.add(dataBlockLabel);
		northPanel.add(next);
		northPanel.add(loadTuples);
		northPanel.add(loadAllTuples);
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(addTuple);
		
		this.tablePanel = new JScrollPane(jTable);
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(this.tablePanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void setTupleButtonsStatusTo(boolean status) {
		loadAllTuples.setEnabled(status);
		loadTuples.setEnabled(status);
		addTuple.setEnabled(status);
	}
	
	private void configActions() {
		loadAllTuples.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setTupleButtonsStatusTo(false);
				remove(tablePanel);
				add(loadingPanel);
				revalidate();
				repaint();
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							tuples.clear();
							tuples = table.allTuples();
							tableModel = new TupleTableModel(tuples);
							jTable = new JTable(tableModel);
							tablePanel = new JScrollPane(jTable);
							remove(loadingPanel);
							add(tablePanel, BorderLayout.CENTER);
							setTupleButtonsStatusTo(true);
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
		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dataBlockId ++;
				updatePageLabel();
			}
		});
		previous.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dataBlockId - 1 >= 1) {
					dataBlockId-- ;
					updatePageLabel();
				}
			}
		});
		
		loadTuples.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setTupleButtonsStatusTo(false);
				remove(tablePanel);
				add(loadingPanel);
				revalidate();
				repaint();
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							DataBlock db = table.getDataBlock(dataBlockId);
							db.loadTuples();
							tuples.clear();
							tuples = db.getTuples();
							tableModel = new TupleTableModel(tuples);
							jTable = new JTable(tableModel);
							tablePanel = new JScrollPane(jTable);
							remove(loadingPanel);
							
							add(tablePanel, BorderLayout.CENTER);
							setTupleButtonsStatusTo(true);
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
		
		dataBlockLabel.addMouseListener(new MouseAdapter() {
						
			@Override
			public void mouseClicked(MouseEvent e) {
				String pageString = JOptionPane.showInputDialog(null, "Qual página você deseja ver?", "Seletor de páginas", JOptionPane.INFORMATION_MESSAGE);
				if (pageString == null || "".equals(pageString))
					return;
				else {
					int page = Integer.valueOf(pageString);
					dataBlockId = page;
					updatePageLabel();
					revalidate();
					repaint();					
				}
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
	
	private void updatePageLabel() {
		this.dataBlockLabel.setText("" + this.dataBlockId);
	}
}
