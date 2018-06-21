package exp.bilibili.protocol.ws;

import java.util.LinkedList;
import java.util.List;

import exp.bilibili.plugin.Config;
import exp.bilibili.protocol.envm.BiliBinary;
import exp.libs.warp.net.websock.WebSockClient;
import exp.libs.warp.net.websock.bean.Frame;

public class BiliWebSocketGroup {

	private final static String WEBSOCKET = Config.getInstn().WEBSOCKET();
	
	private final static Frame HB_FRAME = new Frame(BiliBinary.CLIENT_HB);
	
	private final static int HB_SECOND = 30;
	
	private final static String[] AREA = new String[] {
		"游戏区", "手游区", "娱乐区", "绘画区"
	};
	
	/** 用于版聊直播间的websocket */
	private WebSockClient liveClient;
	
	/**
	 * 用于监听分区礼物广播的websocket.
	 *  分区名称 -> WebSockClient
	 */
	private List<WebSockClient> linstenClients;
	
	public BiliWebSocketGroup() {
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
		// TODO 每半小时刷新一次
		return null;
	}
	
	public void clear() {
		liveClient.close();
		
		for(WebSockClient linstenClient : linstenClients) {
			linstenClient.close();
		}
		linstenClients.clear();
	}
	
}
