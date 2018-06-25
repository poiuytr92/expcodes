package exp.libs.envm;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 颜色枚举.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Colors {
	
	public final static Colors BLACK = new Colors(
			"black", "无脑�?", 0, 0, 0);
	
	public final static Colors WHITE = new Colors(
			"white", "脑残�?", 255, 255, 255);
	
	public final static Colors RED = new Colors(
			"red", "姨妈�?", 255, 104, 104);
	
	public final static Colors BLUE = new Colors(
			"blue", "海底�?", 102, 204, 255);
	
	public final static Colors PURPLE = new Colors(
			"purple", "基佬�?", 227, 63, 255);

	public final static Colors CYAN = new Colors(
			"cyan", "散光�?", 0, 255, 252);
	
	public final static Colors GREEN = new Colors(
			"green", "宝强�?", 126, 255, 0);
	
	public final static Colors SEA_GREEN = new Colors(
			"sea_green", "深海�?", 64, 128, 0);
	
	public final static Colors YELLOW = new Colors(
			"yellow", "菊花�?", 255, 237, 79);
	
	public final static Colors ORANGE = new Colors(
			"orange", "柠檬�?", 255, 152, 0);
	
	public final static Colors PINK = new Colors(
			"pink", "情书�?", 255, 115, 154);
	
	public final static Colors PEACH_PINK = new Colors(
			"peachPink", "桃花�?", 250, 173, 229);
	
	public final static Colors GOLD = new Colors(
			"gold", "土豪�?", 251, 254, 182);
	
	/** 颜色�?: 用于取随机颜�? */
	private final static List<Colors> COLORS = Arrays.asList(new Colors[] {
			BLACK, WHITE, RED, BLUE, PURPLE, CYAN, GREEN, SEA_GREEN, 
			YELLOW, ORANGE, PINK, PEACH_PINK, GOLD
	});
	
	private String en;
	
	private String zh;
	
	private String rgb;
	
	private Color color;
	
	private Colors(String en, String zh, int R, int G, int B) {
		this.en = en;
		this.zh = zh;
		this.rgb = String.valueOf(toRGB(R, G, B));
		this.color = new Color(R, G, B);
	}
	
	/**
	 * RGB颜色值计�?
	 * @param R
	 * @param G
	 * @param B
	 * @return
	 */
	private long toRGB(int R, int G, int B) {
		String RGB = StrUtils.concat(
				StrUtils.leftPad(BODHUtils.decToHex(R), '0', 2), 
				StrUtils.leftPad(BODHUtils.decToHex(G), '0', 2), 
				StrUtils.leftPad(BODHUtils.decToHex(B), '0', 2));
		return BODHUtils.hexToDec(RGB);
	}
	
	public String EN() {
		return en;
	}
	
	public String ZH() {
		return zh;
	}
	
	public String RGB() {
		return rgb;
	}
	
	public Color COLOR() {
		return color;
	}
	
	/**
	 * 获取随机颜色
	 * @return
	 */
	public static Colors RANDOM() {
		return RandomUtils.genElement(COLORS);
	}
	
}
