package exp.libs.warp.net.socket.io.client;

import exp.libs.utils.pub.StrUtils;

/**
 * <PRE>
 * Socket配置信息
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SocketInfo {

	public final static String DEFAULT_IP = "127.0.0.1"; 
	
	public final static int DEFAULT_PORT = 9998;
	
	public final static int DEFAULT_BUFF_SIZE_UNIT = 1024 * 1024;	//1MB
	
	public final static String DEFAULT_CHARSET = "GBK";
	
	public final static String DEFAULT_DELIMITER = "\0";
	
	public final static int DEFAULT_OVERTIME = 120000;
	
	private String id;
	
	private String ip;
	
	private int port;
	
	private String socket;
	
	private String username;	//emsname
	
	private String password;	//domain
	
	private String charset;
	
	private int bufferSize;
	
	private String delimiter;
	
	private int overtime;
	
	public SocketInfo(String id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.socket = ip + ":" + port;
		this.username = "";
		this.password = "";
		this.bufferSize = DEFAULT_BUFF_SIZE_UNIT;
		this.charset = DEFAULT_CHARSET;
		this.delimiter = DEFAULT_DELIMITER;
		this.overtime = DEFAULT_OVERTIME;
	}
	
	public String getId() {
		return id;
	}
	
	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getSocket() {
		return socket;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		try {
			"charset".getBytes(charset);
			this.charset = charset;
			
		} catch (Exception e) {
			// 非法字符集编码
		}
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		if(bufferSize > 0) {
			this.bufferSize = bufferSize * DEFAULT_BUFF_SIZE_UNIT;
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		if(!StrUtils.isEmpty(delimiter)) {
			this.delimiter = delimiter;
		}
	}

	public int getOvertime() {
		return overtime;
	}

	public void setOvertime(int overtime) {
		if(overtime > 0) {
			this.overtime = overtime;
		}
	}
	
	/**
	 * 生成告警导航信息, 用于日志追踪.
	 * @return 告警导航信息
	 */
	public String toNaviInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("[id=").append(getId()).append(' ');
		sb.append("ip=").append(getIp()).append(' ');
		sb.append("port=").append(getPort()).append(' ');
		sb.append("username=").append(getUsername()).append(' ');
		sb.append("password=").append(getPassword()).append(']');
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    socketId = ").append(getId()).append("\r\n");
		sb.append("    ip = ").append(getIp()).append("\r\n");
		sb.append("    port = ").append(getPort()).append("\r\n");
		sb.append("    username = ").append(getUsername()).append("\r\n");
		sb.append("    password = ").append(getPassword()).append("\r\n");
		sb.append("    charset = ").append(getCharset()).append("\r\n");
		sb.append("    bufferSize = ").append(getBufferSize()).append("\r\n");
		sb.append("    delimiter = ").append(StrUtils.view(getDelimiter())).append("\r\n");
		sb.append("    overtime = ").append(getOvertime()).append("ms\r\n");
		return sb.toString();
	}

}
