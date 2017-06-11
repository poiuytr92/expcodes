package exp.libs.utils.pub;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import exp.libs.envm.Regex;

/**
 * <PRE>
 * 数值处理工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NumUtils {

	/** 最小精度 */
	private final static double PRECISION = 1.0e-6D;
	
	/** 私有化构造函数. */
	protected NumUtils() {}
	
	/**
	 * 比较a与b的大小
	 * @param a 比较参数a
	 * @param b 比较参数b
	 * @return 1:a>b; 0:a=b; -1:a<b
	 */
	public static int compare(double a, double b) {
		final double PRECISION = 0.0000001D;	// 精度阀值
		
		int rst = 0;
		double diff = a - b;
		if(diff > 0) {
			rst = 1;
			if(diff < PRECISION) {
				rst = 0;
			}
			
		} else if(diff < 0) {
			rst = -1;
			if(-diff < PRECISION) {
				rst = 0;
			}
			
		} else {
			rst = 0;
		}
		return rst;
	}
	
	/**
	 * 转换浮点数为百分比格式字符串
	 * @param n 浮点数
	 * @return 百分比格式字符串
	 */
	public static String numToPrecent(final double n) {
		DecimalFormat df = new DecimalFormat("0.00%");
		return df.format(n);
	}
	
	public static double precentToNum(String precent) {
		double n = 0;
		if(precent != null) {
			precent = StrUtils.trimAll(precent);
			precent = precent.replace("%", "");
			n = toDouble(precent);
			n /= 100.0D;
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[短整型]
	 * @param s 数字字符串
	 * @return 整型（若转换失败返回0）
	 */
	public static short toShort(final String s) {
		return toShort(s, ((short) 0));
	}
	
	public static short toShort(final String s, final short defavlt) {
		short n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Short.parseShort(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[整型]
	 * @param s 数字字符串
	 * @return 整型（若转换失败返回0）
	 */
	public static int toInt(final String s) {
		return toInt(s, 0);
	}
	
	public static int toInt(final String s, final int defavlt) {
		int n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Integer.parseInt(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[长整型]
	 * @param s 数字字符串
	 * @return 长整型（若转换失败返回0）
	 */
	public static long toLong(final String s) {
		return toLong(s, 0L);
	}
	
	public static long toLong(final String s, final long defavlt) {
		long n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Long.parseLong(s);
		}
		return n;
	}
	
	public static float toFloat(final String s) {
		return toFloat(s , 0F);
	}
	
	public static float toFloat(final String s, final float defavlt) {
		float n = defavlt;
		if(s != null && s.matches(Regex.FLOAT.VAL)) {
			n = Float.parseFloat(s);
		}
		return n;
	}
	
	public static double toDouble(final String s) {
		return toDouble(s, 0D);
	}
	
	public static double toDouble(final String s, final double defavlt) {
		double n = defavlt;
		if(s != null && s.matches(Regex.FLOAT.VAL)) {
			n = Double.parseDouble(s);
		}
		return n;
	}
	
	/**
	 * 数字字符串自增1
	 * @param sNum 数字字符串
	 * @return 自增1的数字字符串
	 */
	public static String increment(final String sNum) {
		long num = toLong(sNum) + 1;
		return String.valueOf(num);
	}
	
	/**
	 * 返回整数的负数
	 * @param n 整数
	 * @return 负数
	 */
	public static int toNegative(int n) {
		return n > 0 ? -n : n;
	}
	
	/**
	 * 返回整数的负数
	 * @param n 整数
	 * @return 负数
	 */
	public static long toNegative(long n) {
		return n > 0 ? -n : n;
	}
	
	/**
	 * 返回整数的正数
	 * @param n 整数
	 * @return 正数
	 */
	public static int toPositive(int n) {
		return n < 0 ? -n : n;
	}
	
	/**
	 * 返回整数的正数
	 * @param n 整数
	 * @return 正数
	 */
	public static long toPositive(long n) {
		return n < 0 ? -n : n;
	}
	
	public static long max(long a, long b) {
		return a > b ? a : b;
	}
	
	public static long max(long... nums) {
		long max = 0;
		if(nums != null && nums.length > 0) {
			max = nums[0];
			for(int i = 1; i < nums.length; i++) {
				max = (max < nums[i] ? nums[i] : max);
			}
		}
		return max;
	}
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public static int max(int... nums) {
		int max = 0;
		if(nums != null && nums.length > 0) {
			max = nums[0];
			for(int i = 1; i < nums.length; i++) {
				max = (max < nums[i] ? nums[i] : max);
			}
		}
		return max;
	}
	
	public static long min(long a, long b) {
		return a < b ? a : b;
	}
	
	public static long min(long... nums) {
		long min = 0;
		if(nums != null && nums.length > 0) {
			min = nums[0];
			for(int i = 1; i < nums.length; i++) {
				min = (min > nums[i] ? nums[i] : min);
			}
		}
		return min;
	}
	
	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	public static int min(int... nums) {
		int min = 0;
		if(nums != null && nums.length > 0) {
			min = nums[0];
			for(int i = 1; i < nums.length; i++) {
				min = (min > nums[i] ? nums[i] : min);
			}
		}
		return min;
	}
	
	public static List<String> compress(long[] ascSeries) {
		return compress(ascSeries, '~');
	}
	
	/**
	 * 递增序列压缩.
	 * 	例如把 { 1, 2, 3, 5, 6, 8, 10 }
	 *  压缩为 [1~3, 5~6, 8, 10]
	 * @param ascSeries 递增序列
	 * @param endash 连字符
	 * @return
	 */
	public static List<String> compress(long[] ascSeries, char endash) {
		List<String> cmpNums = new LinkedList<String>();
		if(ascSeries == null || ascSeries.length <= 0) {
			return cmpNums;
		}
		
		int len = ascSeries.length;
		int ps = 0;
		int pe = 0;
		while(ps < len) {
			while(pe + 1 < len && ascSeries[pe] + 1 == ascSeries[pe + 1]) {
				pe++;
			}
			
			long bgn = ascSeries[ps];
			long end = ascSeries[pe];
			if(bgn == end) {
				cmpNums.add(String.valueOf(bgn));
			} else {
				cmpNums.add(StrUtils.concat(bgn, "~", end));
			}
			
			ps = ++pe;
		}
		return cmpNums;
	}
	
	public static boolean isZero(double num) {
		return (Math.abs(num) < PRECISION)? true : false;
	}
	
}
