package exp.bilibili.protocol.xhr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.cache.RoomMgr;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.cookie.CookiesMgr;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 高能礼物抽奖
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LotteryEnergy extends _Lottery {
	
	/** 高能礼物ID查询URL */
	private final static String EG_CHECK_URL = Config.getInstn().EG_CHECK_URL();
	
	/** 高能礼物抽奖URL */
	private final static String EG_JOIN_URL = Config.getInstn().EG_JOIN_URL();
	
	/** 最上一次抽奖过的礼物编号(礼物编号是递增的) */
	private static int LAST_RAFFLEID = 0;
	
	/** 私有化构造函数 */
	protected LotteryEnergy() {}
	
	/**
	 * 高能礼物抽奖
	 * @param roomId
	 * @return
	 */
	public static int toDo(int roomId) {
		roomId = RoomMgr.getInstn().getRealRoomId(roomId);
		List<String> raffleIds = getRaffleId(EG_CHECK_URL, roomId, 
				CookiesMgr.INSTN().VEST().toNVCookie());
		
		int cnt = 0;
		if(ListUtils.isEmpty(raffleIds)) {
			return cnt;
		}
		
		for(String raffleId : raffleIds) {
			int id = NumUtils.toInt(raffleId, 0);
			if(id > LAST_RAFFLEID) {	// 礼物编号是递增
				LAST_RAFFLEID = id;
				join(roomId, raffleId);
			}
		}
		return cnt;
	}
	
	/**
	 * 获取礼物编号
	 * @param response {"code":0,"msg":"success","message":"success","data":[{"raffleId":46506,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46507,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46508,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1},{"raffleId":46509,"type":"openfire","from":"喵熊°","from_user":{"uname":"喵熊°","face":"http://i1.hdslb.com/bfs/face/66b91fc04ccd3ccb23ad5f0966a7c3da5600b0cc.jpg"},"time":60,"status":1}]}
	 * @return
	 */
	private static List<String> getRaffleId(String url, int roomId, String cookie) {
		List<String> raffleIds = new LinkedList<String>();
		
		String sRoomId = String.valueOf(roomId);
		Map<String, String> header = GET_HEADER(cookie, sRoomId);
		Map<String, String> request = getRequest(sRoomId);
		
		String response = HttpURLUtils.doGet(url, header, request);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				for(int i = 0; i < array.size(); i++) {
					JSONObject obj = array.getJSONObject(i);
					int raffleId = JsonUtils.getInt(obj, BiliCmdAtrbt.raffleId, 0);
					if(raffleId > 0) {
						raffleIds.add(String.valueOf(raffleId));
					}
				}
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("获取礼物编号失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("获取礼物编号异常: {}", response, e);
		}
		return raffleIds;
	}
	
	// FIXME 某个用户是否抽奖成功
	private static boolean join(int roomId, String raffleId) {
		boolean isOk = false;
		Iterator<HttpCookie> cookieIts = CookiesMgr.INSTN().ALL();
		while(cookieIts.hasNext()) {
			HttpCookie cookie = cookieIts.next();
			String errDesc = join(LotteryType.ENGERY, cookie, 
					EG_JOIN_URL, roomId, raffleId);
			if(StrUtils.isEmpty(errDesc)) {
				isOk = true;
				
			} else {
				if(!errDesc.contains("你已加入抽奖")) {
					UIUtils.statistics("失败(", errDesc, "): 抽奖直播间 [", roomId, "]");
				}
				log.info("参与直播间 [{}] 抽奖失败: {}", roomId, errDesc);
			}
		}
		return isOk;
	}
	
}