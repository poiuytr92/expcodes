package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <PRE>
 * XML文件配置器.
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
 *  List<String> enums = System.out.println(conf.getEnumVals("datasource", "WXP");
 *  Map<String, Element> enums = conf.getChildElements("config/bases/base", "ws");
 *  Map<String, Element> enums = conf.getChildElements("datasource@WXP");
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
 *   &lt;/foo&gt;
 * &lt;/root&gt;
 * 标签名称格式: tag  (模糊名称查找，若存在同名标签则取随机值，若非唯一不建议使用)
 * 标签名称格式: tag@here  (模糊路径精确名称查找，若存在同位置标签则取第一个, 若非唯一不建议使用, 此处取值为456)
 * 标签路径格式: root/foo/bar@a/tag  (精确路径查找, 推荐使用，此处取值为123)
 * 	【注：[@]后是标签中id属性的值】
 * 
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-25
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XConfig implements Runnable, IConfig {

	/** 日志器 */
	protected final static Logger log = LoggerFactory.getLogger(_Config.class);
	
	private final static long MIN_REFLASH_TIME = 10000L;
	
	private final static long DEFAULT_REFLASH_TIME = 60000L;
	
	private String configName;
	
	private _Config config;
	
	private boolean isInit;
	
	private boolean isRun;
	
	private boolean isReflash;
	
	private boolean reflashing;
	
	private long reflashTime;
	
	private byte[] tLock;
	
	private byte[] rLock;
	
	/**
	 * 构造函数
	 * @param configName 配置器名称
	 */
	protected XConfig(String configName) {
		this.configName = configName;
		this.config = new _Config(configName);
		
		this.isInit = false;
		this.isRun = false;
		this.isReflash = false;
		this.reflashing = false;
		this.reflashTime = DEFAULT_REFLASH_TIME;
		this.tLock = new byte[1];
		this.rLock = new byte[1];
	}
	
	/**
	 * <PRE>
	 * 刷新配置文件(每60秒刷新一次).
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
					ThreadUtils.tSleep(2000);	// 初次启动, 用时间差保证先让线程陷入第一次无限阻塞状态
				}
			}
		}
		
		if(!isReflash) {
			isReflash = true;
			ThreadUtils.tNotify(tLock);	// 退出无限阻塞， 进入限时阻塞状态
			log.info("配置 [{}] 自动刷新被激活, 刷新间隔为 [{} ms].", configName, reflashTime);
			
		} else {
			log.info("配置 [{}] 刷新间隔变更为 [{} ms], 下个刷新周期生效.", configName, reflashTime);
		}
	}
	
	/**
	 * 暂停刷新
	 */
	public void pause() {
		isReflash = false;
		ThreadUtils.tNotify(tLock);	// 退出限时阻塞， 进入无限阻塞状态
		log.info("配置 [{}] 自动刷新被暂停.", configName);
	}
	
	/**
	 * 销毁配置（删除内存所有配置参数）
	 */
	public void destroy() {
		isReflash = false;
		isRun = false;
		ThreadUtils.tNotify(tLock);	// 退出阻塞态, 通过掉落陷阱终止线程
		
		config.clear();
		log.info("配置 [{}] 内容已销毁.", configName);
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
		log.info("配置 [{}] 开始重载文件...", configName);
		if(config.getConfFiles() == null || config.getConfFiles().isEmpty()) {
			log.info("配置 [{}] 并未加载过任何文件(或文件已被删除), 取消重载操作.", configName);
			return;
		}
		
		reflashing = true;
		_Config conf = new _Config(configName);
		for(Iterator<String[]> fileInfos = config.getConfFiles().iterator(); 
				fileInfos.hasNext();) {
			String[] fileInfo = fileInfos.next();
			String filePath = fileInfo[0];
			String fileType = fileInfo[1];
			
			File file = new File(filePath);
			if(!file.exists()) {
				log.info("配置文件 [{}] 已不存在, 不重载.", filePath);
				fileInfos.remove();
			}
			
			if(DISK_FILE.equals(fileType)) {
				boolean isOk = conf.loadConfFile(filePath);
				log.info("配置 [{}] 重载文件 [{}] {}.", configName, filePath, (isOk ? "成功" : "失败"));
				
			} else if(JAR_FILE.equals(fileType)) {
				boolean isOk = conf.loadConfFileInJar(filePath);
				log.info("配置 [{}] 重载文件 [{}] {}.", configName, filePath, (isOk ? "成功" : "失败"));
				
			} else {
				log.info("配置文件 [{}] 类型异常, 不重载.", filePath);
				fileInfos.remove();
			}
		}
		
		// 替换配置
		synchronized (rLock) {
			config.clear();
			config = conf;
		}
		reflashing = false;
		log.info("配置 [{}] 重载所有文件完成.", configName);
	}
	
	/**
	 * 获取配置加载器名称
	 */
	@Override
	public String getConfigName() {
		return configName;
	}

	/**
	 * <PRE>
	 * 加载多个配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePaths 配置文件路径
	 */
	@Override
	public boolean loadConfFiles(String[] confFilePaths) {
		return config.loadConfFiles(confFilePaths);
	}

	/**
	 * <PRE>
	 * 加载配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePath 配置文件路径
	 */
	@Override
	public boolean loadConfFile(String confFilePath) {
		return config.loadConfFile(confFilePath);
	}

	/**
	 * <PRE>
	 * 加载多个jar内配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePaths 配置文件路径
	 */
	@Override
	public boolean loadConfFilesInJar(String[] confFilePaths) {
		return config.loadConfFilesInJar(confFilePaths);
	}

	/**
	 * <PRE>
	 * 加载jar内配置文件.
	 * 	后加载的配置文件若与前面加载的配置文件存在同位置配置项，则覆盖之.
	 * </PRE>
	 * @param confFilePath 配置文件路径
	 */
	@Override
	public boolean loadConfFileInJar(String confFilePath) {
		return config.loadConfFileInJar(confFilePath);
	}

	/**
	 * 获取Element对象.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若找不到对象则返回null
	 */
	@Override
	public Element getElement(String eNameOrPath) {
		Element e = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				e = config.getElement(eNameOrPath);
			}
		} else {
			e = config.getElement(eNameOrPath);
		}
		return e;
	}

	/**
	 * 获取Element对象.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若找不到对象则返回null
	 */
	@Override
	public Element getElement(String eNameOrPath, String eId) {
		Element e = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				e = config.getElement(eNameOrPath, eId);
			}
		} else {
			e = config.getElement(eNameOrPath, eId);
		}
		return e;
	}

	/**
	 * 获取String标签值(使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无值则返回default属性值, 若default无值则返回"" (绝不返回null)
	 */
	@Override
	public String getVal(String eNameOrPath) {
		String val = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getVal(eNameOrPath);
			}
		} else {
			val = config.getVal(eNameOrPath);
		}
		return val;
	}

	/**
	 * 获取String标签值(使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无值则返回default属性值, 若default无值则返回"" (绝不返回null)
	 */
	@Override
	public String getVal(String eNameOrPath, String eId) {
		String val = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				val = config.getVal(eNameOrPath, eId);
			}
		} else {
			val = config.getVal(eNameOrPath, eId);
		}
		return val;
	}

	/**
	 * 获取int标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回0
	 */
	@Override
	public int getInt(String eNameOrPath) {
		int n = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				n = config.getInt(eNameOrPath);
			}
		} else {
			n = config.getInt(eNameOrPath);
		}
		return n;
	}

	/**
	 * 获取int标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回0
	 */
	@Override
	public int getInt(String eNameOrPath, String eId) {
		int n = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				n = config.getInt(eNameOrPath, eId);
			}
		} else {
			n = config.getInt(eNameOrPath, eId);
		}
		return n;
	}

	/**
	 * 获取long标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回0
	 */
	@Override
	public long getLong(String eNameOrPath) {
		long n = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				n = config.getLong(eNameOrPath);
			}
		} else {
			n = config.getLong(eNameOrPath);
		}
		return n;
	}

	/**
	 * 获取long标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回0
	 */
	@Override
	public long getLong(String eNameOrPath, String eId) {
		long n = 0;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				n = config.getLong(eNameOrPath, eId);
			}
		} else {
			n = config.getLong(eNameOrPath, eId);
		}
		return n;
	}

	/**
	 * 获取bool标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回false
	 */
	@Override
	public boolean getBool(String eNameOrPath) {
		boolean bool = false;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				bool = config.getBool(eNameOrPath);
			}
		} else {
			bool = config.getBool(eNameOrPath);
		}
		return bool;
	}

	/**
	 * 获取bool标签值(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无值则返回default属性值, 若default无值或异常则返回false
	 */
	@Override
	public boolean getBool(String eNameOrPath, String eId) {
		boolean bool = false;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				bool = config.getBool(eNameOrPath, eId);
			}
		} else {
			bool = config.getBool(eNameOrPath, eId);
		}
		return bool;
	}

	/**
	 * 获取枚举标签值列表(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无效则返回无元素的List<String> （绝不返回null）
	 */
	@Override
	public List<String> getEnumVals(String eNameOrPath) {
		List<String> enumVals = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				enumVals = config.getEnumVals(eNameOrPath);
			}
		} else {
			enumVals = config.getEnumVals(eNameOrPath);
		}
		return enumVals;
	}

	/**
	 * 获取枚举标签值列表(原值使用trim处理).
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无效则返回无元素的List<String> （绝不返回null）
	 */
	@Override
	public List<String> getEnumVals(String eNameOrPath, String eId) {
		List<String> enumVals = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				enumVals = config.getEnumVals(eNameOrPath, eId);
			}
		} else {
			enumVals = config.getEnumVals(eNameOrPath, eId);
		}
		return enumVals;
	}

	/**
	 * 获取枚举标签对象列表.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无效则返回无元素的List<Element> （绝不返回null）
	 */
	@Override
	public List<Element> getEnum(String eNameOrPath) {
		List<Element> envm = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				envm = config.getEnum(eNameOrPath);
			}
		} else {
			envm = config.getEnum(eNameOrPath);
		}
		return envm;
	}

	/**
	 * 获取枚举标签对象列表.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无效则返回无元素的List<Element> （绝不返回null）
	 */
	@Override
	public List<Element> getEnum(String eNameOrPath, String eId) {
		List<Element> envm = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				envm = config.getEnum(eNameOrPath, eId);
			}
		} else {
			envm = config.getEnum(eNameOrPath, eId);
		}
		return envm;
	}

	/**
	 * 获取标签子对象.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若标签无效则返回无元素的Map<String, Element> （绝不返回null）
	 */
	@Override
	public Map<String, Element> getChildElements(String eNameOrPath) {
		Map<String, Element> childElements = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				childElements = config.getChildElements(eNameOrPath);
			}
		} else {
			childElements = config.getChildElements(eNameOrPath);
		}
		return childElements;
	}

	/**
	 * 获取标签子对象.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若标签无效则返回无元素的Map<String, Element> （绝不返回null）
	 */
	@Override
	public Map<String, Element> getChildElements(String eNameOrPath, String eId) {
		Map<String, Element> childElements = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				childElements = config.getChildElements(eNameOrPath, eId);
			}
		} else {
			childElements = config.getChildElements(eNameOrPath, eId);
		}
		return childElements;
	}

	/**
	 * 获取标签属性值.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param attributeName 标签的属性名
	 * @return 若无效则返回"" （绝不返回null）
	 */
	@Override
	public String getAttribute(String eNameOrPath, String attributeName) {
		String attribute = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attribute = config.getAttribute(eNameOrPath, attributeName);
			}
		} else {
			attribute = config.getAttribute(eNameOrPath, attributeName);
		}
		return attribute;
	}

	/**
	 * 获取标签属性值.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @param attributeName 标签的属性名
	 * @return 若无效则返回"" （绝不返回null）
	 */
	@Override
	public String getAttribute(String eNameOrPath, String eId, String attributeName) {
		String attribute = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attribute = config.getAttribute(eNameOrPath, eId, attributeName);
			}
		} else {
			attribute = config.getAttribute(eNameOrPath, eId, attributeName);
		}
		return attribute;
	}

	/**
	 * 获取标签属性表.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @return 若无效则返回无元素的Map<String, String> （绝不返回null）
	 */
	@Override
	public Map<String, String> getAttributes(String eNameOrPath) {
		Map<String, String> attributes = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attributes = config.getAttributes(eNameOrPath);
			}
		} else {
			attributes = config.getAttributes(eNameOrPath);
		}
		return attributes;
	}

	/**
	 * 获取标签属性表.
	 * @param eNameOrPath Element对象的标签名称或标签路径
	 * @param eId Element对象的标签名称的id属性值（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若无效则返回无元素的Map<String, String> （绝不返回null）
	 */
	@Override
	public Map<String, String> getAttributes(String eNameOrPath, String eId) {
		Map<String, String> attributes = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				attributes = config.getAttributes(eNameOrPath, eId);
			}
		} else {
			attributes = config.getAttributes(eNameOrPath, eId);
		}
		return attributes;
	}

	/**
	 * 获取固定格式配置对象 - 数据源.
	 * @param dsId 数据源标签的id
	 * @return 若无效则返回默认数据源对象 (绝对不返回null)
	 */
	@Override
	public DataSourceBean getDataSourceBean(String dsId) {
		DataSourceBean ds = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				ds = config.getDataSourceBean(dsId);
			}
		} else {
			ds = config.getDataSourceBean(dsId);
		}
		return ds;
	}

	/**
	 * 获取固定格式配置对象 - 数据源.
	 * @param eNameOrPath 数据源对象的标签名称或标签路径
	 * @param dsId 数据源标签的id（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若无效则返回默认数据源对象 (绝对不返回null)
	 */
	@Override
	public DataSourceBean getDataSourceBean(String eNameOrPath, String dsId) {
		DataSourceBean ds = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				ds = config.getDataSourceBean(eNameOrPath, dsId);
			}
		} else {
			ds = config.getDataSourceBean(eNameOrPath, dsId);
		}
		return ds;
	}

	/**
	 * 获取固定格式配置对象 - socket.
	 * @param sockId socket标签的id（若eNameOrPath已通过[@]配置id属性值 则无需再填）
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
			sb = config.getSocketBean(sockId);
		}
		return sb;
	}

	/**
	 * 获取固定格式配置对象 - socket.
	 * @param eNameOrPath socket对象的标签名称或标签路径
	 * @param sockId socket标签的id（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若无效则返回默认socket对象 (绝对不返回null)
	 */
	@Override
	public SocketBean getSocketBean(String eNameOrPath, String sockId) {
		SocketBean sb = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				sb = config.getSocketBean(eNameOrPath, sockId);
			}
		} else {
			sb = config.getSocketBean(eNameOrPath, sockId);
		}
		return sb;
	}

	/**
	 * 获取固定格式配置对象 - jms.
	 * @param jmsId jms标签的id（若eNameOrPath已通过[@]配置id属性值 则无需再填）
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
			jb = config.getJmsBean(jmsId);
		}
		return jb;
	}

	/**
	 * 获取固定格式配置对象 - jms.
	 * @param eNameOrPath jms对象的标签名称或标签路径
	 * @param jmsId jms标签的id（若eNameOrPath已通过[@]配置id属性值 则无需再填）
	 * @return 若无效则返回默认jms对象 (绝对不返回null)
	 */
	@Override
	public JmsBean getJmsBean(String eNameOrPath, String jmsId) {
		JmsBean jb = null;
		if(isReflash && reflashing) {
			synchronized (rLock) {
				jb = config.getJmsBean(eNameOrPath, jmsId);
			}
		} else {
			jb = config.getJmsBean(eNameOrPath, jmsId);
		}
		return jb;
	}

}
