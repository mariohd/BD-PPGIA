package userInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import db.modules.buffer.BufferController;
import db.modules.buffer.algorithm.BufferAlgorithm;
import db.modules.fs.FileSystem;
import db.modules.metaStructure.PageBlock;

public class ExecSearch {
	public static void main(String[] args) throws IOException {
		FileSystem fs = new FileSystem();
		
		Path path = Paths.get("database/TPCH_CLI.csv");
	    Charset charset = Charset.forName("ISO-8859-1");

		List<String> lines = Files.readAllLines(path, charset);
		
		int index = 0;
		for (String string : lines) {
			System.out.print(index ++ + " => ");
			System.out.println(fs.search(string).values());	
		}
		BufferAlgorithm<PageBlock> buffer = BufferController.getLRUAlgorithm();
		
		System.out.println("Buffer => " + buffer.getCollection());
		System.out.println("Misses => " + buffer.getMisses());
		System.out.println("Hits => " + buffer.getHits());
		System.out.println("Hit Ratio => " + buffer.getHitRatio());
	}
}
