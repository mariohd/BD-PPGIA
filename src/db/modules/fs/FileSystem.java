package db.modules.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import db.Utils;
import db.modules.buffer.BufferController;
import db.modules.buffer.algorithm.BufferAlgorithm;
import db.modules.dataStructure.DataBlock;
import db.modules.dataStructure.Table;
import db.modules.dataStructure.Tuple;
import db.modules.descriptors.ColumnDescriptor;
import db.modules.metaStructure.PageBlock;

public class FileSystem {
	
	private static final String fileSystemControl = "database/conf.fsc";
	public static final int pageSize = 8 * 1024;
	
	private List<Table> tables;
	private BufferAlgorithm<PageBlock> buffer;
	
	public FileSystem() {
		try {
			this.tables = new ArrayList<Table>();
			this.buffer = BufferController.getLRUAlgorithm();
			this.preLoadTables();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getTableContainerCount() {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");
			file.seek(0);
			byte[] s = new byte[4];
			file.read(s, 0, 4);
			int container = Utils.toInt(s, 0);
			setTableContainerCount(container + 1);
			file.close();
			return container;
		} catch (FileNotFoundException e) {
			setTableContainerCount(0);
			return getTableContainerCount();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return -1;
	}

	public boolean createTable(String tableName, List<ColumnDescriptor> columns) throws IOException {
		columns.add(0, new ColumnDescriptor("ROWID", String.class, 10));

		Table newTable = new Table(new Integer(FileSystem.getTableContainerCount()).byteValue(), tableName);
		newTable.setColumns(columns);
		if (newTable.save()) {
			tables.add(newTable);
			saveTableOnConf();
			return true;
		} else {
			return false;
		}
	}
	
	public List<Table> getTables() {
		return this.tables;
	}
	
	public DataBlock getDataBlock(String rowid) throws IOException {
		DataBlock db = null;
		String[] parts = rowid.split("\\.");
		int container = Integer.valueOf(parts[0]);
		int blockId = Integer.valueOf(parts[1]);
		String blockIdent = container + "." + blockId;
		if (buffer.getNode(blockIdent) != null) {
			db = (DataBlock) buffer.getNode(blockIdent).getValue().get();
			buffer.addHit();
		} else {
			
			Table i = null;
			
			for (Table t : tables) {
				if (t.getContainer() == container) {
					i = t;
				}
			}
			
			db = new DataBlock(i, blockId);
			db.load();
			
			buffer.set(blockIdent, db);
			buffer.addMiss();
		}
		
		return db;
	}
	
	private static void setTableContainerCount(int container) {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");
			file.seek(0);
			file.write(Utils.toByteArray(container, 4), 0, 4);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void preLoadTables() throws IOException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");
			
			file.seek(5);
			byte[] size = new byte[4];
			file.read(size, 0, 4);
			int tableNamesSize = Utils.toInt(size, 0);
			
			file.seek(10);
			byte [] tableNamesBytes = new byte[tableNamesSize];
			file.read(tableNamesBytes, 0, tableNamesSize);
			
			String names = new String(tableNamesBytes, "UTF-8");
			
			byte index = 0;
			if (names != null && ! "".equals(names)) {
				for (String tableName : names.split(";")) {
					tables.add(new Table(index, tableName.substring(2)));
					index++;
				}
			}
			file.close();

		}catch (FileNotFoundException e) {
			File f = new File(fileSystemControl);
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
	}
	
	private void saveTableOnConf() throws IOException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");

			String names = "";
			for (Table t : tables) {
				names += "T=" + t.getNome() + ";";
			}
			
			byte[] tableNameInBytes = names.getBytes();
			
			file.seek(5);
			file.write(Utils.toByteArray(tableNameInBytes.length, 4), 0, 4);
			
			file.seek(10);
			file.write(tableNameInBytes, 0, tableNameInBytes.length);
			file.close();
		} catch (FileNotFoundException e) {
			setTableContainerCount(0);
			saveTableOnConf();
		}
	}

	public Tuple search(String rowid) throws IOException {
		DataBlock db = getDataBlock(rowid);
		db.getParent().load();
		String[] parts = rowid.split("\\.");
		return db.getTuple(Integer.valueOf(parts[2]));
	}
}
