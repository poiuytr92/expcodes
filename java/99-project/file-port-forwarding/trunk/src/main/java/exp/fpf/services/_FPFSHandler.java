package exp.fpf.services;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.fpf.envm.Param;
import exp.libs.warp.net.sock.io.common.IHandler;
import exp.libs.warp.net.sock.io.common.ISession;

/**
 * <pre>
 * [端口转发代理服务-S] 业务处理器
 * </pre>	
 * <B>PROJECT : </B> file-port-forwarding
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFSHandler implements IHandler {

	private Logger log = LoggerFactory.getLogger(_FPFSHandler.class);
	
	private SRMgr srMgr;
	
	private FPFConfig config;
	
	/**
	 * 
	 * @param srDir	文件收发目录
	 * @param remoteIP
	 * @param remotePort
	 */
	protected _FPFSHandler(SRMgr srMgr, FPFConfig config) {
		this.srMgr = srMgr;
		this.config = config;
	}
	
	@Override
	public void _handle(ISession session) {
		long overtime = session.getSocketBean().getOvertime();
		Socket socket = session.getSocket();
		log.info("新增一个到转发端口 [{}-{}:{}] 的会�? [{}], 本地socket�? [{}:{}]", 
				session.getSocketBean().getPort(), 
				config.getRemoteIP(), config.getRemotePort(), session.ID(), 
				socket.getInetAddress().getHostAddress(), socket.getPort());
		
		new _TranslateSData(srMgr, config, session.ID(), 
				Param.PREFIX_SEND, overtime, socket).start();	// 请求转发
		new _TranslateSData(srMgr, config, session.ID(), 
				Param.PREFIX_RECV, overtime, socket).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _FPFSHandler(srMgr, config);
	}

	@Deprecated
	@Override
	public boolean _login(ISession arg0) {
		return true;
	}
	
}
