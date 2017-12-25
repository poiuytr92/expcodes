/*
	Author:     Exp
	Date:       2017-12-25
	Code:       POJ 2586
	Problem:    Y2K Accounting Bug
	URL:		http://poj.org/problem?id=2586
*/

/*
	Ã‚“‚∑÷Œˆ£∫
	 
*/

#include <iostream>
using namespace std;

int main(void) {
	int s, d;
	while(cin >> s >> d) {

		int surplus = 0;

		if(4 * d <= s) {
			surplus = -1;

		} else if(4 * d > s) {
			surplus = 3 * s - 9 * d;

		} else if(3 * d > 2 * s) {
			surplus = 6 * s - 6 * d;

		} else if(2 * d > 3 * s) {
			surplus = 8 * s - 4 * d;

		} else if(d > 4 * s) {
			surplus = 10 * s - 2 * d;

		}

		if(surplus < 0) {
			cout << "Deficit" << endl;
		} else {
			cout << surplus << endl;
		}
	}
	return 0;
}