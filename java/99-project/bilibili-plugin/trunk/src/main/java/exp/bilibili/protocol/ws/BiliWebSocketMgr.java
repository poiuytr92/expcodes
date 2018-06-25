package exp.bilibili.protocol.ws;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.Area;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.XHRSender;
import exp.bilibili.protocol.envm.BiliBinary;
import exp.libs.warp.net.websock.WebSockClient;
import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * B站WebSocket管理器
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-06-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class BiliWebSocketMgr extends LoopThread {
	
	/** websocket服务器地址 */
	private final static String WEBSOCKET = Config.getInstn().WEBSOCKET();
	
	/** 心跳数据�? */
	private final static Frame HB_FRAME = new Frame(BiliBinary.CLIENT_HB);
	
	/** 心跳间隔(�?) */
	private final static int HB_SECOND = 30;
	
	/** 刷新分区监听会话的时�?(ms) */
	private final static long REFLASH_TIME = 1800000;
	
	/** 每次轮询的休眠时�?(ms) */
	private final static long SLEEP_TIME = 1000;
	
	/** 心跳频率上限(轮询次数达到此上限则触发心跳) */
	private final static int REFLASH_LIMIT = (int) (REFLASH_TIME / SLEEP_TIME);
	
	/** 当前轮询次数 */
	private int loopCnt;
	
	/** 用于版聊直播间的websocket */
	private WebSockClient live;
	
	/**
	 * 用于监听分区礼物广播的websocket.
	 *  分区名称 -> WebSockClient
	 */
	private List<WebSockClient> listeners;
	
	/**
	 * 构造函�?
	 */
	public BiliWebSocketMgr() {
		super("websocket会话管理�?");
		
		this.loopCnt = REFLASH_LIMIT;
		this.listeners = new LinkedList<WebSockClient>();
	}
	
	/**
	 * 创建websocket会话, 并连接到服务�?
	 * @param name 会话名称
	 * @param roomId 被监听的房间�?
	 * @param onlyListen 此websocket会话是否只用于监听分区礼�?
	 * @return
	 */
	private WebSockClient createWebSocket(String name, int roomId, boolean onlyListen) {
		BiliHandler handler = new BiliHandler(roomId, onlyListen);
		WebSockClient client = new WebSockClient(name.concat("监控线程"), WEBSOCKET, handler);
		client.setHeartbeat(HB_FRAME, HB_SECOND);
		client.conn();
		return client;
	}
	
	/**
	 * 重连版聊直播�?
	 * @param roomId 被监听的房间�?
	 */
	public void relinkLive(int roomId) {
		if(live != null) {
			live.close();
		}
		live = createWebSocket("版聊直播�?", roomId, false);
	}
	
	/**
	 * 重连所有分区的监听会话
	 */
	public void relinkListeners() {
		clearListeners();
		
		Map<Area, Integer> roomIds = XHRSender.getAreaTopOnes();
		Iterator<Area> areas = roomIds.keySet().iterator();
		while(areas.hasNext()) {
			Area area = areas.next();
			int roomId = roomIds.get(area);
			
			if(roomId > 0) {
				WebSockClient listener = createWebSocket(
						area.DESC().concat("直播�?"), roomId, true);
				listeners.add(listener);
				UIUtils.log("监听 [", area.DESC(), "] 榜首直播�? [", roomId, "] 成功");
				
			} else {
				UIUtils.log("[", area.DESC(), "] 无人直播, 取消监听");
			}
		}
	}
	
	/**
	 * 清理所有websocket会话
	 */
	public void clear() {
		live.close();
		clearListeners();
	}
	
	/**
	 * 清理监听分区的websocket会话
	 */
	private void clearListeners() {
		for(WebSockClient linstener : listeners) {
			linstener.close();
		}
		listeners.clear();
	}

	@Override
	protected void _before() {
		log.info("[{}] 已启�?", getName());
	}

	@Override
	protected void _loopRun() {
		if(UIUtils.isJoinLottery()) {
			if(++loopCnt >= REFLASH_LIMIT) {
				loopCnt = 0;
				relinkListeners();
			}
		} else {
			loopCnt = REFLASH_LIMIT;
			clearListeners();
		}
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		clear();
		log.info("[{}] 已停�?", getName());
	}
	
}
