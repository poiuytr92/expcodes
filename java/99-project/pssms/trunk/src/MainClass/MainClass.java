package MainClass;

import windows.LoginWindow;
import Tool.LinkToDB;

/**
 * 
 * @software 进销存管理系统
 * 
 * @team 邓伟文， 邝泽徽， 廖权斌 ，罗伟聪
 *
 */

public class MainClass {
	
	public static void main(String[] args) {
		if((new LinkToDB().connection()) == true) {
			new LoginWindow("进销存管理系统 - 登陆");
		}
	}
}
