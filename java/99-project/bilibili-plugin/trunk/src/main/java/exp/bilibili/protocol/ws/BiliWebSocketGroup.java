package exp.bilibili.protocol.ws;

import java.util.LinkedList;
import java.util.List;

import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.envm.BiliBinary;
import exp.bilibili.protocol.xhr.Query;
import exp.libs.warp.net.websock.WebSockClient;
import exp.libs.warp.net.websock.bean.Frame;
import exp.libs.warp.thread.LoopThread;

public class BiliWebSocketGroup extends LoopThread {
	
	private final static String WEBSOCKET = Config.getInstn().WEBSOCKET();
	
	private final static Frame HB_FRAME = new Frame(BiliBinary.CLIENT_HB);
	
	private final static int HB_SECOND = 30;
	
	private final static long REFLASH_TIME = 1800000;
	
	private final static long SLEEP_TIME = 1000;
	
	private final static int LOOP_LIMIT = (int) (REFLASH_TIME / SLEEP_TIME);
	
	private int loopCnt;
	
	/** 用于版聊直播间的websocket */
	private WebSockClient liveClient;
	
	/**
	 * 用于监听分区礼物广播的websocket.
	 *  分区名称 -> WebSockClient
	 */
	private List<WebSockClient> linstenClients;
	
	public BiliWebSocketGroup() {
		super("websocket组管理器");
		
		this.loopCnt = 0;
		this.linstenClients = new LinkedList<WebSockClient>();
	}
	
	private WebSockClient createWebSockClient(int roomId, boolean listenGift) {
		BiliHandler handler = new BiliHandler(roomId, listenGift);
		WebSockClient client = new WebSockClient(WEBSOCKET, handler);
		client.setHeartbeat(HB_FRAME, HB_SECOND);
		client.conn();
		return client;
	}
	
	public void relinkLive(int roomId) {
		if(liveClient != null) {
			liveClient.close();
		}
		
		liveClient = createWebSockClient(roomId, false);
	}
	
	/**
	 * 重连所有分区监听
	 */
	public void relinkListen() {
		for(WebSockClient linstenClient : linstenClients) {
			linstenClient.close();
		}
		linstenClients.clear();
		
		int[] roomIds = getAreaRoomIds();
		for(int roomId : roomIds) {
			WebSockClient linstenClient = createWebSockClient(roomId, true);
			linstenClients.add(linstenClient);
		}
	}
	
	/**
	 * 获取当前每个分区的TOP1直播间
	 * @return
	 */
	private int[] getAreaRoomIds() {
		int[] roomIds = new int[4];
		roomIds[0] = Query.queryGameTop1();
		roomIds[1] = Query.queryAppGameTop1();
		roomIds[2] = Query.queryAmuseTop1();
		roomIds[3] = Query.queryDrawTop1();
		return roomIds;
	}
	
	public void clear() {
		liveClient.close();
		
		for(WebSockClient linstenClient : linstenClients) {
			linstenClient.close();
		}
		linstenClients.clear();
	}

	@Override
	protected void _before() {
		log.info("{} 启动", getName());
		loopCnt = LOOP_LIMIT;
	}

	@Override
	protected void _loopRun() {
		
		if(++loopCnt >= LOOP_LIMIT) {
			loopCnt = 0;
			relinkListen();
		}
		
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		clear();
		log.info("{} 停止", getName());
	}
	
}
