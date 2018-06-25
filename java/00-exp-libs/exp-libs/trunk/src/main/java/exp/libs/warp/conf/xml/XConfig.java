package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <PRE>
 * XML文件配置器.
 * 	此组件由于支持配置节点合并/覆写，因此不提供获取Element方法.
 * 	(合并/覆写后的Element已与原节点不是同一个对象).
 * =========================================================
 * 
 * 使用示例:
 * 	XConfig conf = XConfigFactory.createConfig("CONF_NAME");
 * 
 * 	// 加载多份配置文件， 后加载的会覆盖前加载的相同配置项
 * 	conf.loadConfFile("./conf/wsc_app_cfg.dat");
 * 	conf.loadConfFile("./conf/wsc_monitor_cfg.dat");
 * 	conf.loadConfFile("./conf/wsc_conf.xml");
 * 
 * 	// 配置路径用 [ / ] 分隔, 返回值绝对不为null
 * 	// 若含有多个同名配置项， 配置项中包含属性 [id] 则可用 [ @ ] 定位 (也可直接通过传参定位)
 * 	// 若配置项中含有 [ default ] 属性且无配置值，则取 [ default ] 属性值
 * 	boolean val = conf.getBool("config/bases/base@app/useIf");
 * 	String val = conf.getVal("pool");
 * 	int val = conf.getInt("iteratorMode");
 *  String val = conf.getAttribute("base@ftp", "hint");
 *  List&lt;String&gt; enums = conf.getEnums("enumTag");	// 枚举节点需要声明属性type="enum"
 * =========================================================
 * 
 * 格式定义:
 * &lt;root&gt;
 *   &lt;tag&gt;789&lt;/tag&gt;
 *   &lt;foo&gt;
 *     &lt;bar id="a"&gt;
 *       &lt;tag id="here"&gt;123&lt;/tag&gt;
 *     &lt;/bar&gt;
 *     &lt;bar id="b"&gt;
 *       &lt;tag id="here"&gt;456&lt;/tag&gt;
 *     &lt;/bar&gt;
 *     &lt;bar id="c" type="enum"&gt;	&lt;!-- 枚举节点要声明type="enum" --&gt;
 *       &lt;tag&gt;qwe&lt;/tag&gt;
 *       &lt;tag&gt;asd&lt;/tag&gt;
 *       &lt;tag&gt;zxc&lt;/tag&gt;
 *     &lt;/bar&gt;
 *   &lt;/foo&gt;
 * &lt;/root&gt;
 * 标签名称格式: tag  (模糊名称查找，若存在同名标签则取随机值，若非唯一不建议使用)
 * 标签名称格式: tag@here  (模糊路径精确名称查找，若存在同位置标签则取第一个, 若非唯一不建议使用, 此处取值为456)
 * 标签路径格式: root/foo/bar@a/tag  (精确路径查找, 推荐使用，此处取值为123)
 * 	【注：[@]后是标签中id属性的值】
 * 
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XConfig implements Runnable, _IConfig {

	/** 日志�? */
	protected final static Logger log = LoggerFactory.getLogger(_Config.class);
	
	public final static XNode NULL_XNODE = _Config.NULL_XNODE;
	
	private final static long MIN_REFLASH_TIME = 10000L;
	
	private final static long DEFAULT_REFLASH_TIME = 60000L;
	
	private String name;
	
	private _Config config;
	
	private boolean isInit;
	
	private boolean isRun;
	
	private boolean isReflash;
	
	private boolean reflashing;
	
	private long reflashTime;
	
	/** 线程�? */
	private byte[] tLock;
	
	/** 刷新�? */
	private byte[] rLock;
	
	/** 保存最近查找过的配置值（用于快速检索重复配置） */
	private Map<String, Object> nearValues;
	
	/**
	 * 构造函�?
	 * @param name 配置器名�?
	 */
	protected XConfig(String name) {
		this.name = name;
		this.config = new _Config(name);
		
		this.isInit = false;
		this.isRun = false;
		this.isReflash = false;
		this.reflashing = false;
		this.reflashTime = DEFAULT_REFLASH_TIME;
		this.tLock = new byte[1];
		this.rLock = new byte[1];
		
		this.nearValues = new HashMap<String, Object>();
	}
	
	/**
	 * <PRE>
	 * 刷新配置文件(�?60秒刷新一�?).
	 * 	刷新操作会对所加载过的配置文件依次重新加载.
	 * <PRE>
	 */
	public void reflash() {
		reflash(DEFAULT_REFLASH_TIME);
	}
	
	/**
	 * <PRE>
	 * 刷新配置文件.
	 * 	刷新操作会对所加载过的配置文件依次重新加载.
	 * <PRE>
	 * @param timeMillis 刷新间隔
	 */
	public void reflash(long timeMillis) {
		reflashTime = (timeMillis < MIN_REFLASH_TIME ? 
				MIN_REFLASH_TIME : timeMillis);
		
		if(!isInit) {
			synchronized (tLock) {
				if(!isInit) {
					isInit = true;
					isRun = true;
					new Thread(this).start();
					ThreadUtils.tSleep(2000);	// 初次启动, 用时间差保证先让线程陷入第一次无限阻塞状�?
				}
			}
		}
		
		if(!isReflash) {
			isReflash = true;
			ThreadUtils.tNotify(tLock);	// 退出无限阻塞， 进入限时阻塞状�?
			log.info("配置 [{}] 自动刷新被激�?, 刷新间隔�? [{} ms].", name, reflashTime);
			
		} else {
			log.info("配置 [{}] 刷新间隔变更�? [{} ms], 下个刷新周期生效.", name, reflashTime);
		}
	}
	
	/**
	 * 暂停刷新
	 */
	public void pause() {
		isReflash = false;
		ThreadUtils.tNotify(tLock);	// 退出限时阻塞， 进入无限阻塞状�?
		log.info("配置 [{}] 自动刷新被暂�?.", name);
	}
	
	/**
	 * 销毁配置（删除内存所有配置参数）
	 */
	public void destroy() {
		isReflash = false;
		isRun = false;
		ThreadUtils.tNotify(tLock);	// 退出阻塞�?, 通过掉落陷阱终止线程
		
		nearValues.clear();
		config.clear();
		log.info("配置 [{}] 内容已销�?.", name);
	}
	
	@Override
	public void run() {
		while(isRun) {
			ThreadUtils.tWait(tLock, 0);
			if(!isRun) { break; }
			
			while(isReflash) {
				ThreadUtils.tWait(tLock, reflashTime);
				if(!isRun || !isReflash) { break; }
				reload();
			}
		}
	}
	
	/**
	 * 重载配置文件
	 */
	private void reload() {
		log.info("配置 [{}] 开始重载文�?...", name);
		if(config.getConfFiles() == null || config.getConfFiles().isEmpty()) {
			log.info("配置 [{}] 并未加载过任何文�?(或文件已被删�?), 取消重载操作.", name);
			return;
		}
		
		reflashing = true;
		_Config conf = new _Config(name);
		for(Iterator<String[]> fileInfos = config.getConfFiles().iterator(); 
				fileInfos.hasNext();) {
			String[] fileInfo = fileInfos.next();
			String filxPath = fileInfo[0];
			String fileType = fileInfo[1];
			
			File file = new File(filxPath);
			if(!file.exists()) {
				log.info("配置文件 [{}] 已不存在, 不重�?.", filxPath);
				fileInfos.remove();
			}
			
			if(DISK_FILE.equals(fileType)) {
				boolean isOk = (conf.loadConfFile(filxPath) != null);
				log.info("配置 [{}] 重载文件 [{}] {}.", name, filxPath, (isOk ? "成功" : "失败"));
				
			} else if(JAR_FILE.equals(fileType)) {
				boolean isOk = (conf.loadConfFileInJar(filxPath) != null);
				log.info("配置 [{}] 重载文件 [{}] {}.", name, filxPath, (isOk ? "成功" : "失败"));
				
			} else {
				log.info("配置文件 [{}] 类型异常, 不重�?.", filxPath);
				fileInfos.remove();
			}
		}
		
		// 替换配置
		synchronized (rLock) {
			nearValues.clear();
			config.clear();
			config = conf;
		}
		reflashing = false;
		log.info("配置 [{}] 重载所有文件完�?.", name);
	}
	
	/**
	 * 获取配置加载器名�?
	 */
	@Override
	public String NAME() {
		return name;
	}

	/**
	 * <PRE>
	 * 加载多个配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * 
	 * 该方法会自动判断当前是否通过tomcat启动，若是则自动切换到loadConfFilesByTomcat
	 * </PRE>
	 * @param confFilePaths 配置文件路径�?, 形如: ./conf/config.xml
	 * @return true:全部加载成功; false:存在加载失败
	 */
	@Override
	public boolean loadConfFiles(String... confFilePaths) {
		return config.loadConfFiles(confFilePaths);
	}

	/**
	 * <PRE>
	 * 加载配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * 
	 * 该方法会自动判断当前是否通过tomcat启动，若是则自动切换到loadConfFileByTomcat
	 * </PRE>
	 * @param confFilePath 配置文件路径, 形如: ./conf/config.xml
	 * @return 若为null则加载失�?; 否则为配置文件的根节�?
	 */
	@Override
	public Element loadConfFile(String confFilePath) {
		return config.loadConfFile(confFilePath);
	}

	/**
	 * <PRE>
	 * 加载多个jar内配置文�?.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePaths 配置文件路径�?, 形如: /foo/bar/config.xml
	 * @return true:全部加载成功; false:存在加载失败
	 */
	@Override
	public boolean loadConfFilesInJar(String... confFilePaths) {
		return config.loadConfFilesInJar(confFilePaths);
	}

	/**
	 * <PRE>
	 * 加载jar内配置文�?.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePath 配置文件路径, 形如: /foo/bar/config.xml
	 * @return 若为null则加载失�?; 否则为配置文件的根节�?
	 */
	@Override
	public Element loadConfFileInJar(String confFilePath) {
		return config.loadConfFileInJar(confFilePath);
	}
	
	/**
	 * <PRE>
	 * 加载多个配置文件(程序以tomcat启动时使用此方法).
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * 
	 * 该方法会自动判断当前是否通过tomcat启动，若否则自动切换到loadConfFiles
	 * </PRE>
	 * @param confFilePaths 配置文件路径�?, 形如: ./conf/config.xml
	 * 			方法内会自动在配置文件路径前拼接前缀�? %tomcat%/%wepapp%/%project%/classes
	 * 			若拼接前缀后找不到配置文件, 会修正前缀为：%tomcat%/%wepapp%/%project%
	 * @return true:全部加载成功; false:存在加载失败
	 */
	@Override
	public boolean loadConfFilesByTomcat(String... confFilePaths) {
		return config.loadConfFilesByTomcat(confFilePaths);
	}

	/**
	 * <PRE>
	 * 加载配置文件(程序以tomcat启动时使用此方法).
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * 
	 * 该方法会自动判断当前是否通过tomcat启动，若否则自动切换到loadConfFile
	 * </PRE>
	 * @param confFilePath 配置文件路径, 形如: ./conf/config.xml
	 * 			方法内会自动在配置文件路径前拼接前缀�? %tomcat%/%wepapp%/%project%/classes
	 * 			若拼接前缀后找不到配置文件, 会修正前缀为：%tomcat%/%wepapp%/%project%
	 * @return 若为null则加载失�?; 否则为配置文件的根节�?
	 */
	@Override
	public Element loadConfFileByTomcat(String confFilePath) {
		return config.loadConfFileByTomcat(confFilePath);
	}

	/**
	 * 获取节点.
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若节点不存在则返回无效对象节�? NULL_XNODE (绝不返回null)
	 */
	@Override
	public XNode getNode(String xPath) {
		XNode node = NULL_XNODE;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				node = config.getNode(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			node = (obj == null ? config.getNode(xPath) : (XNode) obj);
			nearValues.put(xPath, node);
		}
		return node;
	}

	/**
	 * 获取节点.
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若节点不存在则返回无效对象节�? NULL_XNODE (绝不返回null)
	 */
	@Override
	public XNode getNode(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getNode(xPath);
	}
	
	/**
	 * 获取String标签�?(使用trim处理).
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若标签无值则返回default属性�?, 若default无值则返回"" (绝不返回null)
	 */
	@Override
	public String getVal(String xPath) {
		String val = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getVal(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			val = (obj == null ? config.getVal(xPath) : (String) obj);
			nearValues.put(xPath, val);
		}
		return val;
	}

	/**
	 * 获取String标签�?(使用trim处理).
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若标签无值则返回default属性�?, 若default无值则返回"" (绝不返回null)
	 */
	@Override
	public String getVal(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getVal(xPath);
	}

	/**
	 * 获取int标签�?(原值使用trim处理).
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返�?0
	 */
	@Override
	public int getInt(String xPath) {
		int val = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getInt(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			val = (obj == null ? config.getInt(xPath) : (Integer) obj);
			nearValues.put(xPath, val);
		}
		return val;
	}

	/**
	 * 获取int标签�?(原值使用trim处理).
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返�?0
	 */
	@Override
	public int getInt(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getInt(xPath);
	}

	/**
	 * 获取long标签�?(原值使用trim处理).
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返�?0
	 */
	@Override
	public long getLong(String xPath) {
		long val = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getLong(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			val = (obj == null ? config.getLong(xPath) : (Long) obj);
			nearValues.put(xPath, val);
		}
		return val;
	}

	/**
	 * 获取long标签�?(原值使用trim处理).
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返�?0
	 */
	@Override
	public long getLong(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getLong(xPath);
	}

	/**
	 * 获取bool标签�?(原值使用trim处理).
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返回false
	 */
	@Override
	public boolean getBool(String xPath) {
		boolean val = false;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getBool(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			val = (obj == null ? config.getBool(xPath) : (Boolean) obj);
			nearValues.put(xPath, val);
		}
		return val;
	}

	/**
	 * 获取bool标签�?(原值使用trim处理).
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若标签无值则返回default属性�?, 若default无值或异常则返回false
	 */
	@Override
	public boolean getBool(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getBool(xPath);
	}

	/**
	 * <PRE>
	 * 枚举Element节点下所有子节点的配置�?(使用trim处理).
	 * 	<B>若子节点同名, 则被枚举节点Element要声明属�? type="enum"</B>
	 * 
	 * 子节点同名，父节点需要声明枚举属性：
	 * &lt;tag type="enum"&gt;
	 * 	&lt;foo&gt;xxx&lt;/foo&gt;
	 * 	&lt;foo&gt;yyy&lt;/foo&gt;
	 * 	&lt;foo&gt;zzz&lt;/foo&gt;
	 * &lt;/tag&gt;
	 * 
	 * 子节点不同名，父节点可以不声明枚举属性：
	 * &lt;tag&gt;
	 * 	&lt;foo1&gt;xxx&lt;/foo1&gt;
	 * 	&lt;foo2&gt;yyy&lt;/foo2&gt;
	 * 	&lt;foo3&gt;zzz&lt;/foo3&gt;
	 * &lt;/tag&gt;
	 * 
	 * 子节点同名但声明了不同id，父节点可以不声明枚举属性：
	 * &lt;tag&gt;
	 * 	&lt;foo id="a"&gt;xxx&lt;/foo&gt;
	 * 	&lt;foo id="b"&gt;yyy&lt;/foo&gt;
	 * 	&lt;foo id="c"&gt;zzz&lt;/foo&gt;
	 * &lt;/tag&gt;
	 * 
	 * </PRE>
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若标签无效则返回无元素的List<String> （绝不返回null�?
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getEnums(String xPath) {
		List<String> enums = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				enums = config.getEnums(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			enums = (obj == null ? config.getEnums(xPath) : (List<String>) obj);
			nearValues.put(xPath, new LinkedList<String>(enums));
		}
		return enums;
	}

	/**
	 * <PRE>
	 * 枚举Element节点下所有子节点的配置�?(使用trim处理).
	 * 	<B>若子节点同名, 则被枚举节点Element要声明属�? type="enum"</B>
	 * 
	 * 子节点同名，父节点需要声明枚举属性：
	 * &lt;tag type="enum"&gt;
	 * 	&lt;foo&gt;xxx&lt;/foo&gt;
	 * 	&lt;foo&gt;yyy&lt;/foo&gt;
	 * 	&lt;foo&gt;zzz&lt;/foo&gt;
	 * &lt;/tag&gt;
	 * 
	 * 子节点不同名，父节点可以不声明枚举属性：
	 * &lt;tag&gt;
	 * 	&lt;foo1&gt;xxx&lt;/foo1&gt;
	 * 	&lt;foo2&gt;yyy&lt;/foo2&gt;
	 * 	&lt;foo3&gt;zzz&lt;/foo3&gt;
	 * &lt;/tag&gt;
	 * 
	 * 子节点同名但声明了不同id，父节点可以不声明枚举属性：
	 * &lt;tag&gt;
	 * 	&lt;foo id="a"&gt;xxx&lt;/foo&gt;
	 * 	&lt;foo id="b"&gt;yyy&lt;/foo&gt;
	 * 	&lt;foo id="c"&gt;zzz&lt;/foo&gt;
	 * &lt;/tag&gt;
	 * 
	 * </PRE>
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若标签无效则返回无元素的List<String> （绝不返回null�?
	 */
	@Override
	public List<String> getEnums(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getEnums(xPath);
	}

	/**
	 * 获取标签属性�?.
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @param attributxName 标签的属性名
	 * @return 若无效则返回"" （绝不返回null�?
	 */
	@Override
	public String getAttribute(String xPath, String attributxName) {
		String attribute = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attribute = config.getAttribute(xPath, attributxName);
			}
		} else {
			String axPath = StrUtils.concat(xPath, ".", attributxName);
			Object obj = nearValues.remove(axPath);
			attribute = (obj == null ? config.getAttribute(xPath, attributxName) : (String) obj);
			nearValues.put(axPath, attribute);
		}
		return attribute;
	}

	/**
	 * 获取标签属性�?.
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @param attributxName 标签的属性名
	 * @return 若无效则返回"" （绝不返回null�?
	 */
	@Override
	public String getAttribute(String xName, String xId, String attributxName) {
		String xPath = config.toXPath(xName, xId);
		return getAttribute(xPath, attributxName);
	}

	/**
	 * 获取标签属性表.
	 * @param xPath Element对象的标签路�?, 形如: /foo/bar@id/xx@xId/yy@yId/tag
	 * @return 若无效则返回无元素的Map<String, String> （绝不返回null�?
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getAttributes(String xPath) {
		Map<String, String> attributes = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attributes = config.getAttributes(xPath);
			}
		} else {
			Object obj = nearValues.remove(xPath);
			attributes = (obj == null ? config.getAttributes(xPath) : (Map<String, String>) obj);
			nearValues.put(xPath, new HashMap<String, String>(attributes));
		}
		return attributes;
	}

	/**
	 * 获取标签属性表.
	 * @param xName Element对象的标签名�?
	 * @param xId Element对象的标签名称的id属性�?
	 * @return 若无效则返回无元素的Map<String, String> （绝不返回null�?
	 */
	@Override
	public Map<String, String> getAttributes(String xName, String xId) {
		String xPath = config.toXPath(xName, xId);
		return getAttributes(xPath);
	}

	/**
	 * 获取固定格式配置对象 - 数据�?.
	 * @param dsId 数据源标签的id属性�?
	 * @return 若无效则返回默认数据源对�? (绝对不返回null)
	 */
	@Override
	public DataSourceBean getDataSourceBean(String dsId) {
		DataSourceBean ds = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				ds = config.getDataSourceBean(dsId);
			}
		} else {
			Object obj = nearValues.get(dsId);
			if(obj == null) {
				obj = config.getDataSourceBean(dsId);
				nearValues.put(dsId, obj);
			}
			ds = ((DataSourceBean) obj).clone();
		}
		return ds;
	}

	/**
	 * 获取固定格式配置对象 - socket.
	 * @param sockId socket标签的id属性�?
	 * @return 若无效则返回默认socket对象 (绝对不返回null)
	 */
	@Override
	public SocketBean getSocketBean(String sockId) {
		SocketBean sb = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				sb = config.getSocketBean(sockId);
			}
		} else {
			Object obj = nearValues.get(sockId);
			if(obj == null) {
				obj = config.getSocketBean(sockId);
				nearValues.put(sockId, obj);
			}
			sb = ((SocketBean) obj).clone();
		}
		return sb;
	}

	/**
	 * 获取固定格式配置对象 - jms.
	 * @param jmsId jms标签的id属性�?
	 * @return 若无效则返回默认jms对象 (绝对不返回null)
	 */
	@Override
	public JmsBean getJmsBean(String jmsId) {
		JmsBean jb = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				jb = config.getJmsBean(jmsId);
			}
		} else {
			Object obj = nearValues.get(jmsId);
			if(obj == null) {
				obj = config.getJmsBean(jmsId);
				nearValues.put(jmsId, obj);
			}
			jb = ((JmsBean) obj).clone();
		}
		return jb;
	}

}
