package exp.bilibli.plugin.bean.pdm;

import java.util.Iterator;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;

/**
 * 
 * <PRE>
 * 
 	特殊礼物：(房间内)节奏风暴消息
	{
	  "cmd": "SPECIAL_GIFT",
	  "data": {
	    "39": {
	      "id": 152125,
	      "time": 90,
	      "hadJoin": 0,
	      "num": 1,
	      "content": "皈依小乔~钵钵鸡~藤藤菜~啦啦啦",
	      "action": "start"
	    }
	  }
	}
 * </PRE>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SpecialGift extends _Msg {

	private String id;
	
	public SpecialGift(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.SPECIAL_GIFT;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void analyse(JSONObject json) {
		JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data);
		Iterator keys = data.keys();
		if(keys.hasNext()) {
			JSONObject obj = JsonUtils.getObject(data, keys.next().toString());
			this.id = JsonUtils.getStr(obj, BiliCmdAtrbt.id);
		}
	}

	public String getId() {
		return id;
	}
	
}
