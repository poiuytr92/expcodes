/*
	Author:     Exp
	Date:       2017-12-01
	Code:       POJ 1753
	Problem:    Flip Game
	URL:		http://poj.org/problem?id=1753
*/

/*
	题意分析：
	 从某个状态开始，使棋盘全黑或全白，至少需要翻转多少步

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

// 无符号整型(32位)
typedef unsigned int _UINT;

const static int MAX_STEP = 16;			// 可翻棋的最大步数
const static int MAX_STATUS = 65536;	// 总状态数 = 2^16

class Chess {
	public:
		Chess();
		~Chess();
		int getStep(int chess);

	private:
		void initAllStatus(void);
		_UINT filp(_UINT chess, int bitPos);
		int getMaxBitPos(_UINT chess);

		bool isMonochrome(_UINT chess);
		int getFilpCount(_UINT chess);

	private:
		set<_UINT>* chessStatus;
		int* steps;
};


int main(void) {
	Chess* chess = new Chess();

	int status = 0;
	int byteCnt = 0;
	char byteBuff[5] = { '\0' };
	while(cin >> byteBuff && ++byteCnt) {
		int offset = 4 * (byteCnt - 1);
		for(int i = 0; i < 4; i++) {
			if(byteBuff[i] == 'w') {	// b标记为0, w标记为1
				status |= (0x00000001 << (i + offset));
			}
		}

		if(byteCnt >= 4) {
			int step = chess->getStep(status);
			if(step >= 0) {
				cout << step << endl;
			} else {
				cout << "Impossible" << endl;
			}

			byteCnt = 0;
			status = 0;
		}
	}
	delete chess;
	return 0;
}


Chess::Chess() {
	this->chessStatus = new set<_UINT>[MAX_STEP + 1];

	this->steps = new int[MAX_STATUS];
	memset(steps, -1, sizeof(int) * MAX_STATUS);

	initAllStatus();
}


Chess::~Chess() {
	for(int s = 0; s <= MAX_STEP; s++) {
		chessStatus->clear();
	}
	delete[] chessStatus; chessStatus = NULL;
	delete[] steps; steps = NULL;
}


void Chess::initAllStatus(void) {
	const int ALL_ZERO_CHESS = 0;
	steps[ALL_ZERO_CHESS] = 0;
	chessStatus[0].insert(ALL_ZERO_CHESS);	// 翻动0次的状态集(初始状态，棋盘全黑)

	//int cnt = 1;

	// 记录翻动1-16次的所有状态集
	for(int filpCnt = 1; filpCnt <= MAX_STEP; filpCnt++) {
		set<_UINT>* lastStatus = &chessStatus[filpCnt - 1];	// 在前一次的状态集基础上翻动棋子
		set<_UINT>* curStatus = &chessStatus[filpCnt];	// 更新翻动filpCnt的状态集

		for(set<_UINT>::iterator its = lastStatus->begin(); its != lastStatus->end(); its++) {
			_UINT chess = *its;
			for(int pos = getMaxBitPos(chess) + 1; pos < 16; pos++) {
				_UINT nextChess = filp(chess, pos);
				curStatus->insert(nextChess);

				// 取第一次出现的即是最小步数
				int status = (nextChess & 0x0000FFFF); // 屏蔽操作位
				if(steps[status] < 0) {
					steps[status] = filpCnt;
				}
			}
		}
		//cout << "翻动" << filpCnt << "次共" << curStatus->size() << "种状态" << endl;
		//cnt += curStatus->size();
	}
	//cout << "总状态数" << cnt << endl;
}


/**
 * 翻转棋盘上的一只棋子
 * @param chess 翻转前的棋盘编码(低16位表示棋盘状态, 高16位表示从全0状态翻到当前状态的操作位)
 * @param bitPos 要翻转的棋子位置, 取值范围为[0, 15]
 * return 翻转后的棋盘编码
 */
_UINT Chess::filp(_UINT chess, int bitPos) {

	// 高16位记录当前操作位
	_UINT op = 0x00010000 << bitPos;

	// 低16位记录状态
	const _UINT BASE = 0x00000001;
	op |= BASE << bitPos;	// 翻转棋子自身位置
	if(bitPos > 3) { op |= BASE << (bitPos - 4);  }	// 翻转棋子上方的棋子
	if(bitPos < 12) { op |= BASE << (bitPos + 4);  }	// 翻转棋子下方的棋子

	int mod = bitPos % 4;
	if(mod != 0) { op |= BASE << (bitPos - 1);  }	// 翻转棋子左方的棋子
	if(mod != 3) { op |= BASE << (bitPos + 1);  }	// 翻转棋子右方的棋子
	return chess ^ op;
}


/**
 * 获取棋盘被翻转棋子中编号最大的一个
 * return 没有操作过则返回-1，否则返回0-15
 */
int Chess::getMaxBitPos(_UINT chess) {
	_UINT MASK = 0x80000000;
	int bitPos = -1;
	for(int i = MAX_STEP - 1; i >= 0; i--) {
		if((chess & MASK) == MASK) {	// 注意加括号, ==优先级比&要高
			bitPos = i;
			break;
		}
		MASK >>= 1;
	}
	return bitPos;
}


int Chess::getStep(int chess) {
	int step = -1;
	if(chess >= 0 && chess < MAX_STATUS) {
		step = steps[chess];

		//if(step < 0) {
		//	chess = ((~chess) & 0x0000FFFF);	// 取反
		//	step = steps[chess];
		//}
	}
	return step;
}


/**
 * 获取棋盘被翻转棋子的棋子数量
 * return 
 */
int Chess::getFilpCount(_UINT chess) {
	const _UINT MASK = 0xFFFF0000;
	chess &= MASK;	// 屏蔽低16位的状态位

	// 判断高16位操作位有多少个1, 即为翻转次数
	int cnt = 0;
	while(chess > 0) {
		chess = (chess & (chess - 1));
		cnt++;
	}
	return cnt;
}

/**
 * 判断棋盘是否为单色（全黑或全白）
 * return true:单色; false:非单色
 */
bool Chess::isMonochrome(_UINT chess) {
	const _UINT MASK = 0x0000FFFF;
	chess &= MASK;	// 屏蔽高16位的操作位
	return (chess == 0 || chess == MASK);
}
