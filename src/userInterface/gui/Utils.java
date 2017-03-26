package userInterface.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Utils {
	
	public static Dimension screenResolution() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return screenSize;
	}
	
	public static String leadingZeros(int number) {
		return ("00" + number).substring(("" + number).length());
	}
}
