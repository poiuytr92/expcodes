package exp.bilibili.plugin.envm;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Web驱动类型
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebDriverType {

	private final static String DRIVER_DIR = "./conf/driver/";
	
	// JS属于内存浏览器，对JQuery支持不好
	private final static String PhantomJS = "phantomjs-driver.exe";
	public final static WebDriverType PHANTOMJS = new WebDriverType(PhantomJS);
	
	// chrom属于真实浏览器，对JS、JQuery支持很好，但是无法正常关闭进行，必须通过命令行关�?
	private final static String Chrome = "chrome-driver.exe";
	public final static WebDriverType CHROME = new WebDriverType(Chrome);
	
	private final static String HtmlUnit = "HtmlUnit";
	public final static WebDriverType HTMLUTIL = new WebDriverType(HtmlUnit);
	
	private String driverName;
	
	private String driverPath;
	
	private WebDriverType(String driverName) {
		this.driverName = driverName;
		this.driverPath = StrUtils.concat(DRIVER_DIR, driverName);
	}
	
	public String DRIVER_NAME() {
		return driverName;
	}
	
	public String DRIVER_PATH() {
		return driverPath;
	}
	
}
