package exp.fpf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.bean.FPFConfig;
import exp.fpf.envm.ResponseMode;
import exp.libs.utils.num.NumUtils;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;
import exp.libs.warp.net.sock.bean.SocketBean;

/**
 * <pre>
 * 配置类
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {

	private Logger log = LoggerFactory.getLogger(Config.class);
	
	private final static String APP_CONF_PATH = "./conf/fpf_conf.dat";
	
	private final static String USE_CONF_PATH = "./conf/fpf_conf.xml";
	
	private XConfig xConf;
	
	/** 发送文件目�? */
	private String sendDir;
	
	/** 接收文件目录 */
	private String recvDir;
	
	/**
	 * 响应数据的接收模�?:
	 *  1: sock监听模式 (需隔离装置开放TCP转发端口)
	 *  2: file扫描模式 (需隔离装置主动扫描文件目录转发)
	 *  
	 * 注：隔离装置的请求数据只能通过文件扫描模式发�?
	 */
	private int rspMode;
	
	/** 用于接收返回数据的监听socket(IP): 仅rspMode=1时有�? */
	private String rspIp;
	
	/** 用于接收返回数据的监听socket(端口): 仅rspMode=1时有�? */
	private int rspPort;
	
	/** 扫描文件间隔 */
	private long scanInterval;
	
	/** 超时时间(ms) */
	private int overtime;
	
	/** 单个代理会话的缓冲区大小(kb) */
	private int buffSize;
	
	/** 代理服务配置�? */
	private List<FPFConfig> fpfConfigs;
	
	private static volatile Config instance;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("FPF");
		this.fpfConfigs = new LinkedList<FPFConfig>();
		this.sendDir = "./tmp/sendDir";
		this.recvDir = "./tmp/recvDir";
		this.rspMode = ResponseMode.SOCKET;
		this.rspIp = "0.0.0.0";
		this.rspPort = 9527;
		this.scanInterval = 1;
		this.overtime = 10000;
		this.buffSize = 20;
	}
	
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
					instance.load();
				}
			}
		}
		return instance;
	}
	
	private void load() {
		xConf.loadConfFile(APP_CONF_PATH);
		Element root = xConf.loadConfFile(USE_CONF_PATH);
		
		this.sendDir = xConf.getVal("sendDir");
		this.recvDir = xConf.getVal("recvDir");
		this.rspMode = xConf.getInt("rspMode");
		this.rspIp = xConf.getVal("config/rspSock/ip");
		this.rspPort = xConf.getInt("config/rspSock/port");
		this.scanInterval = xConf.getLong("scanInterval");
		this.overtime = xConf.getInt("overtime");
		this.buffSize = xConf.getInt("buffSize");
		_loadServicesConfig(root);
	}
	
	@SuppressWarnings("unchecked")
	private void _loadServicesConfig(Element root) {
		try {
			Iterator<Element> services = root.element("services").elementIterator();
			while(services.hasNext()) {
				FPFConfig fpfc = toFPFConfig(services.next());
				if(fpfc != null) {
					fpfConfigs.add(fpfc);
				}
			}
		} catch(Exception e) {}
	}
	
	private FPFConfig toFPFConfig(Element service) {
		FPFConfig fpfc = null;
		try {
			boolean enable = "true".equalsIgnoreCase(service.attributeValue("enable"));
			if(enable == true) {
				String name = service.elementTextTrim("name");
				int localListnPort = NumUtils.toInt(service.elementTextTrim("localListnPort"), 0);
				String remoteIP = service.elementTextTrim("remoteIP");
				int remotePort = NumUtils.toInt(service.elementTextTrim("remotePort"), 0);
				int maxConn = NumUtils.toInt(service.elementTextTrim("maxConn"), 0);
				
				fpfc = new FPFConfig(name, localListnPort, remoteIP, remotePort, 
						maxConn, overtime, buffSize);
			}
		} catch(Exception e) {
			log.error("加载端口转发服务配置失败", e);
		}
		return fpfc;
	}

	public String getSendDir() {
		return sendDir;
	}

	public String getRecvDir() {
		return recvDir;
	}
	
	public int getRspMode() {
		return rspMode;
	}

	public long getScanInterval() {
		return scanInterval;
	}
	
	public int getOvertime() {
		return overtime;
	}
	
	public List<FPFConfig> getFPFConfigs() {
		return fpfConfigs;
	}
	
	public SocketBean newSocketConf() {
		return newSocketConf(rspIp, rspPort);
	}
	
	public SocketBean newSocketConf(String ip, int port) {
		SocketBean sockConf = new SocketBean(ip, port, overtime);
		sockConf.setBufferSize(buffSize, SocketBean.BUFF_SIZE_UNIT_KB);
		return sockConf;
	}
	
}
