/*****************************************
  Description: 算法工具包-数学
  Authod     : EXP
  Modify By  : None
  Date       : 2017-12-01
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include <math.h>
#include <memory.h>
#include "algorithm_math.h"


/**
 * 构造函数（构造 [2, range] 范围内的素数集）
 *  注意：当range>100W时，某些编译器在运行时可能会出现运行时异常，
 *			原因是编译器默认分配的堆栈大小不够（默认1M），此时需人工修改可分配的堆栈大小
 * @param range 自然数范围
 */
DLL_API Prime::Prime(int range) {
	this->range = (range < 2 ? 2 : range + 1);
	this->isPrimes = new bool[this->range];

	this->count = screen();
	this->primes = new int[this->count];
	toPrimes();
}


/**
 * 析构函数
 */
DLL_API Prime::~Prime() {
	delete[] isPrimes; isPrimes = NULL;
	delete[] primes; primes = NULL;
}


/**
 * 使用埃拉托斯特尼筛法求解素数集
 * return 素数集大小
 */
int Prime::screen(void) {
	memset(isPrimes, true, sizeof(bool) * range);	// 注意memset是按字节覆写内存的
	isPrimes[0] = isPrimes[1] = false;
	int cnt = 2;	// 非素数个数

	const int SQRT_NUM = ceil(sqrt((double) range));
	for(int i = 2; i <= SQRT_NUM; i++) {
		if(isPrimes[i] == false) {
			continue;
		}

		// 筛掉最小素数的所有倍数
		int multiple = 2;	// i的倍率（因不包括自身, 从2倍开始）	
		while(true) {
			int mNum = i * multiple;	// i的倍数
			if(mNum >= range) {
				break;
			}

			if(isPrimes[mNum] == true) {	// 避免重复计数
				isPrimes[mNum] = false;
				cnt++;
			}
			multiple++;
		}
	}
	return range - cnt;
}


/**
 * 把素数标记集转换成素数集
 */
void Prime::toPrimes(void) {
	for(int i = 0, j = 0; i < range; i++) {
		if(isPrimes[i] == true) {
			primes[j++] = i;
		}
	}
}

/**
 * 获取范围内的素数个数
 * @return 素数个数
 */
DLL_API int Prime::getCount(void) {
	return count;
}


/**
 * 检测指定整数是否为素数
 * @param num 被检测整数
 * @return true:是素数; false:不是素数 或 所检测整数不在范围内
 */
DLL_API bool Prime::isPrime(int num) {
	bool isPrime = false;
	if(num > 1 && num <= range) {
		isPrime = isPrimes[num];
	}
	return isPrime;
}


/**
 * 获取范围内的素数集
 * @return 素数集
 */
DLL_API int* Prime::getPrimes(void) {
	return primes;
}