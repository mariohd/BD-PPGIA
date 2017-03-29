package userInterface.gui;

import java.math.BigDecimal;

public interface PropertyMatcher {

	public boolean test(String expected, String tested);
	
	public boolean test(BigDecimal expected, BigDecimal tested);
}
