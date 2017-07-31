package exp.libs.warp.net.pf.file;

import java.net.Socket;

import exp.libs.warp.net.socket.io.common.ISession;
import exp.libs.warp.net.socket.io.server.IHandler;

class _FPFSHandler implements IHandler {

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
	public void _handle(ISession localClient) {
		Socket localSocket = localClient.getSocket();
		new _TranslateSData(localClient.ID(), _TranslateSData.PREFIX_SEND,  
				localSocket, config, srFileMgr).start();	// 请求转发
		new _TranslateSData(localClient.ID(), _TranslateSData.PREFIX_RECV, 
				localSocket, config, srFileMgr).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _FPFSHandler(srFileMgr, config);
	}

}
