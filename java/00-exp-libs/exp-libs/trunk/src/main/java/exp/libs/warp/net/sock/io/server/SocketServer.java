package exp.libs.warp.net.sock.io.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.io.client.SocketClient;
import exp.libs.warp.net.sock.io.common.IHandler;
import exp.libs.warp.net.sock.io.common.ISession;
import exp.libs.warp.thread.ThreadPool;

/**
 * <pre>
 * Socket服务端(阻塞模式)
 * 
 * 使用示例:
 * 	SocketBean sockConf = new SocketBean(SERVER_IP, SERVER_PORT);
 * 	ServerHandler handler = new ServerHandler();	// 实现IHandler接口（注意包路径为io）
 * 	SocketServer server = new SocketServer(sockConf, handler);
 * 	server._start();
 * 	// ...
 * 	server._stop();
 * 
 * </pre>	
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SocketServer extends Thread {

	/** 日志�? */
	private Logger log = LoggerFactory.getLogger(SocketServer.class);
	
	/** Socket配置信息 */
	private SocketBean sockConf;
	
	/** Socket服务�? */
	private ServerSocket socketServer;
	
	/** 未登陆客户端的Socket会话注册线程�? */
	private ThreadPool loginPool;
	
	/** 已登陆客户端的Socket会话执行线程�? */
	private ThreadPool execPool;
	
	/** 已登陆客户端的Socket会话队列 */
	private List<_SocketClientProxy> clientProxys;
	
	/** 运行标识 */
	private boolean running;
	
	/** 业务处理�? */
	private IHandler sHandler;
	
	/**
	 * 构造函�?
	 * @param sockConf socket配置信息
	 */
	public SocketServer(SocketBean sockConf, IHandler handler) {
		this.sockConf = (sockConf == null ? new SocketBean() : sockConf);
		this.sHandler = (handler == null ? new _DefaultHandler() : handler);
		this.clientProxys = new LinkedList<_SocketClientProxy>();
		this.loginPool = new ThreadPool(this.sockConf.getMaxConnectionCount());
		this.execPool = new ThreadPool(this.sockConf.getMaxConnectionCount());
		this.running = false;
		this.setName(this.sockConf.getAlias());
	}
	
	/**
	 * 初始化服务端
	 * @param listenAllIP 是否侦听所有IP上的同一端口(适用于多网卡)
	 */
	private boolean init(boolean listenAllIP) {
		boolean isOk = true;
		InetSocketAddress socket = (listenAllIP ? 
				new InetSocketAddress(sockConf.getPort()) : 
				new InetSocketAddress(sockConf.getIp(), sockConf.getPort()));
		
		try {
			socketServer = new ServerSocket();
			socketServer.bind(socket);
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
	
	@Deprecated
	@Override
	public synchronized void start() {
		if(init(true)) {
			super.start();
		}
	}
	
	/**
	 * 启动服务�?(默认侦听所有IP上的同一端口)
	 * @return true:启动成功; false:启动失败
	 */
	public boolean _start() {
		return _start(true);
	}
	
	/**
	 * 启动服务�?
	 * @param listenAllIP 是否侦听所有IP上的同一端口(适用于多网卡)
	 * @return true:启动成功; false:启动失败
	 */
	public boolean _start(boolean listenAllIP) {
		boolean isOk = false;
		if(init(listenAllIP)) {
			super.start();
			isOk = true;
		}
		return isOk;
	}
	
	/**
	 * 停止服务�?
	 */
	public void _stop() {
		if(running == false || socketServer == null) {
			return;
		}
		running = false;
		
		// 利用虚拟连接跳出accept阻塞循环
		SocketClient client = new SocketClient(sockConf);
		client.conn();
		client.close();
	}
	
	@Override
	public void run() {
		log.debug(sockConf.toString());
		log.info("Socket服务 [{}] 已启�?", getName());
		
		running = true;
		do {
			_SocketClientProxy clientProxy = listen();
			if(clientProxy != null) {
				boolean isOver = checkOverLimit();
				if(isOver == true) {
//					clientProxy.write("[ERROR] CONNECTION LIMIT");
					clientProxy.close();
					log.debug("Socket服务 [{}] 注册新会话失�?, 连接数已达上�?: [{}]", 
							getName(), sockConf.getMaxConnectionCount());
					
				} else {
					_ReserveThread reserve = new _ReserveThread(getName(), 
							execPool, clientProxys, clientProxy);
					loginPool.execute(reserve);
				}
			}
		} while(running == true);
		
		clear();
		log.info("Socket服务 [{}] 已停�?", getName());
	}
	
	private _SocketClientProxy listen() {
		_SocketClientProxy clientProxy = null;
		if(running == true) {
			try {
				Socket client = socketServer.accept();
				client.setSoTimeout(sockConf.getOvertime());
				
				IHandler cHandler = sHandler._clone();
				clientProxy = new _SocketClientProxy(sockConf, client, 
						(cHandler == null ? sHandler : cHandler));
				
			} catch (Exception e) {
				log.error("Socket服务 [{}] 添加一个新的连接请求失�?", getName(), e);
			}
		}
		
		if(running == false && clientProxy != null) {
			clientProxy.close();
			clientProxy = null;
		}
		return clientProxy;
	}
	
	/**
	 * 检查再增加一个会话是否会导致会话数越�?
	 * @return
	 */
	private boolean checkOverLimit() {
		Iterator<_SocketClientProxy> clients = clientProxys.iterator();
		while(clients.hasNext()) {
			_SocketClientProxy client = clients.next();
			if(client.isClosed()) {
				clients.remove();
			}
		}
		return (getClientSize() >= sockConf.getMaxConnectionCount());
	}
	
	/**
	 * <PRE>
	 * 获取当下这一个时间点所有连接到服务端的客户端数�?.
	 * 	(可能存在部分客户端连接已失效)
	 * <PRE>
	 * @return 客户端数�?
	 */
	public int getClientSize() {
		return clientProxys.size();
	}
	
	/**
	 * 获取当下这一个时间点所有连接到服务端的客户端会�?.
	 * @return 客户端会话集
	 */
	public Iterator<ISession> getClients() {
		List<ISession> sessions = new LinkedList<ISession>();
		Iterator<_SocketClientProxy> clients = clientProxys.iterator();
		while(clients.hasNext()) {
			_SocketClientProxy client = clients.next();
			if(client.isVaild() && !client.isClosed()) {
				sessions.add(client);
			} else {
				clients.remove();
			}
		}
		return sessions.iterator();
	}
	
	/**
	 * 强制关闭所有会话和线程�?
	 */
	private void clear() {
		Iterator<_SocketClientProxy> clients = clientProxys.iterator();
		while(clients.hasNext()) {
			_SocketClientProxy client = clients.next();
			client.close();
		}
		clientProxys.clear();
		
		loginPool.shutdown();
		execPool.shutdown();
		
		try {
			socketServer.close();
		} catch (IOException e) {}
	}
	
	/**
	 * 测试socket服务是否在运�?
	 * @return true:运行�?; false:已停�?
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * 返回socket配置信息
	 * @return socket配置信息
	 */
	@Override
	public String toString() {
		return sockConf.toString();
	}
	
}
