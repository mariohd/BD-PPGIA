package userInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import db.modules.dataStructure.Table;
import db.modules.descriptors.ColumnDescriptor;
import db.modules.fs.FileSystem;

public class LoadTPCHForn {
	public static void main(String[] args) throws IOException {
		
		List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
		ColumnDescriptor cd = new ColumnDescriptor("COD_FORN", Integer.class, 6);
		ColumnDescriptor nome = new ColumnDescriptor("NOME_FORN", String.class, 25);
		ColumnDescriptor end = new ColumnDescriptor("END_FORN", String.class, 25);
		ColumnDescriptor nasc = new ColumnDescriptor("NASC_FORN", Integer.class, 4);
		ColumnDescriptor fon = new ColumnDescriptor("FON_FORN", String.class, 15);
		ColumnDescriptor con = new ColumnDescriptor("CON_CLI", String.class, 101);
		
		columns.add(cd);
		columns.add(nome);
		columns.add(end);
		columns.add(nasc);
		columns.add(fon);
		columns.add(con);
		
		FileSystem fs = new FileSystem();
		Table tb = null;
		for (Table ts : fs.getTables().values()) {
			if (ts.getNome() == "TPCH_FORN") {
				tb = ts;
			}
		}
		
		if (tb == null) {			
			fs.createTable("TPCH_FORN", columns);
			columns.remove(0);
			
			for (Table ts : fs.getTables().values()) {
				if (ts.getNome() == "TPCH_FORN") {
					tb = ts;
				}
			}
		}
		
	
		
		Path path = Paths.get("database/forn-tpch.txt");
	    Charset charset = Charset.forName("ISO-8859-1");

		List<String> lines = Files.readAllLines(path, charset);
		int index = 0;
		lines.remove(0);
		
		for (String string : lines) {
			tb.insert(parseLine(string, columns));
			System.out.println(index++);
		}
		
	}
	
	private static Map<ColumnDescriptor, Object> parseLine(String line, List<ColumnDescriptor> columns) {
		String[] parts = line.split("\\|");
		Map<ColumnDescriptor, Object> values = new LinkedHashMap<ColumnDescriptor, Object>();
		
		for (int i = 0; i < columns.size(); i++) {
			ColumnDescriptor cd = columns.get(i);
			if (cd.getType() == Integer.class) {
				values.put(cd, Integer.parseInt(parts[i]));
			} else {
				values.put(cd, parts[i]);
			}
		}
		
		return values;
	}
}
