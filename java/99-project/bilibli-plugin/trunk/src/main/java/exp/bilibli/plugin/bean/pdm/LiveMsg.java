package exp.bilibli.plugin.bean.pdm;

import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import net.sf.json.JSONObject;

/**
 * 
 * <PRE>
 * 
 	开播通知
	{
		"cmd": "LIVE",
		"roomid": "390480",
	}
 * </PRE>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class LiveMsg extends _Msg {

	private String roomId;
	
	public LiveMsg(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.LIVE;
	}
	
	@Override
	protected void analyse(JSONObject json) {
		this.roomId = JsonUtils.getStr(json, BiliCmdAtrbt.roomid);
	}

	public String getRoomId() {
		return roomId;
	}

}
