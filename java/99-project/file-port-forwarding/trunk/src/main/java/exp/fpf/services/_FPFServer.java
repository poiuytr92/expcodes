package exp.fpf.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.libs.warp.net.sock.bean.SocketBean;
import exp.libs.warp.net.sock.io.server.SocketServer;

/**
 * <pre>
 * [端口转发代理服务-S] (请求发送器/响应接收器).
 * 	1. 服务代理器: 为[本侧应用程序]提供[虚拟服务端口].
 * 	2. 请求发送器: 把[本侧应用程序]的请求[发送]到[对侧真正的服务端口].
 * 	3. 响应接收器: 把[对侧真正的服务端口]返回的响应数据[回传]到[本侧应用程序].
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
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
	
	protected _FPFServer(SRMgr srMgr, FPFConfig config) {
		this.config = config;
		
		// 设置Socket端口监听服务
		SocketBean localSockBean = new SocketBean(); {
			localSockBean.setId(config.getServerName());
			localSockBean.setIp(LOCAL_IP);
			localSockBean.setPort(config.getLocalListenPort());
			localSockBean.setOvertime(config.getOvertime());
			localSockBean.setBufferSize(config.getBuffSize(), SocketBean.BUFF_SIZE_UNIT_KB);
			localSockBean.setMaxConnectionCount(config.getMaxConn());
		}
		_FPFSHandler fpfHandler = new _FPFSHandler(srMgr, config);
		this.listenServer = new SocketServer(localSockBean, fpfHandler);
	}
	
	protected boolean _start() {
		boolean isOk = listenServer._start();
		if(isOk == true) {
			log.info("端口转发服务 [{}] 启动成功: 本地侦听端口 [{}], 转发端口: [{}:{}]",
					config.getServerName(), config.getLocalListenPort(), 
					config.getRemoteIP(), config.getRemotePort());
			
		} else {
			log.warn("端口转发服务 [{}] 启动失败", config.getServerName());
		}
		return isOk;
	}
	
	protected void _stop() {
		listenServer._stop();
		log.info("端口转发服务 [{}] 已停�?", config.getServerName());
	}
	
}
