package exp.libs.warp.conf.xml;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.socket.bean.SocketBean;

interface IConfig {

	/** 磁盘上的配置文件 */
	public final static String DISK_FILE = "0";
	
	/** jar包内的配置文件 */
	public final static String JAR_FILE = "1";
	
	public String getConfigName();
	
	public boolean loadConfFiles(String[] confFilePaths);
	
	public boolean loadConfFile(String confFilePath);
	
	public boolean loadConfFilesInJar(String[] confFilePaths);
	
	public boolean loadConfFileInJar(String confFilePath);
	
	public void clear();

	public Element getElement(String eNameOrPath);
	
	public Element getElement(String eNameOrPath, String eId);
	
	public String getVal(String eNameOrPath);
	
	public String getVal(String eNameOrPath, String eId);
	
	public int getInt(String eNameOrPath);
	
	public int getInt(String eNameOrPath, String eId);
	
	public long getLong(String eNameOrPath);
	
	public long getLong(String eNameOrPath, String eId);
	
	public boolean getBool(String eNameOrPath);
	
	public boolean getBool(String eNameOrPath, String eId);
	
	public List<String> getEnumVals(String eNameOrPath);
	
	public List<String> getEnumVals(String eNameOrPath, String eId);
	
	public List<Element> getEnum(String eNameOrPath);
	
	public List<Element> getEnum(String eNameOrPath, String eId);
	
	public Map<String, Element> getChildElements(String eNameOrPath);
	
	public Map<String, Element> getChildElements(String eNameOrPath, String eId);
	
	public String getAttribute(String eNameOrPath, String attributeName);
	
	public String getAttribute(String eNameOrPath, String eId, String attributeName);
	
	public Map<String, String> getAttributes(String eNameOrPath);
	
	public Map<String, String> getAttributes(String eNameOrPath, String eId);
	
	public DataSourceBean getDataSourceBean(String dsId);
	
	public DataSourceBean getDataSourceBean(String eNameOrPath, String dsId);
	
	public SocketBean getSocketBean(String sockId);
	
	public SocketBean getSocketBean(String eNameOrPath, String sockId);
	
	public JmsBean getJmsBean(String jmsId);
	
	public JmsBean getJmsBean(String eNameOrPath, String jmsId);
	
}
