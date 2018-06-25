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
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
class WebSockSession extends WebSocketClient {

	private final static Logger log = LoggerFactory.getLogger(WebSockSession.class);
	
	/** 子消息的�?32位字节是该子消息的含消息�? */
	private final static int MSG_HEADER_LEN = 32;
	
	/** 子消息的�?8位字节是该子消息的字符长度（含消息头�? */
	private final static int MSG_LENGTH_LEN = 8;
	
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
	 * 				WebSocket协议说明可查�? http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
	 * 				通过打开调试开�? WebSocketImpl.DEBUG = true 可以知道服务端的协议版本
	 * 				Draft_6455 为最新的WebSocket协议版本
	 * @param timeout 本地连接保活超时�?0不生效，默认60，即60秒后自动断开�?
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
		log.info("正在连接websocket服务�?...");
	}

	@Override
	public void onMessage(String msg) {
		log.debug("接收�? [String] 类型数据: {}", msg);
	}
	
	@Override
	public void onMessage(ByteBuffer byteBuffer) {
		byte[] buff = byteBuffer.array();
		String hex = BODHUtils.toHex(buff);
		log.debug("接收到推送消�?: {}", hex);
		
		if(hex.startsWith(Binary.SERVER_HB_CONFIRM)) {
			log.debug("websocket连接保活确认");
			
		} else if(Binary.SERVER_CONN_CONFIRM.equals(hex)) {
			log.debug("websocket连接成功确认");
			UIUtils.log("入侵直播间成�?, 正在暗中观察...");
			
		} else if(alalyseHexMsg(hex) == false) {
			log.warn("存在无效的推送消�?: {}", hex);
		}
    }
	
	/**
	 * 解析十六进制消息
	 * @param hexMsg 可能是多条子消息拼接而成
	 * @return
	 */
	private boolean alalyseHexMsg(String hexMsg) {
		boolean isOk = true;
		while(StrUtils.isNotEmpty(hexMsg)) {
			int len = getHexLen(hexMsg);	// 获取子消息长�?
			if(len <= MSG_HEADER_LEN) {	// 消息的前32个字�?(�?16个字�?)为消息头
				break;
			}
			
			String subHexMsg = hexMsg.substring(MSG_HEADER_LEN, len);
			String msg = CharsetUtils.toStr(
					BODHUtils.toBytes(subHexMsg), Config.DEFAULT_CHARSET);
			if(JsonUtils.isVaild(msg)) {
				JSONObject json = JSONObject.fromObject(msg);
				if(!WSAnalyser.toMsgBean(json)) {
					isOk = false;
				}
			} else {
				isOk = false;
			}
			hexMsg = hexMsg.substring(len);
		}
		return isOk;
	}
	
	/**
	 * 获取子消息的长度
	 * @param hexMsg 所有消�?
	 * @return 子消息的16进制长度
	 */
	private static int getHexLen(String hexMsg) {
		String hexLen = hexMsg.substring(0, MSG_LENGTH_LEN); // 子消息的�?8位是该子消息的字符长度（含消息头�?
		long len = BODHUtils.hexToDec(hexLen);
		return (int) (len * 2);	// 1字符 = 2�?16进制字节
	}
	
	@Override
    public void onFragment(Framedata framedata) {
		log.debug("接收�? [Framedata] 类型数据: {}", framedata.toString());
    }
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		isClosed = true;
		log.error("websocket连接断开: [错误�?:{}] [发起�?:{}] [原因:{}]", 
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
