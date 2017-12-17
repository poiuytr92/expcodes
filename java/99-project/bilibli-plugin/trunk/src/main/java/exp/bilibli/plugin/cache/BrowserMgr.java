package exp.bilibli.plugin.cache;

import exp.bilibli.plugin.Config;
import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.envm.WebDriverType;

/**
 * <PRE>
 * 浏览器驱动管理器
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BrowserMgr {

	private BrowserDriver browser;
	
	private static volatile BrowserMgr instance;
	
	private BrowserMgr() {
		reCreate(true);
	}
	
	public static BrowserMgr getInstn() {
		if(instance == null) {
			synchronized (BrowserMgr.class) {
				if(instance == null) {
					instance = new BrowserMgr();
				}
			}
		}
		return instance;
	}
	
	public BrowserDriver reCreate(boolean loadImages) {
		quit();
		browser = new BrowserDriver(WebDriverType.PHANTOMJS, loadImages, 
				Config.getInstn().WAIT_ELEMENT_TIME());
		return browser;
	}
	
	public BrowserDriver getBrowser() {
		return browser;
	}

	/**
	 * 关闭当前页面
	 */
	public void close() {
		if(browser != null) {
			browser.close();
		}
	}
	
	/**
	 * 退出浏览器
	 */
	public void quit() {
		if(browser != null) {
			browser.quit();
		}
	}
	
}
