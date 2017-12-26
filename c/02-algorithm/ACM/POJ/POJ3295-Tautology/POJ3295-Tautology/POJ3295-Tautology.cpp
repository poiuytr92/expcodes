/*
	Author:     Exp
	Date:       2017-12-26
	Code:       POJ 3295
	Problem:    Tautology
	URL:		http://poj.org/problem?id=3295
*/

/*
	题意分析：


    解题思路：


*/

#include <iostream>
#include <stack>
using namespace std;

typedef unsigned char byte;

byte toVal(int code, char ch);
bool judgeWFF(char* WFF, int code);
byte K(byte w, byte x);	//	K --> and:  x && y
byte A(byte w, byte x);	//	A --> or:  x || y
byte N(byte w);			//	N --> not :  !x
byte C(byte w, byte x);	//	C --> implies :  (!x)||y
byte E(byte w, byte x);	//	E --> equals :  x==y



int main(void) {
	const int LEN =101;
	char WFF[LEN];

	while(cin >> WFF && WFF[0] != '0') {

		bool isTautology = true;

		// 尝试32种变量取值可能 0x1F : 0001 1111
		for(int code = 0; code < 0x1F; code++) {
			isTautology &= judgeWFF(WFF, code);
			if(isTautology == false) {
				break;
			}
		}

		cout << (isTautology ? "tautology" : "not") << endl;
	}
	return 0;
}

bool judgeWFF(char* WFF, int code) {
	bool isTrue = false;
	const int LEN = strlen(WFF);

	stack<char> st;
	for(int i = LEN - 1; i >= 0; i--) {
		char ch = WFF[i];
		byte val = toVal(code, ch);

		// 变量
		if(val <= 1) {
			st.push(val);

		// 运算符
		} else {

			if('N' == ch) {
				byte w = st.top();
				st.pop();
				st.push(N(w));

			} else {
				byte w = st.top();
				st.pop();
				byte x = st.top();
				st.pop();

				if('K' == ch) {
					st.push(K(w, x));
				} else if('A' == ch) {
					st.push(A(w, x));
				} else if('C' == ch) {
					st.push(C(w, x));
				} else if('E' == ch) {
					st.push(E(w, x));
				}
			}
		}
	}

	if(st.size() == 1) {
		byte rst = st.top();
		isTrue = (rst == 1);
	}
	return isTrue;
}

byte toVal(int code, char ch) {
	byte val = 2; 
	switch(ch) {
	case 'p' : { val = ((code & 0x01) == 0 ? 0 : 1); break; }
	case 'q' : { val = ((code & 0x02) == 0 ? 0 : 1); break; }
	case 'r' : { val = ((code & 0x04) == 0 ? 0 : 1); break; }
	case 's' : { val = ((code & 0x08) == 0 ? 0 : 1); break; }
	case 't' : { val = ((code & 0x10) == 0 ? 0 : 1); break; }
	default : { val = 2; }
	}
	return val;
}


//	K --> and:  x && y
byte K(byte w, byte x) {
	return (w & x);
}

//	A --> or:  x || y
byte A(byte w, byte x) {
	return (w | x);
}

//	N --> not :  !x
byte N(byte w) {
	return ~w;
}

//	C --> implies :  (!x)||y
byte C(byte w, byte x) {
	return ((~w) | x);
}

//	E --> equals :  x==y
byte E(byte w, byte x) {
	return (w == x);
}