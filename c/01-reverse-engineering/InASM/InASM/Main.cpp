#include <iostream>
#include <windows.h>
using namespace std;

EXTERN_C ULONG64 myAdd(ULONG64 u1,ULONG64 u2);

int main(void)
{
	ULONG64 result = myAdd(0x111111111,0x333333333);
	cout << result << endl;
	system("pause");
	return 0;
}