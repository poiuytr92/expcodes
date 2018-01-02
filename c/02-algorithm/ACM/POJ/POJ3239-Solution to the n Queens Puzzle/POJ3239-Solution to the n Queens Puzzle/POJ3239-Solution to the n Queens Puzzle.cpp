/*
	Author:     Exp
	Date:       2017-12-27
	Code:       POJ 3239
	Problem:    Solution to the n Queens Puzzle
	URL:		http://poj.org/problem?id=3239
*/

/*
	题意分析：
	 N皇后问题: 
	  在NxN棋盘内 纵、横、斜不重叠放置棋子，
	  当给定N (N ∈ [8, 300]) 时, 输出任意一种解法


	解题思路：
	  （为了便于下文分析讨论，此处开始修正为“M皇后问题”）
	 【禁位排列】的一种题型。
	 
	 传统解法是【回溯法（DFS+剪枝）】，但本题N值过大，回溯法会爆栈.
	 LRJ的《算法艺术与信息学竞赛》提出的解法是【启发式修补算法】(属于人工智能算法的一种)
	 启发式算法在M<10000左右都是可解的，但是因为启发式算法均存在随机性，
	 收敛速度视不同的收敛因子而变化（我看过某篇论文称启发式算法在M=10000时的耗时等价于回溯法M=30的耗时）

	 
	 这题还有用【构造法】（即通过分析问题潜在的数学规律直接得到解）的通解公式，时间复杂度为O(1)
	 通解公式的来源是由E.J.Hoffman、J.C.Loessi、R.C.Moore三人在1969年发表的一篇数学论文：
	 原理原文： http://penguin.ewu.edu/~trolfe/QueenLasVegas/Hoffman.pdf 
	 原理译文（本人亲译）：


	 而目前网上流传的通解公式，则是为了便于编程，在坐标变换后得到的：
	 变换坐标后的求解公式如下：
	 ① 当m mod 6 != 2 且 m mod 6 != 3时：
	   [2,4,6,8,...,m], [1,3,5,7,...,m-1]            (m为偶数)
	   [2,4,6,8,...,m-1], [1,3,5,7,...,m-2], [m]     (m为奇数)

	 ② 当m mod 6 == 2 或 m mod 6 == 3时，
	  令 n= m / 2 (m为偶数) 或 n = (m-1)/2 (m为奇数)
       [n,n+2,n+4,...,m], [2,4,...,n-2], [n+3,n+5,...,m-1], [1,3,5,...,n+1]         (m为偶数,n为偶数)
	   [n,n+2,n+4,...,m-1], [1,3,5,...,n-2], [n+3,...,m], [2,4,...,n+1]             (m为偶数,n为奇数)
	   [n,n+2,n+4,...,m-1], [2,4,...,n-2], [n+3,n+5,...,m-2], [1,3,5,...,n+1], [m]  (m为奇数,n为偶数)
	   [n,n+2,n+4,...,m-2], [1,3,5,...,n-2], [n+3,...,m-1], [2,4,...,n+1], [m]      (m为奇数,n为奇数)

	 上面有六条解序列: 一行一个序列，中括号是我额外加上的，以便辨认子序列。
	 
	 第i个数为Ai，表示在第i行Ai列放一个皇后.
	 子序列与子序列之间的数序是连续关系(无视中括号就可以了), 所有子序列内的递增值为2

*/

#include <iostream>
using namespace std;

/* 
 * 每隔1个数打印从bgn到end的序列
 * @param bgn 起始值（包括）
 * @param end 终止值（包括）
 */
void print(int bgn, int end);


int main(void) {
	int n = 0;
	while(cin >> n && n > 0) {
		const int mod = n % 6;
		const bool nOdd = (n % 2 == 1);

		if(mod != 2 && mod != 3) {
			if(nOdd == true) {
				print(2, n - 1);
				print(1, n);

			} else {
				print(2, n);
				print(1, n - 1);
			}

		} else {
			int k = n / 2;
			const bool kOdd = (k % 2 == 1);

			if(nOdd == true) {
				if(kOdd == true) {
					print(k, n - 2);
					print(1, k - 2);
					print(k + 3, n - 1);
					print(2, k + 1);
					print(n, n);

				} else {
					print(k, n - 1);
					print(2, k - 2);
					print(k + 3, n - 2);
					print(1, k + 1);
					print(n, n);
				}

			} else {
				if(kOdd == true) {
					print(k, n - 1);
					print(1, k - 2);
					print(k + 3, n);
					print(2, k + 1);

				} else {
					print(k, n);
					print(2, k - 2);
					print(k + 3, n - 1);
					print(1, k + 1);
				}
			}
		}
	}
	return 0;
}


void print(int bgn, int end) {
	for(int i = bgn; i <= end; i+= 2) {
		cout << i << ' ';
	}
}