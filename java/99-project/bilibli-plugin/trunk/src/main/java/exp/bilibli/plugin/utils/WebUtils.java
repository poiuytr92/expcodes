package exp.bilibli.plugin.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebUtils {

	protected WebUtils() {} 
	
	public static boolean exist(WebElement element, By by) {
		boolean exist = true;
		try {
			element.findElement(by);
		} catch(Exception e) {
			exist = false;
		}
		return exist;
	}
	
}
