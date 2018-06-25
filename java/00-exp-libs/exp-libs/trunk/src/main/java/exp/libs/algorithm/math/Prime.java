package exp.libs.algorithm.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * <PRE>
 * 通过埃拉托斯特尼筛法找出指定范围内的所有素数.
 * 
 * 关于素数的求解方法，不外乎用到：
 * 	[定义]：只能被1或者自身整除的自然数（不包括1），称为素数
 * 	[定理]：如果一个数k是合数，那么它的最小质因数肯定<=sqrt(k) 
 * 		由于一个自然数若不是合数则必是素数，这个定理可以反过来用于素数：
 * 			如果一个数k是素数, 那么k必不能被<=sqrt(k)的所有整数整除
 * 	[算法]：埃拉托斯特尼筛法，也简称筛法，是一种空间换时间算法.
 * 		筛法主要用于求出某一个范围内的所有素数，而不用于判断某个数是否为素数.
 * 		其主要思想是利用了合数定理, 剔除范围内所有合数，剩下的必是素数.
 * 		例如要求 (1, n] 以内的所有素数：
 * 			那么把2的所有倍数删掉（不包括2）；
 * 			 在剩下的数中第一个是3，把3的所有倍数删掉（不包括3）；
 * 			在剩下的数中第一个是7，把7的所有倍数删掉（不包括7）
 * 			......
 * 			 一直重复直到遍历完 (1, sqrt(n)] 范围内的所有数，那么剩下的就是这个范围内的素数
 * 		
 * 	常规情况下，
 * 		使用定义+定理求解素数，时间复杂度约为O(n*sqrt(n))，超过千万级的话短时间内跑不动
 * 		使用筛法求解素数，时间复杂度可达到O(n)，但空间复杂度也达到了O(n)
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-11-30
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Prime {

	/** 要求解的素数集的自然数范�?(包含range) */
	private int range;
	
	/** 范围内的素数个数 */
	private int count;
	
	/** 素数标记�?: 标记范围内的每一个数是否为素�? */
	private boolean[] isPrimes;
	
	/** 素数�?: 依次存储范围内每一个素�? */
	private List<Integer> primes;
	
	/**
	 * 构�? [2, range] 范围内的素数�?
	 * @param range 自然数范�?
	 */
	public Prime(int range) {
		this.range = (range < 2 ? 2 : range + 1);
		this.count = 0;
		this.isPrimes = new boolean[this.range];
		this.primes = new LinkedList<Integer>();
	}
	
	/**
	 * 使用埃拉托斯特尼筛法求解素数�?
	 */
	public void screen() {
		Arrays.fill(isPrimes, true);
		isPrimes[0] = isPrimes[1] = false;
		count = 2;
		
		// 根据合数定理，在 [2, range] 范围内筛�? [2, sqrt(range)] 之间的所有数的倍数
		final int SQRT_NUM = (int) Math.ceil(Math.sqrt(range));
		for(int i = 2; i <= SQRT_NUM; i++) {
			if(isPrimes[i] == false) {
				continue;
			}

			// 筛掉最小素数的所有倍数
			int multiple = 2;	// i的倍率（因不包括自�?, �?2倍开始）	
			while(true) {
				int mNum = i * multiple;	// i的倍数
				if(mNum >= range) {
					break;
				}
				
				if(isPrimes[mNum] == true) {	// 避免重复计数
					isPrimes[mNum] = false;
					count++;
				}
				multiple++;
			}
		}
		count = range - count;	// 除外所有合数剩下的就是素数
		
		// 构造素数集
		for(int i = 0; i < range; i++) {
			if(isPrimes[i] == true) {
				primes.add(i);
			}
		}
	}
	
	/**
	 * 清空内存
	 */
	public void clear() {
		primes.clear();
	}

	/**
	 * 获取范围内的素数个数
	 * @return 素数个数
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 获取范围内的素数集合
	 * @return 素数�?
	 */
	public List<Integer> getPrimes() {
		return new LinkedList<Integer>(primes);
	}
	
	/**
	 * 检测范围内的指定整数是否为素数
	 * @param num 被检测整�?
	 * @return true:是素�?; false:不是素数 �? 所检测整数不在范围内
	 */
	public boolean isPrime(int num) {
		boolean isPrime = false;
		if(num > 1 && num <= range) {
			isPrime = isPrimes[num];
		}
		return isPrime;
	}
	
}
