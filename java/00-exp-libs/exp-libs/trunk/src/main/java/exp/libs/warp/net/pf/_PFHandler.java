package exp.libs.warp.net.pf;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.socket.bean.SocketBean;
import exp.libs.warp.net.socket.io.client.SocketClient;
import exp.libs.warp.net.socket.io.common.ISession;
import exp.libs.warp.net.socket.io.server.IHandler;

class _PFHandler implements IHandler {

	private Logger log = LoggerFactory.getLogger(_PFHandler.class);
	
	private SocketBean remoteSockBean;
	
	protected _PFHandler(SocketBean remoteSockBean) {
		this.remoteSockBean = remoteSockBean;
	}
	
	@Override
	public void _handle(ISession localClient) {
		SocketClient virtualClient = new SocketClient(remoteSockBean);
		if(!virtualClient.conn()) {
			localClient.close();
			log.warn("转发端口 [{}] 无响应, Socket会话 [{}] 关闭.", 
					remoteSockBean.getSocket(), localClient.ID());
			return;
		}
		
		// 转发本地真实客户端的IO流到虚拟客户端
		long overtime = localClient.getSocketBean().getOvertime();
		Socket localSocket = localClient.getSocket();
		Socket virtualSocket = virtualClient.getSocket();
		new _TranslateData(localClient.ID(), _TranslateData.TYPE_REQUEST, 
				overtime, localSocket, virtualSocket).start();	// 请求转发
		new _TranslateData(localClient.ID(), _TranslateData.TYPE_RESPONE, 
				overtime, virtualSocket, localSocket).start();	// 响应转发
	}

	@Override
	public IHandler _clone() {
		return new _PFHandler(remoteSockBean);
	}

}
