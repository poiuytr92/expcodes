package exp.libs.warp.fpf;

import java.net.Socket;

import exp.libs.warp.net.socket.io.common.ISession;
import exp.libs.warp.net.socket.io.server.IHandler;

class _FPFSHandler implements IHandler {

	private FPFAgentConfig agentConf;
	
	/**
	 * 
	 * @param srDir	文件收发目录
	 * @param remoteIP
	 * @param remotePort
	 */
	protected _FPFSHandler(FPFAgentConfig agentConf) {
		this.agentConf = agentConf;
	}
	
	@Override
	public void _handle(ISession localClient) {
		Socket localSocket = localClient.getSocket();
		new _TranslateSData(localClient.ID(), _TranslateSData.PREFIX_SEND,  
				localSocket, agentConf).start();	// 请求转发
		new _TranslateSData(localClient.ID(), _TranslateSData.PREFIX_RECV, 
				localSocket, agentConf).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _FPFSHandler(agentConf);
	}

}
