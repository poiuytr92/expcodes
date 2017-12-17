package exp.bilibli.plugin.cache;

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

	// FIXME: 配置文件设置等待时间
	private final static long WAIT_ELEMENT_TIME = 10;
	
	private BrowserDriver browser;
	
	private static volatile BrowserMgr instance;
	
	private BrowserMgr() {
		this.browser = new BrowserDriver(WebDriverType.PHANTOMJS, WAIT_ELEMENT_TIME);
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
	
	public BrowserDriver getBrowser() {
		return browser;
	}

	public void close() {
		browser.close();
	}
	
}
