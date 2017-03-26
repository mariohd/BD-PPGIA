package db.modules.dataStructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import db.modules.descriptors.ColumnDescriptor;
import db.modules.metaStructure.TableHeader;

public class Table {
	private final String nome;
	private List<ColumnDescriptor> columns;
	private List<DataBlock> dataBlocks;
	private byte container;
	private RandomAccessFile file;
	private TableHeader header;
	private final String filePath;
	
	public Table(String nome) throws FileNotFoundException {
		this.nome = nome;
		this.header = new TableHeader(this);
		this.filePath = "database/data/" + this.nome + ".tb";
		this.file = new RandomAccessFile(filePath, "rw");
		this.dataBlocks = new ArrayList<DataBlock>();
	}
	
	public Table(String nome, List<ColumnDescriptor> columns, byte container) {
		super();
		this.nome = nome;
		this.columns = columns;
		this.container = container;
		this.filePath = "database/data/" + this.nome + ".tb";
		this.header = new TableHeader(this);
		this.dataBlocks = new ArrayList<DataBlock>();
	}
	
	public boolean save() throws IOException {		
		try {
			this.file = new RandomAccessFile(filePath, "rw");			
		} catch (FileNotFoundException e) {
			Path pathToFile = Paths.get(filePath);
			Files.createDirectories(pathToFile.getParent());
			Files.createFile(pathToFile);
			this.save();
		}
		
		return header.save();
	}
	
	public boolean load() throws IOException {
		return header.load();
	}
	
	public boolean insert(Map<ColumnDescriptor, Object> values) throws IOException {
		this.load();
		DataBlock db = this.header.getNextWritingBlock();
		db.load();

		String rowid = db.generateNewRowid();
		Map<ColumnDescriptor, Object> values2 = new LinkedHashMap<ColumnDescriptor, Object>();
		values2.put(new ColumnDescriptor("ROWID", String.class, 10), rowid);
		values2.putAll(values);

		Tuple tuple = new Tuple(values2);

		if (db.availableSpaceFor(tuple.size())) {
			db.insert(tuple);
		} else {
			this.header.updateNextWritingBlock();
			this.insert(values);
		}

		return true;
	}
	
	public List<Tuple> allTuples() throws IOException {
		int qtBlock = this.header.lastBlock();
		List<Tuple> tuples = new ArrayList<Tuple>();
		if (dataBlocks.size() != qtBlock) {
			dataBlocks.clear();
			for (int i = 1; i <= qtBlock; i ++) {
				dataBlocks.add(new DataBlock(this, i));
			}
		}

		for (DataBlock db: dataBlocks) {
			db.load();
			db.loadTuples();
			tuples.addAll(db.getTuples());
		}
		
		return tuples;
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
	
	public String print() {
		String s = 
		"TABELA: " + this.nome.toUpperCase() + "\n" +
		header.print()  +
		"\tCOLUNAS: \n";
		for (ColumnDescriptor columnDescriptor : columns) {
			s += 
			"\t\tNOME: " + columnDescriptor.getName() + "\n" +
			("\t\tTIPO: " + columnDescriptor.getType()) + "\n" +
			("\t\tTAMANHO: " + columnDescriptor.getSize() + " Bytes") + "\n" +
			("\t\t------------------\n");
		}
		return s;
	}
	
	public String toString() {
		return this.nome;
		
	}
}
