package exp.bilibili.protocol.bean.xhr;

import exp.bilibili.protocol.envm.BiliCmdAtrbt;
import exp.libs.utils.format.JsonUtils;
import net.sf.json.JSONObject;

/**
 * <PRE>
 * 
 * 2018春节红包兑奖活动，奖池中pool_list的award对象：
 * 
   {
	  "code": 0,
	  "msg": "success",
	  "message": "success",
	  "data": {
	    "red_bag_num": 0,
	    "round": 80,
	    "pool_list": [
	      {
	        "award_id": "guard-3",
	        "award_name": "舰长体验券（1个月）",
	        "stock_num": 0,
	        "exchange_limit": 5,
	        "user_exchange_count": 5,
	        "price": 6699
	      },
	      {
	        "award_id": "gift-113",
	        "award_name": "新春抽奖",
	        "stock_num": 2,
	        "exchange_limit": 0,
	        "user_exchange_count": 0,
	        "price": 23333
	      },
	      {
	        "award_id": "danmu-gold",
	        "award_name": "金色弹幕特权（1天）",
	        "stock_num": 20,
	        "exchange_limit": 42,
	        "user_exchange_count": 42,
	        "price": 2233
	      },
	      {
	        "award_id": "uname-gold",
	        "award_name": "金色昵称特权（1天）",
	        "stock_num": 20,
	        "exchange_limit": 42,
	        "user_exchange_count": 42,
	        "price": 8888
	      },
	      {
	        "award_id": "title-92",
	        "award_name": "年兽头衔",
	        "stock_num": 0,
	        "exchange_limit": 1,
	        "user_exchange_count": 1,
	        "price": 999
	      },
	      {
	        "award_id": "stuff-3",
	        "award_name": "贤者之石",
	        "stock_num": 31,
	        "exchange_limit": 5,
	        "user_exchange_count": 5,
	        "price": 1888
	      },
	      {
	        "award_id": "stuff-1",
	        "award_name": "经验原石",
	        "stock_num": 0,
	        "exchange_limit": 80,
	        "user_exchange_count": 80,
	        "price": 30
	      },
	      {
	        "award_id": "gift-109",
	        "award_name": "红灯笼",
	        "stock_num": 0,
	        "exchange_limit": 500,
	        "user_exchange_count": 500,
	        "price": 15
	      }
	    ],
	    "pool": {
	      "award_id": "award-pool",
	      "award_name": "刷新兑换池",
	      "stock_num": 99999,
	      "exchange_limit": 0,
	      "price": 6666
	    }
	  }
	}
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Award {

	private String id;
	
	private String name;
	
	/** 本轮奖池中剩余的数量 */
	private int stockNum;
	
	/** 用户的总兑换上限：若为0则可无限次兑�? */
	private int exchangeLimit;
	
	/** 用户剩余可以兑换的数�? */
	private int userExchangeCount;
	
	public Award(String id, int redbagNum) {
		this.id = id;
		this.stockNum = redbagNum;
	}
	
	public Award(JSONObject json) {
		this.id = JsonUtils.getStr(json, BiliCmdAtrbt.award_id);
		this.name = JsonUtils.getStr(json, BiliCmdAtrbt.award_name);
		this.stockNum = JsonUtils.getInt(json, BiliCmdAtrbt.stock_num, 0);
		this.exchangeLimit = JsonUtils.getInt(json, BiliCmdAtrbt.exchange_limit, -1);
		this.userExchangeCount = JsonUtils.getInt(json, BiliCmdAtrbt.user_exchange_count, 0);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getStockNum() {
		return stockNum;
	}

	public int getExchangeLimit() {
		return exchangeLimit;
	}

	public int getUserExchangeCount() {
		return userExchangeCount;
	}
	
}
