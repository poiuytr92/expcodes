package exp.bilibli.plugin.bean.pdm;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;

/**
 * 
 * <PRE>
 * 
	提督/总督进入直播间欢迎消息：
	{
	  "cmd": "WELCOME_GUARD",
	  "data": {
	    "uid": 1650868,
	    "username": "M-亚絲娜",
	    "guard_level": "2"
	  }
	}
 * </PRE>
 * 
 * @author Administrator
 * @date 2017年12月15日
 */
public class WelcomeGuardMsg extends _Msg {

	protected final static String[] GUARD = {
		"平民", "总督", "提督", "舰长"
	};
	
	protected String uid;
	
	protected String username;
	
	protected int guardLevel;
	
	protected String guardDesc;
	
	public WelcomeGuardMsg(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.WELCOME_GUARD;
	}
	
	@Override
	protected void analyse(JSONObject json) {
		JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data); {
			this.uid = JsonUtils.getStr(data, BiliCmdAtrbt.uid);
			this.username = JsonUtils.getStr(data, BiliCmdAtrbt.username);
			this.guardLevel = JsonUtils.getInt(data, BiliCmdAtrbt.guard_level, 0);
			guardLevel = (guardLevel >= GUARD.length ? 0 : guardLevel);
			this.guardDesc = GUARD[guardLevel];
		}
	}

	public String getUid() {
		return uid;
	}

	public String getUsername() {
		return username;
	}

	public int getGuardLevel() {
		return guardLevel;
	}

	public String getGuardDesc() {
		return guardDesc;
	}

}
