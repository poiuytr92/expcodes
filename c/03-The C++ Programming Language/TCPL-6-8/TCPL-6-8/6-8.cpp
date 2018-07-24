//看看编译器对“除0”,“上溢”和“下溢”的反应

#include<iostream>
using namespace std;
int main(void)
{
	int a=2,b;
	int c=987654321123456789;   //上溢，编译可以通过（语法没有错误）
	int d=-987654321123456789; //下溢，编译可以通过（语法没有错误）
	//	b=a/0;            //除以0，编译可以通过（语法没有错误），但无法执行
	//	cout<<b<<endl;   
	cout<<c<<endl;   //上溢输出随机值（根据c的原值决定）
	cout<<d<<endl;   //下溢输出随机值（根据d的原值决定），当c、d的原值绝对值相等时，输出值的绝对值也相等

	system("pause");
	return 0;
}