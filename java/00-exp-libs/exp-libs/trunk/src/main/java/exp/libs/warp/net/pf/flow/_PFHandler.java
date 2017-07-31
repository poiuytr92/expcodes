package exp.libs.warp.net.pf.flow;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.io.client.SocketClient;
import exp.libs.warp.net.socket.io.common.ISession;
import exp.libs.warp.net.socket.io.server.IHandler;

class _PFHandler implements IHandler {

	private Logger log = LoggerFactory.getLogger(_PFHandler.class);
	
	/** 端口转发代理服务配置 */
	private PFConfig config;
	
	private String ip;
	
	private int port;
	
	protected _PFHandler(PFConfig config) {
		this.config = config;
		this.ip = config.getRemoteIP();
		this.port = config.getRemotePort();
	}
	
	@Override
	public void _handle(ISession localClient) {
		SocketClient virtualClient = new SocketClient(ip, port);
		if(!virtualClient.conn()) {
			localClient.close();
			log.warn("会话 [{}] 连接到真实服务端口 [{}:{}] 失败", 
					localClient.ID(), ip, port);
			return;
		}
		
		// 转发本地真实客户端的IO流到虚拟客户端
		long overtime = config.getOvertime();
		Socket localSocket = localClient.getSocket();
		Socket virtualSocket = virtualClient.getSocket();
		new _TranslateData(localClient.ID(), _TranslateData.TYPE_REQUEST, 
				overtime, localSocket, virtualSocket).start();	// 请求转发
		new _TranslateData(localClient.ID(), _TranslateData.TYPE_RESPONE, 
				overtime, virtualSocket, localSocket).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _PFHandler(config);
	}

}
