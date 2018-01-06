package exp.bilibli.plugin.bean.pdm;

import net.sf.json.JSONObject;
import exp.bilibli.plugin.envm.BiliCmd;
import exp.bilibli.plugin.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;

/**
 * 
 * <PRE>
 * 
 	(直播间内)高能抽奖开始消息
	{
	  "cmd": "RAFFLE_END",
	  "roomid": 390480,
	  "data": {
	    "raffleId": 61825,
	    "type": "openfire",
	    "from": "\u9f20\u4e8c\u4e09\u4e09",
	    "fromFace": "http:\/\/i2.hdslb.com\/bfs\/face\/4b309c88cc66c5d11558c47beb716b0fd8dd1438.jpg",
	    "win": {
	      "uname": "\u51b7\u7406Remonn",
	      "face": "http:\/\/i2.hdslb.com\/bfs\/face\/4b309c88cc66c5d11558c47beb716b0fd8dd1438.jpg",
	      "giftId": 105,
	      "giftName": "\u706b\u529b\u7968",
	      "giftNum": 50
	    }
	  }
	}
 * </PRE>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RaffleEnd extends _Msg {

	private String roomId;
	
	private String raffleId;
	
	private String from;
	
	private String winner;
	
	private String giftName;
	
	private String giftNum;
	
	public RaffleEnd(JSONObject json) {
		super(json);
		this.cmd = BiliCmd.RAFFLE_END;
	}
	
	@Override
	protected void analyse(JSONObject json) {
		this.roomId = JsonUtils.getStr(json, BiliCmdAtrbt.roomid);
		JSONObject data = JsonUtils.getObject(json, BiliCmdAtrbt.data); {
			this.raffleId = JsonUtils.getStr(data, BiliCmdAtrbt.raffleId);
			this.from = JsonUtils.getStr(data, BiliCmdAtrbt.from);
			
			JSONObject win = JsonUtils.getObject(json, BiliCmdAtrbt.win); {
				this.winner = JsonUtils.getStr(win, BiliCmdAtrbt.uname);
				this.giftName = JsonUtils.getStr(win, BiliCmdAtrbt.giftName);
				this.giftNum = JsonUtils.getStr(win, BiliCmdAtrbt.giftNum);
			}
		}
	}

	public String getRoomId() {
		return roomId;
	}

	public String getRaffleId() {
		return raffleId;
	}

	public String getFrom() {
		return from;
	}

	public String getWinner() {
		return winner;
	}

	public String getGiftName() {
		return giftName;
	}

	public String getGiftNum() {
		return giftNum;
	}

}
