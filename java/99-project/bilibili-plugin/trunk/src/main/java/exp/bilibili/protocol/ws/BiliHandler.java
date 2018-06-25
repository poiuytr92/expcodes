package exp.bilibili.protocol.ws;

import java.nio.ByteBuffer;

import net.sf.json.JSONObject;

import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.WSAnalyser;
import exp.bilibili.protocol.envm.BiliBinary;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.net.websock.interfaze.IHandler;
import exp.libs.warp.net.websock.interfaze.ISession;

/**
 * <PRE>
 * B站WebSocket业务逻辑
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-06-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BiliHandler implements IHandler {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(BiliHandler.class);
	
	/** WebSoekct原始报文日志�? */
	private final static Logger wslog = LoggerFactory.getLogger("WEBSOCKET");
	
	/** 子消息的�?32位字节是该子消息的含消息�? */
	private final static int MSG_HEADER_LEN = 32;
	
	/** 子消息的�?8位字节是该子消息的字符长度（含消息头�? */
	private final static int MSG_LENGTH_LEN = 8;
	
	/** 被监听的房间�? */
	private int roomId;
	
	/** 此websocket会话是否只用于监听分区礼�? */
	private boolean onlyListen;
	
	/** 连接websocket服务器后发送的数据�? */
	private final Frame CONN_FRAME;
	
	/** 断开websocket连接前发送的数据�? */
	private final Frame CLOSE_FRAME;
	
	/**
	 * 构造函�?
	 * @param roomId 被监听的房间�?
	 */
	public BiliHandler(int roomId) {
		this(roomId, false);
	}
	
	/**
	 * 构造函�?
	 * @param roomId 被监听的房间�?
	 * @param onlyListen 此websocket会话是否只用于监听分区礼�?
	 */
	public BiliHandler(int roomId, boolean onlyListen) {
		this.roomId = RoomMgr.getInstn().getRealRoomId(roomId);
		this.onlyListen = onlyListen;
		this.CONN_FRAME = new Frame(BiliBinary.CLIENT_CONNECT(this.roomId));
		this.CLOSE_FRAME = new Frame(BiliBinary.CLIENT_CLOSE, Opcode.CLOSING);
	}
	
	@Override
	public void onOpen(ServerHandshake serverhandshake) {
		// Undo
	}

	@Override
	public void afterConnect(ISession session) {
		session.send(CONN_FRAME);	// B站的websocket连接成功后需要马上发送连接请�?
		
		if(onlyListen == false) {
			UIUtils.log("正在尝试入侵直播�? [", roomId, "] 后台...");
		}
	}

	@Override
	public void onMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(ByteBuffer byteBuffer) {
		String hex = BODHUtils.toHex(byteBuffer.array());
		wslog.info("RECEIVE: {}", hex);
		
		if(hex.startsWith(BiliBinary.SERVER_HB_CONFIRM)) {
			log.debug("websocket连接保活确认");
			
		} else if(BiliBinary.SERVER_CONN_CONFIRM.equals(hex)) {
			log.debug("websocket连接成功确认");
			
			if(onlyListen == false) {
				UIUtils.log("入侵直播�? [", roomId, "] 成功, 正在暗中观察...");
			}
			
		} else if(alalyseHexMsg(hex) == false) {
			log.error("存在无效的推送消�?: {}", hex);
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
				if(!WSAnalyser.toMsgBean(json, roomId, onlyListen)) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeClose(ISession session) {
		session.send(CLOSE_FRAME);	// 断开连接前通知服务端断开
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		if(onlyListen == false) {
			UIUtils.log("与直播间 [", roomId, "] 的连接已断开 (Reason:", 
					(remote ? "server" : "client"), ")");
		}
	}

	@Override
	public void onError(Exception e) {
		if(onlyListen == false) {
			UIUtils.log("与直播间 [", roomId, 
					"] 的连接已断开 (Reason:", e.getMessage(), ")");
		}
	}

}
