package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
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
	
	private JButton addTuple;
	
	public TableContentPanel(Table table, Runnable callback) {
		try {
			this.table = table;
			this.callback = callback;
			this.tuples = this.table.allTuples();
			
			this.tableModel = new TupleTableModel(this.tuples);
			this.jTable = new JTable(tableModel);
			
			this.addTuple = new JButton("Adicionar Linhas");
			
			init();
			configActions();
			mount();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
	}
	
	private void mount() {
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(addTuple);	
		
		this.add(new JScrollPane(jTable), BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void configActions() {
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
