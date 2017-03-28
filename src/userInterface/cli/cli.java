package userInterface.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import db.modules.buffer.BufferController;
import db.modules.buffer.algorithm.BufferAlgorithm;
import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;
import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;
import db.modules.metaStructure.PageBlock;

public class cli {
	
	private static FileSystem fs = new FileSystem();
	
	public static void main(String[] args) {
		mainMenu();
	}
	
	private static void mainMenu() {
		tyo();
		syso("1 - Operação com tabelas");
		syso("2 - Operação com linhas");
		syso("3 - Ver Buffer");
		syso("4 - Sair");
		
		int opt = getScanner().nextInt();
		
		switch (opt) {
		case 1:
			operationsWithTables();
			break;
		case 2:
			chooseTable();
			break;
		case 3:
			viewBuffer();
			break;
		case 4: 
			break;
		default:
			syse("Opção inválida");
			mainMenu();
		} 
	}

	private static void viewBuffer() {
		BufferAlgorithm<PageBlock> buffer = BufferController.getLRUAlgorithm();
		
		syso(buffer.getCollection());
		
		syse("Misses => " + buffer.getMisses());
		syso("Hits => " + buffer.getHits());
		syso("Hit Ratio => " + buffer.getHitRatio());
		mainMenu();
	}

	private static void chooseTable() {
		operationsWithTuples(listTables());
	}

	private static void operationsWithTuples (Table t) {
		tyo();
		syso("1 - Inseção de linhas");
		syso("2 - Consulta de linhas");
		syso("3 - Retornar");

		int option = getScanner().nextInt();

		switch (option) {
		case 1:
			insertTuple(t);
			break;
		case 2:
			readTable(t);
			operationsWithTuples(t);
			break;
		case 3:
			chooseTable();
			break;
		default:
			syse("Opção inválida");
			operationsWithTuples(t);
		}
	}

	private static void readTable(Table t) {
		try {
			TableBuilder tb = new TableBuilder();
			
			List<ColumnDescriptor> columns = t.getColumns();
			String[] columnsName = new String[columns.size()];
			int index = 0;
			for (ColumnDescriptor columnDescriptor : columns) {
				columnsName[index] = (columnDescriptor.getName().toUpperCase());
				index++;
			}
			tb.addRow(columnsName);
			List<Tuple> allTuples = t.allTuples();
			for (Tuple tuple : allTuples) {
				String[] tx = new String[columns.size()];
				index = 0;				
				for (Object value : tuple.values()) {
					tx[index] = value.toString();
					index++;
				}
				tb.addRow(tx);
			} 
			
			syso(tb.toString());
			operationsWithTuples(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void insertTuple(Table t) {
		Map<ColumnDescriptor, Object> tuple = new LinkedHashMap<ColumnDescriptor, Object>();
		Object value;
		for (ColumnDescriptor column : t.getColumns()) {
			if (! column.isSsystemColumn()) {
				syso("Digite o valor para a coluna \"" + column.getName() + "\"");
				if (column.getType() == Integer.class) {
					value = getScanner().nextInt();
				} else {
					value = getScanner().nextLine();
				}
				tuple.put(column, value);
			}
		}

		try {
			t.insert(tuple);
			syso("Linha inserida com sucesso!");
			operationsWithTuples(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void operationsWithTables() {
		tyo();
		syso("1 - Inseção de tabelas");
		syso("2 - Listar de tabelas");
		syso("3 - Retornar");
		
		int opt = getScanner().nextInt();
		
		switch (opt) {
		case 1:
			insertTable();
			break;

		case 2:
			listTables();
			operationsWithTables();
			break;
		case 3: 
			mainMenu();
			break;
		default:
			syse("Opção inválida");
			operationsWithTables();
		}
	}
	
	private static void insertTable() {
		syso("Digite o nome da tabela: ");
		
		String tableName = getScanner().next();
				
		boolean moreColumns = true;
		List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
		while (moreColumns) {
			syso("Digite o nome da coluna #" + (columns.size() + 1) + ":");
			String columnName = getScanner().next();
			syso("Escolha o tipo de dado da coluna #" + (columns.size() + 1) + ":");
			syso("1 - Integer \n2 - String");
			Class type = getScanner().nextInt() == 1? Integer.class : String.class;
			syso("Escolha o tamanho da coluna #" + (columns.size() + 1) + ":");
			
			int size = getScanner().nextInt();
			
			columns.add(new ColumnDescriptor(columnName, type, size));
			
			syso("Deseja continuar inserindo colunas? S/N");
			String more = getScanner().next();
			if ("N".equals(more.toUpperCase())) moreColumns = false;
		}
		
		try {
			if (fs.createTable(tableName, columns)) {
				syso("Tabela criada com sucesso!");
			} else {
				syse("Algum problema ocorreu =(");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		operationsWithTables();
	}
	
	private static Table listTables() {
		tyo();
		syso("Tabelas => ");
		
		int index = 1;
		
		for (Table table : fs.getTables()) {
			syso("\t" + (index) + " - " + table.getNome());
			index ++;
		}
		
		syso(index + " - Retornar");
		
		int opt = getScanner().nextInt();
		
		if (opt <= 0) {
			syse("Opção Inválida"); 
			listTables();
		}
		
		if (opt == index) {
			mainMenu();
		}
		
		if (opt > fs.getTables().size()) {
			return null;
		} else {
			Table tb = fs.getTables().get(opt - 1);
			
			try {
				tb.load();
				syso(tb.print());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return tb;
		}
	}
	
	private static Scanner getScanner() {
		Scanner s = new Scanner(System.in);
		return s;
	}

	private static void tyo() {
		syso("Digite a opção desejada");
	}
	
	private static void syso(Object o) {
		System.out.println(o);
	}
	
	private static void syse(Object o) {
		System.err.println(o);
		System.err.flush();
	}
}
