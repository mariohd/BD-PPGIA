package db.modules.dataStructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import db.modules.descriptors.ColumnDescriptor;
import db.modules.metaStructure.TableHeader;

public class Table {
	private final String nome;
	private List<ColumnDescriptor> columns;
	private byte container;
	private RandomAccessFile file;
	private TableHeader header;
	private final String filePath;
	
	public Table(String nome) throws FileNotFoundException {
		this.nome = nome;
		this.header = new TableHeader(this);
		this.filePath = "database/data/" + this.nome + ".tb";
		this.file = new RandomAccessFile(filePath, "rw");
	}
	
	public Table(String nome, List<ColumnDescriptor> columns, byte container) {
		super();
		this.nome = nome;
		this.columns = columns;
		this.container = container;
		this.filePath = "database/data/" + this.nome + ".tb";
		this.header = new TableHeader(this);
	}
	
	public boolean save() throws IOException {		
		try {
			this.file = new RandomAccessFile("database/data/" + this.nome + ".tb", "rw");			
		} catch (FileNotFoundException e) {
			Path pathToFile = Paths.get("database/data/" + this.nome + ".tb");
			Files.createDirectories(pathToFile.getParent());
			Files.createFile(pathToFile);
			this.save();
		}
		
		return header.save();
	}
	
	public boolean load() throws IOException {
		return header.load();
	}
	
	public RandomAccessFile getContainerFile() {
		return file;
	}
	
	public List<ColumnDescriptor> getColumns() {
		return columns;
	}

	public byte getContainer() {
		return container;
	}

	public void setContainer(byte container) {
		this.container = container;
	}
	
	public void setColumns(List<ColumnDescriptor> columns) {
		this.columns = columns;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public void print() throws IOException {
		this.load();
		System.out.println("TABELA: " + this.nome.toUpperCase());
		header.print();
		System.out.println("\tCOLUNAS:");
		for (ColumnDescriptor columnDescriptor : columns) {
			System.out.println("\t\tNOME: " + columnDescriptor.getName());
			System.out.println("\t\tTIPO: " + columnDescriptor.getType());
			System.out.println("\t\tTAMANHO: " + columnDescriptor.getSize() + " Bytes");
			System.out.println("\t\t------------------");
		}

	}
}