package exp.libs.warp.fpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.io.listn.FileMonitor;
import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.server.SocketServer;

class _FPFServer {

	private Logger log = LoggerFactory.getLogger(_FPFServer.class);
	
	private final static String LOCAL_IP = "0.0.0.0";
	
	private FPFAgentConfig agentConf;
	
	private SocketBean localSockBean;
	
	/** 本地端口侦听服务 */
	private SocketServer listenServer;
	
	private FileMonitor fileMonitor;
	
	protected _FPFServer(FPFAgentConfig agentConf) {
		this.agentConf = agentConf;
		
		// FIXME 需要开启多个监听服务， 或者添加上层服务集群
		this.localSockBean = new SocketBean(); {
			localSockBean.setIp(LOCAL_IP);
			localSockBean.setPort(agentConf.getLocalListenPort());
			localSockBean.setOvertime(agentConf.getOvertime());
			localSockBean.setMaxConnectionCount(agentConf.getMaxConn());
		}
		
		_FPFSHandler ioPFHandler = new _FPFSHandler(agentConf);
		this.listenServer = new SocketServer(localSockBean, ioPFHandler);
		
		// FIXME 间隔时间
		_FileListener fileListener = new _FileListener(
				_FileListener.PREFIX_RECV, _FileListener.SUFFIX);
		this.fileMonitor = new FileMonitor(agentConf.getSrDir(), 100, fileListener);
	}
	
	protected boolean _start() {
		// TODO 先清空目录下所有数据流文件(转移到上层处理)
		FileUtils.delete(agentConf.getSrDir());
		FileUtils.createDir(agentConf.getSrDir());
		ThreadUtils.tSleep(1000);
		
		boolean isOk = listenServer._start();
		if(isOk == true) {
			fileMonitor._start();
			log.info("启动端口转发服务成功");
			log.info("本地侦听端口: [{}], 收发目录: [{}], 转发端口: [{}:{}]", 
					localSockBean.getPort(), agentConf.getSrDir(), 
					agentConf.getRemoteIP(), agentConf.getRemotePort());
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
