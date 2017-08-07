package exp.libs.warp.net.sock.nio.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.sock.nio.common.envm.Protocol;
import exp.libs.warp.net.sock.nio.common.envm.Times;

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

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(NioSocketServer.class);
	
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
	private SessionMgr sessionMgr = null;

	private boolean running;

	/**
	 * 构造函数
	 * @param sockConf 服务器配置
	 */
	public NioSocketServer(NioServerConfig sockConf) {
		this.running = false;
		this.sockConf = (sockConf == null ? NioServerConfig.DEFAULT : sockConf);
		this.setName(sockConf.getAlias());
	}

	/**
	 * 获取服务器配置
	 * @return 服务器配置
	 */
	public NioServerConfig getSockConf() {
		return sockConf;
	}

	
	public boolean _start() {
		return _start(true);
	}
	
	/**
	 * 启动服务端
	 * @param listenAllIP 是否侦听所有IP上的同一端口(适用于多网卡)
	 */
	public boolean _start(boolean listenAllIP) {
		boolean isOk = false;
		if(init(listenAllIP)) {
			this.start();
			isOk = true;
		}
		return isOk;
	}
	
	private boolean init(boolean listenAllIP) {
		boolean isOk = true;
		InetSocketAddress socket = (listenAllIP ? 
				new InetSocketAddress(sockConf.getPort()) : 
				new InetSocketAddress(sockConf.getIp(), sockConf.getPort()));
		
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().setReceiveBufferSize(sockConf.getReadBufferSize());
			serverSocketChannel.socket().bind(socket);
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.info("Socket服务 [{}] 侦听 {}{} 端口成功.", getName(), 
					(listenAllIP ? "" : sockConf.getIp().concat(" 上的 ")), 
					sockConf.getPort());
			
		} catch (Exception e) {
			isOk = false;
			log.error("无法启动Socket服务 [{}] : 侦听 {}{} 端口失败.", getName(), 
					(listenAllIP ? "" : sockConf.getIp().concat(" 上的 ")), 
					sockConf.getPort(), e);
		}
		return isOk;
	}

	/**
	 * Socket服务器主线程核心
	 */
	@Override
	public void run() {
		log.debug(sockConf.toString());
		log.info("Socket服务 [{}] 已启动", getName());
		
		// 启动会话管理线程
		sessionMgr = new SessionMgr(sockConf);
		new Thread(sessionMgr, "SocketSessionMgr").start();
		
		// 启动会话监听服务
		running = true;
		long lastHbTime = System.currentTimeMillis();
		do {
			listen();	
			
			//打印心跳
			long curTime = System.currentTimeMillis();
            if(curTime - lastHbTime >= Times.HEART_BEAT) {
            	log.debug("Socket服务 [{}] 当前活动会话数: [{}].", 
            			getName(), sessionMgr.getSessionCnt());
                lastHbTime = curTime;
            }
		} while (running && sessionMgr.isRunning());
		
		clear();
		log.info("Socket服务 [{}] 已停止", getName());
	}

	/**
	 * 启动Socket服务器
	 * @throws Exception 异常
	 */
	private void listen() {
		try {
			int eventNum = selector.select(Times.BLOCK);
			if (eventNum > 0) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				for (SelectionKey key : selectionKeys) {
					handleSelectionKey(key);
				}
				selectionKeys.clear();
			}
		} catch (Exception e) {
			log.error("Socket服务 [{}] 添加一个新的连接请求失败", getName(), e);
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

			// 新的连接请求
			Session clientSession = new Session(clientChannel, sockConf);
			if(!sessionMgr.addSession(clientSession)) {
				clientSession.writeErrMsg(Protocol.CONN_LIMIT);
				clientSession.close();
				log.warn("Socket服务 [{}] 连接数越限, 已拒绝新连接请求.", getName());
				
			} else {
				log.debug("Socket服务 [{}] 新增会话 [{}] {}, 当前活动会话数: [{}]", 
						getName(), clientSession, sessionMgr.getSessionCnt());
			}
		}
	}

	public void _stop() {
		this.running = false;
	}
	
	public void clear() {
		sessionMgr._stop();
		sockConf.clearFilters();
		
		try {
			if (selector != null) {
				selector.close();
				selector = null;
			}

			if (serverSocketChannel != null) {
				serverSocketChannel.close();
				serverSocketChannel = null;
			}
		} catch (Exception e) {
			log.error("停止Socket服务 [{}] 异常.", getName(), e);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * 重载toString，返回主线程名称
	 * @return 主线程名称
	 */
	@Override
	public String toString() {
		return sockConf.toString();
	}

}
