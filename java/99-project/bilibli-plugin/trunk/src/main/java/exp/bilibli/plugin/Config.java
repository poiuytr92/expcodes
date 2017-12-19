package exp.bilibli.plugin;

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
	
	public String WS_URL() {
		return xConf.getVal("/config/biliUrls/ws");
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
	
	public int WAIT_ELEMENT_TIME() {
		return xConf.getInt("/config/app/waitElementTime");
	}
	
	public int CLEAR_CACHE_CYCLE() {
		return xConf.getInt("/config/app/clearCacheCycle");
	}
	
	public String ADJS() {
		return xConf.getVal("/config/keywords/adjs");
	}
	
	public String NIGHTS() {
		return xConf.getVal("/config/keywords/nights");
	}
		
}
