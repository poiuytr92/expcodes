package exp.libs.warp.conf.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.utils.pub.BoolUtils;
import exp.libs.utils.pub.FileUtils;
import exp.libs.utils.pub.NumUtils;
import exp.libs.utils.pub.StrUtils;
import exp.libs.utils.pub.XmlUtils;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.socket.bean.SocketBean;

class _Config {

	/** 日志器 */
	protected final static Logger log = LoggerFactory.getLogger(_Config.class);
	
	private final static XNode NULL_XNODE = new XNode(null);
	
	private final String REGEX = StrUtils.concat(
			XNode.ID_SPLIT, "[^", XNode.PATH_SPLIT, "]+$");
	
	/** 磁盘上的配置文件 */
	protected final String DISK_FILE = "0";
	
	/** jar包内的配置文件 */
	protected final String JAR_FILE = "1";
	
	/**
	 * 依序记录所加载过的配置文件.
	 *  其中 单个元素为  String[2] { filePath, isJarFile }
	 */
	protected List<String[]> confFiles; 
	
	/** 配置对象名称 */
	protected String configName;
	
	/** 名字索引, 用于加速检索 */
	private Map<String, String> namePath;
	
	/** 路径索引 */
	private List<String> pathIndex;	
	
	/** 路径树 */
	private Map<String, XNode> pathTree;
	
	protected _Config(String configName) {
		this.configName = configName;
		this.namePath = new HashMap<String, String>();
		this.pathIndex = new LinkedList<String>();
		this.pathTree = new HashMap<String, XNode>();
		this.confFiles = new LinkedList<String[]>();
	}
	
	public boolean loadConfFiles(String[] confFilePaths) {
		if(confFilePaths == null) {
			return false;
		}
		
		boolean isOk = true;
		for(String confFilePath : confFilePaths) {
			if(StrUtils.isNotEmpty(confFilePath)) {
				isOk &= loadConfFile(confFilePath);
			}
		}
		return isOk;
	}
	
	public boolean loadConfFile(String confFilePath) {
		boolean isOk = false;
		if(confFilePath == null) {
			return isOk;
		}
		
		try {
			File confFile = new File(confFilePath);
			String charset = XmlUtils.getCharset(confFile);
			String xml = FileUtils.readFileToString(confFile, charset);
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			createPathTree(root);
			
			confFiles.add(new String[] { confFilePath, DISK_FILE });
			isOk = true;
			
		} catch (Exception e) {
			log.error("加载文件失败: [{}].", confFilePath, e);
		}
		return isOk;
	}
	
	public boolean loadConfFilesInJar(String[] confFilePaths) {
		if(confFilePaths == null) {
			return false;
		}
		
		boolean isOk = true;
		for(String confFilePath : confFilePaths) {
			if(StrUtils.isNotEmpty(confFilePath)) {
				isOk &= loadConfFileInJar(confFilePath);
			}
		}
		return isOk;
	}
	
	public boolean loadConfFileInJar(String confFilePath) {
		boolean isOk = false;
		if(confFilePath == null) {
			return isOk;
		}
		
		try {
			String content = FileUtils.readFileInJar(confFilePath, Charset.ISO);
			String charset = XmlUtils.getCharset(content);
			String xml = FileUtils.readFileInJar(confFilePath, charset);
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			createPathTree(root);
			
			confFiles.add(new String[] { confFilePath, JAR_FILE });
			isOk = true;
			
		} catch (Exception e) {
			log.error("加载文件失败: [{}].", confFilePath, e);
		}
		return isOk;
	}
	
	@SuppressWarnings("unchecked")
	private void createPathTree(Element root) {
		List<Element> bfsQueue = new ArrayList<Element>();
		bfsQueue.add(root);
		int head = 0;
		int tail = 0;
		
		while(head <= tail) {
			Element element = bfsQueue.get(head);
			XNode xNode = new XNode(element);
			
			// 更新路径树
			if(pathTree.remove(xNode.getEPath()) == null) {
				pathIndex.add(xNode.getEPath());
			}
			pathTree.put(xNode.getEPath(), xNode);
			
			// 更新名字索引，用于加速检索
			namePath.put(xNode.getEName(), xNode.getEPath());
			if(StrUtils.isEmpty(xNode.getId()) == false) {
				String eNameId = StrUtils.concat(
						xNode.getEName(), XNode.ID_SPLIT, xNode.getId());
				namePath.put(eNameId, xNode.getEPath());
			}
			
			// 更新BFS队列
			Iterator<Element> childs = element.elementIterator();
			while(childs.hasNext()) {
				bfsQueue.add(childs.next());
				tail++;
			}
			
			head++;
		}
	}
	
	private XNode getXNode(String eNameOrPath, String eId) {
		XNode xNode = null;
		
		if(eNameOrPath != null) {
			String ePath = eNameOrPath;
			if(!eNameOrPath.contains(XNode.PATH_SPLIT)) {
				ePath = getEPath(eNameOrPath, eId);
				
			} else {
				ePath = StrUtils.isEmpty(eId) ? ePath : 
					StrUtils.concat(ePath, XNode.ID_SPLIT, eId);
			}
			xNode = pathTree.get(ePath);
		}
		return (xNode == null ? NULL_XNODE : xNode);
	}
	
	/**
	 * 根据 eName 和 eId 检索对应的 ePath
	 * 
	 * @param eName
	 * @param eId
	 * @return
	 */
	private String getEPath(final String eName, final String eId) {
		final String eNameId = StrUtils.isEmpty(eId) ? eName : 
			StrUtils.concat(eName, XNode.ID_SPLIT, eId);
		String ePath = namePath.get(eNameId);	// 避免重复检索
		
		// 一般不会触发此逻辑，除非是无效的 eName
		if(StrUtils.isEmpty(ePath) == true) {
			Iterator<String> paths = pathIndex.iterator();
			while(paths.hasNext()) {
				String path = paths.next();
				
				if(path.endsWith(eNameId)) {
					ePath = path;
					break;
					
				} else if(StrUtils.isEmpty(eId)) {
					path = path.replaceFirst(REGEX, "");
					if(path.endsWith(eNameId)) {
						ePath = path;
						break;
					}
				}
			}
			
			if(StrUtils.isEmpty(ePath) == false) {
				namePath.put(eNameId, ePath);
			}
		}
		return ePath;
	}
	
	public Element getElement(String eNameOrPath) {
		return getElement(eNameOrPath, null);
	}
	
	public Element getElement(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		return xNode.getElement();
	}
	
	public String getVal(String eNameOrPath) {
		return getVal(eNameOrPath, null);
	}
	
	public String getVal(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		return xNode.getVal();
	}
	
	public int getInt(String eNameOrPath) {
		return getInt(eNameOrPath, null);
	}
	
	public int getInt(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		int val = 0;
		try {
			val = Integer.parseInt(xNode.getVal());
		} catch (Exception e) {
			try {
				val = Integer.parseInt(xNode.getDefavlt());
			} catch (Exception ex) {}
		}
		return val;
	}
	
	public long getLong(String eNameOrPath) {
		return getLong(eNameOrPath, null);
	}
	
	public long getLong(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		long val = 0L;
		try {
			val = Long.parseLong(xNode.getVal());
		} catch (Exception e) {
			try {
				val = Long.parseLong(xNode.getDefavlt());
			} catch (Exception ex) {}
		}
		return val;
	}
	
	public boolean getBool(String eNameOrPath) {
		return getBool(eNameOrPath, null);
	}
	
	public boolean getBool(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		boolean bool = false;
		try {
			bool = Boolean.parseBoolean(xNode.getVal());
		} catch (Exception e) {
			try {
				bool = Boolean.parseBoolean(xNode.getDefavlt());
			} catch (Exception ex) {}
		}
		return bool;
	}
	
	public List<String> getEnumVals(String eNameOrPath) {
		return getEnumVals(eNameOrPath, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getEnumVals(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		Element element = xNode.getElement();
		
		List<String> enums = new LinkedList<String>();
		if(element != null) {
			Iterator<Element> its = element.elementIterator();
			while(its.hasNext()) {
				Element child = its.next();
				enums.add(child.getTextTrim());
			}
		}
		return enums;
	}
	
	public List<Element> getEnum(String eNameOrPath) {
		return getEnum(eNameOrPath, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Element> getEnum(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		Element element = xNode.getElement();
		
		List<Element> enums = new LinkedList<Element>();
		if(element != null) {
			Iterator<Element> its = element.elementIterator();
			while(its.hasNext()) {
				Element child = its.next();
				enums.add(child);
			}
		}
		return enums;
	}
	
	public Map<String, Element> getChildElements(String eNameOrPath) {
		return getChildElements(eNameOrPath, null);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Element> getChildElements(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		Element element = xNode.getElement();
		
		Map<String, Element> childElements = new HashMap<String, Element>();
		if(element != null) {
			Iterator<Element> childs = element.elementIterator();
			while(childs.hasNext()) {
				Element child = childs.next();
				childElements.put(child.getName(), child);
			}
		}
		return childElements;
	}
	
	public String getAttribute(String eNameOrPath, String attributeName) {
		return getAttribute(eNameOrPath, null, attributeName);
	}
	
	public String getAttribute(String eNameOrPath, String eId, String attributeName) {
		Element element = getElement(eNameOrPath, eId);
		String val = null;
		if(element != null) {
			val = element.attributeValue(attributeName);
		}
		return (val == null ? "" : val.trim());
	}
	
	public Map<String, String> getAttributes(String eNameOrPath) {
		return getAttributes(eNameOrPath, null);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getAttributes(String eNameOrPath, String eId) {
		XNode xNode = getXNode(eNameOrPath, eId);
		Element element = xNode.getElement();
		
		Map<String, String> attributes = new HashMap<String, String>();
		if(element != null) {
			Iterator<Attribute> its = element.attributeIterator();
			while(its.hasNext()) {
				Attribute attribute = its.next();
				String name = attribute.getName();
				String value = attribute.getText();
				attributes.put(name, value.trim());
			}
		}
		return attributes;
	}
	
	public DataSourceBean getDataSourceBean(String dsId) {
		return getDataSourceBean("datasource", dsId);
	}
	
	public DataSourceBean getDataSourceBean(String eNameOrPath, String dsId) {
		DataSourceBean ds = new DataSourceBean();
		if(dsId != null) {
			dsId = dsId.trim();
			Element datasource = getElement(eNameOrPath, dsId);
			if(datasource != null) {
				ds.setId(dsId);
				ds.setDriver(XmlUtils.getChildValue(datasource, "driver"));
				ds.setIp(XmlUtils.getChildValue(datasource, "ip"));
				ds.setPort(NumUtils.toInt(XmlUtils.getChildValue(datasource, "port")));
				ds.setUsername(XmlUtils.getChildValue(datasource, "username"));
				ds.setPassword(XmlUtils.getChildValue(datasource, "password"));
				ds.setName(XmlUtils.getChildValue(datasource, "name"));
				ds.setCharset(XmlUtils.getChildValue(datasource, "charset"));
				ds.setHouseKeepingTestSql(XmlUtils.getChildValue(datasource, "house-keeping-test-sql"));
				ds.setHouseKeepingSleepTime(NumUtils.toLong(XmlUtils.getChildValue(datasource, "house-keeping-sleep-time")));
				ds.setSimultaneousBuildThrottle(NumUtils.toInt(XmlUtils.getChildValue(datasource, "simultaneous-build-throttle")));
				ds.setMaximumConnectionCount(NumUtils.toInt(XmlUtils.getChildValue(datasource, "maximum-connection-count")));
				ds.setMinimumConnectionCount(NumUtils.toInt(XmlUtils.getChildValue(datasource, "minimum-connection-count")));
				ds.setMaximumNewConnections(NumUtils.toInt(XmlUtils.getChildValue(datasource, "maximum-new-connections")));
				ds.setPrototypeCount(NumUtils.toInt(XmlUtils.getChildValue(datasource, "prototype-count")));
				ds.setMaximumConnectionLifetime(NumUtils.toLong(XmlUtils.getChildValue(datasource, "maximum-connection-lifetime")));
				ds.setTestBeforeUse(BoolUtils.toBool(XmlUtils.getChildValue(datasource, "test-before-use"), true));
				ds.setTestAfterUse(BoolUtils.toBool(XmlUtils.getChildValue(datasource, "test-after-use"), false));
				ds.setTrace(BoolUtils.toBool(XmlUtils.getChildValue(datasource, "false"), true));
			}
		}
		return ds;
	}
	
	public SocketBean getSocketBean(String sockId) {
		return getSocketBean("socket", sockId);
	}
	
	public SocketBean getSocketBean(String eNameOrPath, String sockId) {
		// TODO
		return null;
	}
	
	public JmsBean getJmsBean(String jmsId) {
		return getJmsBean("jms", jmsId);
	}
	
	public JmsBean getJmsBean(String eNameOrPath, String jmsId) {
		// TODO
		return null;
	}
	
	public void clear() {
		namePath.clear();
		pathIndex.clear();
		pathTree.clear();
	}

	public String getConfigName() {
		return configName;
	}

}
