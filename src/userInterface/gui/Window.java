package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.omg.CORBA.ACTIVITY_COMPLETED;

import db.modules.dataStructure.Table;
import db.modules.fs.FileSystem;

public class Window extends JFrame {
	
	private FileSystem fs;
	private JPanel center = new JPanel();
	
	public Window(FileSystem fs) {
		this.fs = fs;
		init();
		config();
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
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JComboBox<Table> tableChooser = new JComboBox<Table>();
		JButton confirm = new JButton("Confirma");
		
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (center.isShowing())
					remove(center);
				center = new SelectedTable((Table) tableChooser.getSelectedItem());
				add(center, BorderLayout.CENTER);
				repaint();
				revalidate();
			}
		});
		
		for (Table t : fs.getTables()) {
			tableChooser.addItem(t);
		}
		
		
		panel.add(new JLabel("Tabela: "));
		panel.add(tableChooser);
		panel.add(confirm);
		this.add(panel, BorderLayout.NORTH);
		this.setVisible(true);
	}
}
