
#include <stdlib.h> 
#include <time.h> 
#include <iostream>
using namespace std;


/* 
 * 解决N皇后问题
 * @param n 皇后数
 */
void solveNQueue(int n);

/* 
 * 使用启发式修补迭代解决N皇后问题
 * @param n 皇后数
 */
bool iterateNQueue(int n);


/* 
 * 生成一个在范围[0, scope)内的随机数
 * @param scope 随机数范围
 * return int随机数
 */
int randInt(int scope);

/* 
 * 生成一个随机布尔值
 * return bool随机数
 */
bool randBool();



int main(void) {
	int n = 0;
	while(cin >> n && n > 0) {
		solveNQueue(n);
	}
	return 0;
}


void solveNQueue(int n) {
	const int MAX_ITERATION = 100;
	for(int cnt = 1; cnt <= MAX_ITERATION; cnt++) {
		cout << "Iterator " << cnt << "/" << MAX_ITERATION << ":" << endl;
		if(iterateNQueue(n)) {
			break;
		}
	}
}


bool iterateNQueue(int n) {

	// 不考虑皇后的位置冲突, 随机初始化第i个皇后的位置: 
	// pos[i]表示第i个皇后的行列位置为(i, pos[i])
	int* pos = new int[n];
	for(int i = 0; i < n; i++) {
		pos[i] = randInt(n);
	}

	// 计算第r行的每一格受到来自其他n-1个皇后的攻击次数
	// 取出被攻击次数最少的一格（若存在多个最小攻击格, 则随机选择一格）
	// 把第r行的皇后位置修正到该格
	for(int r = 0; r < n; r++) {
		int* attacks = new int[n];	// 第r行的每一格被攻击数
		memset(attacks, 0, sizeof(int) * n);

		// 枚举其他n-1个皇后，对第r行的相关格进行攻击
		for(int i = 0; i < n; i++) {
			if(i == r) {
				continue;	// 在第r行的皇后不攻击自己的所在行的格子
			}

			const int C = pos[i];	// 第i行的皇后所在列
			attacks[C]++;			// 第r行中 [与第i行的皇后同一列] 的格子受到攻击

			const int R_OFFSET = r - i;		// 第r行与第i行的行偏移值
			const int PC = C + R_OFFSET;	// 第i行的皇后与第[r,C]格子的正列偏移值
			const int NC = C - R_OFFSET;	// 第i行的皇后与第[r,C]格子的负列偏移值

			// 第r行中 [与第i行的皇后位置斜率为±1] 的格子受到攻击
			if(PC >= 0 && PC < n) { attacks[PC]++; }
			if(NC >= 0 && NC < n) { attacks[PC]++; }
		}

		// 提取第r行中被攻击次数最小的格子
		int c = pos[r];
		int min = 0x7FFFFFFF;
		for(int j = 0; j < n; j++) {
			if(min > attacks[j]) {
				min = attacks[j];
				c = j;

				// 存在多个最小值, 随机选择一个
			} else if(min == attacks[j] && randBool()) {
				c = j;
			}
		}
		pos[r] = c;	// 修正第r行的皇后位置

		//delete attacks;
	}

	// 判断修正后的n个皇后位置是否互不冲突
	// 只需判断第i个皇后是否会攻击到前i-1个皇后即可
	bool isOk = true;
	for(int ri = 1; isOk && ri < n; ri++) {
		const int ci = pos[ri];

		for(int rj = 0; isOk && rj < ri; rj++) {
			const int cj = pos[rj];

			if(ci == cj) { isOk = false; }
			else if(cj - ci == rj - ri) { isOk = false; }
			else if(cj - ci == ri - rj) { isOk = false; }
		}
	}

	// 打印解
	if(isOk == true) {
		for(int i = 0; i < n; i++) {
			cout << pos[i] << " ";
		}
		cout << endl;
	} else {
		cout << "Fail" << endl;
	}

	//delete pos;
	return isOk;
}


int randInt(int scope) {
	int num = 0;
	if(scope > 0) {
		srand((unsigned int) time(NULL));	// 初始化随机数种子
		num = rand() % scope;
	}
	return num;
}


bool randBool() {
	return (randInt(2) > 0);
}