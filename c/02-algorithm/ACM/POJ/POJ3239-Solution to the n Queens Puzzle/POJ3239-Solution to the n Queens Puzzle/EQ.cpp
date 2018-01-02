
// ����ʹ������ʽ�޲��Ľⷨ����ϧ����̫��


#include <stdlib.h> 
#include <time.h> 
#include <iostream>
using namespace std;


/* 
 * ���N�ʺ�����
 * @param n �ʺ���
 */
void solveNQueue(int n);

/* 
 * ʹ������ʽ�޲��������N�ʺ�����
 * @param n �ʺ���
 */
bool iterateNQueue(int n);


/* 
 * ����һ���ڷ�Χ[0, scope)�ڵ������
 * @param scope �������Χ
 * return int�����
 */
int randInt(int scope);

/* 
 * ����һ���������ֵ
 * return bool�����
 */
bool randBool();



int __main(void) {
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

	// �����ǻʺ��λ�ó�ͻ, �����ʼ����i���ʺ��λ��: 
	// pos[i]��ʾ��i���ʺ������λ��Ϊ(i, pos[i])
	int* pos = new int[n];
	for(int i = 0; i < n; i++) {
		pos[i] = randInt(n);
	}

	// �����r�е�ÿһ���ܵ���������n-1���ʺ�Ĺ�������
	// ȡ���������������ٵ�һ�������ڶ����С������, �����ѡ��һ��
	// �ѵ�r�еĻʺ�λ���������ø�
	for(int r = 0; r < n; r++) {
		int* attacks = new int[n];	// ��r�е�ÿһ�񱻹�����
		memset(attacks, 0, sizeof(int) * n);

		// ö������n-1���ʺ󣬶Ե�r�е���ظ���й���
		for(int i = 0; i < n; i++) {
			if(i == r) {
				continue;	// �ڵ�r�еĻʺ󲻹����Լ��������еĸ���
			}

			const int C = pos[i];	// ��i�еĻʺ�������
			attacks[C]++;			// ��r���� [���i�еĻʺ�ͬһ��] �ĸ����ܵ�����

			const int R_OFFSET = r - i;		// ��r�����i�е���ƫ��ֵ
			const int PC = C + R_OFFSET;	// ��i�еĻʺ����[r,C]���ӵ�����ƫ��ֵ
			const int NC = C - R_OFFSET;	// ��i�еĻʺ����[r,C]���ӵĸ���ƫ��ֵ

			// ��r���� [���i�еĻʺ�λ��б��Ϊ��1] �ĸ����ܵ�����
			if(PC >= 0 && PC < n) { attacks[PC]++; }
			if(NC >= 0 && NC < n) { attacks[PC]++; }
		}

		// ��ȡ��r���б�����������С�ĸ���
		int c = pos[r];
		int min = 0x7FFFFFFF;
		for(int j = 0; j < n; j++) {
			if(min > attacks[j]) {
				min = attacks[j];
				c = j;

				// ���ڶ����Сֵ, ���ѡ��һ��
			} else if(min == attacks[j] && randBool()) {
				c = j;
			}
		}
		pos[r] = c;	// ������r�еĻʺ�λ��

		//delete attacks;
	}

	// �ж��������n���ʺ�λ���Ƿ񻥲���ͻ
	// ֻ���жϵ�i���ʺ��Ƿ�ṥ����ǰi-1���ʺ󼴿�
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

	// ��ӡ��
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
		srand((unsigned int) time(NULL));	// ��ʼ�����������
		num = rand() % scope;
	}
	return num;
}


bool randBool() {
	return (randInt(2) > 0);
}