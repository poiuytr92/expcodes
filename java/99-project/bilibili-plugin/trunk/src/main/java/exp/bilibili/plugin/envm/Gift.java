package exp.bilibili.plugin.envm;

/**
 * <PRE>
 * 各种礼物对应的活跃度枚举
 * 
 * 
http://api.live.bilibili.com/gift/v2/live/room_gift_list?roomid=51108&area_v2_id=0
{
  "code": 0,
  "msg": "success",
  "message": "success",
  "data": [
    {
      "id": 7,
      "type": 1,
      "price": 666,
      "name": "666",
      "action": "赠送",
      "desc": "使用666会在视频上出现特效弹幕666666，增加赠送数量会加长特效弹幕的长度。",
      "effect": "该礼物没有连击效果和高能预警哦(｀・ω・´)",
      "custom_input": 0,
      "count_set": "1,2,3,4,5",
      "combo_num": 0,
      "super_num": 0,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": 5,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 8,
      "type": 1,
      "price": 233,
      "name": "233",
      "action": "赠送",
      "desc": "使用233会在视频上出现特效弹幕233，增加赠送数量会加长特效弹幕的长度。",
      "effect": "该礼物没有连击效果和高能预警哦(｀・ω・´)",
      "custom_input": 0,
      "count_set": "1,2,3,4,5",
      "combo_num": 0,
      "super_num": 0,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": 5,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 25,
      "type": 0,
      "price": 1245000,
      "name": "小电视",
      "action": "赠送",
      "desc": "哔哩哔哩的吉祥物，经过改造后可以完成各种任务，顺便一提，这只小电视的代号是04D。",
      "effect": "<span>小电视抽奖</span>：赠送小电视会触发抽奖，可掉落小电视抱枕或10万银瓜子哦！<br><span>高能预警</span>：小电视抽奖时会发送频道公告，点击可直达抽奖直播间，积攒人气的利器。</span>",
      "custom_input": 0,
      "count_set": "1,2,3,4,5",
      "combo_num": 1,
      "super_num": 1,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": -1,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 39,
      "type": 0,
      "price": 100000,
      "name": "节奏风暴",
      "action": "赠送",
      "desc": "作为弹幕界的长者，是时候带一波节奏了。",
      "effect": "<span>特殊功能</span>：选择弹幕开启风暴，前<span>100</span>的观众可以通过点击节奏风暴，发送节奏弹幕，领取亿圆。节奏风暴最大持续时间为<span>90</span>秒。每增加<span>1</span>倍节奏倍率可以增加<span>100</span>人的亿圆领取量。<br><span>高能预警</span>：单次赠送20个或以上，触发全站公告。</span>",
      "custom_input": 1,
      "count_set": "1,2,3,4,5",
      "combo_num": 1,
      "super_num": 20,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": 100,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 3,
      "type": 0,
      "price": 9900,
      "name": "B坷垃",
      "action": "赠送",
      "desc": "主播吃了B坷垃，吸收方圆百里人气值——American SHENGDIYAGE",
      "effect": "<span>特殊功能</span>：投喂后可以获得主播的粉丝勋章（需主播已经开启粉丝勋章并且自己勋章数小于20），同时会自动加入主播的bilibili link应援团。快来投喂，成为主播的头号粉丝吧！<br><span>连击效果</span>：每赠送<span>1</span>个或以上，可触发看板娘的连击感谢。<br><span>高能预警</span>：连击<span>50</span>次/单次赠送<span>46</span>个或以上，触发全站公告。",
      "custom_input": 1,
      "count_set": "1,46,188,520,1314",
      "combo_num": 1,
      "super_num": 46,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": -1,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 4,
      "type": 0,
      "price": 5200,
      "name": "喵娘",
      "action": "赠送",
      "desc": " ( >ω<) 喵~~ ",
      "effect": "<span>连击效果</span>：单次赠送<span>2</span>个或以上，赠送相同数量叠加连击。<br><span>高能预警</span>：连击<span>50</span>次/单次赠送超过<span>87</span>个或以上。",
      "custom_input": 1,
      "count_set": "2,10,87,188,520,1314",
      "combo_num": 2,
      "super_num": 87,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": -1,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 6,
      "type": 0,
      "price": 1000,
      "name": "亿圆",
      "action": "赠送",
      "desc": "虽然只要1元，但是四舍五入之后就值一个亿啊。",
      "effect": "<span>连击效果</span>：每赠送<span>10</span>个或以上，可触发看板娘的连击感谢。<br><span>高能预警</span>：连击<span>50</span>次/单次赠送<span>450</span>个或以上，触发全站公告。",
      "custom_input": 1,
      "count_set": "10,99,188,450,520,1314",
      "combo_num": 10,
      "super_num": 450,
      "coin_type": {
        "gold": "gold"
      },
      "max_limit": -1,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    },
    {
      "id": 1,
      "type": 0,
      "price": 100,
      "name": "辣条",
      "action": "喂食",
      "desc": "辣条是流行于哔哩哔哩的坊间美食，可以直接食用，也能用来打赌。",
      "effect": "(银瓜子专属道具)",
      "custom_input": 1,
      "count_set": "10,99,188,520,1314,4500",
      "combo_num": 0,
      "super_num": 0,
      "coin_type": {
        "silver": "silver"
      },
      "max_limit": -1,
      "area_ids": [
        
      ],
      "score": 0,
      "buyable": 1,
      "expire": [
        
      ],
      "self": 0
    }
  ]
}
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Gift {

	public final static Gift CHAT = new Gift("", "弹幕", 1);
	
	public final static Gift HOT_STRIP = new Gift("1", "辣条", 100);
	
	public final static Gift MILLION = new Gift("6", "亿圆", 1000);
	
	public final static Gift MEOW = new Gift("4", "喵娘", 5200);
	
	public final static Gift B_CLOD = new Gift("3", "B坷垃", 9900);
	
	public final static Gift STORM = new Gift("39", "节奏风暴", 100000);
	
	public final static Gift TV = new Gift("25", "小电视", 1245000);
	
	public final static Gift _233 = new Gift("8", "233", 233);
	
	public final static Gift _666 = new Gift("7", "666", 666);
	
	public final static Gift PANGCI = new Gift("10", "蓝白胖次", 19900);
	
	public final static Gift LUNCH = new Gift("30", "爱心便当", 4500);
	
	public final static Gift CAPTAIN = new Gift("", "舰长", 198000);
	
	public final static Gift ADMIRAL = new Gift("", "提督", 1998000);
	
	public final static Gift GOVERNOR = new Gift("", "总督", 19998000);
	
	public final static Gift LANTERN = new Gift("109", "红灯笼", 2000);
	
	public final static Gift SQUIB = new Gift("110", "小爆竹", 2000);
	
	public final static Gift PEACH = new Gift("115", "桃花", 2000);
	
	public final static Gift LETTER = new Gift("116", "情书", 2000);
	
	/** 礼物ID */
	private String id;
	
	/** 礼物名称 */
	private String name;
	
	/** 价值/活跃值 */
	private int cost;
	
	/** 亲密度（礼物价值/100） */
	private int intimacy;
	
	private Gift(String id, String name, int cost) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.intimacy = (int) Math.ceil(cost / 100.0);	// 向上取整, 便于计算
	}
	
	public String ID() {
		return id;
	}
	
	public String NAME() {
		return name;
	}
	
	public int COST() {
		return cost;
	}
	
	public int INTIMACY() {
		return intimacy;
	}
	
	public static int getIntimacy(String giftId) {
		int intimacy = 0;
		if(HOT_STRIP.ID().equals(giftId)) {
			intimacy = HOT_STRIP.INTIMACY();
			
		} else if(MILLION.ID().equals(giftId)) {
			intimacy = MILLION.INTIMACY();
			
		} else if(MEOW.ID().equals(giftId)) {
			intimacy = MEOW.INTIMACY();
			
		} else if(B_CLOD.ID().equals(giftId)) {
			intimacy = B_CLOD.INTIMACY();
			
		} else if(STORM.ID().equals(giftId)) {
			intimacy = STORM.INTIMACY();
			
		} else if(TV.ID().equals(giftId)) {
			intimacy = TV.INTIMACY();
			
		} else if(_233.ID().equals(giftId)) {
			intimacy = _233.INTIMACY();
			
		} else if(_666.ID().equals(giftId)) {
			intimacy = _666.INTIMACY();
			
		} else if(LANTERN.ID().equals(giftId)) {
			intimacy = LANTERN.INTIMACY();
			
		} else if(SQUIB.ID().equals(giftId)) {
			intimacy = SQUIB.INTIMACY();
			
		} else if(PEACH.ID().equals(giftId)) {
			intimacy = PEACH.INTIMACY();
			
		} else if(LETTER.ID().equals(giftId)) {
			intimacy = LETTER.INTIMACY();
			
		}
		return intimacy;
	}
	
	public static int getCost(String giftName) {
		int cost = 0;
		if(CHAT.NAME().equals(giftName)) {
			cost = CHAT.COST();
			
		} else if(HOT_STRIP.NAME().equals(giftName)) {
			cost = HOT_STRIP.COST();
			
		} else if(MILLION.NAME().equals(giftName)) {
			cost = MILLION.COST();
			
		} else if(MEOW.NAME().equals(giftName)) {
			cost = MEOW.COST();
			
		} else if(B_CLOD.NAME().equals(giftName)) {
			cost = B_CLOD.COST();
			
		} else if(STORM.NAME().equals(giftName)) {
			cost = STORM.COST();
			
		} else if(TV.NAME().equals(giftName)) {
			cost = TV.COST();
			
		} else if(_233.NAME().equals(giftName)) {
			cost = _233.COST();
			
		} else if(_666.NAME().equals(giftName)) {
			cost = _666.COST();
			
		} else if(PANGCI.NAME().equals(giftName)) {
			cost = PANGCI.COST();
			
		} else if(LUNCH.NAME().equals(giftName)) {
			cost = LUNCH.COST();
			
		} else if(CAPTAIN.NAME().equals(giftName)) {
			cost = CAPTAIN.COST();
			
		} else if(ADMIRAL.NAME().equals(giftName)) {
			cost = ADMIRAL.COST();
			
		} else if(GOVERNOR.NAME().equals(giftName)) {
			cost = GOVERNOR.COST();
			
		} else if(LANTERN.NAME().equals(giftName)) {
			cost = LANTERN.COST();
			
		} else if(SQUIB.NAME().equals(giftName)) {
			cost = SQUIB.COST();
			
		} else if(PEACH.NAME().equals(giftName)) {
			cost = PEACH.COST();
			
		} else if(LETTER.NAME().equals(giftName)) {
			cost = LETTER.COST();
			
		}
		return cost;
	}
	
}
