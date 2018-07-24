//分别利用指针和引用交换两个变量的值

#include<iostream>
using namespace std;
void f(int* p,int* s)
{
	int k;
	k=*p;
	*p=*s;
	*s=k;
	return;
}
void g(int& m,int& n)
{
	int i;
	i=m;
	m=n;
	n=i;
	return;
}
int main()
{
	int a=1,b=2;
	f(&a,&b);
	cout<<"a="<<a<<",b="<<b<<endl;
	g(a,b);
	cout<<"a="<<a<<",b="<<b<<endl;

	system("pause");
	return 0;
}
