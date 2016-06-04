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
	 * 把[数字字符串]转换为[整型]
	 * @param s 数字字符串
	 * @return 整型（若转换失败返回0）
	 */
	public static int toInt(final String s) {
		int n = 0;
		if(s != null && s.matches("-?\\d+")) {
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
		long n = 0;
		if(s != null && s.matches(Regex.INTEGER.VAL)) {
			n = Long.parseLong(s);
		}
		return n;
	}
	
	public static float toFloat(final String s) {
		float n = 0;
		if(s != null && s.matches(Regex.FLOAT.VAL)) {
			n = Float.parseFloat(s);
		}
		return n;
	}
	
	public static double toDouble(final String s) {
		double n = 0;
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
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public static long min(long a, long b) {
		return a < b ? a : b;
	}
	
	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	/**
	 * 递增序列压缩.
	 * 	例如把 { 1, 2, 3, 5, 6, 8, 10 }
	 *  压缩为 [1~3, 5~6, 8, 10]
	 * @param ascSeries 递增序列
	 * @return
	 */
	public static List<String> compress(long[] ascSeries) {
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
	
}
