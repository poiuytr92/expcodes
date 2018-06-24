package exp.bilibili.plugin.bean.pdm;

import net.sf.json.JSONObject;
import exp.bilibili.plugin.envm.BiliCmd;

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
 * @author    EXP: www.exp-blog.com
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
