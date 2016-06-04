package exp.libs.warp.net.socket.nio.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.nio.common.envm.Protocol;

/**
 * <pre>
 * NIOSocket服务端
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NioSocketServer extends Thread {

	/**
	 * 日志器
	 */
	private static Logger log = LoggerFactory.getLogger(NioSocketServer.class);
	
	/**
	 * 服务器名称
	 */
	private String serverName;

	/**
	 * 事件选择器
	 */
	private Selector selector = null;

	/**
	 * Socket通讯通道
	 */
	private ServerSocketChannel serverSocketChannel = null;

	/**
	 * Socket配置
	 */
	private NioServerConfig sockConf = null;

	/**
	 * 会话管理线程
	 */
	private SessionManager sessionManager = null;

	/**
	 * 服务器线程是否死亡
	 */
	private boolean isStop;

	/**
	 * 构造函数
	 * @param sockConf 服务器配置
	 */
	public NioSocketServer(NioServerConfig sockConf) {
		this.isStop = true;
		this.setServerName("SocketServer@" + this.hashCode());
		this.setSockConf(sockConf);
	}

	/**
	 * 构造函数
	 * @param threadName 服务器名称
	 * @param sockConf 服务器配置
	 */
	public NioSocketServer(String threadName, NioServerConfig sockConf) {
		this.isStop = true;
		this.setServerName(threadName);
		this.setSockConf(sockConf);
	}

	/**
	 * 构造函数
	 * @param threadName 服务器名称
	 */
	public void setServerName(String threadName) {
		this.serverName = threadName;
		this.setName(this.serverName);
	}

	/**
	 * 获取服务端名称
	 * @return 服务端名称
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 获取服务器配置
	 * @return 服务器配置
	 */
	public NioServerConfig getSockConf() {
		return sockConf;
	}
	
	/**
	 * 配置服务器配置，并初始化日志打印器
	 * @param sockConf 服务器配置
	 */
	public void setSockConf(NioServerConfig sockConf) {
		this.sockConf = sockConf;
	}

	/**
	 * Socket服务器主线程核心
	 */
	@Override
	public void run() {
		isStop = false;
		log.info(sockConf.toString());
		
		sessionManager = new SessionManager(sockConf);
		new Thread(sessionManager, "SocketSessionManager").start();
		
		try {
			//启动服务器
			startServer();
			
		} catch (UnknownHostException e) {
			log.error("Socket服务器 [" + serverName + 
					"] 获取主机信息失败,请检查网络环境是否正常.", 
					e);
			
		} catch (CancelledKeyException  e) {
			log.error("Socket服务器 [" + serverName + 
					"] 取消事件键失败.", e);
			
		} catch (ClosedChannelException e) {
			log.error("Socket服务器 [" + serverName + 
					"] 注册selector失败.", e);
			
		} catch (BindException e) {
			log.error("Socket服务器 [" + serverName + 
					"] 绑定IP或端口失败，请检查是否被占用.", 
					e);
			
		} catch (IOException e) {
			log.error("Socket服务器 [" + serverName + 
					"] I/O操作异常.", e);
			
		} catch (Exception e) {
			log.error("Socket服务器 [" + serverName + 
					"] 抛出未知异常.", e);
			
		} finally {
			closeServer();	//关闭服务器
		}
	}

	/**
	 * 启动Socket服务器
	 * @throws Exception 异常
	 */
	private void startServer() throws Exception {
		selector = Selector.open();

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReceiveBufferSize(
				sockConf.getReadBuffSize());
		
		if(sockConf.getIp() == null || 
				"".equals(sockConf.getIp())) {
			serverSocketChannel.socket().bind(
					new InetSocketAddress(sockConf.getPort()));
		}
		else {
			serverSocketChannel.socket().bind(
					new InetSocketAddress(sockConf.getIp(), 
							sockConf.getPort()));
		}
		
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		log.info("创建NioSocket服务器 [" + serverName + "] 成功.");
		log.info("服务IP:[" + InetAddress.getLocalHost().getHostAddress() + 
				"],服务端口:[" + sockConf.getPort() + "]");
		log.info("等待客户端的连接请求到来...");

		long lastHbTime = 0;
		while (isStop == false) {
			
			//若会话管理器线程死亡，则主线程也退出
			if(sessionManager.isStop() == true) {
				isStop = true;
				break;
			}
			
			int eventNum = selector.select(sockConf.getBlockTime());

			if (eventNum > 0) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();

				for (SelectionKey key : selectionKeys) {
					handleSelectionKey(key); // 处理 Select事件
				}

				// 清除 Select事件
				selectionKeys.clear();
				continue;
			}
			
			//打印心跳
			long currentTime = System.currentTimeMillis();
            if(currentTime - lastHbTime >= sockConf.getHbTime()) {
            	log.debug("[HeartBeat] Socket服务器 [" + serverName + 
            			"] 当前活动会话数: [" + sessionManager.getSessionCnt() + 
            			"].");
                lastHbTime = currentTime;
            }
		}
	}

	/**
	 * 处理关注事件Acceptable
	 * @param sk 事件键
	 * @throws Exception
	 */
	private void handleSelectionKey(SelectionKey sk) throws Exception {
		ServerSocketChannel serverChannel = null;
		SocketChannel clientChannel = null;

		if (sk.channel().equals(serverSocketChannel) && sk.isAcceptable()) {
			serverChannel = (ServerSocketChannel) sk.channel();
			clientChannel = serverChannel.accept();

			log.info("有新的客户端请求连接到Socket服务器 [" + serverName + "] .");

			Session clientSession = new Session(clientChannel, sockConf);
			if(false == sessionManager.addSession(clientSession)) {
				
				log.info("拒绝新客户端连接请求,已达到服务器连接上限.");
				
				clientSession.writeErrMsg(Protocol.CONN_LIMIT);
				clientSession.close();
			}
		}
	}

	/**
	 * 关闭服务器
	 */
	private void closeServer() {

		//设置主线程已停止标识
		isStop = true;
				
		//诱导会话管理线程自杀
		sessionManager.setStop(true);
		
		try {
			
			//清理过滤链
			sockConf.getFilterChain().clean();
			
			//关闭事件选择器
			if (selector != null) {
				selector.close();
				selector = null;
			}

			//关闭服务通道
			if (serverSocketChannel != null) {
				serverSocketChannel.close();
				serverSocketChannel = null;
			}
			
			log.info("关闭Socket服务器 [" + serverName + "] 成功.");
			
		} catch (ClosedSelectorException  e) {
			log.error("Socket服务器 [" + serverName + 
 					"] 关闭事件选择器失败.", e);
 			
 		} catch (ClosedChannelException e) {
			log.error("Socket服务器 [" + serverName + 
					"] 关闭Socket通道失败.", e);
			
		} catch (NullPointerException e) {
			log.warn("Socket服务端[" + serverName + 
					"] 已经提交过关闭请求.无需再次关闭.请耐心等待程序结束.");
			
		} catch (IOException e) {
			log.error("关闭Socket服务器 [" + serverName + 
					"] 通讯通道时发生异常.程序结束.", e);
			
		} catch (Exception e) {
			log.error("关闭Socket服务器 [" + serverName + 
					"] 时出现未知异常.程序结束.", e);
		} finally {
			log.info("NIOSocket服务器 [" + serverName + 
					"]  停止.");
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
	 * 停止服务器
	 */
	public void stopServer() {
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
