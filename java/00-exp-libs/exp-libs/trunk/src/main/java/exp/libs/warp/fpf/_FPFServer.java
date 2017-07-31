package exp.libs.warp.fpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.server.SocketServer;

class _FPFServer {

	private Logger log = LoggerFactory.getLogger(_FPFServer.class);
	
	private final static String LOCAL_IP = "0.0.0.0";
	
	private _SRFileMgr srFileMgr;
	
	private FPFConfig config;
	
	private SocketBean localSockBean;
	
	/** 本地端口侦听服务 */
	private SocketServer listenServer;
	
	private FileMonitor fileMonitor;
	
	protected _FPFServer(_SRFileMgr srFileMgr, FPFConfig config) {
		this.srFileMgr = srFileMgr;
		this.config = config;
		
		// FIXME 需要开启多个监听服务， 或者添加上层服务集群
		this.localSockBean = new SocketBean(); {
			localSockBean.setIp(LOCAL_IP);
			localSockBean.setPort(config.getLocalListenPort());
			localSockBean.setOvertime(config.getOvertime());
			localSockBean.setMaxConnectionCount(config.getMaxConn());
		}
		
		_FPFSHandler ioPFHandler = new _FPFSHandler(srFileMgr, config);
		this.listenServer = new SocketServer(localSockBean, ioPFHandler);
		
		// FIXME 间隔时间
		_FileListener fileListener = new _FileListener(srFileMgr, 
				_FileListener.PREFIX_RECV, _FileListener.SUFFIX);
		this.fileMonitor = new FileMonitor(srFileMgr.getDir(), 100, fileListener);
	}
	
	protected boolean _start() {
		boolean isOk = listenServer._start();
		if(isOk == true) {
			fileMonitor._start();
			log.info("启动端口转发服务成功");
			log.info("本地侦听端口: [{}], 收发目录: [{}], 转发端口: [{}:{}]", 
					localSockBean.getPort(), srFileMgr.getDir(), 
					config.getRemoteIP(), config.getRemotePort());
		} else {
			log.warn("启动端口转发服务失败");
		}
		return isOk;
	}
	
	protected void _stop() {
		fileMonitor._stop();
		listenServer._stop();
		log.info("端口转发服务已停止");
	}
	
}
