package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.utils.format.XmlUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.io.JarUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.BoolUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.sock.bean.SocketBean;

class _Config implements _IConfig {

	/** 日志器 */
	protected final static Logger log = LoggerFactory.getLogger(_Config.class);
	
	private final static _XNode NULL_XNODE = new _XNode(null, null);
	
	/** 配置对象名称 */
	protected String name;
	
	/**
	 * 依序记录所加载过的配置文件.
	 *  其中 单个元素为  String[2] { filxPath, 文件类型:DISK|JAR }
	 */
	protected List<String[]> confFiles; 
	
	private _XTree xTree;
	
	/**
	 * 构造函数
	 * @param configName 配置器名称
	 */
	protected _Config(String name) {
		this.name = name;
		this.confFiles = new LinkedList<String[]>();
		this.xTree = new _XTree();
	}
	
	@Override
	public String NAME() {
		return name;
	}
	
	protected void clear() {
		confFiles.clear();
		xTree.clear();
	}
	
	@Override
	public boolean loadConfFiles(String[] confFilxPaths) {
		if(confFilxPaths == null) {
			return false;
		}
		
		boolean isOk = true;
		for(String confFilxPath : confFilxPaths) {
			if(StrUtils.isNotEmpty(confFilxPath)) {
				isOk &= (loadConfFile(confFilxPath) != null);
			}
		}
		return isOk;
	}

	@Override
	public Element loadConfFile(String confFilxPath) {
		if(confFilxPath == null) {
			return null;
		}
		
		Element root = null;
		try {
			File confFile = new File(confFilxPath);
			String charset = XmlUtils.getCharset(confFile);
			String xml = FileUtils.readFileToString(confFile, charset);
			Document doc = DocumentHelper.parseText(xml);
			root = doc.getRootElement();
			xTree.update(root);
			confFiles.add(new String[] { confFilxPath, DISK_FILE });
			
		} catch (Exception e) {
			log.error("加载文件失败: [{}].", confFilxPath, e);
		}
		return root;
	}
	
	@Override
	public boolean loadConfFilesInJar(String[] confFilxPaths) {
		if(confFilxPaths == null) {
			return false;
		}
		
		boolean isOk = true;
		for(String confFilxPath : confFilxPaths) {
			if(StrUtils.isNotEmpty(confFilxPath)) {
				isOk &= (loadConfFileInJar(confFilxPath) != null);
			}
		}
		return isOk;
	}
	
	@Override
	public Element loadConfFileInJar(String confFilxPath) {
		if(confFilxPath == null) {
			return null;
		}
		
		Element root = null;
		try {
			String content = JarUtils.read(confFilxPath, Charset.ISO);
			String charset = XmlUtils.getCharset(content);
			String xml = JarUtils.read(confFilxPath, charset);
			Document doc = DocumentHelper.parseText(xml);
			root = doc.getRootElement();
			xTree.update(root);
			confFiles.add(new String[] { confFilxPath, JAR_FILE });
			
		} catch (Exception e) {
			log.error("加载文件失败: [{}].", confFilxPath, e);
		}
		return root;
	}
	
	protected List<String[]> getConfFiles() {
		return confFiles;
	}
	
	protected String toXPath(String eName, String xId) {
		return xTree.toXPath(eName, xId);
	}
	
	private _XNode findXNode(String xPath) {
		_XNode node = xTree.findXNode(xPath);
		return (node == null ? NULL_XNODE : node);
	}
	
	@Override
	public String getVal(String xPath) {
		_XNode xNode = findXNode(xPath);
		return xNode.VAL();
	}
	
	@Override
	public String getVal(String eName, String xId) {
		return getVal(toXPath(eName, xId));
	}
	
	@Override
	public int getInt(String xPath) {
		return NumUtils.toInt(getVal(xPath), 0);
	}
	
	@Override
	public int getInt(String eName, String xId) {
		return getInt(toXPath(eName, xId));
	}
	
	@Override
	public long getLong(String xPath) {
		return NumUtils.toLong(getVal(xPath), 0L);
	}
	
	@Override
	public long getLong(String eName, String xId) {
		return getLong(toXPath(eName, xId));
	}
	
	@Override
	public boolean getBool(String xPath) {
		return BoolUtils.toBool(getVal(xPath), false);
	}
	
	@Override
	public boolean getBool(String eName, String xId) {
		return getBool(toXPath(eName, xId));
	}
	
	@Override
	public List<String> getEnums(String xPath) {
		List<String> enums = new LinkedList<String>();
		_XNode xNode = findXNode(xPath);
		Iterator<_XNode> childs = xNode.getChilds();
		while(childs.hasNext()) {
			_XNode child = childs.next();
			enums.add(child.VAL());
		}
		return enums;
	}
	
	@Override
	public List<String> getEnums(String eName, String xId) {
		return getEnums(toXPath(eName, xId));
	}
	
	@Override
	public String getAttribute(String xPath, String attributeName) {
		_XNode xNode = findXNode(xPath);
		return xNode.getAttribute(attributeName);
	}
	
	@Override
	public String getAttribute(String eName, String xId, String attributeName) {
		return getAttribute(toXPath(eName, xId), attributeName);
	}
	
	@Override
	public Map<String, String> getAttributes(String xPath) {
		_XNode xNode = findXNode(xPath);
		return xNode.getAttributes();
	}
	
	@Override
	public Map<String, String> getAttributes(String eName, String xId) {
		return getAttributes(toXPath(eName, xId));
	}
	
	@Override
	public DataSourceBean getDataSourceBean(String dsId) {
		DataSourceBean ds = new DataSourceBean();
		if(StrUtils.isEmpty(dsId)) {
			return ds;
		}
		
		dsId = dsId.trim();
		String xPath = toXPath("datasource", dsId);
		_XNode xNode = findXNode(xPath);
		if(xNode != NULL_XNODE) {
			ds.setId(dsId);
			ds.setDriver(xNode.getChildVal("driver"));
			ds.setIp(xNode.getChildVal("ip"));
			ds.setPort(NumUtils.toInt(xNode.getChildVal("port")));
			ds.setUsername(xNode.getChildVal("username"));
			ds.setPassword(xNode.getChildVal("password"));
			ds.setName(xNode.getChildVal("name"));
			ds.setCharset(xNode.getChildVal("charset"));
			ds.setMaximumActiveTime(NumUtils.toLong(xNode.getChildVal("maximum-active-time"), -1));
			ds.setHouseKeepingTestSql(xNode.getChildVal("house-keeping-test-sql"));
			ds.setHouseKeepingSleepTime(NumUtils.toLong(xNode.getChildVal("house-keeping-sleep-time"), -1));
			ds.setSimultaneousBuildThrottle(NumUtils.toInt(xNode.getChildVal("simultaneous-build-throttle"), -1));
			ds.setMaximumConnectionCount(NumUtils.toInt(xNode.getChildVal("maximum-connection-count"), -1));
			ds.setMinimumConnectionCount(NumUtils.toInt(xNode.getChildVal("minimum-connection-count"), -1));
			ds.setMaximumNewConnections(NumUtils.toInt(xNode.getChildVal("maximum-new-connections"), -1));
			ds.setPrototypeCount(NumUtils.toInt(xNode.getChildVal("prototype-count"), -1));
			ds.setMaximumConnectionLifetime(NumUtils.toLong(xNode.getChildVal("maximum-connection-lifetime"), -1));
			ds.setTestBeforeUse(BoolUtils.toBool(xNode.getChildVal("test-before-use"), false));
			ds.setTestAfterUse(BoolUtils.toBool(xNode.getChildVal("test-after-use"), false));
			ds.setTrace(BoolUtils.toBool(xNode.getChildVal("trace"), true));
		}
		return ds;
	}
	
	@Override
	public SocketBean getSocketBean(String sockId) {
		SocketBean sb = new SocketBean();
		if(StrUtils.isEmpty(sockId)) {
			return sb;
		}
		
		sockId = sockId.trim();
		String xPath = toXPath("socket", sockId);
		_XNode xNode = findXNode(xPath);
		if(xNode != NULL_XNODE) {
			sb.setId(sockId);
			sb.setIp(xNode.getChildVal("ip"));
			sb.setPort(NumUtils.toInt(xNode.getChildVal("port")));
			sb.setUsername(xNode.getChildVal("username"));
			sb.setPassword(xNode.getChildVal("password"));
			sb.setCharset(xNode.getChildVal("charset"));
			sb.setReadCharset(xNode.getChildVal("readCharset"));
			sb.setWriteCharset(xNode.getChildVal("writeCharset"));
			sb.setBufferSize(NumUtils.toInt(xNode.getChildVal("bufferSize"), -1));
			sb.setReadBufferSize(NumUtils.toInt(xNode.getChildVal("readBufferSize"), -1));
			sb.setWriteBufferSize(NumUtils.toInt(xNode.getChildVal("writeBufferSize"), -1));
			sb.setDelimiter(xNode.getChildVal("delimiter"));
			sb.setReadDelimiter(xNode.getChildVal("readDelimiter"));
			sb.setWriteDelimiter(xNode.getChildVal("writeDelimiter"));
			sb.setOvertime(NumUtils.toInt(xNode.getChildVal("overtime"), -1));
			sb.setMaxConnectionCount(NumUtils.toInt(xNode.getChildVal("maxConnectionCount"), -1));
			sb.setExitCmd(xNode.getChildVal("exitCmd"));
		}
		return sb;
	}
	
	@Override
	public JmsBean getJmsBean(String jmsId) {
		// TODO
		return new JmsBean();
	}
	
}
