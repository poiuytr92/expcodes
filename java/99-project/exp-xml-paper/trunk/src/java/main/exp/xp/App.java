package exp.xp;

import exp.xp.ui.XmlPaper;
import exp.xp.utils.BeautyEye;

/**
 * <PRE>
 * 程序实例
 * </PRE>
 * <B>PROJECT：</B> exp-xml-paper
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-06-01
 * @author    EXP: www.exp-blog.com
 * @since     jdk版本：jdk1.6
 */
public class App {

	private final String appName = "exp-xml-paper";
	
	private static volatile App instance;
	
	private App(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				clean();
			}
		});
		
		init();
		run();
	}
	
	public static void createInstn(String[] args) {
		if(instance == null) {
			synchronized (App.class) {
				if(instance == null) {
					instance = new App(args);
				}
			}
		}
	}
	
	private void init() {
		BeautyEye.init();
	}
	
	private void run() {
		XmlPaper.createInstn(appName);
	}
	
	private void clean() {
		
	}
	
}
