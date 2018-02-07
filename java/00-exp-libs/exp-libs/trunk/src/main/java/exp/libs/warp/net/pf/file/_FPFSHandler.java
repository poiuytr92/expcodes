package exp.libs.warp.net.pf.file;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.sock.io.common.ISession;
import exp.libs.warp.net.sock.io.server.IHandler;

/**
 * <pre>
 * [端口转发代理服务-S] 业务处理器
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-07-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _FPFSHandler implements IHandler {

	private Logger log = LoggerFactory.getLogger(_FPFSHandler.class);
	
	private _SRFileMgr srFileMgr;
	
	private FPFConfig config;
	
	/**
	 * 
	 * @param srDir	文件收发目录
	 * @param remoteIP
	 * @param remotePort
	 */
	protected _FPFSHandler(_SRFileMgr srFileMgr, FPFConfig config) {
		this.srFileMgr = srFileMgr;
		this.config = config;
	}
	
	@Override
	public void _handle(ISession session) {
		long overtime = session.getSocketBean().getOvertime();
		Socket socket = session.getSocket();
		log.info("新增一个到转发端口 [{}-{}:{}] 的会话 [{}], 本地socket为 [{}:{}]", 
				session.getSocketBean().getPort(), 
				config.getRemoteIP(), config.getRemotePort(), session.ID(), 
				socket.getInetAddress().getHostAddress(), socket.getPort());
		
		new _TranslateSData(srFileMgr, config, session.ID(), 
				_Envm.PREFIX_SEND, overtime, socket).start();	// 请求转发
		new _TranslateSData(srFileMgr, config, session.ID(), 
				_Envm.PREFIX_RECV, overtime, socket).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _FPFSHandler(srFileMgr, config);
	}

	@Deprecated
	@Override
	public boolean _login(ISession session) {
		return true;
	}
	
}
