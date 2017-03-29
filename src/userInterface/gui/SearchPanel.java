package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;
import db.modules.descriptors.ColumnDescriptor;

public class SearchPanel extends JPanel {
	private JTextField textInput;
	private JComboBox<ColumnDescriptor> matchColumn;
	private JComboBox<String> propertyMatch;
	private TupleTableModel resultSetModel;
	private JTable resultTable;
	private JButton searchButton;
	private Table table;
	private Map<String, PropertyMatcher> matchers;
	private JScrollPane center;
	private JPanel south;
	private JLabel resultSizeLabel;
	private JLabel loading;
	private JScrollPane loadingPanel;
	private JPanel cards;

	private PropertyMatcher equals = new PropertyMatcher() {

		@Override
		public boolean test(String expected, String tested) {

			return expected.compareTo(tested) == 0;
		}

		@Override
		public boolean test(BigDecimal expected, BigDecimal tested) {
			return expected.compareTo(tested) == 0;
		}

	};

	private PropertyMatcher bigger = new PropertyMatcher() {

		@Override
		public boolean test(String expected, String tested) {
			return expected.compareTo(tested) == -1;
		}

		@Override
		public boolean test(BigDecimal expected, BigDecimal tested) {
			return expected.compareTo(tested) == -1;
		}

	};

	private PropertyMatcher lower = new PropertyMatcher() {

		@Override
		public boolean test(String expected, String tested) {
			return expected.compareTo(tested) == 1;
		}

		@Override
		public boolean test(BigDecimal expected, BigDecimal tested) {
			return expected.compareTo(tested) == 1;
		}

	};

	private PropertyMatcher diff = new PropertyMatcher() {

		@Override
		public boolean test(String expected, String tested) {
			return !equals.test(expected, tested);
		}

		@Override
		public boolean test(BigDecimal expected, BigDecimal tested) {
			return !equals.test(expected, tested);
		}

	};

	public SearchPanel(Table table) {
		this.table = table;
		this.textInput = new JTextField(40);
		this.matchColumn = new JComboBox<ColumnDescriptor>();
		this.propertyMatch = new JComboBox<String>();
		this.resultSetModel = new TupleTableModel(new ArrayList<Tuple>());
		this.resultTable = new JTable(this.resultSetModel);
		this.searchButton = new JButton("Pesquisar");
		this.matchers = new LinkedHashMap<String, PropertyMatcher>();
		this.resultSizeLabel = new JLabel();
		this.loading = new JLabel(Icons.getIcon("icons/loading.gif"), JLabel.CENTER);
		this.loadingPanel = new JScrollPane();
		this.cards = new JPanel(new CardLayout());
		init();
		config();
		configActions();
		mount();
	}

	private void init() {
		this.setLayout(new BorderLayout());
	}

	private void config() {
		for (ColumnDescriptor cd : this.table.getColumns()) {
			this.matchColumn.addItem(cd);
		}

		this.propertyMatch.addItem("=");
		this.propertyMatch.addItem(">");
		this.propertyMatch.addItem("<");
		this.propertyMatch.addItem("!");

		this.matchers.put("=", equals);
		this.matchers.put(">", bigger);
		this.matchers.put("<", lower);
		this.matchers.put("!", diff);

	}

	private void configActions() {
		this.searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) cards.getLayout();
				cl.show(cards, "loading");
				validate();
				repaint();
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						ColumnDescriptor columnSelected = (ColumnDescriptor) matchColumn.getSelectedItem();
						String selectedMatcher = (String) propertyMatch.getSelectedItem();
						PropertyMatcher matcher = matchers.get(selectedMatcher);
						
						String value = textInput.getText();
						List<Tuple> matches;
						try {
							matches = table.search(columnSelected, matcher, value);
							CardLayout cl = (CardLayout) cards.getLayout();
							cl.removeLayoutComponent(center);
							
							resultSetModel = new TupleTableModel(matches);
							resultTable = new JTable(resultSetModel);
							center = new JScrollPane(resultTable);
							
							cards.add(center, "result");
							cl.show(cards, "result");
							resultSizeLabel.setText("Matches: " + matches.size());
							
							
							validate();
							repaint();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				t.start();
			}
		});
	}

	private void mount() {
		JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));

		north.add(matchColumn);
		north.add(propertyMatch);
		north.add(textInput);
		north.add(searchButton);
		this.add(north, BorderLayout.NORTH);

		this.center = new JScrollPane(resultTable);
		this.loadingPanel = new JScrollPane(loading);
		
		this.cards.add(center, "result");
		this.cards.add(loadingPanel, "loading");
		this.add(cards, BorderLayout.CENTER);

		south = new JPanel(new FlowLayout(FlowLayout.LEFT));
		south.add(resultSizeLabel);
		this.add(south, BorderLayout.SOUTH);
	}
}
