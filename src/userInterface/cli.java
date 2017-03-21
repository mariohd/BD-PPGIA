package userInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;

public class cli {
	
	private static FileSystem fs = new FileSystem();
	
	public static void main(String[] args) {
		mainMenu();
	}
	
	private static void mainMenu() {
		tyo();
		syso("1 - Operação com tabelas");
		syso("2 - Operação com linhas");
		syso("3 - Sair");
		
		int opt = getScanner().nextInt();
		
		switch (opt) {
		case 1:
			operationsWithTables();
			break;
		case 2:
			//chooseTable();
			break;
		case 3: 
			break;
		default:
			syse("Opção inválida");
			mainMenu();
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
			//listTables();
			//operationsWithTables();
			break;
		case 3: 
			mainMenu();
			break;
		default:
			System.err.println("Opção inválida");
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
		
		if (fs.createTable(tableName, columns)) {
			syso("Tabela criada com sucesso!");
		} else {
			syse("Algum problema ocorreu =/");
		}
		operationsWithTables();
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
	}
}
