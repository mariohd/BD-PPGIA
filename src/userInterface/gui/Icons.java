package userInterface.gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Icons {

	public static ImageIcon getIcon(String path) {
		return new ImageIcon(Icons.class.getResource(path));
	}
	
	public static ImageIcon getIcon(String path, Dimension size) {
		ImageIcon i = getIcon(path);
		Image image = i.getImage(); 
		Image newimg = image.getScaledInstance(size.width, size.height, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(newimg);
	}
}
