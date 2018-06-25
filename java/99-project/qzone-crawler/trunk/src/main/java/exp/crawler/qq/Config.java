package exp.crawler.qq;

import exp.libs.envm.Charset;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;
import exp.libs.warp.ver.VersionMgr;

/**
 * <PRE>
 * 配置类
 * </PRE>
 * <B>PROJECT : </B> qzone-crawler
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {

	/** 默认编码 */
	public final static String CHARSET = Charset.UTF8;
	
	/** 应用名称 */
	public final static String APP_NAME = RegexUtils.findFirst(VersionMgr.getAppName(), "([a-zA-Z\\-]+)");
	
	/** 应用配置文件 */
	private final static String APP_PATH = "/exp/crawler/qq/qc_conf.xml";
	
	/** 下载数据保存目录 */
	public final static String DATA_DIR = "./data/";
	
	/** 登陆信息保存路径 */
	public final static String LOGIN_INFO_PATH = "./conf/account.dat";
	
	/** 验证码图片保存路�? */
	public final static String VCODE_IMG_PATH = "./conf/vcode.jpg";
	
	/** 行为休眠间隔(ms) */
	public final static long SLEEP_TIME = 100;
	
	/** 请求超时(ms) */
	public final static int TIMEOUT = 10000;
	
	/**
	 * 每次批量请求的数量限�?
	 * 	(说说最�?20, 相册�?30, 此处取最小�?)
	 */
	public final static int BATCH_LIMT = 20;
	
	/** 重试次数 */
	public final static int RETRY = 5;
	
	/** 配置对象 */
	private XConfig xConf;
	
	/** 单例 */
	private static volatile Config instance;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("QC_CONF");
		xConf.loadConfFileInJar(APP_PATH);
	}
	
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
	public String TEST_SERVER() {
		return xConf.getVal("/config/monitor/testServer");
	}
	
	public String OFFICIAL_SERVER() {
		return xConf.getVal("/config/monitor/officialServer");
	}
	
}
