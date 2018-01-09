package exp.bilibili.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exp.libs.envm.Charset;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	/** 用户代理（浏览器头标识）: 假装是谷歌，避免被反爬   （浏览器头可以用Fiddler抓包抓到）*/
	public final static String USER_AGENT = 
//			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
	
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
	
	public String LOGIN_HOST() {
		return xConf.getVal("/config/biliUrls/loginHost");
	}
	
	public String LOGIN_URL() {
		return xConf.getVal("/config/biliUrls/login");
	}
	
	public String MINI_LOGIN_URL() {
		return xConf.getVal("/config/biliUrls/miniLogin");
	}
	
	public String VCCODE_URL() {
		return xConf.getVal("/config/biliUrls/vccode");
	}
	
	public String RSA_URL() {
		return xConf.getVal("/config/biliUrls/rsa");
	}
	
	public String LIVE_URL() {
		return xConf.getVal("/config/biliUrls/live");
	}
	
	public String SSL_HOST() {
		return xConf.getVal("/config/biliUrls/ssl");
	}
	
	public String WS_URL() {
		return xConf.getVal("/config/biliUrls/ws");
	}
	
	public String CHAT_URL() {
		return xConf.getVal("/config/biliUrls/chat");
	}
	
	public String SIGN_URL() {
		return xConf.getVal("/config/biliUrls/sign");
	}
	
	public String ACCOUNT_URL() {
		return xConf.getVal("/config/biliUrls/account");
	}
	
	public String STORM_CHECK_URL() {
		return xConf.getVal("/config/biliUrls/stormCheck");
	}
	
	public String STORM_JOIN_URL() {
		return xConf.getVal("/config/biliUrls/stormJoin");
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
	
	public String CHECK_TASK_URL() {
		return xConf.getVal("/config/biliUrls/checkTask");
	}
	
	public String VERCODE_URL() {
		return xConf.getVal("/config/biliUrls/vercode");
	}
	
	public String DO_TASK_URL() {
		return xConf.getVal("/config/biliUrls/doTask");
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
	
	public String ORC_DIR() {
		return xConf.getVal("/config/files/orc");
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
	
	public String CARD_PATH() {
		return xConf.getVal("/config/files/cards");
	}
	
	public String NOTICE_PATH() {
		return xConf.getVal("/config/files/notices");
	}
	
	public String SIGN_ROOM_ID() {
		return xConf.getVal("/config/app/signRoomId");
	}
	
	public int WAIT_ELEMENT_TIME() {
		return xConf.getInt("/config/app/waitElementTime");
	}
	
	public int CLEAR_CACHE_CYCLE() {
		return xConf.getInt("/config/app/clearCacheCycle");
	}
	
	public long COOKIE_TIME() {
		return xConf.getLong("/config/app/cookieTime") * 1000;
	}
	
	/**
	 * 设置默认房间号（每日签到用）
	 * (房间勋章等级越高签到奖励越多)
	 */
	public boolean setSignRoomId(String roomId) {
		boolean isOk = false;
		final String REGEX = "(<signRoomId[^>]+>)[^<]*(</signRoomId>)";
		if(StrUtils.isNotEmpty(roomId)) {
			String xml = FileUtils.read(CONF_PATH, DEFAULT_CHARSET);
			Pattern ptn = Pattern.compile(REGEX);
			Matcher mth = ptn.matcher(xml);
			if(mth.find()) {
				String head = mth.group(1);
				String tail = mth.group(2);
				String txt = StrUtils.concat(head, roomId, tail);
				xml = xml.replace(mth.group(0), txt);
				
				isOk = FileUtils.write(CONF_PATH, xml, DEFAULT_CHARSET, false);
			}
		}
		return isOk;
	}
	
}
