package exp.bilibli.plugin;

import exp.libs.envm.Charset;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 用户代理（浏览器头标识）: 假装是谷歌，避免被反爬   （浏览器头可以用Fiddler抓包抓到）*/
	public final static String USER_AGENT = 
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
	
	private final static String CONF_PATH = "./conf/bp_conf.dat";
	
	private static volatile Config instance;
	
	private XConfig xConf;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("biliConf");
		xConf.loadConfFile(CONF_PATH);
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
	
	public String HOME_URL() {
		return xConf.getVal("/config/biliUrls/home");
	}
	
	public String LOGIN_URL() {
		return xConf.getVal("/config/biliUrls/login");
	}
	
	public String LIVE_URL() {
		return xConf.getVal("/config/biliUrls/live");
	}
	
	public String SSL_URL() {
		return xConf.getVal("/config/biliUrls/ssl");
	}
	
	public String WS_URL() {
		return xConf.getVal("/config/biliUrls/ws");
	}
	
	public String CHAT_URL() {
		return xConf.getVal("/config/biliUrls/chat");
	}
	
	public String EG_CHECK_URL() {
		return xConf.getVal("/config/biliUrls/egCheck");
	}
	
	public String EG_JOIN_URL() {
		return xConf.getVal("/config/biliUrls/egJoin");
	}
	
	public String TV_JOIN_URL() {
		return xConf.getVal("/config/biliUrls/tvJoin");
	}
	
	public String COOKIE_DIR() {
		return xConf.getVal("/config/files/cookies");
	}
	
	public String IMG_DIR() {
		return xConf.getVal("/config/files/img");
	}
	
	public String DRIVER_DIR() {
		return xConf.getVal("/config/files/driver");
	}
	
	public String ROOM_PATH() {
		return xConf.getVal("/config/files/room");
	}
	
	public String ADV_PATH() {
		return xConf.getVal("/config/files/advs");
	}
	
	public String NIGHT_PATH() {
		return xConf.getVal("/config/files/nights");
	}
		
	public String CALL_PATH() {
		return xConf.getVal("/config/files/calls");
	}
	
	public int WAIT_ELEMENT_TIME() {
		return xConf.getInt("/config/app/waitElementTime");
	}
	
	public int CLEAR_CACHE_CYCLE() {
		return xConf.getInt("/config/app/clearCacheCycle");
	}
	
}
