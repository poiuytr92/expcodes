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
import exp.libs.warp.net.socket.bean.SocketBean;

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
	 * 刷新操作会对所加载过的配置文件依次重新加载.
	 */
	public void reflash() {
		reflash(DEFAULT_REFLASH_TIME);
	}
	
	/**
	 * 刷新操作会对所加载过的配置文件依次重新加载.
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
					ThreadUtils.tSleep(1000);	// 初次启动, 用时间差保证先让线程陷入第一次无限阻塞状态
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
	
	public void pause() {
		isReflash = false;
		ThreadUtils.tNotify(tLock);	// 退出限时阻塞， 进入无限阻塞状态
		log.info("配置 [{}] 自动刷新被暂停.", configName);
	}
	
	public void destroy() {
		isReflash = false;
		isRun = false;
		ThreadUtils.tNotify(tLock);	// 退出阻塞态, 通过掉落陷阱终止线程
		log.info("配置 [{}] 自动刷新被终止.", configName);
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
	
	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public boolean loadConfFiles(String[] confFilePaths) {
		return config.loadConfFiles(confFilePaths);
	}

	@Override
	public boolean loadConfFile(String confFilePath) {
		return config.loadConfFile(confFilePath);
	}

	@Override
	public boolean loadConfFilesInJar(String[] confFilePaths) {
		return config.loadConfFilesInJar(confFilePaths);
	}

	@Override
	public boolean loadConfFileInJar(String confFilePath) {
		return config.loadConfFileInJar(confFilePath);
	}

	@Override
	public void clear() {
		config.clear();
	}

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
