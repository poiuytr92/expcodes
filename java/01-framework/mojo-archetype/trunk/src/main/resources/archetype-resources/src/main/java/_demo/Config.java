#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}._demo;

import java.util.List;

import org.dom4j.Element;

import exp.libs.envm.Charset;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <PRE>
 * XML配置加载器 [exp.libs.warp.conf.xml.XConfig.class] 使用参考.
 * </PRE>
 * <B>项    目：</B> xxxxxxx
 * <B>技术支持：</B> xxxxxxx
 * @version   xxxxxxx
 * @author    xxxxxxx
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	/** 程序配置文件（全量配置） */
	private final static String APP_CONF = "./conf/[prj-name]_app_cfg.dat";
	
	/** 工程配置文件（常用配置） */
	private final static String USE_CONF = "./conf/[prj-name]_conf.xml";
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	private final static String SOCK_NAME = "TEST-SOCK";
	
	private final static String DS_NAME = "TEST-DS";
	
	/** XML配置加载器 */
	private XConfig xConf;
	
	private static volatile Config instance;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("MSTP");
		xConf.loadConfFile(APP_CONF);	// 先加载全量配置文件
		xConf.loadConfFile(USE_CONF);	// 使用工程配置文件覆盖全量配置中同位置配置项
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
	
	/**
	 * 定时刷新配置文件
	 * @param timeMillis 刷新间隔(ms)
	 */
	public void reflash(long timeMillis) {
		xConf.reflash(timeMillis);
	}
	
	/**
	 * 清理配置缓存.
	 * 	一般在程序终止前调用
	 */
	public void clear() {
		xConf.destroy();
	}
	
	/**
	 * 根据配置文件的socket结构体，获取socket配置对象
	 * @return
	 */
	public SocketBean getSocket() {
		return xConf.getSocketBean(SOCK_NAME);
	}
	
	/**
	 * 根据配置文件的datasource结构体，获取datasource配置对象
	 * @return
	 */
	public DataSourceBean getDataSource() {
		return xConf.getDataSourceBean(DS_NAME);
	}
	
	/**
	 * 取值样例
	 */
	@SuppressWarnings("unused")
	public void loadExample() {
		// 取对象
		DataSourceBean ds = xConf.getDataSourceBean(DS_NAME);
		SocketBean sb = xConf.getSocketBean(SOCK_NAME);
		
		// 取枚举
		List<Element> sysPorts = xConf.getEnum("system");
		List<String> systemPorts = xConf.getEnumVals("system");
		List<String> customPorts = xConf.getEnumVals("config/tabuPorts/coustom");
		
		// 取字符串
		String anyName = xConf.getVal("name");	// 重名标签返回任意值
		String mysqlName = xConf.getVal("config/datasources/datasource@TEST-DS/name");
		String oracleName = xConf.getVal("config/datasources/datasource@TMP-DS/name");
		
		// 取数值
		int httpPort = xConf.getInt("port@http");
		httpPort = xConf.getInt("port", "http");
		int anyPort = xConf.getInt("config/tabuPorts/coustom/port");	// 重名标签返回任意值
		
		// 取属性值
		String caption = xConf.getAttribute("config/sockets/socket/charset", "caption");
		caption = xConf.getAttribute("config/sockets/socket@TEST-SOCK/charset", "caption");
	}
	
}
