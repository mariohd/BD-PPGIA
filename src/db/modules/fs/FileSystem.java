package db.modules.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import db.Utils;
import db.modules.descriptors.ColumnDescriptor;

public class FileSystem {
	
	private static final String fileSystemControl = "database/conf.fsc";
	public static final int pageSize = 1 * 1024;
	
	public FileSystem() {
		//loadTableNames();
	}
	
	public static int getTableContainerCount() throws IOException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");
			file.seek(0);
			byte[] s = new byte[4];
			file.read(s, 0, 4);
			int container = Utils.toInt(s, 0);
			setTableContainerCount(container + 1);
			return container;
		} catch (FileNotFoundException e) {
			setTableContainerCount(0);
			return getTableContainerCount();
		} finally {
			if (file != null) file.close();
		}
	}
	
	public boolean createTable(String tableName, List<ColumnDescriptor> columns) {
		return false;
	}
	
	/*
	 ************** PRIVATE METHODS ************
	*/
	private static void setTableContainerCount(int container) throws IOException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileSystemControl, "rw");
			file.seek(0);
			file.write(Utils.toByteArray(container, 4), 0, 4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (file != null) file.close();
		}
	}
	
	private void loadTableNames() {
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
			
			if (names == null || "".equals(names)) {
				file.close();
			}
			
			for (String table : names.split(";")) {
				//tableNames.add(table.substring(2));
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
