package exp.libs.warp.net.websock;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.net.websock.interfaze.IHandler;
import exp.libs.warp.net.websock.interfaze.ISession;

/**
 * <PRE>
 * websocket会话
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2018-06-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class _WebSockSession extends WebSocketClient implements ISession {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(_WebSockSession.class);
	
	private final static long SECOND = 1000L;
	
	private final static Draft DRAFT = new Draft_6455();
	
	/** 默认连接超时(单位:ms) */
	private final static long CONN_TIMEOUT = 10000;
	
	/** websocket服务器的URI */
	private String wsURL;
	
	/** 连接超时时间(单位:ms) */
	private long connTimeout;
	
	/** 连接断开标识 */
	private boolean isClosed;
	
	/** 业务处理�? */
	private IHandler handler;
	
	/**
	 * 构造函�?
	 * @param wsURL WebSocket服务器地址, 格式形如  ws://broadcastlv.chat.bilibili.com:2244/sub
	 * @param handler 业务处理�?
	 */
	protected _WebSockSession(String wsURL, IHandler handler) 
			throws Exception {
		this(wsURL, DRAFT, handler);
	}
	
	/**
	 * 构造函�?
	 * @param wsURL WebSocket服务器地址, 格式形如  ws://broadcastlv.chat.bilibili.com:2244/sub
	 * @param draft WebSocket协议版本
	 * 				WebSocket协议说明可查�? http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	 * 				通过打开调试开�? WebSocketImpl.DEBUG = true 可以知道服务端的协议版本
	 * 				Draft_6455 为最新的WebSocket协议版本
	 * @param handler 业务处理�?
	 */
	protected _WebSockSession(String wsURL, Draft draft, IHandler handler) 
			throws Exception {
		this(wsURL, draft, handler, 0, 0, false);
	}
	
	/**
	 * 构造函�?
	 * @param wsURL WebSocket服务器地址, 格式形如  ws://broadcastlv.chat.bilibili.com:2244/sub
	 * @param draft WebSocket协议版本
	 * 				WebSocket协议说明可查�? http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	 * 				通过打开调试开�? WebSocketImpl.DEBUG = true 可以知道服务端的协议版本
	 * 				Draft_6455 为最新的WebSocket协议版本
	 * @param handler 业务处理�?
	 * @param keepTimeout 连接超时时间（默�?10秒）, 单位:�?
	 * @param keepTimeout 本地连接保活超时（默�?0, 不生�?; 若为N, 表示N秒后自动断开�?, 单位:�?
	 * @param debug 调试模式
	 * @throws Exception 
	 */
	protected _WebSockSession(String wsURL, Draft draft, IHandler handler, 
			int connTimeout, int keepTimeout, boolean debug) throws Exception {
		super(new URI(wsURL), (draft == null ? DRAFT : draft));
		
		this.wsURL = wsURL;
		this.handler = (handler == null ? new _DefaultHandler() : handler);
		this.connTimeout = -1;
		this.isClosed = true;
		
		setConnTimeout(connTimeout);
		setKeepTimeout(keepTimeout);
		debug(debug);
	}
	
	/**
	 * 设置连接超时时间
	 * @param connTimeout 连接超时时间, 单位:�?
	 */
	private void setConnTimeout(int connTimeout) {
		if(connTimeout > 0) {
			this.connTimeout = connTimeout * SECOND;
			
		} else if(this.connTimeout <= 0) {
			this.connTimeout = CONN_TIMEOUT;
		}
	}
	
	/**
	 * 设置本地连接保活超时�?0不生效，默认60，即60秒后自动断开�?
	 * @param keepTimeout 保活超时时间, 单位:�?
	 */
	@Override
	public void setKeepTimeout(int keepTimeout) {
		setConnectionLostTimeout(keepTimeout);
	}
	
	/**
	 * 切换调试模式
	 * @param debug
	 */
	@Override
	public void debug(boolean debug) {
		WebSocketImpl.DEBUG = debug;
	}
	
	@Deprecated
	@Override
	public void connect() {
		// Undo
	}

	/**
	 * <pre>
	 * 连接到WebSocket服务.
	 * 	(此方法会阻塞, 直到连接成功)
	 * </pre>
	 * @return
	 */
	public boolean conn() {
		super.connect();
		
		// 检查是否连接成�?
		long bgnTime = System.currentTimeMillis();
		do {
			if(isOpen()) {
				isClosed = false;
				break;
			}
			ThreadUtils.tSleep(1000);
		} while(System.currentTimeMillis() - bgnTime <= connTimeout);
		return isConnecting();
	}
	
	/**
	 * 断开WebSocket连接
	 */
	@Override
	public void close() {
		super.close();
		isClosed = true;
	}
	
	/**
	 * 检查WebSocket连接是否还存�?
	 * @return
	 */
	@Override
	public boolean isConnecting() {
		return ((isOpen() || super.isConnecting()) && !isClosed);
	}
	
	/**
	 * 向服务器发送数据帧
	 * @param frame
	 */
	@Override
	public void send(Frame frame) {
		if(frame != null) {
			sendFrame(frame.clone());	// 通过克隆数据帧刷新时间戳
		}
	}
	
	/**
	 * 向服务器发送字节数�?
	 */
	@Override
	public void send(byte[] bytes) {
		send(new Frame(bytes));
	}
	
	@Override
	public void onOpen(ServerHandshake serverhandshake) {
		log.info("正在连接websocket服务�?: {}", wsURL);
		handler.onOpen(serverhandshake);
	}

	@Override
	public void onMessage(String msg) {
		log.debug("接收�? [String] 类型数据: {}", msg);
		handler.onMessage(msg);
	}
	
	@Override
	public void onMessage(ByteBuffer byteBuffer) {
		log.debug("接收�? [ByteBuffer] 类型数据: {}", 
				BODHUtils.toHex(byteBuffer.array()));
		handler.onMessage(byteBuffer);
    }
	
	@Override
    public void onFragment(Framedata framedata) {
		log.debug("接收�? [Framedata] 类型数据: {}", 
				BODHUtils.toHex(framedata.getPayloadData().array()));
		handler.onFragment(framedata);
    }
	
	/**
	 * websocket连接断开时触�?
	 * @param code 错误�?
	 * @param reason 断开原因
	 * @param remote 是否为远端导致（true:服务器导致断开; false:客户端导致断开�?
	 */
	@Override
	public void onClose(int code, String reason, boolean remote) {
		isClosed = true;
		log.error("websocket连接已断开: [错误�?:{}] [发起�?:{}] [原因:{}]", 
				code, (remote ? "server" : "client"), reason);
		handler.onClose(code, reason, remote);
	}

	@Override
	public void onError(Exception e) {
		isClosed = true;
		log.error("websocket连接异常", e);
		handler.onError(e);
	}

}
