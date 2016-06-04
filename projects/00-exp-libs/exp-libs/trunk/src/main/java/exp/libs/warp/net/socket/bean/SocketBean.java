package exp.libs.warp.net.socket.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import exp.libs.envm.Charset;
import exp.libs.envm.OsDelimiter;


/**
 * <pre>
 * Socket配置实体类
 * 
 * 可用于客户端和服务端，但是有些共有的配置项的意义不同、或者是无效配置项
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SocketBean implements Serializable {

	/**
	 * 序列化全局ID
	 */
	private static final long serialVersionUID = 5381231100560667440L;

	/**
	 * SocketBean标识ID
	 */
	private String id;
	
	/** 
	 * <pre>
	 * socket类型:
	 * 服务端或者客户端
	 * </pre>
	 */
	private String type;
	
	/**
	 * 阻塞时间（单位ms）
	 */
	private int blockTime;
	
	/**
	 * 断开连接命令
	 */
	private String disconCmd;

	/**
	 * 心跳时间间隔（单位ms）
	 */
	private long hbTime;
	
	/**
	 * 服务IP
	 */
	private String ip;

	/**
	 * 允许最大的客户端连接数(-1表示不限制)
	 */
	private int maxClientLinkNum;

	/**
	 * 允许单个客户端连续发送的消息数(-1表示不限制)
	 */
	private int maxEachClientTaskNum;
	
	/**
	 * 启用多线程模式
	 */
	private boolean multiThreadMode;

	/**
	 * <pre>
	 * 服务端：客户端的无动作超时断开时间（单位ms），-1表示不启用
	 * 客户端：获取客户端会话时，等待其完成初始化的超时时间，即服务器响应连接请求的超时时间（单位ms）
	 * </pre>
	 */
	private long overTime;
	
	/**
	 * 本地机口
	 */
	private int port;

	/**
	 * 读缓冲区大小
	 */
	private int readBuffSize;
	
	/**
	 * <pre>
	 * 接收消息使用的字符集编码，默认为本地操作平台编码
	 * 使用此字符集读取接收到的字节流
	 * </pre>
	 */
	private String recvCharset;

	/**
	 * <pre>
	 * 接收消息分隔符。
	 * 接收消息时以该分隔符判断完整消息。
	 * 不配置则使用本地操作系统的默认分隔符。
	 * </pre>
	 */
	private String recvDelimiter;

	/**
	 * <pre>
	 * 发送消息使用的字符集编码，默认为本地操作平台编码
	 * 使用此字符集发送字节流
	 * </pre>
	 */
	private String sendCharset;
	
	/**
	 * <pre>
	 * 发送消息分隔符。
	 * 发送消息时自动附加，该值应与远端机处理消息时用的分隔符一致。
	 * 不配置则使用本地操作系统的默认分隔符。
	 * </pre>
	 */
	private String sendDelimiter;

	/**
	 * 休眠时间（单位ms）
	 */
	private int sleepTime;

	/**
	 * <pre>
	 * 等待断开连接上限时间（单位ms），配合“通知远端机关闭连接notifyClose”方法使用
	 * 
	 * 通知远端机断开连接后，若超时仍未断开连接，则在本地马上断开连接。
	 * 该值若小于0，则置为10 000 ms
	 * </pre>
	 */
	private long waitDisconTime;

	/**
	 * 写缓冲区大小，暂不起任何作用
	 */
	private int writeBuffSize;

	/** 配置文件读取记录  */
	public Set<String> record = new HashSet<String>();
	
	/**
	 * 构造函数，使用默认参数
	 */
	public SocketBean() {
		this.setId("S0001");
		this.setType("server");
		this.setIp(null);
		this.setPort(9998);
		this.setBlockTime(1000);
		this.setSleepTime(100);
		this.setHbTime(30 * 1000);
		this.setOverTime(10000);
		this.setWaitDisconTime(5 * 1000);
		this.setRecvCharset(Charset.DEFAULT);
		this.setSendCharset(Charset.DEFAULT);
		this.setReadBuffSize(1024);
		this.setWriteBuffSize(1024);
		this.setRecvDelimiter(OsDelimiter.DEFAULT);
		this.setSendDelimiter(OsDelimiter.DEFAULT);
		this.setDisconCmd("exit");
		this.setMaxClientLinkNum(-1);
		this.setMaxEachClientTaskNum(-1);
		this.setMultiThreadMode(false);
	}
	
	/**
	 * 构造函数，配合配置文件用
	 * @param sb 从配置文件获取的配置实体
	 */
	public SocketBean(SocketBean sb) {
		this.setId(sb.getId());
		this.setType(sb.getType());
		this.setIp(sb.getIp());
		this.setPort(sb.getPort());
		this.setBlockTime(sb.getBlockTime());
		this.setSleepTime(sb.getSleepTime());
		this.setHbTime(sb.getHbTime());
		this.setOverTime(sb.getOverTime());
		this.setWaitDisconTime(sb.getWaitDisconTime());
		this.setRecvCharset(sb.getRecvCharset());
		this.setSendCharset(sb.getSendCharset());
		this.setReadBuffSize(sb.getReadBuffSize());
		this.setWriteBuffSize(sb.getWriteBuffSize());
		this.setRecvDelimiter(sb.getRecvDelimiter());
		this.setSendDelimiter(sb.getSendDelimiter());
		this.setDisconCmd(sb.getDisconCmd());
		this.setMaxClientLinkNum(sb.getMaxClientLinkNum());
		this.setMaxEachClientTaskNum(sb.getMaxEachClientTaskNum());
		this.setMultiThreadMode(sb.isMultiThreadMode());
	}
	
	/**
	 * 获取阻塞时间
	 * @return 阻塞时间
	 */
	public int getBlockTime() {
		return blockTime;
	}

	/**
	 * 获取断开连接的命令
	 * @return 断开连接的命令
	 */
	public String getDisconCmd() {
		return disconCmd;
	}
	
	/**
	 * 获取心跳时间
	 * @return 心跳时间
	 */
	public long getHbTime() {
		return hbTime;
	}
	
	/**
	 * id
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取服务IP
	 * @return 服务IP
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 获取最大客户端连接数
	 * @return 最大客户端连接数
	 */
	public int getMaxClientLinkNum() {
		return maxClientLinkNum;
	}

	/**
	 * 获取单个客户端允许连续发送的消息数
	 * @return 单个客户端允许连续发送的消息数
	 */
	public int getMaxEachClientTaskNum() {
		return maxEachClientTaskNum;
	}

	/**
	 * 获取超时时间
	 * @return 超时时间
	 */
	public long getOverTime() {
		return overTime;
	}

	/**
	 * 获取本地机口
	 * @return 本地机口
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 获取读缓冲区大小
	 * @return 读缓冲区大小
	 */
	public int getReadBuffSize() {
		return readBuffSize;
	}

	/**
	 * 获取接收消息的字符集
	 * @return 接收消息的字符集
	 */
	public String getRecvCharset() {
		return recvCharset;
	}
	
	/**
	 * 获取接收消息分隔符.
	 * 若配置了多个,会得到用!#@{[...]}@#!包围分隔的分隔符.
	 * 若配置了1个,则没有.
	 * 
	 * 可用 SocketUtils 提供的 splitDelimiter 方法进行切割.
	 * @return 接收消息分隔符
	 */
	public String getRecvDelimiter() {
		return recvDelimiter;
	}

	/**
	 * 获取发送消息的字符集
	 * @return 发送消息的字符集
	 */
	public String getSendCharset() {
		return sendCharset;
	}

	/**
	 * 获取发送消息分隔符
	 * @return 发送消息分隔符
	 */
	public String getSendDelimiter() {
		return sendDelimiter;
	}

	/**
	 * 获取休眠时间
	 * @return 休眠时间
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * type
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * <pre>
	 * 获取等待断开连接上限时间（单位ms）
	 * 配合“通知远端机关闭连接notifyClose”方法使用
	 * </pre>
	 * @return 等待断开连接上限时间、
	 */
	public long getWaitDisconTime() {
		return waitDisconTime;
	}

	/**
	 * 获取写缓冲区大小
	 * @return 写缓冲区大小
	 */
	public int getWriteBuffSize() {
		return writeBuffSize;
	}

	/**
	 * 检查是否启用了多线程模式
	 * @return 是否启用了多线程模式
	 */
	public boolean isMultiThreadMode() {
		return multiThreadMode;
	}

	/**
	 * 设置阻塞时间
	 * @param blockTime 阻塞时间
	 */
	public void setBlockTime(int blockTime) {
		this.blockTime = blockTime;
	}

	/**
	 * 设置断开连接的命令
	 * @param disconCmd 断开连接的命令
	 */
	public void setDisconCmd(String disconCmd) {
		this.disconCmd = disconCmd;
	}

	/**
	 * 设置心跳时间
	 * @param hbTime 心跳时间
	 */
	public void setHbTime(long hbTime) {
		this.hbTime = hbTime;
	}

	/**
	 * id
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 设置服务IP
	 * @param ip 服务IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * <pre>
	 * 设置最大客户端连接数
	 * 若启用多线程模式，此参数会作为线程池过滤器的初始化参数之一
	 * </pre>
	 * @param maxClientLinkNum 最大客户端连接数
	 */
	public void setMaxClientLinkNum(int maxClientLinkNum) {
		this.maxClientLinkNum = maxClientLinkNum;
	}

	/**
	 * <pre>
	 * 设置单个客户端允许连续发送的消息数
	 * 若启用多线程模式，此参数会作为线程池过滤器的初始化参数之一
	 * </pre>
	 * @param taskNum 单个客户端允许连续发送的消息数
	 */
	public void setMaxEachClientTaskNum(int taskNum) {
		this.maxEachClientTaskNum = taskNum;
	}

	/**
	 * <pre>
	 * 设置多线程模式
	 * 
	 * 服务端建议启用
	 * 客户端不建议启用，客户端启用多线程会导致发送的顺序与返回消息的顺序不一致
	 * </pre>
	 * @param multiThreadMode 是否启用
	 */
	public void setMultiThreadMode(boolean multiThreadMode) {
		this.multiThreadMode = multiThreadMode;
	}

	/**
	 * 设置超时时间
	 * @param overTime 超时时间
	 */
	public void setOverTime(long overTime) {
		this.overTime = overTime;
	}

	/**
	 * 设置本地机口
	 * @param port 本地机口
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 设置读缓冲区大小
	 * @param readBuffSize 读缓冲区大小
	 */
	public void setReadBuffSize(int readBuffSize) {
		this.readBuffSize = readBuffSize;
	}

	/**
	 * 设置接收消息的字符集.
	 * 若配置多个需用!#@{[...]}@#!包围分隔,若仅配置1个则不必要.
	 * @param recvCharset 接收消息的字符集
	 */
	public void setRecvCharset(String recvCharset) {
		this.recvCharset = recvCharset;
	}

	/**
	 * 设置接收消息分隔符
	 * @param recvDelimiter 接收消息分隔符
	 */
	public void setRecvDelimiter(String recvDelimiter) {
		this.recvDelimiter = recvDelimiter;
	}

	/**
	 * 设置发送消息的字符集
	 * @param sendCharset 发送消息的字符集
	 */
	public void setSendCharset(String sendCharset) {
		this.sendCharset = sendCharset;
	}

	/**
	 * 设置发送消息分隔符
	 * @param sendDelimiter 发送消息分隔符
	 */
	public void setSendDelimiter(String sendDelimiter) {
		this.sendDelimiter = sendDelimiter;
	}
	
	/**
	 * 设置休眠时间
	 * @param sleepTime 休眠时间
	 */
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	/**
	 * type
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 设置等待断开连接上限时间（单位ms）
	 * @param waitDisconTime 等待断开连接上限时间
	 */
	public void setWaitDisconTime(long waitDisconTime) {
		this.waitDisconTime = waitDisconTime;
	}

	/**
	 * 设置写缓冲区大小
	 * @param writeBuffSize 写缓冲区大小
	 */
	public void setWriteBuffSize(int writeBuffSize) {
		this.writeBuffSize = writeBuffSize;
	}

	/**
	 * 打印配置信息
	 * @return 配置信息
	 */
	@Override
	public String toString() {
		StringBuilder confInfo = new StringBuilder();
		confInfo.append("\r\n" +
				"*************** Nio Socket Config ***************\r\n");
		confInfo.append("配置类型: [" + this.getType() + "]\r\n");
		confInfo.append("标识ID：" + this.getId() + "\r\n");
		if(this.getIp() == null) {
			confInfo.append("服务地址: [localhost]\r\n");
		}
		else {
			confInfo.append("服务地址: [" + this.getIp() + "]\r\n");
		}
		confInfo.append("服务端口: [" + this.getPort() + "]\r\n");
		confInfo.append("阻塞时间: [" + this.getBlockTime() + " ms]\r\n");
		confInfo.append("休眠时间: [" + this.getSleepTime() + " ms]\r\n");
		confInfo.append("心跳间隔: [" + this.getHbTime() + " ms]\r\n");
		confInfo.append("[服务端]客户端无动作超时时间/[客户端]初始化会话超时时间: [" + 
				this.getOverTime() + " ms (-1表示不启用)]\r\n");
		confInfo.append("等待断开连接超时时间: [" + this.getWaitDisconTime() + 
				" ms]\r\n");
		confInfo.append("接收消息使用的字符集编码: [" + this.getRecvCharset() + "]\r\n");
		confInfo.append("发送消息使用的字符集编码: [" + this.getSendCharset() + "]\r\n");
		confInfo.append("读缓冲区大小: [" + this.getReadBuffSize() + " bytes]\r\n");
		confInfo.append("写缓冲区大小: [" + this.getWriteBuffSize() + 
				" bytes] 暂不起任何作用.\r\n");
		confInfo.append("接收消息分隔符: [" + this.getRecvDelimiter() + "]\r\n");
		confInfo.append("发送消息分隔符: [" + this.getSendDelimiter() + "]\r\n");
		confInfo.append("断开连接命令: [" + this.getDisconCmd() + "]\r\n");
		confInfo.append("允许最大连接数: [" + this.getMaxClientLinkNum() + 
				" (-1表示不限制,仅服务端有效)]\r\n");
		confInfo.append("允许单个远端机连续发送的消息数: [" + this.getMaxEachClientTaskNum()
				+ " (-1表示不限制,仅服务端有效)]\r\n");
		confInfo.append("启用多线程模式: [" + this.isMultiThreadMode() + "]\r\n");
		confInfo.append("*************************************************\r\n");
		return confInfo.toString();
	}

}
