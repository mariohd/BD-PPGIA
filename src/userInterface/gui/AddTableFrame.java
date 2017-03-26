package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;

public class AddTableFrame extends JFrame {
	private JTextField tableName;
	
	private JButton moreColumns;
	private JButton confirmTable;
	
	private JPanel columnsPanel;
	private List<ColumnForm> columnsForms;
	private Runnable callback;
		
	public AddTableFrame(Runnable callback) {
		this.tableName = new JTextField(40);
		this.moreColumns = new JButton("+");
		this.confirmTable = new JButton("Salvar Tabela");
		this.columnsPanel = new JPanel();
		this.columnsForms = new LinkedList<ColumnForm>();
		this.callback = callback;
		
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
		this.setTitle("Nova Tabela");
		this.setResizable(false);
	}
	
	private void mount() {
		this.columnsPanel.setLayout(new BoxLayout(this.columnsPanel, BoxLayout.Y_AXIS));
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(new JLabel("Tabela: "));
		northPanel.add(new JPanel().add(tableName));
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(confirmTable);
		
		ColumnForm first = new ColumnForm(1);
		columnsForms.add(first);
		columnsPanel.add(first);
		
		JPanel eastPanel = new JPanel(new FlowLayout());
		eastPanel.add(moreColumns);
		this.add(eastPanel,BorderLayout.EAST);
		this.add(northPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(this.columnsPanel), BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void configActions() {
		this.moreColumns.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ColumnForm newColumn = new ColumnForm(columnsForms.size() + 1);
				columnsPanel.add(newColumn);
				columnsForms.add(newColumn);
				validate();
				repaint();
			}
		});
		final JFrame self = this;
		this.confirmTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					List<ColumnDescriptor> columns = new LinkedList<ColumnDescriptor>();
					for (ColumnForm columnForm : columnsForms) {
						columns.add(columnForm.getValue());
					}
					String name = tableName.getText();
					if (new FileSystem().createTable(name, columns)) {
						setVisible(false);
						JOptionPane.showMessageDialog(null, "Tabela salva com sucesso!", "Mensagem", JOptionPane.INFORMATION_MESSAGE, Icons.getIcon("icons/ok.png"));
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
	
	public class ColumnForm extends JPanel {
		private JTextField columnName;
		private JComboBox<String> columnType;
		private JFormattedTextField columnSize;
		private JLabel indexLabel;
		
		public ColumnForm(int index) {
			this.indexLabel = new JLabel("#"+ Utils.leadingZeros(index), JLabel.LEFT); 
			this.columnName = new JTextField(20);
			this.columnType = new JComboBox<String>();
			this.columnSize = new JFormattedTextField();
			
			this.init();
			this.mount();
		}
		
		public ColumnDescriptor getValue() {
			String name = this.columnName.getText();
			Class type =  this.columnType.getSelectedItem().equals("Integer") ? Integer.class : String.class;
			int size = Integer.valueOf(this.columnSize.getText());
			return new ColumnDescriptor(name, type, size);
		}
		
		private void init() {
			this.columnSize.setColumns(8);
			this.columnType.addItem("Integer");
			this.columnType.addItem("String");
		}
		
		private void mount() {
			this.add(indexLabel);
			this.add(columnName);
			this.add(columnType);
			this.add(columnSize);
		}
	}
	
}
