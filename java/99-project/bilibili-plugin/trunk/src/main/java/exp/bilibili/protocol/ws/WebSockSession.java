package exp.bilibili.protocol.ws;

import java.net.URI;
import java.nio.ByteBuffer;

import net.sf.json.JSONObject;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.WSAnalyser;
import exp.bilibili.protocol.bean.other.Frame;
import exp.bilibili.protocol.envm.Binary;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * websocket会话
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class WebSockSession extends WebSocketClient {

	private final static Logger log = LoggerFactory.getLogger(WebSockSession.class);
	
	/** 连接超时 */
	private final static long CONN_TIMEOUT = 10000;
	
	private boolean isClosed;
	
	protected WebSockSession(URI serverURI, Draft draft) {
		this(serverURI, draft, 0, false);
	}
	
	/**
	 * 
	 * @param serverUri
	 * @param draft WebSocket协议版本
	 * 				WebSocket协议说明可查看 http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	 * 				通过打开调试开关 WebSocketImpl.DEBUG = true 可以知道服务端的协议版本
	 * 				Draft_6455 为最新的WebSocket协议版本
	 * @param timeout 本地连接保活超时（0不生效，默认60，即60秒后自动断开）
	 * @param debug 调试模式
	 */
	protected WebSockSession(URI serverURI, Draft draft, int timeout, boolean debug) {
		super(serverURI, draft);
		setTimeout(timeout);
		debug(debug);
		this.isClosed = true;
	}
	
	public void setTimeout(int timeout) {
		setConnectionLostTimeout(timeout);
	}
	
	public void debug(boolean open) {
		WebSocketImpl.DEBUG = open;
	}
	
	@Deprecated
	@Override
	public void connect() {
		// Undo
	}

	public boolean conn() {
		boolean isOk = false;
		super.connect();
		
		long bgnTime = System.currentTimeMillis();
		do {
			if(isOpen()) {
				isClosed = false;
				isOk = true;
				break;
			}
			ThreadUtils.tSleep(1000);
		} while(System.currentTimeMillis() - bgnTime <= CONN_TIMEOUT);
		return isOk;
	}
	
	public void close() {
		super.close();
		isClosed = true;
	}
	
	public boolean isConn() {
		return (isOpen() && !isClosed);
	}
	
	public void send(Frame frame) {
		sendFrame(frame);
	}
	
	public void send(byte[] bytes) {
		send(new Frame(bytes));
	}
	
	@Override
	public void onOpen(ServerHandshake serverhandshake) {
		log.info("正在连接websocket服务器...");
	}

	@Override
	public void onMessage(String msg) {
		log.debug("接收到 [String] 类型数据: {}", msg);
	}
	
	@Override
	public void onMessage(ByteBuffer byteBuffer) {
		byte[] buff = byteBuffer.array();
		String hex = BODHUtils.toHex(buff);
		log.debug("接收到推送消息: {}", hex);
		
		if(hex.startsWith(Binary.SERVER_HB_CONFIRM)) {
			log.debug("websocket连接保活确认");
			
		} else if(Binary.SERVER_CONN_CONFIRM.equals(hex)) {
			log.debug("websocket连接成功确认");
			UIUtils.log("入侵直播间成功, 正在暗中观察...");
			
		} else if(alalyseHexMsg(hex) == false) {
			log.warn("存在无效的推送消息: {}", hex);
		}
    }
	
	private boolean alalyseHexMsg(String hexMsg) {
		boolean isOk = true;
		String split = hexMsg.substring(0, 32);	// 消息的前32个字节(即16个字符)为分隔符
		String[] hexs = hexMsg.split(split);
		for(String hex : hexs) {
			if(StrUtils.isEmpty(hex)) {
				continue;
			}
			
			String msg = CharsetUtils.toStr(BODHUtils.toBytes(hex), Config.DEFAULT_CHARSET);
			if(JsonUtils.isVaild(msg)) {
				JSONObject json = JSONObject.fromObject(msg);
				if(!WSAnalyser.toMsgBean(json)) {
					isOk = false;
				}
			} else {
				isOk = false;
			}
		}
		return isOk;
	}
	
	@Override
    public void onFragment(Framedata framedata) {
		log.debug("接收到 [Framedata] 类型数据: {}", framedata.toString());
    }
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		isClosed = true;
		log.info("websocket连接正在断开: [错误码:{}] [发起人:{}] [原因:{}]", 
				code, (remote ? "server" : "client"), reason);
		UIUtils.log("与直播间的连接已断开 (Reason:", (remote ? "server" : "client"), ")");
	}

	@Override
	public void onError(Exception e) {
		isClosed = true;
		log.error("websocket连接异常", e);
		UIUtils.log("与直播间的连接已断开 (Reason:", e.getMessage(), ")");
	}

}
