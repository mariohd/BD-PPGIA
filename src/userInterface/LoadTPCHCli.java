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

public class LoadTPCHCli {
public static void main(String[] args) throws IOException {
		
		List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
		ColumnDescriptor cd = new ColumnDescriptor("COD_CLI", Integer.class, 6);
		ColumnDescriptor nome = new ColumnDescriptor("NOME_CLI", String.class, 25);
		ColumnDescriptor end = new ColumnDescriptor("END_CLI", String.class, 40);
		ColumnDescriptor nasc = new ColumnDescriptor("NASC_CLI", Integer.class, 4);
		ColumnDescriptor fon = new ColumnDescriptor("FON_CLI", String.class, 15);
		ColumnDescriptor mkt = new ColumnDescriptor("MKT_CLI", String.class, 10);
		ColumnDescriptor con = new ColumnDescriptor("COM_CLI", String.class, 117);
		
		columns.add(cd);
		columns.add(nome);
		columns.add(end);
		columns.add(nasc);
		columns.add(fon);
		columns.add(mkt);
		columns.add(con);
		
		FileSystem fs = new FileSystem();
		Table tb = null;
		for (Table ts : fs.getTables()) {
			if (ts.getNome() == "TPCH_CLI") {
				tb = ts;
			}
		}
		
		if (tb == null) {			
			fs.createTable("TPCH_CLI", columns);
			columns.remove(0);
			
			for (Table ts : fs.getTables()) {
				if (ts.getNome() == "TPCH_CLI") {
					tb = ts;
				}
			}
		}
		
	
		
		Path path = Paths.get("database/cli-tpch.txt");
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
