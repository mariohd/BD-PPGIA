package userInterface.gui;

import db.modules.fs.FileSystem;

public class gui {
	private static FileSystem fs = new FileSystem();

	public static void main(String[] args) {
		new Window(fs);
	}
}
