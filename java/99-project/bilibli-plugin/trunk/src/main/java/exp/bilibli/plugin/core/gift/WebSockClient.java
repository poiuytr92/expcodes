package exp.bilibli.plugin.core.gift;

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

import exp.bilibli.plugin.bean.ldm.Frame;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.bilibli.plugin.envm.BinaryData;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.RegexUtils;

public class WebSockClient extends WebSocketClient {

	private final static Logger log = LoggerFactory.getLogger(WebSockClient.class);
	
	private final static String RGX_JSON = "[^{]*(.*)";
	
	/** 连接超时 */
	private final static long CONN_TIMEOUT = 10000;
	
	public WebSockClient(URI serverURI, Draft draft) {
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
	public WebSockClient(URI serverURI, Draft draft, int timeout, boolean debug) {
		super(serverURI, draft);
		setTimeout(timeout);
		debug(debug);
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
				isOk = true;
				break;
			}
			ThreadUtils.tSleep(1000);
		} while(System.currentTimeMillis() - bgnTime <= CONN_TIMEOUT);
		return isOk;
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
		
		if(hex.startsWith(BinaryData.SERVER_HB_CONFIRM)) {
			log.debug("websocket连接保活确认");
			
		} else if(BinaryData.SERVER_CONN_CONFIRM.equals(hex)) {
			log.info("websocket连接成功确认");
			
		} else {
			String msg = new String(buff);
			String sJson = RegexUtils.findFirst(msg, RGX_JSON);
			
			if(JsonUtils.isVaild(sJson)) {
				JSONObject json = JSONObject.fromObject(sJson);
				if(!handle(json)) {
					log.info("接收到未识别的 [ByteBuffer] 类型数据: {}", hex);
				}
			}
		}
    }
	
	private boolean handle(JSONObject msg) {
		boolean isOk = true;
		String cmd = JsonUtils.getStr(msg, BiliCmdAtrbt.cmd);
		if(BiliCmd.SYS_MSG.equals(cmd)) {
			String roomId = getRoomId(msg);
			if(StrUtils.isNotEmpty(roomId)) {
				GiftRoomMgr.getInstn().add(roomId);
				log.info("直播间 [{}] 正在小电视抽奖中!!!", roomId);
				
			} else {
				// Undo: 系统公告
				isOk = false;
			}
			
		} else if(BiliCmd.SYS_GIFT.equals(cmd)) {
			String roomId = getRoomId(msg);
			if(StrUtils.isNotEmpty(roomId)) {
				GiftRoomMgr.getInstn().add(roomId);
				log.info("直播间 [{}] 正在高能抽奖中!!!", roomId);
				
			} else {
				String msgText = JsonUtils.getStr(msg, BiliCmdAtrbt.msg_text);
				log.info("全频道公告: {}", msgText);
			}
			
		} else if(BiliCmd.WELCOME_GUARD.equals(cmd)) {
			// Undo 被监听的直播间送过来的欢迎舰长/老爷进入直播间的消息
			isOk = false;
						
		} else if(BiliCmd.SEND_GIFT.equals(cmd)) {
			// Undo 被监听的直播间送过来的投喂消息
			isOk = false;
			
		} else {
			// Undo 其他跟刷礼物无关的消息
			isOk = false;
		}
		return isOk;
	}
	
	private String getRoomId(JSONObject msg) {
		String roomId = JsonUtils.getStr(msg, BiliCmdAtrbt.real_roomid);
		if(StrUtils.isEmpty(roomId)) {
			roomId = JsonUtils.getStr(msg, BiliCmdAtrbt.roomid);
		}
		return roomId;
	}

	@Override
    public void onFragment(Framedata framedata) {
		log.debug("接收到 [Framedata] 类型数据: {}", framedata.toString());
    }
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		log.info("websocket连接正在断开: [错误码:{}] [发起人:{}] [原因:{}]", 
				code, (remote ? "server" : "client"), reason);
	}

	@Override
	public void onError(Exception e) {
		log.error("websocket连接异常", e);
	}

}
