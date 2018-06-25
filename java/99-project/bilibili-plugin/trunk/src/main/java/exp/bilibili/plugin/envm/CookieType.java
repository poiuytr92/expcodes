package exp.bilibili.plugin.envm;

/**
 * <PRE>
 * cookie类型
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CookieType {

	public final static CookieType MAIN = new CookieType(1, "主号");
	
	public final static CookieType MINI = new CookieType(2, "小号");
	
	public final static CookieType VEST = new CookieType(3, "马甲�?");
	
	public final static CookieType UNKNOW = new CookieType(4, "未知�?");
	
	private int id;
	
	private String desc;
	
	private CookieType(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}
	
	public int ID() {
		return id;
	}
	
	public String DESC() {
		return desc;
	}
	
	@Override
	public String toString() {
		return DESC();
	}
	
}
