package exp.libs.warp.fpf;


/**
 * <pre>
 * 文件流端口转发代理服务配置
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class FPFAgentConfig {

	private final static int DEFAULT_OVERTIME = 10000;
	
	private final static int DEFAULT_MAX_CONN = 100;
	
	private String agentName;
	
	private int localListenPort;
	
	private String remoteIP;
	
	private int remotePort;
	
	private String srDir;
	
	private int overtime;
	
	private int maxConn;
	
	public FPFAgentConfig(String agentName, int localListenPort, 
			String remoteIP, int remotePort, String srDir) {
		this(agentName, localListenPort, remoteIP, remotePort, srDir, 
				DEFAULT_OVERTIME, DEFAULT_MAX_CONN);
	}
	
	/**
	 * 
	 * @param agentName 代理服务名称（唯一即可）
	 * @param localListenPort 本地监听端口
	 * @param remoteIP 远程代理IP（真实服务IP）
	 * @param remotePort 远程代理端口（真是服务端口）
	 * @param srDir 数据流文件的收发目录
	 * @param overtime 超时时间(单位ms)
	 * @param maxConn 单个代理服务的最大连接数
	 */
	public FPFAgentConfig(String agentName, int localListenPort, String remoteIP, 
			int remotePort, String srDir, int overtime, int maxConn) {
		this.agentName = agentName;
		this.localListenPort = localListenPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.srDir = srDir;
		this.overtime = (overtime <= 0 ? DEFAULT_OVERTIME : overtime);
		this.maxConn = (maxConn <= 0 ? DEFAULT_MAX_CONN : maxConn);
	}

	public String getAgentName() {
		return agentName;
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

	public String getSrDir() {
		return srDir;
	}

	public int getOvertime() {
		return overtime;
	}

	public int getMaxConn() {
		return maxConn;
	}

}
