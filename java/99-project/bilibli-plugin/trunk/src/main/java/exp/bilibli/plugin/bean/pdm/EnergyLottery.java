package exp.bilibli.plugin.bean.pdm;

import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import exp.libs.utils.other.StrUtils;
import net.sf.json.JSONObject;

/**
 * 
 * <PRE>
 * 
	高能礼物抽奖：
	{
		"cmd": "SYS_GIFT",
		"msg": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
		"msg_text": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
		"tips": "00\u515c\u515c00\u5728\u76f4\u64ad\u95f45279\u706b\u529b\u5168\u5f00\uff0c\u55e8\u7ffb\u5168\u573a\uff0c\u901f\u53bb\u56f4\u89c2\uff0c\u8fd8\u80fd\u514d\u8d39\u9886\u53d6\u706b\u529b\u7968\uff01",
		"url": "http:\/\/live.bilibili.com\/5279",
		"roomid": 5279,
		"real_roomid": 5279,
		"giftId": 106,
		"msgTips": 0
	}
 * </PRE>
 * 
 * @author Administrator
 * @date 2017年12月15日
 */
public class EnergyLottery extends SysGift {

	private String tips;
	
	private String url;
	
	private String roomId;
	
	private String realRoomId;
	
	private String giftId;
	
	private String msgTips;
	
	public EnergyLottery(JSONObject json) {
		super(json);
	}

	public String ROOM_ID() {
		String id = getRealRoomId();
		return (StrUtils.isEmpty(id) ? getRoomId() : id);
	}
	
	@Override
	protected void analyse(JSONObject json) {
		super.analyse(json);
		this.tips = JsonUtils.getStr(json, BiliCmdAtrbt.tips);
		this.url = JsonUtils.getStr(json, BiliCmdAtrbt.url);
		this.roomId = JsonUtils.getStr(json, BiliCmdAtrbt.roomid);
		this.realRoomId = JsonUtils.getStr(json, BiliCmdAtrbt.real_roomid);
		this.giftId = JsonUtils.getStr(json, BiliCmdAtrbt.giftId);
		this.msgTips = JsonUtils.getStr(json, BiliCmdAtrbt.msgTips);
	}
	
	public String getTips() {
		return tips;
	}

	public String getUrl() {
		return url;
	}

	public String getRoomId() {
		return roomId;
	}

	public String getRealRoomId() {
		return realRoomId;
	}

	public String getGiftId() {
		return giftId;
	}

	public String getMsgTips() {
		return msgTips;
	}

}
