package exp.libs.warp.net.socket.io.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.client.SocketClient;
import exp.libs.warp.thread.ThreadPool;

public class SocketServer extends Thread {

	/** 日志器 */
	private Logger log = LoggerFactory.getLogger(SocketServer.class);
	
	/** Socket配置信息 */
	private SocketBean socketBean;
	
	/** Socket服务端 */
	private ServerSocket socketServer;
	
	/** Socket会话队列 */
	private List<_SocketClientProxy> clientProxys;
	
	/** Socket会话线程池 */
	private ThreadPool tp;
	
	/** 运行标识 */
	private boolean running;
	
	/** 业务处理器 */
	private IHandler sHandler;
	
	/**
	 * 构造函数
	 * @param socketBean socket配置信息
	 */
	public SocketServer(SocketBean socketBean, IHandler handler) {
		this.socketBean = (socketBean == null ? new SocketBean() : socketBean);
		this.sHandler = (handler == null ? new _DefaultHandler() : handler);
		this.clientProxys = new LinkedList<_SocketClientProxy>();
		this.running = false;
	}
	
	/**
	 * 初始化服务端
	 * @param listenAllIP 是否侦听所有IP上的同一端口(适用于多网卡)
	 */
	private boolean init(boolean listenAllIP) {
		boolean isOk = true;
		try {
			socketServer = new ServerSocket(socketBean.getPort());
			if(listenAllIP == false) {
				socketServer.bind(new InetSocketAddress(
						socketBean.getIp(), socketBean.getPort()));
			}
			tp = new ThreadPool(this.socketBean.getMaxConnectionCount());
			
		} catch (Exception e) {
			isOk = false;
			log.error("无法启动Socket服务: 侦听 {}{} 端口失败.", 
					(listenAllIP ? "" : socketBean.getIp().concat(" 上的 ")), 
					socketBean.getPort(), e);
		}
		return isOk;
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
	
	/**
	 * 停止服务端
	 */
	public void _stop() {
		if(running == false || socketServer == null) {
			return;
		}
		running = false;
		
		// 利用虚拟连接跳出accept阻塞循环
		SocketClient client = new SocketClient(socketBean);
		client.conn();
		client.close();
	}
	
	@Override
	public void run() {
		log.info("{}", socketBean.toString());
		log.info("Socket服务已启动");
		
		running = true;
		do {
			_SocketClientProxy clientProxy = listen();
			if(clientProxy != null) {
				boolean isOver = checkOverLimit();
				if(isOver == true) {
					clientProxy.close();
					
				} else {
					clientProxys.add(clientProxy);
					tp.execute(clientProxy);
				}
				
				log.info("新增Socket会话 [{}] {}, 当前会话数: {}/{}", clientProxy.ID(), 
						(isOver ? "失败" : ""), clientProxys.size(), 
						socketBean.getMaxConnectionCount());
			}
		} while(running == true);
		
		clear();
		log.info("Socket服务已停止");
	}
	
	private _SocketClientProxy listen() {
		_SocketClientProxy clientProxy = null;
		if(running == true) {
			try {
				Socket client = socketServer.accept();
				
				IHandler cHandler = sHandler._clone();
				clientProxy = new _SocketClientProxy(socketBean, client, 
						(cHandler == null ? sHandler : cHandler));
				
			} catch (Exception e) {
				log.error("添加一个新的Socket连接请求失败", e);
			}
		}
		
		if(running == false && clientProxy != null) {
			clientProxy.close();
			clientProxy = null;
		}
		return clientProxy;
	}
	
	/**
	 * 检查再增加一个会话是否会导致会话数越限
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
		return (clientProxys.size() >= socketBean.getMaxConnectionCount());
	}
	
	/**
	 * 强制关闭所有会话和线程池
	 */
	private void clear() {
		Iterator<_SocketClientProxy> clients = clientProxys.iterator();
		while(clients.hasNext()) {
			_SocketClientProxy client = clients.next();
			client.close();
		}
		clientProxys.clear();
		tp.shutdown();
	}
	
}
