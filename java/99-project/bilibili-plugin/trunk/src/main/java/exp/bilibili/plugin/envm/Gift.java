package exp.bilibili.plugin.envm;

/**
 * <PRE>
 * 各种礼物对应的活跃度枚举
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Gift {

	public final static Gift CHAT = new Gift("", "弹幕", 1);
	
	public final static Gift CAPTAIN = new Gift("", "舰长", 198000);
	
	public final static Gift ADMIRAL = new Gift("", "提督", 1998000);
	
	public final static Gift GOVERNOR = new Gift("", "总督", 19998000);
	
	public final static Gift HOT_STRIP = new Gift("1", "辣条", 100);
	
	public final static Gift B_CLOD = new Gift("3", "B坷垃", 9900);
	
	public final static Gift MEOW = new Gift("4", "喵娘", 5200);
	
	public final static Gift MILLION = new Gift("6", "亿圆", 1000);
	
	public final static Gift _666 = new Gift("7", "666", 666);
	
	public final static Gift _233 = new Gift("8", "233", 233);
	
	public final static Gift LUNCH = new Gift("9", "爱心便当", 4500);
	
	public final static Gift PANGCI = new Gift("10", "蓝白胖次", 19900);
	
	public final static Gift TV = new Gift("25", "小电�?", 1245000);
	
	public final static Gift STORM = new Gift("39", "节奏风暴", 100000);
	
	public final static Gift LANTERN = new Gift("109", "红灯�?", 2000);
	
	public final static Gift SQUIB = new Gift("110", "小爆�?", 2000);
	
	public final static Gift PEACH = new Gift("115", "桃花", 2000);
	
	public final static Gift LETTER = new Gift("116", "情书", 2000);

	public final static Gift GAMEBOY = new Gift("120", "游戏�?", 100);
	
	public final static Gift STAR = new Gift("121", "闪耀之星", 200);
	
	public final static Gift FLAG = new Gift("20002", "flag", 100);
	
	public final static Gift SKYSCRAPER = new Gift("20013", "摩天大楼", 450000);
	
	public final static Gift RUBBERNECK = new Gift("20004", "吃瓜", 100);
	
	public final static Gift COIN = new Gift("20005", "金币", 1000);
	
	public final static Gift BQM = new Gift("20007", "？？�?", 100);
	
	public final static Gift COLA = new Gift("20008", "冰阔�?", 1000);
	
	public final static Gift SPRAY = new Gift("20009", "变欧喷雾", 12000);
	
	public final static Gift COLD = new Gift("20010", "凉了", 100);
	
	public final static Gift CHEERS = new Gift("20011", "干杯", 100);
	
	public final static Gift KEYBOARD = new Gift("20012", "氪金键盘", 38000);
	
	public final static Gift FLOWER = new Gift("20013", "小花�?", 2000);
	
	public final static Gift HEART = new Gift("20014", "比心", 100);
	
	/** 礼物ID */
	private String id;
	
	/** 礼物名称 */
	private String name;
	
	/** 价�?/活跃�? */
	private int cost;
	
	/** 亲密度（礼物价�?/100�? */
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
			
		} else if(B_CLOD.ID().equals(giftId)) {
			intimacy = B_CLOD.INTIMACY();
			
		} else if(MEOW.ID().equals(giftId)) {
			intimacy = MEOW.INTIMACY();
			
		} else if(MILLION.ID().equals(giftId)) {
			intimacy = MILLION.INTIMACY();
			
		} else if(_666.ID().equals(giftId)) {
			intimacy = _666.INTIMACY();
			
		} else if(_233.ID().equals(giftId)) {
			intimacy = _233.INTIMACY();
			
		} else if(LUNCH.ID().equals(giftId)) {
			intimacy = LUNCH.INTIMACY();
			
		} else if(PANGCI.ID().equals(giftId)) {
			intimacy = PANGCI.INTIMACY();
			
		} else if(PANGCI.ID().equals(giftId)) {
			intimacy = PANGCI.INTIMACY();
			
		} else if(PANGCI.ID().equals(giftId)) {
			intimacy = PANGCI.INTIMACY();
			
		} else if(TV.ID().equals(giftId)) {
			intimacy = TV.INTIMACY();
			
		} else if(STORM.ID().equals(giftId)) {
			intimacy = STORM.INTIMACY();
			
		} else if(LANTERN.ID().equals(giftId)) {
			intimacy = LANTERN.INTIMACY();
			
		} else if(SQUIB.ID().equals(giftId)) {
			intimacy = SQUIB.INTIMACY();
			
		} else if(PEACH.ID().equals(giftId)) {
			intimacy = PEACH.INTIMACY();
			
		} else if(LETTER.ID().equals(giftId)) {
			intimacy = LETTER.INTIMACY();
			
		} else if(GAMEBOY.ID().equals(giftId)) {
			intimacy = GAMEBOY.INTIMACY();
			
		} else if(STAR.ID().equals(giftId)) {
			intimacy = STAR.INTIMACY();
			
		} else if(FLAG.ID().equals(giftId)) {
			intimacy = FLAG.INTIMACY();
			
		} else if(SKYSCRAPER.ID().equals(giftId)) {
			intimacy = SKYSCRAPER.INTIMACY();
			
		} else if(RUBBERNECK.ID().equals(giftId)) {
			intimacy = RUBBERNECK.INTIMACY();
			
		} else if(COIN.ID().equals(giftId)) {
			intimacy = COIN.INTIMACY();
			
		} else if(BQM.ID().equals(giftId)) {
			intimacy = BQM.INTIMACY();
			
		} else if(COLA.ID().equals(giftId)) {
			intimacy = COLA.INTIMACY();
			
		} else if(SPRAY.ID().equals(giftId)) {
			intimacy = SPRAY.INTIMACY();
			
		} else if(COLD.ID().equals(giftId)) {
			intimacy = COLD.INTIMACY();
		
		} else if(CHEERS.ID().equals(giftId)) {
			intimacy = CHEERS.INTIMACY();
			
		} else if(KEYBOARD.ID().equals(giftId)) {
			intimacy = KEYBOARD.INTIMACY();
			
		} else if(FLOWER.ID().equals(giftId)) {
			intimacy = FLOWER.INTIMACY();
			
		} else if(HEART.ID().equals(giftId)) {
			intimacy = HEART.INTIMACY();
		}
		return intimacy;
	}
	
	public static int getCost(String giftName) {
		int cost = 0;
		if(CHAT.NAME().equals(giftName)) {
			cost = CHAT.COST();
			
		} else if(CAPTAIN.NAME().equals(giftName)) {
			cost = CAPTAIN.COST();
			
		} else if(ADMIRAL.NAME().equals(giftName)) {
			cost = ADMIRAL.COST();
			
		} else if(GOVERNOR.NAME().equals(giftName)) {
			cost = GOVERNOR.COST();
			
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
			
		} else if(HOT_STRIP.NAME().equals(giftName)) {
			cost = HOT_STRIP.COST();
			
		} else if(B_CLOD.NAME().equals(giftName)) {
			cost = B_CLOD.COST();
			
		} else if(MEOW.NAME().equals(giftName)) {
			cost = MEOW.COST();
			
		} else if(MILLION.NAME().equals(giftName)) {
			cost = MILLION.COST();
			
		} else if(_666.NAME().equals(giftName)) {
			cost = _666.COST();
			
		} else if(_233.NAME().equals(giftName)) {
			cost = _233.COST();
			
		} else if(LUNCH.NAME().equals(giftName)) {
			cost = LUNCH.COST();
			
		} else if(PANGCI.NAME().equals(giftName)) {
			cost = PANGCI.COST();
			
		} else if(PANGCI.NAME().equals(giftName)) {
			cost = PANGCI.COST();
			
		} else if(PANGCI.NAME().equals(giftName)) {
			cost = PANGCI.COST();
			
		} else if(TV.NAME().equals(giftName)) {
			cost = TV.COST();
			
		} else if(STORM.NAME().equals(giftName)) {
			cost = STORM.COST();
			
		} else if(LANTERN.NAME().equals(giftName)) {
			cost = LANTERN.COST();
			
		} else if(SQUIB.NAME().equals(giftName)) {
			cost = SQUIB.COST();
			
		} else if(PEACH.NAME().equals(giftName)) {
			cost = PEACH.COST();
			
		} else if(LETTER.NAME().equals(giftName)) {
			cost = LETTER.COST();
			
		} else if(GAMEBOY.NAME().equals(giftName)) {
			cost = GAMEBOY.COST();
			
		} else if(STAR.NAME().equals(giftName)) {
			cost = STAR.COST();
			
		} else if(FLAG.NAME().equals(giftName)) {
			cost = FLAG.COST();
			
		} else if(SKYSCRAPER.NAME().equals(giftName)) {
			cost = SKYSCRAPER.COST();
			
		} else if(RUBBERNECK.NAME().equals(giftName)) {
			cost = RUBBERNECK.COST();
			
		} else if(COIN.NAME().equals(giftName)) {
			cost = COIN.COST();
			
		} else if(BQM.NAME().equals(giftName)) {
			cost = BQM.COST();
			
		} else if(COLA.NAME().equals(giftName)) {
			cost = COLA.COST();
			
		} else if(SPRAY.NAME().equals(giftName)) {
			cost = SPRAY.COST();
			
		} else if(COLD.NAME().equals(giftName)) {
			cost = COLD.COST();
			
		} else if(CHEERS.NAME().equals(giftName)) {
			cost = CHEERS.COST();
			
		} else if(KEYBOARD.NAME().equals(giftName)) {
			cost = KEYBOARD.COST();
			
		} else if(FLOWER.NAME().equals(giftName)) {
			cost = FLOWER.COST();
			
		} else if(HEART.NAME().equals(giftName)) {
			cost = HEART.COST();
		}
		return cost;
	}
	
}
