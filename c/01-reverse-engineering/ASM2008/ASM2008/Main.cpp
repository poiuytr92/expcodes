#include <iostream>
#include <windows.h>
using namespace std;

extern "C" ULONG64 Int_3();
extern "C" ULONG64 MY_TEST();

int main(void) {
	ULONG64 a = Int_3();
	ULONG64 b = MY_TEST();
	cout << a + b << endl;

	system("pause");
	return 0;
}