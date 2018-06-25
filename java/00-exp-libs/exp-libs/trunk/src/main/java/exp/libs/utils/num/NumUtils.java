package exp.libs.utils.num;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import exp.libs.envm.Regex;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 数值处理工具
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class NumUtils {

	/** 最小精�? */
	private final static double PRECISION = 1.0e-6D;
	
	/** 自然底数e */
	public final static double E = Math.E;
	
	/** 圆周率π */
	public final static double PI = Math.PI;
	
	/** 角度转弧度公式常�? */
	private final static double TO_RADIAN = PI / 180;
	
	/** 弧度转角度公式常�? */
	private final static double TO_ANGEL = 180 / PI;
	
	/** 私有化构造函�?. */
	protected NumUtils() {}
	
	/**
	 * 比较a与b的大�?
	 * @param a 比较参数a
	 * @param b 比较参数b
	 * @return 1:a>b; 0:a=b; -1:a<b
	 */
	public static int compare(double a, double b) {
		final double PRECISION = 0.0000001D;	// 精度阀�?
		
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
	 * 把[浮点数]转换为[百分比格式字符串]
	 * @param n 浮点�?
	 * @return 百分比格式字符串
	 */
	public static String numToPrecent(final double n) {
		DecimalFormat df = new DecimalFormat("0.00%");
		return df.format(n);
	}
	
	/**
	 * 把[百分比格式字符串]转换为[浮点数]
	 * @param precent 百分比格式字符串
	 * @return 浮点�?
	 */
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
	 * @param s 数字字符�?
	 * @return 整型（若转换失败返回0�?
	 */
	public static short toShort(final String s) {
		return toShort(s, ((short) 0));
	}
	
	/**
	 * 把[数字字符串]转换为[短整型]
	 * @param s 数字字符�?
	 * @param defavlt 默认�?
	 * @return 整型（若转换失败返回默认值）
	 */
	public static short toShort(final String s, final short defavlt) {
		short n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Short.parseShort(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[整型]
	 * @param s 数字字符�?
	 * @return 整型（若转换失败返回0�?
	 */
	public static int toInt(final String s) {
		return toInt(s, 0);
	}
	
	/**
	 * 把[数字字符串]转换为[整型]
	 * @param s 数字字符�?
	 * @param defavlt 默认�?
	 * @return 整型（若转换失败返回默认值）
	 */
	public static int toInt(final String s, final int defavlt) {
		int n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Integer.parseInt(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[长整型]
	 * @param s 数字字符�?
	 * @return 长整型（若转换失败返�?0�?
	 */
	public static long toLong(final String s) {
		return toLong(s, 0L);
	}
	
	/**
	 * 把[数字字符串]转换为[长整型]
	 * @param s 数字字符�?
	 * @param defavlt 默认�?
	 * @return 长整型（若转换失败返回默认值）
	 */
	public static long toLong(final String s, final long defavlt) {
		long n = defavlt;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Long.parseLong(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[单精度浮点数]
	 * @param s 数字字符�?
	 * @return 单精度浮点数（若转换失败返回0�?
	 */
	public static float toFloat(final String s) {
		return toFloat(s , 0F);
	}
	
	/**
	 * 把[数字字符串]转换为[单精度浮点数]
	 * @param s 数字字符�?
	 * @param defavlt 默认�?
	 * @return 单精度浮点数（若转换失败返回默认值）
	 */
	public static float toFloat(final String s, final float defavlt) {
		float n = defavlt;
		if(s != null && s.matches(Regex.FLOAT.VAL)) {
			n = Float.parseFloat(s);
		}
		return n;
	}
	
	/**
	 * 把[数字字符串]转换为[双精度浮点数]
	 * @param s 数字字符�?
	 * @return 双精度浮点数（若转换失败返回0�?
	 */
	public static double toDouble(final String s) {
		return toDouble(s, 0D);
	}
	
	/**
	 * 把[数字字符串]转换为[双精度浮点数]
	 * @param s 数字字符�?
	 * @param defavlt 默认�?
	 * @return 双精度浮点数（若转换失败返回默认值）
	 */
	public static double toDouble(final String s, final double defavlt) {
		double n = defavlt;
		if(s != null && s.matches(Regex.FLOAT.VAL)) {
			n = Double.parseDouble(s);
		}
		return n;
	}
	
	/**
	 * 数字字符串自�?1
	 * @param sNum 数字字符�?
	 * @return 自增1的数字字符串
	 */
	public static String increment(final String sNum) {
		long num = toLong(sNum) + 1;
		return String.valueOf(num);
	}
	
	/**
	 * 返回[int]的负�?
	 * @param n int整数
	 * @return 负数
	 */
	public static int toNegative(int n) {
		return n > 0 ? -n : n;
	}
	
	/**
	 * 返回[long]的负�?
	 * @param n long整数
	 * @return 负数
	 */
	public static long toNegative(long n) {
		return n > 0 ? -n : n;
	}
	
	/**
	 * 返回[int]的正�?
	 * @param n int整数
	 * @return 正数
	 */
	public static int toPositive(int n) {
		return n < 0 ? -n : n;
	}
	
	/**
	 * 返回[long]的正�?
	 * @param n long整数
	 * @return 正数
	 */
	public static long toPositive(long n) {
		return n < 0 ? -n : n;
	}
	
	/**
	 * 返回int最大�?
	 * @param a 数字A
	 * @param b 数字B 
	 * @return 最大�?
	 */
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	/**
	 * 返回int最大�?
	 * @param nums 数字序列
	 * @return 最大�?
	 */
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
	
	/**
	 * 返回long最大�?
	 * @param a 数字A
	 * @param b 数字B 
	 * @return 最大�?
	 */
	public static long max(long a, long b) {
		return a > b ? a : b;
	}
	
	/**
	 * 返回long最大�?
	 * @param nums 数字序列
	 * @return 最大�?
	 */
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
	
	/**
	 * 返回int最小�?
	 * @param a 数字A
	 * @param b 数字B 
	 * @return 最大�?
	 */
	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	/**
	 * 返回int最小�?
	 * @param nums 数字序列
	 * @return 最大�?
	 */
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
	
	/**
	 * 返回long最小�?
	 * @param a 数字A
	 * @param b 数字B 
	 * @return 最大�?
	 */
	public static long min(long a, long b) {
		return a < b ? a : b;
	}
	
	/**
	 * 返回long最小�?
	 * @param nums 数字序列
	 * @return 最大�?
	 */
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
	
	/**
	 * <PRE>
	 * int递增序列压缩.
	 * 	例如�? { 1, 2, 3, 5, 6, 8, 10 }
	 *  压缩�? [1-3, 5-6, 8, 10]
	 * </PRE>
	 * @param ascSeries 递增序列(无需连续, 但必须递增)
	 * @return
	 */
	public static List<String> compress(int[] ascSeries) {
		return compress(ascSeries, '-');
	}
	
	/**
	 * <PRE>
	 * int递增序列压缩.
	 * 	例如�? { 1, 2, 3, 5, 6, 8, 10 }
	 *  压缩�? [1-3, 5-6, 8, 10]
	 * </PRE>
	 * @param ascSeries 递增序列(无需连续, 但必须递增)
	 * @param endash 连字�?
	 * @return 压缩序列
	 */
	public static List<String> compress(int[] ascSeries, char endash) {
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
			
			int bgn = ascSeries[ps];
			int end = ascSeries[pe];
			if(bgn == end) {
				cmpNums.add(String.valueOf(bgn));
			} else {
				cmpNums.add(StrUtils.concat(bgn, "~", end));
			}
			
			ps = ++pe;
		}
		return cmpNums;
	}
	
	/**
	 * <PRE>
	 * 判断双精度数是否�?0或近似于0.
	 * 	(默认最小精度为1.0e-6D，绝对�? 小于最小精度则判定�?0)
	 * </PRE>
	 * @param num 双精度数
	 * @return true: 等于或近似于0; false:�?0
	 */
	public static boolean isZero(double num) {
		return (Math.abs(num) < PRECISION)? true : false;
	}
	
	/**
	 * 角度转弧�?
	 * @param angel 角度
	 * @return 弧度
	 */
	public static double toRadian(double angel) {
		return angel * TO_RADIAN;
	}
	
	/**
	 * 弧度转角�?
	 * @param radian 弧度
	 * @return 角度
	 */
	public static double toAngel(double radian) {
		return radian * TO_ANGEL;
	}
	
	/**
	 * <PRE>
	 * 生成从bgn到end的范围数�?.
	 * 	例如:
	 *    �? 1...5 的范围数组为 [1, 2, 3, 4, 5]
	 *    �? 2...-2 的的范围数组�? [2, 1, 0, -1, 2]
	 * </PRE>
	 * @param bgn 范围数组起始值（包括�?
	 * @param end 范围数组终止值（包括�?
	 * @return 范围数组
	 */
	public static int[] toRangeArray(int bgn, int end) {
		boolean isAsc = (bgn <= end);
		int[] array = new int[(isAsc ? (end - bgn) : (bgn - end)) + 1];
		for(int i = 0; i < array.length; i++) {
			array[i] = (isAsc ? (bgn + i) : bgn - i);
		}
		return array;
	}
	
	/**
	 * <PRE>
	 * 生成从bgn到end的范围数�?.
	 * 	例如:
	 *    �? 1...5 的范围数组为 [1, 2, 3, 4, 5]
	 *    �? 2...-2 的的范围数组�? [2, 1, 0, -1, 2]
	 * </PRE>
	 * @param bgn 范围数组起始值（包括�?
	 * @param end 范围数组终止值（包括�?
	 * @return 范围数组
	 */
	public static long[] toRangeArray(long bgn, long end) {
		boolean isAsc = (bgn <= end);
		long[] array = new long[(int) (isAsc ? (end - bgn) : (bgn - end)) + 1];
		for(int i = 0; i < array.length; i++) {
			array[i] = (isAsc ? (bgn + i) : bgn - i);
		}
		return array;
	}
	
}
