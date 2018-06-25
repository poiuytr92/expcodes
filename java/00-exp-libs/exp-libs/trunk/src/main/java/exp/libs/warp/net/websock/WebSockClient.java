package exp.libs.warp.net.websock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.net.websock.interfaze.IHandler;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * websocket客户端.
 * -----------------------
 *	注：若启用了心跳模式, 则在会话close后, 对象不可再用(因为同一个线程无法被启动两次)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebSockClient extends LoopThread {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(WebSockClient.class);
	
	/** 计时单位(�?) */
	private final static long SECOND = 1000L;
	
	/** 循环次数计数�?(每秒一�?) */
	private int loopCnt;
	
	/** 心跳周期计数�? */
	private int hbCnt;
	
	/** 心跳数据�? */
	private Frame hbFrame;
	
	/** websocket服务地址 */
	private String wsURL;
	
	/** websocket业务处理�? */
	private IHandler handler;
	
	/** websocket会话 */
	private _WebSockSession session;
	
	/**
	 * 构造函�?
	 * @param wsURL websocket服务地址
	 * @param handler websocket业务处理�?
	 */
	public WebSockClient(String wsURL, IHandler handler) {
		this("", wsURL, handler);
	}
	
	/**
	 * 构造函�?
	 * @param name 线程�?
	 * @param wsURL websocket服务地址
	 * @param handler websocket业务处理�?
	 */
	public WebSockClient(String name, String wsURL, IHandler handler) {
		super(StrUtils.isEmpty(name) ? "websocket心跳线程" : name);
		this.loopCnt = 0;
		this.hbCnt = 0;
		this.hbFrame = Frame.NULL;
		
		this.wsURL = (wsURL == null ? "" : wsURL);
		this.handler = (handler == null ? new _DefaultHandler() : handler);
		try {
			this.session = new _WebSockSession(wsURL, handler);
			
		} catch (Exception e) {
			log.error("初始化websocket客户端失�?, 服务器地址格式异常: {}", wsURL, e);
		}
	}
	
	/**
	 * 设置心跳模式: 对websocket会话启用心跳保活
	 * @param heartbeat 发送到服务端的心跳数据�?
	 * @param hbTime 心跳间隔(单位:�?)
	 */
	public void setHeartbeat(Frame hbFrame, int hbTime) {
		if(hbFrame != null && hbFrame != Frame.NULL && hbTime > 0) {
			this.hbFrame = hbFrame;
			this.hbCnt = hbTime;
			
			// 若已连接到服务器, 则启用心跳线�?
			if(isConnecting()) {
				_start();
			}
		}
	}
	
	/**
	 * 连接到websocket服务�?
	 * @return
	 */
	public boolean conn() {
		boolean isOk = _conn();
		if(isOk && hbFrame != Frame.NULL) {
			_start();	// 若心跳数据帧非空, 则启动心跳线�?
		}
		return isOk;
	}
	
	/**
	 * 断开websocket连接
	 */
	public void close() {
		if(hbFrame != Frame.NULL) {
			_stop();	// 若心跳数据帧非空, 则停止心跳线�?
			
		} else {
			_close();
		}
	}
	
	@Override
	protected void _before() {
		log.info("{} 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		if(_conn() == true) {
			
			// 发送心跳数据帧对连接保�?
			if(++loopCnt >= hbCnt) {
				loopCnt = 0;
				session.send(hbFrame);
				log.debug("已向websocket服务器发送心跳帧");
			}
		}
		_sleep(SECOND);
	}

	@Override
	protected void _after() {
		_close();
		log.info("{} 已停�?", getName());
	}
	
	/**
	 * 连接到websocket服务�?
	 * @return
	 */
	private boolean _conn() {
		if(isConnecting()) {
			return true;
			
		} else if(session != null) {
			session.close();
		}
		
		boolean isOk = false;
		try {
			if(session.conn()) {
				isOk = true;
				log.info("连接websocket服务器成�?: [{}]", wsURL);
				handler.afterConnect(session);
			}
		} catch (Exception e) {
			log.error("连接websocket服务器失�?: [{}]", wsURL, e);
		}
		return isOk;
	}
	
	/**
	 * 断开连接
	 */
	private void _close() {
		if(session != null) {
			handler.beforeClose(session);
			session.close();
		}
	}
	
	/**
	 * 连接是否已断开
	 * @return true:�?; false:�?
	 */
	public boolean isClosed() {
		return !isConnecting();
	}
	
	/**
	 * 连接是否有效
	 * @return true:�?; false:�?
	 */
	public boolean isConnecting() {
		boolean isConnecting = false;
		if(session != null) {
			isConnecting = session.isConnecting();
		}
		return isConnecting;
	}
	
}
