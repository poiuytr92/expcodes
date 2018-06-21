package exp.bilibili.protocol.xhr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

public class Query extends __XHR {

	
	/** 游戏区/手游区URL */
	private final static String GAME_URL = 
			"https://api.live.bilibili.com/room/v1/area/getRoomList";

	/** 娱乐区URL */
	private final static String AMUSE_URL = 
			"https://api.live.bilibili.com/room/v1/AmuseArea/allList";
	
	/** 绘画区URL */
	private final static String DRAW_URL = 
			"https://api.live.bilibili.com/area/liveList";
	
	/**
	 * 获取游戏区top1
	 * @return
	 */
	public static int queryGameTop1() {
		String pAreaId = "2";
		String areaId = "0";
		Map<String, String> header = GET_HEADER("", StrUtils.concat(
				"/p/eden/area-tags?parentAreaId=", pAreaId, 
				"&areaId=", areaId, "&visit_id=", getVisitId()));
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.platform, "web");
		request.put(BiliCmdAtrbt.parent_area_id, pAreaId);
		request.put(BiliCmdAtrbt.cate_id, "0");
		request.put(BiliCmdAtrbt.area_id, areaId);
		request.put(BiliCmdAtrbt.sort_type, "online");
		request.put(BiliCmdAtrbt.page, "1");
		request.put(BiliCmdAtrbt.page_size, "1");
		
		int roomId = 0;
		String response = HttpURLUtils.doGet(GAME_URL, header, request);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
				roomId = JsonUtils.getInt(array.getJSONObject(0), BiliCmdAtrbt.roomid, 0);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询游戏区top1失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("查询游戏区top1失败: {}", response, e);
		}
		return roomId;
	}
	         
     /**
 	 * 获取手游区top1
 	 * @return
 	 */
 	public static int queryAppGameTop1() {
 		String pAreaId = "3";
 		String areaId = "0";
 		Map<String, String> header = GET_HEADER("", StrUtils.concat(
 				"/p/eden/area-tags?parentAreaId=", pAreaId, 
 				"&areaId=", areaId, "&visit_id=", getVisitId()));
 		Map<String, String> request = new HashMap<String, String>();
 		request.put(BiliCmdAtrbt.platform, "web");
 		request.put(BiliCmdAtrbt.parent_area_id, pAreaId);
 		request.put(BiliCmdAtrbt.cate_id, "0");
 		request.put(BiliCmdAtrbt.area_id, areaId);
 		request.put(BiliCmdAtrbt.sort_type, "online");
 		request.put(BiliCmdAtrbt.page, "1");
 		request.put(BiliCmdAtrbt.page_size, "1");
 		
 		int roomId = 0;
 		String response = HttpURLUtils.doGet(GAME_URL, header, request);
 		
 		try {
 			JSONObject json = JSONObject.fromObject(response);
 			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
 			if(code == 0) {
 				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
 				roomId = JsonUtils.getInt(array.getJSONObject(0), BiliCmdAtrbt.roomid, 0);
 				
 			} else {
 				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
 				log.warn("查询手游区top1失败: {}", reason);
 			}
 		} catch(Exception e) {
 			log.error("查询手游区top1失败: {}", response, e);
 		}
 		return roomId;
 	}
 	
 	/**
	 * 获取娱乐区top1
	 * @return
	 */
	public static int queryAmuseTop1() {
		Map<String, String> header = GET_HEADER("", "/pages/area/ent");
		Map<String, String> request = new HashMap<String, String>();
		
		int roomId = 0;
		String response = HttpURLUtils.doGet(AMUSE_URL, header, request);
		
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONArray array = JsonUtils.getArray(data, BiliCmdAtrbt.top_recommend);
				roomId = JsonUtils.getInt(array.getJSONObject(0), BiliCmdAtrbt.roomid, 0);
				
			} else {
				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
				log.warn("查询娱乐区top1失败: {}", reason);
			}
		} catch(Exception e) {
			log.error("查询娱乐区top1失败: {}", response, e);
		}
		return roomId;
	}
	
	/**
	 * 获取绘画区top1
	 * @return
	 */
	public static int queryDrawTop1() {
		Map<String, String> header = GET_HEADER("", "/pages/area/draw");
		Map<String, String> request = new HashMap<String, String>();
		request.put(BiliCmdAtrbt.area, "draw");
		request.put(BiliCmdAtrbt.order, "live_time");
		request.put(BiliCmdAtrbt.page, "1");
		
		int roomId = 0;
		String response = HttpURLUtils.doGet(DRAW_URL, header, request);
		try {
 			JSONObject json = JSONObject.fromObject(response);
 			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
 			if(code == 0) {
 				JSONArray array = JsonUtils.getArray(json, BiliCmdAtrbt.data);
 				roomId = JsonUtils.getInt(array.getJSONObject(0), BiliCmdAtrbt.roomid, 0);
 				
 			} else {
 				String reason = JsonUtils.getStr(json, BiliCmdAtrbt.msg);
 				log.warn("查询绘画区top1失败: {}", reason);
 			}
 		} catch(Exception e) {
 			log.error("查询绘画区top1失败: {}", response, e);
 		}
		return roomId;
	}
	
	public static void main(String[] args) {
		System.out.println(queryGameTop1());
		System.out.println(queryAppGameTop1());
		System.out.println(queryAmuseTop1());
		System.out.println(queryDrawTop1());
	}
	
}
