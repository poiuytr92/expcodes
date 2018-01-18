package exp.libs.warp.conf.xml;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import exp.libs.warp.db.sql.bean.DataSourceBean;
import exp.libs.warp.net.jms.mq.bean.JmsBean;
import exp.libs.warp.net.sock.bean.SocketBean;

interface _IConfig {

	/** 磁盘上的配置文件 */
	public final static String DISK_FILE = "0";
	
	/** jar包内的配置文件 */
	public final static String JAR_FILE = "1";
	
	public String NAME();
	
	public boolean loadConfFiles(String... confFilePaths);
	
	public Element loadConfFile(String confFilePath);
	
	public boolean loadConfFilesInJar(String... confFilePaths);
	
	public Element loadConfFileInJar(String confFilePath);
	
	public boolean loadConfFilesByTomcat(String... confFilePaths);
	
	public Element loadConfFileByTomcat(String confFilePath);
	
	public String getVal(String ePath);
	
	public String getVal(String eName, String eId);
	
	public int getInt(String ePath);
	
	public int getInt(String eName, String eId);
	
	public long getLong(String ePath);
	
	public long getLong(String eName, String eId);
	
	public boolean getBool(String ePath);
	
	public boolean getBool(String eName, String eId);
	
	public List<String> getEnums(String ePath);
	
	public List<String> getEnums(String eName, String eId);
	
	public String getAttribute(String ePath, String attributeName);
	
	public String getAttribute(String eName, String eId, String attributeName);
	
	public Map<String, String> getAttributes(String ePath);
	
	public Map<String, String> getAttributes(String eName, String eId);
	
	public DataSourceBean getDataSourceBean(String dsId);
	
	public SocketBean getSocketBean(String sockId);
	
	public JmsBean getJmsBean(String jmsId);
	
}
