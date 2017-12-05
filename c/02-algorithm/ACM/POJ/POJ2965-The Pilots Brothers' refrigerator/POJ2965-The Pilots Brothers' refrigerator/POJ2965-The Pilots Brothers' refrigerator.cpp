/*
	Author:     Exp
	Date:       2017-12-04
	Code:       POJ 2965
	Problem:    The Pilots Brothers' refrigerator
	URL:        http://poj.org/problem?id=2965
*/

/*
	题意分析：
	  有一冰箱，上面4x4共16个开关（"-"状态表示打开，"+"状态表关闭）， 
	  当改变一个开关状态时，该开关所在行、列的全部开关状态也会同时改变。 
	  给出一个开关状态，问从该状态开始，使得所有开关打开（全"-"），
	  至少需要操作多少步，并把操作过程打印出来


	解题思路：
	  这题和 POJ 1753 的解题思路是一模一样的，只是存在几个差异点，使得解题技巧稍微提高了一点.
	  没做 POJ 1753 的同学先去做了那题，理解了再回头做这题.


	  我这里偷懒，直接套用了自己 POJ 1753 的代码，把开关模型抽象成棋盘模型，
	  因此详细的解题思路就不再复述了，这里只列出两题的差异，以及针对差异的处理手法：
		① 最终状态只有全0一种； POJ-1753是全0或全1均可
		② 翻转棋影响的是全列+全行共7个棋子
		③ 存在65536种不重复状态，从全0到全1共需16步
		④ 全状态以第8步为中心、一侧取反后呈对称分布（操作位 与 状态位 均完全对称）， 可用来指导剪枝
		⑤ 要求输出操作过程（使用操作码即可）
		⑥ 这题题目标注为Special Judge，亦即一题多解，原因是步数是固定的，但是操作开关的顺序不要求,
		   这是因为操作同样的若干个开关，操作顺序不会影响最终导向的状态
*/

#include <iostream>
using namespace std;


// 无符号整型(32位)，用于记录棋盘编码，主要为了避免int32的负数影响  
// 初始棋盘状态为全0（全"-"）  
// 高16位为棋盘操作位，翻动过的棋子位置标记为1  
// 低16位为棋盘状态位, 记录当前棋盘状态（"+"朝上为1，"-"朝上为0）  
typedef unsigned int _UINT;


const static int MAX_STEP = 16;			// 可翻棋的最大步数
const static int MAX_STATUS = 65536;	// 总状态数 = 2^16（含重复数）


// 棋盘状态掩码：当翻转位置i的棋子时,STATUS_MASKS[i]为所有受影响的行列位置 
// 位置i：在4x4棋盘内，从左到右、从上到下按0-15依次编码  
const static int MASKS[16] = {
	0x0000111F, 0x0000222F, 0x0000444F, 0x0000888F,
	0x000011F1, 0x000022F2, 0x000044F4, 0x000088F8,
	0x00001F11, 0x00002F22, 0x00004F44, 0x00008F88,
	0x0000F111, 0x0000F222, 0x0000F444, 0x0000F888
};


/**
 * 棋盘对象
 */
class Chess {
	public:
		Chess();
		~Chess();
		void print(int status);
		
	private:
		void bfsAllStatus(void);			// 记录不重复地翻动1-16步可得到的所有棋盘状态
		_UINT filp(_UINT chess, int bitPos);// 翻动棋盘上某个指定位位置的棋子
		
		int toStatus(_UINT chess);		// 从棋盘编码提取棋盘状态信息
		int getMaxBitPos(_UINT chess);	// 从棋盘编码提取棋盘操作信息，获得其中最大翻转编号的位置
		int getFilpCount(_UINT chess);	// 从棋盘编码提取棋盘操作信息，获取棋盘从全0状态开始共被翻动棋子的次数

	private:
		_UINT* chesses;		// 记录从全0开始到达每个棋盘状态的棋盘编码
};


int main(void) {
	// 一次计算把所有棋盘状态打表
	Chess* chess = new Chess();

	// 迭代输入棋盘状态 查表
	int chessStatus = 0;
	int byteCnt = 0;
	char byteBuff[5] = { '\0' };
	while(cin >> byteBuff && ++byteCnt) {
		int offset = 4 * (byteCnt - 1);
		for(int i = 0; i < 4; i++) {
			if(byteBuff[i] == '+') {	// -标记为0, +标记为1
				chessStatus |= (0x00000001 << (i + offset));
			}
		}

		// 每输入4个字节求解一次
		if(byteCnt >= 4) {
			chess->print(chessStatus);
			
			byteCnt = 0;
			chessStatus = 0;
		}
	}
	delete chess;
	return 0;
}


/**
 * 构造函数
 */
Chess::Chess() {
	this->chesses = new _UINT[MAX_STATUS];
	memset(chesses, -1, sizeof(_UINT) * MAX_STATUS);

	bfsAllStatus();
}


/**
 * 析构函数
 */
Chess::~Chess() {
	delete[] chesses;
	chesses = NULL;
}


/**
 * 初始化不重复地翻动1-16步可得到的所有棋盘状态
 *
 *   由于这题状态数比POJ1753要多，因此不再使用STL的set容器维护BFS队列，否则会TLE
 */
void Chess::bfsAllStatus(void) {
	const int ALL_ZERO_CHESS = 0;
	_UINT bfsQueue[MAX_STATUS];
	int head = 0, tail = 0;
	bfsQueue[tail++] = ALL_ZERO_CHESS;

	while(head < tail) {
		_UINT lastChess = bfsQueue[head++];
		int status = toStatus(lastChess);		// 屏蔽高16位操作位，得到低16位的真正棋盘状态
		chesses[status] = lastChess;			// 保存棋盘状态对应的棋盘编码

		// 剪枝1：.....
		for(int pos = getMaxBitPos(lastChess) + 1; pos < MAX_STEP; pos++) {
			_UINT nextChess = filp(lastChess, pos);	// 翻动棋子得到下一个棋盘状态编码
			bfsQueue[tail++] = nextChess;
		}
	}
}


/**
 * 翻动棋盘上某个指定位位置的棋子,
 *  此操作会同时改变棋盘编码的操作位（高16位）和状态位（低16位）
 * @param chess 翻转前的棋盘编码
 * @param bitPos 要翻转的棋子位置, 取值范围为[0, 15]，
 *				依次对应4x4棋盘上从左到右、自上而下的编号，也对应二进制数从低到高的进制位
 * return 翻转后的棋盘编码
 */
_UINT Chess::filp(_UINT chess, int bitPos) {
	_UINT OP_MASK = 0x00010000 << bitPos;	// 高16位:当前操作位
	_UINT STATUS_MASK = MASKS[bitPos];	// 低16位:相关状态位
	return (chess ^ (OP_MASK | STATUS_MASK));	// 更新棋盘编码
}


/**
 * 从棋盘编码提取棋盘状态信息
 * return 棋盘状态信息（低16位）
 */
int Chess::toStatus(_UINT chess) {
	const _UINT MASK = 0x0000FFFF;
	return (int) (chess & MASK);
}


/**
 * 从棋盘编码提取棋盘操作信息（高16位），获得其中最大翻转编号的位置
 * @param chess 棋盘编码
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


/**
 * 从棋盘编码提取棋盘操作信息（高16位），获取棋盘从全0状态开始共被翻动棋子的次数
 * @param chess 棋盘编码
 * return 被翻转次数
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
 * 计算从全0或全1状态到达指定棋盘状态的最小步数
 * @param status 棋盘状态
 * return 最小步数（若不可达则返回-1）
 */
void Chess::print(int status) {
	if(status >= 0 && status < MAX_STATUS) {
		_UINT chess = chesses[status];

		// 打印步数
		int step = getFilpCount(chess);
		cout << step << endl;

		// 打印翻转过程
		for(int mask = 0x00010000, bit = 0; bit < MAX_STEP; bit++, mask <<= 1) {
			if((chess & mask) > 0) {
				cout << (bit / 4) + 1 << " " << (bit % 4) + 1 << endl;
			}
		}
	}
}
