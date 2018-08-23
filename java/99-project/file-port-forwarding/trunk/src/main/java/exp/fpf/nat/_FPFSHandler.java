package exp.fpf.nat;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.fpf.bean.FPFConfig;
import exp.fpf.cache.SRMgr;
import exp.fpf.cache.SessionMgr;
import exp.fpf.envm.Param;
import exp.libs.utils.other.BoolUtils;
import exp.libs.warp.net.sock.io.common.IHandler;
import exp.libs.warp.net.sock.io.common.ISession;

/**
 * <pre>
 * [端口转发代理服务-S] 业务处理器
 * </pre>	
 * <br/><B>PROJECT : </B> file-port-forwarding
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFSHandler implements IHandler {

	private Logger log = LoggerFactory.getLogger(_FPFSHandler.class);
	
	private SRMgr srMgr;
	
	private FPFConfig config;
	
	/**
	 * 
	 * @param srMgr 文件收发目录
	 * @param config
	 */
	protected _FPFSHandler(SRMgr srMgr, FPFConfig config) {
		this.srMgr = srMgr;
		this.config = config;
	}
	
	@Override
	public boolean _login(ISession session) {
		
		// 每次新增会话时交互有50%的几率清除无效会话（目的是惰性删除，避免过份耗时）
		if(BoolUtils.hit(50)) {
			SessionMgr.getInstn().delInvaildSession();
		}
		
		SessionMgr.getInstn().add(session);
		return true;
	}
	
	@Override
	public void _handle(ISession session) {
		long overtime = session.getSocketBean().getOvertime();
		Socket socket = session.getSocket();
		log.info("新增一个到转发端口 [{}-{}:{}] 的会话 [{}], 本地socket为 [{}:{}]", 
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

}
