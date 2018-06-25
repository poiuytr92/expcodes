package exp.libs.warp.net.sock.bean;

import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.VerifyUtils;

/**
 * <pre>
 * Socket配置对象
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SocketBean {

	private String id;
	
	public final static String DEFAULT_ID = "DEFAULT_SOCKET"; 
	
	private String ip;
	
	public final static String DEFAULT_IP = "127.0.0.1"; 
	
	private int port;
	
	public final static int DEFAULT_PORT = 9998;
	
	private String username;
	
	public final static String DEFAULT_USERNAME = "";
	
	private String password;
	
	public final static String DEFAULT_PASSWORD = "";
	
	private String charset;
	
	private String readCharset;
	
	private String writeCharset;
	
	public final static String DEFAULT_CHARSET = "UTF-8";
	
	private int bufferSize;
	
	private int readBufferSize;
	
	private int writeBufferSize;
	
	/** 默认缓冲区大�? */
	public final static int DEFAULT_BUFF_SIZE = 1;
	
	/** 缓冲区大小单位：Byte */
	public final static int BUFF_SIZE_UNIT_BYTE = 1;
	
	/** 缓冲区大小单位：KB */
	public final static int BUFF_SIZE_UNIT_KB = 1024;
	
	/** 缓冲区大小单位：MB */
	public final static int BUFF_SIZE_UNIT_MB = 1024 * 1024;
	
	private String delimiter;
	
	private String readDelimiter;
	
	private String writeDelimiter;
	
	public final static String DEFAULT_DELIMITER = "\0";
	
	private int overtime;
	
	public final static int DEFAULT_OVERTIME = 60000;
	
	private int maxConnectionCount;
	
	public final static int DEFAULT_MAX_CONNECTION_COUNT = 100;
	
	private String exitCmd;
	
	public final static String DEFAULT_EXIT_CMD = "exit";
	
	public SocketBean(String ip, int port) {
		this(ip, port, DEFAULT_OVERTIME);
	}
	
	public SocketBean(String ip, int port, int overtime) {
		this();
		setIp(ip);
		setPort(port);
		setOvertime(overtime);
	}
	
	public SocketBean(SocketBean other) {
		this();
		
		// 内部设值时, 直接赋值而不使用set方法
		// (因为部分set方法有针对外部设值的特殊处理, 在other中已处理过了, 若在this中再处理一次则可能异常)
		if(other != null) {
			this.id = other.getId();
			this.ip = other.getIp();
			this.port = other.getPort();
			this.username = other.getUsername();
			this.password = other.getPassword();
			this.readCharset = other.getReadCharset();
			this.writeCharset = other.getWriteCharset();
			this.readBufferSize = other.getReadBufferSize();
			this.writeBufferSize = other.getWriteBufferSize();
			this.readDelimiter = other.getReadDelimiter();
			this.writeDelimiter = other.getWriteDelimiter();
			this.overtime = other.getOvertime();
			this.maxConnectionCount = other.getMaxConnectionCount();
			this.exitCmd = other.getExitCmd();
		}
	}
	
	public SocketBean() {
		setId(DEFAULT_ID);
		setIp(DEFAULT_IP);
		setPort(DEFAULT_PORT);
		setUsername(DEFAULT_USERNAME);
		setPassword(DEFAULT_PASSWORD);
		setCharset(DEFAULT_CHARSET);
		setBufferSize(DEFAULT_BUFF_SIZE);
		setDelimiter(DEFAULT_DELIMITER);
		setOvertime(DEFAULT_OVERTIME);
		setMaxConnectionCount(DEFAULT_MAX_CONNECTION_COUNT);
		setExitCmd(DEFAULT_EXIT_CMD);
	}

	public String getSocket() {
		return StrUtils.concat(getIp(), ":", getPort());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = (StrUtils.isNotEmpty(id) ? id :
			(StrUtils.isNotEmpty(this.id) ? this.id : DEFAULT_ID));
	}

	public String getAlias() {
		return getId();
	}
	
	public void setAlias(String alias) {
		setId(alias);
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = (StrUtils.isNotEmpty(ip) ? ip :
			(StrUtils.isNotEmpty(this.ip) ? this.ip : DEFAULT_IP));
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = (VerifyUtils.isPort(port) ? port :
			(VerifyUtils.isPort(this.port) ? this.port : DEFAULT_PORT));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = (username != null ? username :
			(this.username != null ? this.username : DEFAULT_USERNAME));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = (password != null ? password :
			(this.password != null ? this.password : DEFAULT_PASSWORD));
	}

	@Deprecated
	public String getCharset() {
		if(readCharset.equals(writeCharset)) {
			return charset;
			
		} else {
			return "differences between [readCharset] and [writeCharset]";
		}
	}

	public void setCharset(String charset) {
		if(CharsetUtils.isVaild(charset)) {
			this.charset = charset;
			this.readCharset = charset;
			this.writeCharset = charset;
		}
	}

	public String getReadCharset() {
		return readCharset;
	}

	public void setReadCharset(String readCharset) {
		if(CharsetUtils.isVaild(readCharset)) {
			this.readCharset = readCharset;
			
			if(this.readCharset.equals(this.writeCharset)) {
				this.charset = readCharset;
			}
		}
	}

	public String getWriteCharset() {
		return writeCharset;
	}

	public void setWriteCharset(String writeCharset) {
		if(CharsetUtils.isVaild(writeCharset)) {
			this.writeCharset = writeCharset;
			
			if(this.readCharset.equals(this.writeCharset)) {
				this.charset = writeCharset;
			}
		}
	}

	@Deprecated
	public int getBufferSize() {
		if(readBufferSize == writeBufferSize) {
			return bufferSize;
			
		} else {
			return NumUtils.max(readBufferSize, writeBufferSize);
		}
	}

	/**
	 * 设置读写缓冲区大小（单位MB�?
	 * @param bufferSize 缓冲区大�?
	 */
	public void setBufferSize(int bufferSize) {
		setBufferSize(bufferSize, BUFF_SIZE_UNIT_MB);
	}
	
	/**
	 * 设置读写缓冲区大�?: (bufferSize * unit) 字节
	 * @param bufferSize 缓冲区大�?
	 * @param unit 单位:默认�? 1024*1024个字�?, �?1MB
	 */
	public void setBufferSize(int bufferSize, int unit) {
		if(bufferSize > 0) {
			unit = (unit <= 0 ? BUFF_SIZE_UNIT_MB : unit);
			this.bufferSize = bufferSize * unit;
			this.readBufferSize = this.bufferSize;
			this.writeBufferSize = this.bufferSize;
		}
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	/**
	 * 设置读缓冲区大小（单位MB�?
	 * @param bufferSize 缓冲区大�?
	 */
	public void setReadBufferSize(int readBufferSize) {
		setReadBufferSize(readBufferSize, BUFF_SIZE_UNIT_MB);
	}
	
	/**
	 * 设置读缓冲区大小: (bufferSize * unit) 字节
	 * @param bufferSize 缓冲区大�?
	 * @param unit 单位:默认�? 1024*1024个字�?, �?1MB
	 */
	public void setReadBufferSize(int readBufferSize, int unit) {
		if(readBufferSize > 0) {
			unit = (unit <= 0 ? BUFF_SIZE_UNIT_MB : unit);
			this.readBufferSize = readBufferSize * unit;
			
			if(this.readBufferSize == this.writeBufferSize) {
				this.bufferSize = readBufferSize;
			}
		}
	}

	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	/**
	 * 设置写缓冲区大小（单位MB�?
	 * @param bufferSize 缓冲区大�?
	 */
	public void setWriteBufferSize(int writeBufferSize) {
		setWriteBufferSize(writeBufferSize, BUFF_SIZE_UNIT_MB);
	}
	
	/**
	 * 设置写缓冲区大小: (bufferSize * unit) 字节
	 * @param bufferSize 缓冲区大�?
	 * @param unit 单位:默认�? 1024*1024个字�?, �?1MB
	 */
	public void setWriteBufferSize(int writeBufferSize, int unit) {
		if(writeBufferSize > 0) {
			unit = (unit <= 0 ? BUFF_SIZE_UNIT_MB : unit);
			this.writeBufferSize = writeBufferSize * unit;
			
			if(this.readBufferSize == this.writeBufferSize) {
				this.bufferSize = writeBufferSize;
			}
		}
	}

	@Deprecated
	public String getDelimiter() {
		if(readDelimiter.equals(writeDelimiter)) {
			return delimiter;
			
		} else {
			return "differences between [readDelimiter] and [writeDelimiter]";
		}
	}

	public void setDelimiter(String delimiter) {
		if(StrUtils.isNotEmpty(delimiter)) {
			delimiter = ESCUtils.toJavaESC(delimiter);
			this.delimiter = delimiter;
			this.readDelimiter = delimiter;
			this.writeDelimiter = delimiter;
		}
	}

	public String getReadDelimiter() {
		return readDelimiter;
	}

	public void setReadDelimiter(String readDelimiter) {
		if(StrUtils.isNotEmpty(readDelimiter)) {
			readDelimiter = ESCUtils.toJavaESC(readDelimiter);
			this.readDelimiter = readDelimiter;
			
			if(this.readDelimiter.equals(this.writeDelimiter)) {
				this.delimiter = readDelimiter;
			}
		}
	}

	public String getWriteDelimiter() {
		return writeDelimiter;
	}

	public void setWriteDelimiter(String writeDelimiter) {
		if(StrUtils.isNotEmpty(writeDelimiter)) {
			writeDelimiter = ESCUtils.toJavaESC(writeDelimiter);
			this.writeDelimiter = writeDelimiter;
			
			if(this.readDelimiter.equals(this.writeDelimiter)) {
				this.delimiter = writeDelimiter;
			}
		}
	}

	public int getOvertime() {
		return overtime;
	}

	public void setOvertime(int overtime) {
		this.overtime = (overtime > 0 ? overtime :
			(this.overtime >= 0 ? this.overtime : DEFAULT_OVERTIME));
	}

	public int getMaxConnectionCount() {
		return maxConnectionCount;
	}

	public void setMaxConnectionCount(int maxConnectionCount) {
		this.maxConnectionCount = (maxConnectionCount > 0 ? maxConnectionCount :
			(this.maxConnectionCount >= 0 ? this.maxConnectionCount : DEFAULT_MAX_CONNECTION_COUNT));
	}

	public String getExitCmd() {
		return exitCmd;
	}

	public void setExitCmd(String exitCmd) {
		this.exitCmd = (StrUtils.isNotEmpty(exitCmd) ? exitCmd :
			(StrUtils.isNotEmpty(this.exitCmd) ? this.exitCmd : DEFAULT_EXIT_CMD));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n++++++++++++++++ Socket ++++++++++++++++\r\n");
		sb.append("id = ").append(getId()).append("\r\n");
		sb.append("ip = ").append(getIp()).append("\r\n");
		sb.append("port = ").append(getPort()).append("\r\n");
		sb.append("username = ").append(getUsername()).append("\r\n");
		sb.append("password = ").append(getPassword()).append("\r\n");
		sb.append("charset = ").append(getCharset()).append("\r\n");
		sb.append("readCharset = ").append(getReadCharset()).append("\r\n");
		sb.append("writeCharset = ").append(getWriteCharset()).append("\r\n");
		sb.append("bufferSize = ").append(getBufferSize()).append(" bytes\r\n");
		sb.append("readBufferSize = ").append(getReadBufferSize()).append(" bytes\r\n");
		sb.append("writeBufferSize = ").append(getWriteBufferSize()).append(" bytes\r\n");
		sb.append("delimiter = ").append(StrUtils.view(getDelimiter())).append("\r\n");
		sb.append("readDelimiter = ").append(StrUtils.view(getReadDelimiter())).append("\r\n");
		sb.append("writeDelimiter = ").append(StrUtils.view(getWriteDelimiter())).append("\r\n");
		sb.append("overtime = ").append(getOvertime()).append(" ms\r\n");
		sb.append("maxConnectionCount = ").append(getMaxConnectionCount()).append("\r\n");
		sb.append("exitCmd = ").append(getExitCmd()).append("\r\n");
		sb.append("----------------------------------------\r\n");
		return sb.toString();
	}

	public SocketBean clone() {
		
		// 部分get/set方法含有校验逻辑，若通过setXX(getXX)可能会因为重复校验导致数据异�?
		// 如bufferSize则会因为重复乘以单位导致数值越�?
		// 因此此处采用直接赋值方式进行克�?
		
		SocketBean _clone = new SocketBean();
		_clone.id = this.id;
		_clone.ip = this.ip;
		_clone.port = this.port;
		_clone.username = this.username;
		_clone.password = this.password;
		_clone.charset = this.charset;
		_clone.readCharset = this.readCharset;
		_clone.writeCharset = this.writeCharset;
		_clone.bufferSize = this.bufferSize;
		_clone.readBufferSize = this.readBufferSize;
		_clone.writeBufferSize = this.writeBufferSize;
		_clone.delimiter = this.delimiter;
		_clone.readDelimiter = this.readDelimiter;
		_clone.writeDelimiter = this.writeDelimiter;
		_clone.overtime = this.overtime;
		_clone.maxConnectionCount = this.maxConnectionCount;
		_clone.exitCmd = this.exitCmd;
		return _clone;
	}
	
}
