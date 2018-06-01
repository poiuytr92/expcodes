package exp.fpf.bean;


/**
 * <pre>
 * (文件流)端口转发代理服务配置
 * </pre>	
 * <B>PROJECT：</B> file-port-forwarding
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-28
 * @author    EXP 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FPFConfig {

	/** 默认可连接到单个代理服务的最大连接数 */
	private final static int DEFAULT_MAX_CONN = 100;
	
	/** 默认代理会话超时时间(ms) */
	private final static int DEFAULT_OVERTIME = 10000;
	
	/** 默认代理会话缓冲区大小(KB) */
	private final static int DEFAULT_BUFFSIZE = 64;
	
	/** 代理服务名 */
	private String serverName;
	
	/** 本地代理端口 */
	private int localListenPort;
	
	/** 远程服务IP（真实IP） */
	private String remoteIP;
	
	/** 远程服务端口（真实端口） */
	private int remotePort;
	
	/** 可连接到单个代理服务的最大连接数 */
	private int maxConn;
	
	/** 代理会话超时时间(ms) */
	private int overtime;
	
	/** 代理会话缓冲区大小(KB) */
	private int buffSize;
	
	/**
	 * 构造函数
	 * @param serverName
	 * @param localListenPort
	 * @param remoteIP
	 * @param remotePort
	 */
	public FPFConfig(String serverName, int localListenPort, 
			String remoteIP, int remotePort) {
		this(serverName, localListenPort, remoteIP, remotePort, 
				DEFAULT_MAX_CONN, DEFAULT_OVERTIME, DEFAULT_BUFFSIZE);
	}
	
	/**
	 * 构造函数
	 * @param serverName 代理服务名称（唯一即可）
	 * @param localListenPort 本地监听端口
	 * @param remoteIP 远程代理IP（真实服务IP）
	 * @param remotePort 远程代理端口（真实服务端口）
	 * @param maxConn 单个代理服务的最大连接数
	 * @param overtime 超时时间(单位ms)
	 * @param buffSize 代理会话缓冲区大小(单位KB)
	 */
	public FPFConfig(String serverName, int localListenPort, 
			String remoteIP, int remotePort, int maxConn, 
			int overtime, int buffSize) {
		this.serverName = serverName;
		this.localListenPort = localListenPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.maxConn = (maxConn <= 0 ? DEFAULT_MAX_CONN : maxConn);
		this.overtime = (overtime <= 0 ? DEFAULT_OVERTIME : overtime);
		this.buffSize = (buffSize <= 0 ? DEFAULT_BUFFSIZE : buffSize);
	}

	public String getServerName() {
		return serverName;
	}

	public int getLocalListenPort() {
		return localListenPort;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public int getOvertime() {
		return overtime;
	}

	public int getBuffSize() {
		return buffSize;
	}

}
