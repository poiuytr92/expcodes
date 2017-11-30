/*
	Author:     Exp
	Date:       2017-11-29
	Code:       POJ 3006
	Problem:    Dirichlet's Theorem on Arithmetic Progressions
	URL:		http://poj.org/problem?id=3006
*/

/*
	题意分析：
	 纯粹引用了狄利克雷数列，但其实没用到狄利克雷定理的问题。
	 狄利克雷数列即形如 a+nd 的等差数列，其中a、d互质（公约数只有1的两个自然数），n=1,2,3,......
	 已知在狄利克雷数列中，会出现无数个素数（这些质数模d同余a，但本题没用到这个特性）

	 现给定a和d，通过构造其狄利克雷数列，求数列中的第n个素数。
	 其中 a∈[1,9307], b∈[1,346], n∈[1,210]


	解题思路：
	 这题的关键不是狄利克雷数列，还是求素数。
	 素数的范围可以通过 a、b、n 的最大值圈定（约为100W以内），
	 有了素数表之后就是暴力美学的工作了

	 ① 用筛法求取题目给定最大范围内的所有素数
	 ② 根据a、b构造狄利克雷数列，通过素数表找到其中第n个素数

*/

#include <memory.h>
#include <math.h>
#include <iostream>
using namespace std;


const static int LEN = 1000000;							// 自然数数组长度(求解素数范围)
const static int SQRT_NUM = ceil(sqrt((double) LEN));	// 根据合数定理得到的质因数范围

/* 
 * 使用筛法找出自然数范围内的所有素数
 * @param primes 素数表
 */
void findPrimes(bool* primes);

/* 
 * 获取狄利克雷数列中第n个素数
 * @param primes 素数集
 * @param a 狄利克雷数列参数 a∈[1,9307]
 * @param d 狄利克雷数列参数 b∈[1,346]
 * @param n 期望得到素数序次 n∈[1,210]
 * return 期望得到的第n个素数
 */
int getDirichletPrime(bool* primes, int a, int d, int n);

int main(void) {
	bool primes[LEN];		// 素数集, 标记每个自然数是否为素数
	findPrimes(primes);		// 找出范围内所有素数

	int a, d, n;
	while(cin >> a >> d >> n && (a + d + n != 0)) {
		int prime = getDirichletPrime(primes, a, d, n);
		cout << prime << endl;
	}
	return 0;
}


void findPrimes(bool* primes) {
	memset(primes, true, sizeof(bool) * LEN);	// 注意memset是按字节覆写内存的
	primes[0] = primes[1] = false;

	for(int i = 2; i <= SQRT_NUM; i++) {
		if(primes[i] == false) {
			continue;
		}

		// 筛掉最小素数的所有倍数
		int multiple = 2;	// i的倍率（因不包括自身, 从2倍开始）	
		while(true) {
			int mNum = i * multiple;	// i的倍数
			if(mNum >= LEN) {
				break;
			}
			primes[mNum] = false;
			multiple++;
		}
	}
}

int getDirichletPrime(bool* primes, int a, int d, int n) {
	int dirichlet = 0;	// 第n个素数
	int cnt = 0;		// 素数出现计数器
	int i = 1;
	while(true) {
		int num = a + i * d;
		if(primes[num] == true) {
			cnt++;
			if(cnt >= n) {
				dirichlet = num;
				break;
			}
		}
	}
	return dirichlet;
}