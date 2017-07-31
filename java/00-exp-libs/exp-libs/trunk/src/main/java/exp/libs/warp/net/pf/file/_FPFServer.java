package exp.libs.warp.net.pf.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.server.SocketServer;

/**
 * <pre>
 * [端口转发代理服务-S] (请求发送器/响应接收器).
 * 	1. 服务代理器: 为[本侧应用程序]提供[虚拟服务端口].
 * 	2. 请求发送器: 把[本侧应用程序]的请求[发送]到[对侧真正的服务端口].
 * 	3. 响应接收器: 把[对侧真正的服务端口]返回的响应数据[回传]到[本侧应用程序].
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFServer {

	private Logger log = LoggerFactory.getLogger(_FPFServer.class);
	
	private final static String LOCAL_IP = "0.0.0.0";
	
	/** 端口转发代理服务配置 */
	private FPFConfig config;
	
	/** 本地端口侦听服务 */
	private SocketServer listenServer;
	
	/** 收发目录文件监听器 */
	private FileMonitor srFileMonitor;
	
	protected _FPFServer(_SRFileMgr srFileMgr, int overtime, FPFConfig config) {
		this.config = config;
		
		// 设置Socket端口监听服务
		SocketBean localSockBean = new SocketBean(); {
			localSockBean.setIp(LOCAL_IP);
			localSockBean.setPort(config.getLocalListenPort());
			localSockBean.setOvertime(overtime);
			localSockBean.setMaxConnectionCount(config.getMaxConn());
		}
		_FPFSHandler ioPFHandler = new _FPFSHandler(srFileMgr, config);
		this.listenServer = new SocketServer(localSockBean, ioPFHandler);
		
		// 设置收发文件目录监听器(只监听 recv 文件)
		_SRFileListener fileListener = new _SRFileListener(srFileMgr, 
				_Envm.PREFIX_RECV, _Envm.SUFFIX);
		this.srFileMonitor = new FileMonitor(srFileMgr.getDir(), 
				_Envm.SCAN_FILE_INTERVAL, fileListener);
	}
	
	protected boolean _start() {
		boolean isOk = listenServer._start();
		if(isOk == true) {
			srFileMonitor._start();
			log.info("端口转发服务 [{}] 启动成功: 本地侦听端口 [{}], 转发端口: [{}:{}]",
					config.getServerName(), config.getLocalListenPort(), 
					config.getRemoteIP(), config.getRemotePort());
			
		} else {
			log.warn("端口转发服务 [{}] 启动失败", config.getServerName());
		}
		return isOk;
	}
	
	protected void _stop() {
		srFileMonitor._stop();
		listenServer._stop();
		log.info("端口转发服务 [{}] 已停止", config.getServerName());
	}
	
}
