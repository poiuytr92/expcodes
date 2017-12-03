/*
	Author:     Exp
	Date:       2017-12-01
	Code:       POJ 1753
	Problem:    Flip Game
	URL:		http://poj.org/problem?id=1753
*/

/*
	题意分析：
	 使棋盘全黑，需要翻转多少步

	解题思路：

	  对棋盘矩阵进行编码：
	    * * * *      从右到左分别为第 0, 1, 2, 3位
	    % % % %      从右到左分别为第 4, 5, 6, 7位
	    # # # #      从右到左分别为第 8, 9,10,11位
        @ @ @ @      从右到左分别为第12,13,14,15位

	  由于棋盘每一格只有黑白两种状态，可用0、1进行表示，由此可转换成二进制数：
	    @@@@ #### %%%% ****
	   15      ←         0

	  最终可以用一个int数存储整个棋盘状态：
	    ① 当翻转某一位上的棋子时，相当于使该位与 1 做异或运算.
		② 棋盘处于全黑状态时，int值为 0x0000
		③ 棋盘处于全白状态时，int值为 0xFFFF


	  记录从全黑=0开始，经过翻转0~16次后可以到达的所有状态.
	  理论上最多只能翻16次，共2^16 = 65536 种状态

*/


#include <set>
#include <iostream>
using namespace std;


/**
 * 翻转棋盘上的一只棋子
 * @param chess 翻转前的棋盘编码
 * @param pos 要翻转的棋子位置, 取值范围为[0, 15]
 * return 翻转后的棋盘编码
 */
int filp(int chess, int pos);


/**
 * 判断棋盘是否为单色（全黑或全白）
 * return true:单色; false:非单色
 */
bool isMonochrome(int chess);


int main(void) {
	set<int>* statusTable = new set<int>[17];	// 记录翻动0~16次的所有状态
	int chess = 0;	// 初始状态（全黑）

	
	//for(int s = 1; s <= 16; s++) {
	//	set<int> lastStatus = status[s - 1];
	//	set<int> curStatus = status[s];

	//}

	//for(int pos = 0; pos < 16; pos++) {
	//	//int chess = status[0].iterator(); 上一次的状态

	//	statusTable[1].insert(filp(chess, pos));
	//}

	statusTable[0].insert(chess);	// 翻动0次
	for(int a = 0; a < 16; a++) {

		for(int b = 0; b < 16; b++) {

			for(int c = 0; c < 16; c++) {

				for(int d = 0; d < 16; d++) {

				}
			}
		}
	}
	
	int input = 0;	// FIXME
	int step = -1;
	for(int s = 0; s <= 16; s++) {
		set<int> status = statusTable[step];
		if(status.count(input) > 0) {
			step = s;
			break;
		}
	}
	if(step >= 0) {
		cout << step << endl;
	} else {
		cout << "Impossible" << endl;
	}
	return 0;
}


int filp(int chess, int pos) {
	int op = 0x01 << pos;	// 翻转棋子自身位置
	if(pos > 3) { op |= (pos - 4);  }	// 翻转棋子上方的棋子
	if(pos < 12) { op |= (pos + 4);  }	// 翻转棋子下方的棋子

	int mod = pos % 4;
	if(mod != 0) { op |= (pos - 1);  }	// 翻转棋子左方的棋子
	if(mod != 3) { op |= (pos + 1);  }	// 翻转棋子右方的棋子

	return chess ^ op;
}


bool isMonochrome(int chess) {
	return (chess == 0 || chess == 0xFFFF);
}