package exp.libs.warp.net.websock;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import exp.libs.warp.net.websock.interfaze.IHandler;
import exp.libs.warp.net.websock.interfaze.ISession;

/**
 * <pre>
 * 默认的WebSocket业务逻辑处理器
 * </pre>	
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-06-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _DefaultHandler implements IHandler {

	@Override
	public void onOpen(ServerHandshake serverhandshake) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterConnect(ISession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(ByteBuffer byteBuffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFragment(Framedata framedata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeClose(ISession session) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception e) {
		// TODO Auto-generated method stub
		
	}

}
