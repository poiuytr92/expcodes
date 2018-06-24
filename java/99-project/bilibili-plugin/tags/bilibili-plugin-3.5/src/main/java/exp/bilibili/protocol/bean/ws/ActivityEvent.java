package exp.bilibili.protocol.bean.ws;

import net.sf.json.JSONObject;
import exp.bilibili.protocol.envm.BiliCmd;

/**
 * 
 * <PRE>
 * 
	2018春节活动-红灯笼高能通知事件：
	{
	  "cmd": "ACTIVITY_EVENT",
	  "data": {
	    "keyword": "newspring_2018",
	    "type": "cracker",
	    "limit": 300000,
	    "progress": 59334
	  }
	}
 * </PRE>
 * @version   1.0 2017-12-17
 * @author    EXP: <a href="http://www.exp-blog.com">www.exp-blog.com</a>
 * @since     jdk版本：jdk1.6
 */
public class ActivityEvent extends _Msg {

	public ActivityEvent(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.ACTIVITY_EVENT;
	}

	@Override
	protected void analyse(JSONObject json) {
		// Undo
	}

}
