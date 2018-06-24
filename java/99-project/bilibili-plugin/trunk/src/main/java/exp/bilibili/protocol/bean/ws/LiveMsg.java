package exp.bilibili.protocol.bean.ws;

import exp.bilibili.protocol.envm.BiliCmd;
import exp.bilibili.protocol.envm.BiliCmdAtrbt;
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
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class LiveMsg extends _Msg {

	private int roomId;
	
	public LiveMsg(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.LIVE;
	}
	
	@Override
	protected void analyse(JSONObject json) {
		this.roomId = JsonUtils.getInt(json, BiliCmdAtrbt.roomid, 0);
	}

	public int getRoomId() {
		return roomId;
	}

}
