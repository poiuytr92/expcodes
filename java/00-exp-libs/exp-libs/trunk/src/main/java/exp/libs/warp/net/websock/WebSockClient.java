package exp.libs.warp.net.websock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.net.websock.interfaze.IHandler;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * websocket客户端
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebSockClient extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebSockClient.class);
	
	private final static long SECOND = 1000L;
	
	private int loopCnt;
	
	private int hbCnt;
	
	private long hbTime;
	
	private Frame hbFrame;
	
	private String wsURL;
	
	private IHandler handler;
	
	private _WebSockSession session;
	
	public WebSockClient(String wsURL, IHandler handler) {
		super("websocket连接线程");
		this.loopCnt = -1;
		this.hbCnt = -1;
		this.hbTime = -1;
		this.hbFrame = Frame.NULL;
		
		this.wsURL = (wsURL == null ? "" : wsURL);
		this.handler = (handler == null ? new _DefaultHandler() : handler);
		try {
			this.session = new _WebSockSession(wsURL, handler);
			
		} catch (Exception e) {
			log.error("初始化websocket客户端失败, 服务器地址格式异常: {}", wsURL, e);
		}
	}
	
	/**
	 * 对websocket会话启用心跳保活
	 * @param heartbeat 发送到服务端的心跳数据帧
	 * @param hbTime 心跳间隔(单位:秒)
	 */
	public void setHeartbeat(Frame hbFrame, int hbTime) {
		if(hbFrame != null && hbTime > 0) {
			this.hbFrame = hbFrame;
			this.hbTime = hbTime * SECOND;
			this.hbCnt = (int) (this.hbTime / SECOND);
		}
	}
	
	public void conn() {
		_start();
	}
	
	public void close() {
		_stop();
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		if(_conn() == true) {
			
			// 发送心跳保活
			if(hbFrame != Frame.NULL && loopCnt >= hbCnt) {
				loopCnt = 0;
				session.send(hbFrame);
				log.debug("已向websocket服务器发送心跳保活");
			}
		}
		
		_sleep(SECOND);
		loopCnt++;
	}

	@Override
	protected void _after() {
		_close();
		log.info("{} 已停止", getName());
	}
	
	private boolean _conn() {
		if(session != null && session.isConnected()) {
			return true;
		}
		
		boolean isOk = false;
		try {
			if(session.conn()) {
				isOk = true;
				log.info("连接websocket服务器成功: [{}]", wsURL);
				handler.afterConnect(session);
			}
		} catch (Exception e) {
			log.error("连接websocket服务器失败: [{}]", wsURL, e);
		}
		return isOk;
	}
	
	public boolean isClosed() {
		boolean isClosed = true;
		if(session != null) {
			isClosed = !session.isConnected();
		}
		return isClosed;
	}
	
	private void _close() {
		if(session != null) {
			handler.beforeClose(session);
			session.close();
		}
	}
	
}
