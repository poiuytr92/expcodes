package exp.bilibili.plugin.envm;

/**
 * <PRE>
 * 活跃度枚举
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Activity {

	public final static Activity CHAT = new Activity("弹幕", 1);
	
	public final static Activity HOT_STRIP = new Activity("辣条", 100);
	
	public final static Activity MILLION = new Activity("亿圆", 1000);
	
	public final static Activity MEOW = new Activity("喵娘", 5200);
	
	public final static Activity B_CLOD = new Activity("B坷垃", 9900);
	
	public final static Activity STORM = new Activity("节奏风暴", 100000);
	
	public final static Activity TV = new Activity("小电视", 1245000);
	
	public final static Activity _233 = new Activity("233", 233);
	
	public final static Activity _666 = new Activity("666", 666);
	
	private String desc;
	
	private int cost;
	
	private Activity(String desc, int cost) {
		this.desc = desc;
		this.cost = cost;
	}
	
	public String DESC() {
		return desc;
	}
	
	public int COST() {
		return cost;
	}
	
}
