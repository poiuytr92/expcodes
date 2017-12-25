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

		if(4 * s < d) {
			surplus = 10 * s - 2 * d;
			
		} else if(3 * s < 2 * d) {
			surplus = 8 * s - 4 * d;

		} else if(2 * s < 3 * d) {
			surplus = 6 * s - 6 * d;

		} else if(s < 4 * d) {
			surplus = 3 * s - 9 * d;

		} else {
			surplus = -1;
		}

		if(surplus < 0) {
			cout << "Deficit" << endl;
		} else {
			cout << surplus << endl;
		}
	}
	return 0;
}