package exp.libs.warp.net.sock.nio.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.sock.bean.SocketByteBuffer;
import exp.libs.warp.net.sock.nio.common.cache.MsgQueue;
import exp.libs.warp.net.sock.nio.common.envm.States;
import exp.libs.warp.net.sock.nio.common.filterchain.impl.FilterChain;
import exp.libs.warp.net.sock.nio.common.interfaze.IConfig;
import exp.libs.warp.net.sock.nio.common.interfaze.ISession;

/**
 * <pre>
 * 客户端会话类
 * 
 * 底层负责通信的是SocketChannel，封装了会话级别的相关操作
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
final class Session implements ISession {

	/**
	 * 日志�?
	 */
	private final static Logger log = LoggerFactory.getLogger(Session.class);
	
	/**
	 * 会话名称
	 */
	private String name;
	
	/**
	 * 底层通讯通道
	 */
	private SocketChannel socketChannel;

	/**
	 * 会话状�?
	 */
	private States state;

	/**
	 * Socket服务端配�?
	 */
	private NioClientConfig sockConf;

	/**
	 * 用于保存SocketChannel返回的数据的网络字节缓冲�?
	 */
	private ByteBuffer channelBuffer;

	/**
	 * 把channelBuffer的数据缓存在本地的字节缓冲区
	 */
	private SocketByteBuffer socketBuffer;

	/**
	 * 从socketBuffer中根据消息分隔符提取的消息队�?
	 */
	private MsgQueue recvMsgQueue;

	/**
	 * 会话属性集�?
	 */
	private Map<String, Object> properties;
	
	/**
	 * 会话通知远端机断开连接的时间点
	 */
	private long notifyDisconTime;
	
	/**
	 * 构造函�?
	 * @param socketChannel Socket通讯通道
	 * @param sockConf 服务器配�?
	 */
	protected Session(SocketChannel socketChannel, NioClientConfig sockConf) {
		this.pack(socketChannel);

		this.state = States.NO_VERIFY;
		this.sockConf = sockConf;
				
		this.channelBuffer = ByteBuffer.allocate(sockConf.getReadBufferSize());
		this.socketBuffer = new SocketByteBuffer(sockConf.getReadBufferSize(), 
				sockConf.getReadCharset());
		this.recvMsgQueue = new MsgQueue();
		
		this.properties = new HashMap<String, Object>();
		this.notifyDisconTime = -1;
	}

	/**
	 * 获取会话名称
	 * @return 会话名称
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * 设置会话名称
	 * @param name 会话名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 封装底层会话对象
	 * @param layerSession 底层会话对象，对NioSocket而言就是socketChannel
	 */
	@Override
	public void pack(SocketChannel layerSession) {
		this.socketChannel = layerSession;
		this.name = "session@" + this.socketChannel.hashCode();
	}

	/**
	 * 获取底层会话对象
	 * @return 底层会话对象，对NioSocket而言就是socketChannel
	 */
	@Override
	public SocketChannel getLayerSession() {
		return socketChannel;
	}
	
	/**
	 * 获取远端机IP
	 * 
	 * @return 远端机IP地址
	 * @throws Exception 异常
	 */
	@Override
	public String getIp() throws Exception {
		return socketChannel.socket().getInetAddress().getHostAddress();
	}

	/**
	 * 获取远端机端�?
	 * 
	 * @return 远端机端�?
	 * @throws Exception 异常
	 */
	@Override
	public int getPort() throws Exception {
		return ((InetSocketAddress) (socketChannel.socket()
				.getRemoteSocketAddress())).getPort();
	}
	
	/**
	 * 获取会话状�?
	 * @return 会话状�?
	 */
	@Override
	public Object getState() {
		return state;
	}
	
	/**
	 * <pre>
	 * 检查会话是否已经执行过验证操作
	 * 
	 * 会话�?3种状态：未验证，验证成功、验证失�?
	 * </pre>
	 * 
	 * @return 验证成功、验证失�?:true; 未验�?:false
	 */
	@Override
	public boolean isVerfied() {
		boolean isVerfied = true;
		if (States.NO_VERIFY.id == this.state.id) {
			isVerfied = false;
		}
		return isVerfied;
	}
	
	/**
	 * <pre>
	 * 检查会话是否通过验证
	 * 
	 * 会话�?3种状态：未验证，验证成功、验证失�?
	 * </pre>
	 * 
	 * @return  未验证、验证失�?:false; 其他会话状�?:true
	 */
	public boolean isPassVerfy() {
		boolean isPassVerfy = false;
		if (States.VERIFY_SUCCESS.id == this.state.id ||
				States.VERIFY_SUCCESS.level < this.state.level) {
			isPassVerfy = true;
		}
		return isPassVerfy;
	}
	
	/**
	 * <pre>
	 * 设置会话验证状�?
	 * </pre>
	 * 
	 * @param isVerfy true:验证成功; false:验证失败
	 */
	@Override
	public void setVerfyState(boolean isVerfy) {
		if(isVerfy == true) {
			this.state = States.VERIFY_SUCCESS;
		}
		else {
			this.state = States.VERIFY_FAIL;
		}
	}
	
	/**
	 * 标记会话发生异常
	 */
	public void markError() {
		this.state = States.EXCEPTION;
	}
	
	/**
	 * 检查会话是否发送异�?
	 * @return 是否发送异�?
	 */
	public boolean isError() {
		boolean isError = false;
		if(this.state.id == States.EXCEPTION.id) {
			isError = true;
		}
		return isError;
	}
	
	/**
	 * 获取Socket服务端的配置对象
	 * @return Socket服务端的配置对象
	 */
	@Override
	public IConfig getConfig() {
		return sockConf;
	}
	
	/**
	 * 获取SockerChannel的字节缓冲区
	 * @return SockerChannel的字节缓冲区
	 */
	public ByteBuffer getChannelBuffer() {
		return channelBuffer;
	}

	/**
	 * 获取本地的字节缓冲区
	 * @return 本地的字节缓冲区
	 */
	public SocketByteBuffer getSocketBuffer() {
		return socketBuffer;
	}
	
	/**
	 * 获取会话所接收到的消息队列
	 * @return 会话所接收到的消息队列
	 */
	public MsgQueue getMsgQueue() {
		return recvMsgQueue;
	}
	
	/**
	 * 获取会话属性集�?
	 * @return 会话属性集�?
	 */
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	/**
	 * 获取会话通知远端机断开连接的时间点
	 * @return 会话通知远端机断开连接的时间点
	 */
	public long getNotifyDisconTime() {
		return notifyDisconTime;
	}

	/**
	 * <pre>
	 * 向远端机发送消�?
	 * 
	 * 此方法会触发过滤链的onMessageSent事件，即onMessageSent的入�?
	 * </pre>
	 * 
	 * @param msg 消息
	 * @throws Exception 异常
	 */
	@Override
	public void write(Object msg) throws Exception {
		if(this.isError() == false && this.isClosed() == false && 
				this.isWaitingToClose() == false) {
			
			FilterChain filterChain = sockConf.getFilterChain();
			filterChain.onMessageSent(this, msg);
		}
		else {
			log.debug("会话 [" + this.getName() + 
					"] 存在异常、或等待关闭、或已关�?.无法发送消�?.");
		}
	}
	
	/**
	 * <pre>
	 * 向远端机发送最后一条消�?
	 * 
	 * 此方法会做两件事�?
	 * 发送消�? + 调用closeNotify方法
	 * </pre>
	 * 
	 * @param msg 消息
	 * @throws Exception 异常
	 */
	@Override
	public void writeClose(Object msg) throws Exception {
		write(msg);		//发送最后一条命�?
		closeNotify();	//通知远端机可以断开连接�?
	}

	/**
	 * <pre>
	 * 通知断开连接，被动的断开连接方式�?
	 * 
	 * 此方法会发送一条断开连接命令到远端机，通知远端机：本地已经准备好断开连接�?
	 * 此时会话进入等待关闭状态，禁止再发送任何消息�?
	 * 远端机收到断开连接命令后，会调用close方法断开连接（此机制已内嵌到过滤链链头）�?
	 * 在本地检测到连接被断开后，会关闭会话并清空资源�?
	 * 
	 * 建议的关闭会话方法�?
	 * 若双方没有定义断开连接命令，则在超时后会断开连接，期间只能接收消息，无法发送消息�?
	 * </pre>
	 */
	@Override
	public void closeNotify() throws Exception{
		
		// 避免重复通知远端机断开连接
		if(isWaitingToClose() == false) {
			write(sockConf.getExitCmd());		//发送一条断开命令
			this.notifyDisconTime = System.currentTimeMillis();
			this.state = States.WAIT_TO_CLOSE;	//进入等待断开状�?
		}
	}
	
	/**
	 * <pre>
	 * 马上断开连接�?
	 * 关闭会话并清空资源，可能会导致未处理消息全部丢失�?
	 * 
	 * 此方法在连接发生异常时（如断开）会被动调用。但不建议的主动调用�?
	 * 建议仅在收到远端机的断开命令时、或远端机超时时才主动调用�?
	 * </pre>
	 * 
	 * @throws Exception 异常
	 */
	@Override
	public void close() throws Exception {
		if (States.CLOSED.id != this.state.id) {
			if (socketChannel != null) {
				socketChannel.close();
			}
			
			channelBuffer = null;
			
			if(socketBuffer != null) {
				socketBuffer.clear();
			}
			
			if(recvMsgQueue != null) {
				recvMsgQueue.clear();
			}
			this.state = States.CLOSED;
		}
	}

	/**
	 * 检查会话是否处于等待关闭状�?
	 * 
	 * @return true:等待关闭; false:非等待关�?
	 */
	@Override
	public boolean isWaitingToClose() {
		boolean isWaitingToClose = false;
		if (States.WAIT_TO_CLOSE.id == this.state.id) {
			isWaitingToClose = true;
		}
		return isWaitingToClose;
	}
	
	/**
	 * <pre>
	 * 检查会话是否已关闭
	 * </pre>
	 * 
	 * @return true:已关�?; false:未关�?
	 */
	@Override
	public boolean isClosed() {
		boolean isClosed = false;
		if (States.CLOSED.id == this.state.id) {
			isClosed = true;
		}
		return isClosed;
	}
	
	/**
	 * 重载toString，返回会话名�?
	 * @return 会话名称
	 */
	@Override
	public String toString() {
		return this.getName();
	}

}
