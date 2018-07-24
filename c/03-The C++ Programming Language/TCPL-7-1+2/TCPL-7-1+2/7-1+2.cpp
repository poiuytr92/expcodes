//一些特殊的函数声明

#include<iostream>
using namespace std;

int main(void)
{
	void f1(char*,int&);   //以指向字符的指针和对整数的引用为参数，不返回值
	void (*pf1)(char*,int&)=f1;  //一个指向这个函数的指针  or    void (*pf1)(char* pc,int& i)=&f1

	typedef void (*pf2)(char*,int&);  
	void f3(pf2);         //一个以这种指针为参数的函数
	pf2 f4(char*,int&);   //返回这种指针的函数
	pf2 f5(pf2);         //以这样的指针作为参数，并返回其参数作为返回值

	//	int rif(int,int);
	typedef int (&rifii)(int,int);
	rifii rif(int,int);               //谁是谁的引用？

	return 0;
}
// pf2 f5(pf2)   定义？？？？？？
// {
//...
// }