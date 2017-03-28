package db.modules.metaStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import db.Utils;
import db.modules.dataStructure.DataBlock;
import db.modules.dataStructure.Table;
import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;

public class TableHeader implements PageBlock {
	
	private final Table parent;
	private byte container = 0;
	private byte containerStatus = 0;
	private byte[] blockSize = new byte[3];
 	private byte[] headerDescriptorSize = new byte[2];
	private byte[] nextWritingBlock = new byte[4];
	private List<ColumnDescriptor> columns;
	
	public TableHeader(Table parent) {
		this.parent = parent;
		this.blockSize = Utils.toByteArray(FileSystem.pageSize, 3);
		this.container = this.parent.getContainer();
		this.nextWritingBlock = Utils.toByteArray(1, 4);
	}

	public boolean save() throws IOException {
		RandomAccessFile file = parent.getContainerFile();
		
		file.seek(0);
		file.write(container);
		
		file.seek(1);
		file.write(blockSize);
		
		file.seek(4);
		file.write(containerStatus);
		
		file.seek(5);
		file.write(nextWritingBlock);
				
		String columns = "";
		
		for (ColumnDescriptor column : this.parent.getColumns()) {
			columns += column.asString();
		}
		
		file.seek(9);
		file.write(Utils.toByteArray(columns.length(), 2));
		
		file.seek(11);
		file.write(columns.getBytes(), 0, columns.getBytes().length);
		
		return true;
	}
	
	public boolean load() throws IOException {
		RandomAccessFile file = parent.getContainerFile();
		
		file.seek(0);
		this.container = file.readByte();
		
		file.read(this.blockSize);

		file.seek(4);
		this.containerStatus = file.readByte();
		
		file.seek(5);
		file.read(this.nextWritingBlock);
		
		file.seek(9);
		file.read(this.headerDescriptorSize);
		
		loadColumns();
		
		return true;
	}
	
	public void loadColumns() throws IOException {
		RandomAccessFile file = parent.getContainerFile();
		
		file.seek(11);
		byte[] byteColumns = new byte[Utils.toInt(headerDescriptorSize, 0)];
		file.read(byteColumns);
		
		String stringColumns = Utils.asString(byteColumns);
		
		columns = new ArrayList<ColumnDescriptor>();
		
		for (String column : stringColumns.split(";")) {
			String[] att = column.split("%");
			columns.add(
					new ColumnDescriptor(
							att[0].substring(2), // NOME
							"I".equals(att[1].substring(2)) ? Integer.class : String.class, // TIPO
							Integer.parseInt(att[2].substring(2)) // TAMANHO
					)
			);
		}
	}
	
	
	
	public String print() {
		String s = 
			 ("\tCONTAINER: " + container) + "\n"
			+ ("\tTAMANHO DO BLOCO: " + Utils.toInt(blockSize, 0))+ "\n"
			+ ("\tCONTAINER STATUS: " + containerStatus)+ "\n"
			+ ("\tPROXIMO BLOCK DE ESCRITA: " + Utils.toInt(nextWritingBlock, 0))+ "\n"
			+ ("\tTAMANHO DO DESCRITOR: " + Utils.toInt(headerDescriptorSize, 0))+ "\n";
		return s;
	}

	public DataBlock getNextWritingBlock() {
		DataBlock db = new DataBlock(parent, Utils.toInt(nextWritingBlock, 0));
		return db;
	}

	public void updateNextWritingBlock() throws IOException {
		int block = lastBlock() + 1;
		nextWritingBlock = Utils.toByteArray(block, 4);
		RandomAccessFile file = parent.getContainerFile();
		file.seek(5);
		file.write(nextWritingBlock);
	}

	public int lastBlock() {
		return Utils.toInt(nextWritingBlock, 0);
	}
	
	public byte getContainer() {
		return container;
	}
	
	@Override
	public Object get() {
		return this;
	}
	
	@Override
	public int type() {
		return this.containerStatus;
	}
	
	public String toString() {
		return "{ " + this.container + ".0 }";
	}

	public void setColumns(List<ColumnDescriptor> columns) {
		this.columns = columns;
	}

	public List<ColumnDescriptor> getColumns() {
		return columns;
	}
}
