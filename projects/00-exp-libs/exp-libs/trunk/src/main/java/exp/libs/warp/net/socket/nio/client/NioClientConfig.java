package exp.libs.warp.net.socket.nio.client;

import exp.libs.envm.Charset;
import exp.libs.envm.OsDelimiter;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.nio.common.envm.Protocol;
import exp.libs.warp.net.socket.nio.common.filter.ThreadPoolFilter;
import exp.libs.warp.net.socket.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.socket.nio.common.interfaze.IConfig;
import exp.libs.warp.net.socket.nio.common.interfaze.IHandler;

/**
 * <pre>
 * NIOSocket客户端配置类。
 * Socket公共配置继承自SocketBean类。
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioClientConfig extends SocketBean implements IConfig {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3228389311411760787L;

	/**
	 * 过滤链
	 */
	private FilterChain filterChain;
	
	/**
	 * 业务处理器
	 */
	private IHandler handler;
	
	/**
	 * 构造函数，配合配置文件用
	 * @param sb 从配置文件获取的配置实体
	 */
	public NioClientConfig(SocketBean sb) {
		super(sb);
		if(this.getOverTime() <= 0) {
			this.setOverTime(10000);
		}
		if(this.getWaitDisconTime() <= 0) {
			this.setWaitDisconTime(10000);
		}
		handleEscapecharacter();
		this.setFilterChain(new FilterChain());
		reflashFilterChain();
	}
	
	/**
	 * 构造函数，使用默认参数
	 */
	public NioClientConfig() {
		super();
		this.defaultConfig();
		handleEscapecharacter();
		this.setFilterChain(new FilterChain());
		reflashFilterChain();
	}

	/**
	 * 使用默认配置
	 */
	public void defaultConfig() {
		this.setIp("127.0.0.1");
		this.setPort(9998);
		this.setBlockTime(1000);
		this.setSleepTime(100);
		this.setHbTime(30 * 1000);
		this.setOverTime(10 * 1000);
		this.setWaitDisconTime(10 * 1000);
		this.setRecvCharset(Charset.DEFAULT);
		this.setSendCharset(Charset.DEFAULT);
		this.setReadBuffSize(1024);
		this.setWriteBuffSize(1024);
		this.setRecvDelimiter(OsDelimiter.DEFAULT);
		this.setSendDelimiter(OsDelimiter.DEFAULT);
		this.setDisconCmd(Protocol.EXIT_CMD);
		this.setMultiThreadMode(false);
	}
	
	/**
	 * 处理分隔符中的转义字符：
	 * 从xml文件读入的 \n（一个字符），在java中会变成 \\n（两个字符）
	 * 因此这里加一个预处理
	 */
	private void handleEscapecharacter() {
		String recvDelimiter = this.getRecvDelimiter();
		recvDelimiter = recvDelimiter.replace("\\r", "\r")
				.replace("\\n", "\n")
				.replace("\\0", "\0")
				.replace("\\b", "\b")
				.replace("\\t", "\t");
		this.setRecvDelimiter(recvDelimiter);
		
		String sendDelimiter = this.getSendDelimiter();
		sendDelimiter = sendDelimiter.replace("\\r", "\r")
				.replace("\\n", "\n")
				.replace("\\0", "\0")
				.replace("\\b", "\b")
				.replace("\\t", "\t");
		this.setSendDelimiter(sendDelimiter);
	}
	
	/**
	 * 打印配置信息
	 * @return 配置信息
	 */
	@Override
	public String toString() {
		StringBuffer confInfo = new StringBuffer();

		confInfo.append("\n" +
				"************ Nio Socket Client Config ************\n");
		if(this.getIp() == null) {
			confInfo.append("服务器地址: [localhost]\n");
		}
		else {
			confInfo.append("服务器地址: [" + this.getIp() + "]\n");
		}
		confInfo.append("服务器端口: [" + this.getPort() + "]\n");
		confInfo.append("阻塞时间: [" + this.getBlockTime() + " ms]\n");
		confInfo.append("休眠时间: [" + this.getSleepTime() + " ms]\n");
		confInfo.append("心跳间隔: [" + this.getHbTime() + " ms]\n");
		confInfo.append("初始化会话超时时间: [" + this.getOverTime() + " ms]\n");
		confInfo.append("等待断开连接超时时间: [" + this.getWaitDisconTime() + " ms]\n");
		confInfo.append("接收消息使用的字符集编码: [" + this.getRecvCharset() + "]\n");
		confInfo.append("发送消息使用的字符集编码: [" + this.getSendCharset() + "]\n");
		confInfo.append("读缓冲区大小: [" + this.getReadBuffSize() + " bytes]\n");
		confInfo.append("写缓冲区大小: [" + this.getWriteBuffSize() + 
				" bytes] 暂不起任何作用.\n");
		confInfo.append("接收消息分隔符: [" + this.getRecvDelimiter() + "]\n");
		confInfo.append("发送消息分隔符: [" + this.getSendDelimiter() + "]\n");
		confInfo.append("断开连接命令: [" + this.getDisconCmd() + "]\n");
		confInfo.append("启用多线程模式: [" + this.isMultiThreadMode() + "]\n");
		confInfo.append("**************************************************\n");
		return confInfo.toString();
	}
	
	/**
	 * 获取过滤链
	 * @return 过滤链
	 */
	public FilterChain getFilterChain() {
		return filterChain;
	}

	/**
	 * <pre>
	 * 设置过滤链
	 * 
	 * 在配置实体初始化时，会创建过滤链，因此set方法不对外开放，
	 * 避免使用默认过滤器配置好的过滤链，被空链覆盖掉
	 * </pre>
	 * @param filterChain 过滤链
	 */
	private void setFilterChain(FilterChain filterChain) {
		this.filterChain = filterChain;
	}
	
	/**
	 * 刷新过滤链。
	 * 当与过滤链链块的开关配置发生变更时，需刷新过滤链，否则这些配置不会生效。
	 */
	public void reflashFilterChain() {
		if(this.isMultiThreadMode() == true) {
			this.getFilterChain().addFilter(
					"ThreadPoolFilter", new ThreadPoolFilter(
							this.getMaxClientLinkNum(), 
							this.getMaxEachClientTaskNum()));
		} else {
			this.getFilterChain().removeFilter("ThreadPoolFilter");
		}
	}
	
	/**
	 * 获取业务逻辑处理器
	 * @return 业务逻辑处理器
	 */
	public IHandler getHandler() {
		return handler;
	}

	/**
	 * 设置业务逻辑处理器，并放入过滤链
	 * @param handler 业务逻辑处理器
	 */
	public void setHandler(IHandler handler) {
		this.handler = handler;
		this.filterChain.setHandler(handler);
	}
	
	/**
	 * 设置接收消息和发送消息时使用的字符集编码
	 * @param rsCharset 接收消息和发送消息时使用的字符集编码
	 */
	public void setRSCharset(String rsCharset) {
		this.setRecvCharset(rsCharset);
		this.setSendCharset(rsCharset);
	}
	
	/**
	 * 设置读缓冲区和写缓冲区大小
	 * @param kbBuffSize 读缓冲区和写缓冲区大小
	 */
	public void setRWBufferSize(int kbBuffSize) {
		this.setReadBuffSize(kbBuffSize);
		this.setWriteBuffSize(kbBuffSize);
	}
	
}
