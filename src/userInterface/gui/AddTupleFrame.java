package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import db.modules.dataStructure.Table;
import db.modules.descriptors.ColumnDescriptor;

public class AddTupleFrame extends JFrame {

	private Table table;
	private JButton confirmTuple;
	private JPanel columnsPanel;
	private List<TupleColumnForm> columnForms;
	private Map<ColumnDescriptor, Object> values;
	
	private Runnable callback;
	
	public AddTupleFrame(Table table, Runnable callback) {
		this.table = table;
		this.callback = callback;
		
		this.confirmTuple = new JButton("Salvar Tupla");
		this.columnsPanel = new JPanel();
		
		this.columnForms = new LinkedList<TupleColumnForm>();
		this.values = new LinkedHashMap<ColumnDescriptor, Object>();
		
		init();
		configActions();
		mount();
		this.setVisible(true);
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		Dimension resolution = Utils.screenResolution();
		resolution.setSize(resolution.getWidth() * 0.5, resolution.getHeight() * 0.5);
		this.setSize(resolution);
		this.setLocationRelativeTo(null);
		this.setTitle("Nova Tupla para a tabela \"" + table.getNome() + "\"");
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.columnsPanel.setLayout(new WrapLayout(FlowLayout.LEFT));			
	}
	
	private void mount() {
		for (ColumnDescriptor column : table.getColumns()) {
			if (! column.isSsystemColumn()) {
				TupleColumnForm tcf = new TupleColumnForm(column);
				columnForms.add(tcf);
				columnsPanel.add(tcf);
			}
		}
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(columnsPanel);
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.add(confirmTuple);
		
		this.add(new JScrollPane(p), BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
	}
	
	private void configActions() {
		final JFrame self = this;
		confirmTuple.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					for (TupleColumnForm tupleColumnForm : columnForms) {
						values.put(tupleColumnForm.getColumnKey(), tupleColumnForm.getValue());
					}
					
					if (table.insert(values)) {
						setVisible(false);
						JOptionPane.showMessageDialog(null, "Linha salva com sucesso!", "Mensagem", JOptionPane.INFORMATION_MESSAGE, Icons.getIcon("icons/ok.png"));
						callback.run();
						self.dispatchEvent(new WindowEvent(self, WindowEvent.WINDOW_CLOSING));
					} else {
						JOptionPane.showMessageDialog(null, "Algo deu errado =/", "Não salvou", JOptionPane.ERROR_MESSAGE, Icons.getIcon("icons/error.png"));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public class TupleColumnForm extends JPanel {
		private ColumnDescriptor column;
		private JTextField columnValue;
		
		public TupleColumnForm(ColumnDescriptor column) {
			this.column = column;
			this.columnValue = new JTextField(20);
			this.mount();
		}
				
		private void mount() {
			JPanel columnPanel = new JPanel();
			columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
			columnValue.setAlignmentX(LEFT_ALIGNMENT);
			this.setAlignmentX(LEFT_ALIGNMENT);
			columnPanel.add(new JLabel(column.getName() + " (" + column.getType().getSimpleName() + " ― " + column.getSize() + ")"));
			columnPanel.add(columnValue);
			
			this.add(columnPanel);
		}
		
		public Object getValue() {
			return column.getType() == Integer.class ? Integer.valueOf(columnValue.getText()) : columnValue.getText();
		}
		
		public ColumnDescriptor getColumnKey() {
			return this.column;
		}
	}
	
}
