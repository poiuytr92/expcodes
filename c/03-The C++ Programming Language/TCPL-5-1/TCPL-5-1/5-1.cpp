//常见函数、类型的声明

#include<iostream>
#include<string>
using namespace std;

int main()
{
	char c='h';
	int x=9,y=0;
	int f(int m);//声明一个函数
	int (&ff)(int n)=f;//对函数的引用
	char* pc=&c;//一个到字符的指针
	char** pcc=&pc;//一个到字符的指针的指针
	int num[10]={1,9,8,7,6,0,5,4,3,2};//一个包含10个整数的数组
	int (&NUM)[10]=num;//一个包含10个整数的数组的引用
	char* ps="wanglin";//一个到字符串的数组的指针
	string str="liangting";//一个到字符串的数组的指针
	const int a=8;//一个常量整数
	const int* pa=&a;//一个到常量整数的指针
	int *const px=&x;//一个到整数的常量指针
	y=ff(y);
	y=f(y);
	cout<<"y="<<y<<endl;
	cout<<"*pc="<<*pc<<endl;
	cout<<"**pcc="<<**pcc<<endl;
	cout<<"ps="<<ps<<endl;
	cout<<"str="<<str<<endl;
	cout<<"const int* pa="<<*pa<<endl;
	cout<<"int *const px="<<*px<<endl;
	for(x=0;x<10;x++)
		cout<<"NUM["<<x<<"]="<<NUM[x]<<endl;

	system("pause");
	return 0;
}

int f(int m)
{
	m++;
	return m;
}