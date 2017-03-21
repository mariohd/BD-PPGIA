package db.modules.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import db.Utils;
import db.modules.dataStructure.Table;
import db.modules.descriptors.ColumnDescriptor;

public class FileSystem {
	
	private static final String fileSystemControl = "database/conf.fsc";
	public static final int pageSize = 1 * 1024;
	
	private List<Table> tables;
	
	public FileSystem() {
		try {
			this.tables = new ArrayList<Table>();
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
		Table t = new Table(tableName, 
							columns, 
							new Integer(FileSystem.getTableContainerCount()).byteValue());
		if (t.save()) {
			tables.add(t);
			saveTableOnConf();
			return true;
		} else {
			return false;
		}
	}
	
	public List<Table> getTables() {
		return this.tables;
	}
	
	/*
	 ************** PRIVATE METHODS ************
	*/
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
			
			if (names != null && ! "".equals(names)) {
				for (String tableName : names.split(";")) {
					tables.add(new Table(tableName.substring(2)));
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
}
