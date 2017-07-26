package exp.libs.warp.net.pf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.server.SocketServer;

public class PFServer {
	
	private Logger log = LoggerFactory.getLogger(PFServer.class);

	private final static int DEFAULT_OVERTIME = 10000;
	
	private final static int DEFAULT_MAX_PF_CONN = 100;
	
	private SocketBean localSockBean;
	
	private SocketBean remoteSockBean;
	
	/** 本地端口侦听服务 */
	private SocketServer listenServer;
	
	/**
	 * 
	 * @param localListenPort 本地侦听端口（转发端口）
	 * @param remoteIP 远程IP
	 * @param remotePort 远程服务端口（被转发端口）
	 */
	public PFServer(int localListenPort, String remoteIP, int remotePort) {
		this(localListenPort, remoteIP, remotePort, 
				DEFAULT_OVERTIME, DEFAULT_MAX_PF_CONN);
	}
	
	/**
	 * 
	 * @param localListenPort 本地侦听端口（转发端口）
	 * @param remoteIP 远程IP
	 * @param remotePort 远程服务端口（被转发端口）
	 * @param overtime 超时断连(单位ms, <=0 表示永不超时, 默认10秒)
	 * @param maxConn 最大的转发连接数(默认100)
	 */
	public PFServer(int localListenPort, String remoteIP, 
			int remotePort, int overtime, int maxPFConn) {
		this.localSockBean = new SocketBean(); {
			localSockBean.setPort(localListenPort);
			localSockBean.setOvertime(
					overtime <= 0 ? DEFAULT_OVERTIME : overtime);
			localSockBean.setMaxConnectionCount(
					maxPFConn <= 0 ? DEFAULT_MAX_PF_CONN : maxPFConn);
		}
		
		this.remoteSockBean = new SocketBean(); {
			remoteSockBean.setIp(remoteIP);
			remoteSockBean.setPort(remotePort);
		}
		
		_PFHandler pfHandler = new _PFHandler(remoteSockBean);
		this.listenServer = new SocketServer(localSockBean, pfHandler);
	}
	
	public boolean _start() {
		boolean isOk = listenServer._start();
		if(isOk == true) {
			log.info("启动端口转发服务成功");
			log.info("本地侦听端口: [{}], 转发端口: [{}]", localSockBean.getPort(), 
					remoteSockBean.getSocket());
		} else {
			log.warn("启动端口转发服务失败");
		}
		return isOk;
	}
	
	public void _stop() {
		listenServer._stop();
		log.info("端口转发服务已停止");
	}
	
}
