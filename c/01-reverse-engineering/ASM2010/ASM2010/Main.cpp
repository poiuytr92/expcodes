#include <iostream>
using namespace std;

extern "C" int Int_3();
extern "C" int MY_TEST();

int main(void) {
	cout << 1111 << endl;
	int a = Int_3();
	int b = MY_TEST();
	cout << a + b << endl;

	system("pause");
	return 0;
}