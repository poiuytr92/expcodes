package exp.libs.warp.net.socket.nio.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.nio.common.interfaze.ISession;

/**
 * <pre>
 * NIOSocket客户端
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioSocketClient extends Thread {

	/**
	 * 日志器
	 */
	private final static Logger log = LoggerFactory.getLogger(NioSocketClient.class);
	
	/**
	 * 客户端名称
	 */
	private String clientName;

	/**
	 * 事件选择器
	 */
	private Selector selector = null;

	/**
	 * Socket通讯通道
	 */
	private SocketChannel clientSocketChannel = null;

	/**
	 * 客户端会话对象
	 */
	private Session session;
	
	/**
	 * Socket配置
	 */
	private NioClientConfig sockConf = null;

	/**
	 * 会话管理器
	 */
	private SessionManager sessionManager;
	
	/**
	 * 线程死亡标识
	 */
	private boolean isStop;

	/**
	 * 构造函数
	 * @param sockConf 客户端名称
	 */
	public NioSocketClient(NioClientConfig sockConf) {
		this.isStop = true;
		this.setClientName("SocketClient@" + this.hashCode());
		this.setSockConf(sockConf);
	}

	/**
	 * 构造函数
	 * @param threadName 客户端名称
	 * @param sockConf 客户端配置
	 */
	public NioSocketClient(String threadName, NioClientConfig sockConf) {
		this.isStop = true;
		this.setClientName(threadName);
		this.setSockConf(sockConf);
	}

	/**
	 * 构造函数
	 * @param threadName 客户端名称
	 */
	public void setClientName(String threadName) {
		this.clientName = threadName;
		this.setName(this.clientName);
	}

	/**
	 * 获取客户端名称
	 * @return 客户端名称
	 */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 * 获取客户端配置
	 * @return 客户端配置
	 */
	public NioClientConfig getSockConf() {
		return sockConf;
	}
	
	/**
	 * 配置客户端配置，并初始化日志打印器
	 * @param sockConf 客户端配置
	 */
	public void setSockConf(NioClientConfig sockConf) {
		this.sockConf = sockConf;
	}

	/**
	 * Socket客户端主线程核心
	 */
	@Override
	public void run() {
		isStop = false;
		
		try {
			//启动客户端
			startClient();
			
		} catch (ConnectException e) {
			log.error("Socket客户端[" + clientName + "] 连接到服务端失败," +
					"可能服务端未启动.程序退出.", e);
			
		} catch (IOException e) {
			log.error("Socket客户端[" + clientName + "] 与服务端连接的通讯" +
					"通道出现异常.程序退出.", e);
			
		} catch (Exception e) {
			log.error("Socket客户端[" + clientName + 
					"] 抛出未知异常.程序退出.", e);
			
		} finally {
			closeClient();	//关闭客户端
		}
	}

	/**
	 * 启动Socket客户端
	 * @throws Exception 
	 */
	private void startClient() throws Exception {
		selector = Selector.open();
		clientSocketChannel = SocketChannel.open();
		clientSocketChannel.configureBlocking(true);	//建立连接时要为阻塞模式
		
		String ip = sockConf.getIp();
		ip = (ip == null ? "127.0.0.1" : ip);
		clientSocketChannel.connect(
				new InetSocketAddress(ip, sockConf.getPort()));
		
		//后续操作为非阻塞模式
		clientSocketChannel.configureBlocking(false);
		
		//封装会话对象
		session = new Session(clientSocketChannel, sockConf);
		
		log.info("NIOSocket客户端 [" + clientName + "] 连接服务器成功.");
		log.info("连接的服务器IP:[" + session.getIp() + 
				"],服务器端口:[" + session.getPort() + "]");
		
		//触发会话创建事件
		sockConf.getFilterChain().onSessionCreated(session);
		
		//启动会话管理器，负责处理服务端返回的数据
		sessionManager = new SessionManager(session, sockConf);
		sessionManager.core(isStop);
	}
	
	/**
	 * <pre>
	 * 获取客户端会话。
	 * 
	 * 此方法会阻塞等待客户端连接到服务端，返回session对象为止。
	 * 但超时依然未返回则会返回null
	 * </pre>
	 * @return 客户端会话
	 */
	public ISession getSession() {
		ISession session = null;
		try {
			session = getSessionEx();
		} catch (SocketTimeoutException e) {
			log.error("Socket客户端 [" + clientName + 
					"] 获取连接超时.程序结束.", e);
		}
		return session;
	}
	
	/**
	 * <pre>
	 * 获取客户端会话。
	 * 
	 * 此方法会阻塞等待客户端连接到服务端，返回session对象为止。
	 * 但超时未返回则会抛出连接超时异常
	 * </pre>
	 * @return 客户端会话
	 * @throws SocketTimeoutException 连接超时
	 */
	public ISession getSessionEx() throws SocketTimeoutException {
		long bgnTime = System.currentTimeMillis();
		long curTime = 0;
		
		// 避免使用时start()后马上获取会话，因isStop状态未修改而返回null
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("线程休眠异常", e);
		}
		
		//不允许获取已停止的客户端会话
		if(this.isStop() == true) {
			return null;
		}
		
		//等待会话初始化
		while(session == null) {
			try {
				Thread.sleep(sockConf.getSleepTime());
				
				//会话超过依然未完成初始化，则认为初始化失败
				curTime = System.currentTimeMillis();
				if((curTime - bgnTime) > (sockConf.getOverTime())) {
					this.stopClient();	//超时未初始化会话成功，结束客户端主线程
					break;
				}
			} catch (InterruptedException e) {
				log.error("线程休眠异常", e);
			}
		}
		
		if(session == null) {
			this.stopClient();
			throw new SocketTimeoutException("Request Connect Timeout");
		}
		return session;
	}
	
	/**
	 * 关闭客户端
	 */
	private void closeClient() {
		try {
			this.setStop(true);
			
			//清理过滤链
			sockConf.getFilterChain().clean();
			
			//关闭事件选择器
			if (selector != null) {
				selector.close();
				selector = null;
			}

			if(session != null) {
				session.close();
				session = null;
			}
			
			log.info("关闭Socket客户端 [" + clientName + "] 成功.");
			
		} catch (ClosedSelectorException  e) {
 			log.error("Socket客户端 [" + clientName + 
 					"] 关闭事件选择器失败.", e);
 			
 		} catch (ClosedChannelException e) {
			log.error("Socket客户端 [" + clientName + 
					"] 关闭Socket通道失败.", e);
			
		} catch (NullPointerException e) {
			log.warn("Socket客户端[" + clientName + 
					"] 已经提交过关闭请求.无需再次关闭.请耐心等待程序结束.");
			
		} catch (IOException e) {
			log.error("关闭Socket客户端 [" + clientName + 
					"] 通讯通道时发生异常.程序结束.", e);
			
		} catch (Exception e) {
			log.error("关闭Socket客户端 [" + clientName + 
					"] 时出现未知异常.程序结束.", e);
			
		} finally {
			log.info("NioSocket客户端 [" + clientName + "] 已停止.");
		}
	}

	/**
	 * 检查主线程是否死亡
	 * @return 死亡标识
	 */
	public boolean isStop() {
		return isStop;
	}

	/**
	 * 设置主线程死亡标识
	 * @param isStop 死亡标识
	 */
	private void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	
	/**
	 * <pre>
	 * 停止客户端。
	 * 不推荐外部使用此方法关闭客户端。
	 * 建议通过session的关闭方法对客户端进行关闭。
	 * </pre>
	 */
	@Deprecated
	private void stopClient() {
		this.setStop(true);
	}
	
	/**
	 * 重载toString，返回主线程名称
	 * @return 主线程名称
	 */
	@Override
	public String toString() {
		return this.getName();
	}

}
