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

byte toVal(int code, char ch) {
	byte val = 2; 
	switch(ch) {
		case 'p' : { val = (code & 0x01 == 0 ? 0 : 1); break; }
		case 'q' : { val = (code & 0x02 == 0 ? 0 : 1); break; }
		case 'r' : { val = (code & 0x04 == 0 ? 0 : 1); break; }
		case 's' : { val = (code & 0x08 == 0 ? 0 : 1); break; }
		case 't' : { val = (code & 0x10 == 0 ? 0 : 1); break; }
		default : { val = 2; }
	}
	return val;
}



int main(void) {
	const int LEN =101;
	char WFF[LEN];

	while(cin >> WFF && WFF[0] != '0') {

		// 0x1F : 0001 1111
		for(int code = 0; code < 0x1F; code++) {
			


		}
	}
	return 0;
}

