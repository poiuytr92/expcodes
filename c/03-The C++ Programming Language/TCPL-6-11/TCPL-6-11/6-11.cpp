//看看编译器对这种错误的反应

#include<iostream>
using namespace std;

void f(int a,int b)
{
	if(a=3);
	if(a&077==0);
	a:=b+1;            //error C2143:syntax error : missing ';' before '='
}

int main(void)
{
	return 0;
}