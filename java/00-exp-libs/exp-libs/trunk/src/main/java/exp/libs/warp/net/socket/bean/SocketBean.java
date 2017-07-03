package exp.libs.warp.net.socket.bean;

import exp.libs.utils.StrUtils;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.format.ESCUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.verify.VerifyUtils;

public class SocketBean {

	private String id;
	
	private final static String DEFAULT_ID = "DEFAULT_SOCKET"; 
	
	private String ip;
	
	private final static String DEFAULT_IP = "127.0.0.1"; 
	
	private int port;
	
	private final static int DEFAULT_PORT = 9998;
	
	private String username;
	
	private final static String DEFAULT_USERNAME = "";
	
	private String password;
	
	private final static String DEFAULT_PASSWORD = "";
	
	private String charset;
	
	private String readCharset;
	
	private String writeCharset;
	
	private final static String DEFAULT_CHARSET = "UTF-8";
	
	private int bufferSize;
	
	private int readBufferSize;
	
	private int writeBufferSize;
	
	private final static int DEFAULT_BUFF_SIZE = 1;
	
	private final static int DEFAULT_BUFF_SIZE_UNIT = 1024 * 1024;	//1MB
	
	private String delimiter;
	
	private String readDelimiter;
	
	private String writeDelimiter;
	
	private final static String DEFAULT_DELIMITER = "\0";
	
	private int overtime;
	
	private final static int DEFAULT_OVERTIME = 60000;
	
	private int maxConnectionCount;
	
	private final static int DEFAULT_MAX_CONNECTION_COUNT = 100;
	
	private String exitCmd;
	
	private final static String DEFAULT_EXIT_CMD = "exit";
	
	public SocketBean() {
		setId(DEFAULT_ID);
		setIp(DEFAULT_IP);
		setPort(DEFAULT_PORT);
		setUsername(DEFAULT_USERNAME);
		setPassword(DEFAULT_PASSWORD);
		setCharset(DEFAULT_CHARSET);
		setBufferSize(DEFAULT_BUFF_SIZE * DEFAULT_BUFF_SIZE_UNIT);
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

	public void setBufferSize(int bufferSize) {
		if(bufferSize > 0) {
			this.bufferSize = bufferSize * DEFAULT_BUFF_SIZE_UNIT;
			this.readBufferSize = bufferSize * DEFAULT_BUFF_SIZE_UNIT;
			this.writeBufferSize = bufferSize * DEFAULT_BUFF_SIZE_UNIT;
		}
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		if(readBufferSize > 0) {
			this.readBufferSize = readBufferSize * DEFAULT_BUFF_SIZE_UNIT;
			
			if(this.readBufferSize == this.writeBufferSize) {
				this.bufferSize = readBufferSize;
			}
		}
	}

	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	public void setWriteBufferSize(int writeBufferSize) {
		if(writeBufferSize > 0) {
			this.writeBufferSize = writeBufferSize * DEFAULT_BUFF_SIZE_UNIT;
			
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
			(StrUtils.isNotEmpty(this.exitCmd) ? this.exitCmd : DEFAULT_ID));
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

}
