package exp.bilibli.plugin.bean.pdm;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;

/**
 * 
 * <PRE>
 * 
	上船消息：
	{
	  "cmd": "GUARD_BUY",
	  "data": {
	    "uid": 14356317,
	    "username": "\u8865\u5200\u706c\u5200",
	    "guard_level": 3,
	    "num": 1
	  },
	  "roomid": "390480"
	}
 * </PRE>
 * 
 * @author Administrator
 * @date 2017年12月15日
 */
public class GuardBuyMsg extends WelcomeGuardMsg {

	private int num;
	
	public GuardBuyMsg(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.GUARD_BUY;
	}
	
	@Override
	protected void analyse(JSONObject json) {
		super.analyse(json);
		
		JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data); {
			this.num = JsonUtils.getInt(data, BiliCmdAtrbt.num, 0);
		}
	}

	public int getNum() {
		return num;
	}

}