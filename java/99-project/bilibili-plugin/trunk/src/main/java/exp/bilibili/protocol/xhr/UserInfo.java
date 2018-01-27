package exp.bilibili.protocol.xhr;

import java.util.Map;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.Config;
import exp.bilibili.plugin.envm.BiliCmdAtrbt;
import exp.bilibili.protocol.cookie.HttpCookie;
import exp.libs.utils.format.JsonUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 账号信息
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class UserInfo extends __Protocol {

	/** 账号信息URL */
	private final static String ACCOUNT_URL = Config.getInstn().ACCOUNT_URL();
	
	/**
	 * 查询账号信息(写入cookie内)
	 * {"code":0,"msg":"\u83b7\u53d6\u6210\u529f","data":{"achieves":960,"userInfo":{"uid":1650868,"uname":"M-\u4e9a\u7d72\u5a1c","face":"https:\/\/i1.hdslb.com\/bfs\/face\/bbfd1b5cafe4719e3a57154ac1ff16a9e4d9c6b3.jpg","rank":10000,"identification":1,"mobile_verify":1,"platform_user_level":4,"official_verify":{"type":-1,"desc":""}},"roomid":"269706","userCoinIfo":{"uid":1650868,"vip":1,"vip_time":"2018-12-12 21:56:04","svip":1,"svip_time":"2018-12-06 21:56:04","cost":63781395,"rcost":2481900,"user_score":440323260,"silver":"29902","gold":"72009","iap_gold":0,"score":24819,"master_level":{"level":10,"current":[6300,18060],"next":[9100,27160]},"user_current_score":504104655,"user_level":45,"user_next_level":46,"user_intimacy":4104655,"user_next_intimacy":50000000,"user_level_rank":4325,"bili_coins":0,"coins":475},"vipViewStatus":false,"discount":false,"svip_endtime":"2018-12-06","vip_endtime":"2018-12-12","year_price":233000,"month_price":20000,"action":"index","liveTime":0,"master":{"level":10,"current":6759,"next":9100,"medalInfo":{"id":"25072","uid":"1650868","medal_name":"\u795e\u624b","live_status":"1","master_status":"1","status":1,"reason":"0","last_rename_time":"0","time_able_change":0,"rename_status":1,"charge_num":50,"coin_num":20,"platform_status":"2"}},"san":12,"count":{"guard":2,"fansMedal":11,"title":24,"achieve":0}}}
	 * @param cookie
	 * @return username
	 */
	public static boolean query(HttpCookie cookie) {
		Map<String, String> headers = GET_HEADER(cookie.toNVCookie());
		String response = HttpURLUtils.doGet(ACCOUNT_URL, headers, null);
		try {
			JSONObject json = JSONObject.fromObject(response);
			int code = JsonUtils.getInt(json, BiliCmdAtrbt.code, -1);
			if(code == 0) {
				JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
				JSONObject userInfo = JsonUtils.getObject(data, BiliCmdAtrbt.userInfo);
				String uid = JsonUtils.getStr(userInfo, BiliCmdAtrbt.uid);
				String username = JsonUtils.getStr(userInfo, BiliCmdAtrbt.uname);
				
				cookie.setUid(uid);
				cookie.setNickName(username);
			}
		} catch(Exception e) {
			log.error("查询账号信息失败: {}", response, e);
		}
		return cookie.isVaild();
	}
	
}
