//重定义类型名

#include<iostream>
#include<string>
using namespace std;
int main(void)
{
	typedef unsigned char uchar;//重定义类型unsigned char
	typedef const unsigned char cuchar;//重定义类型const unsigned char
	typedef int* pint;//到整数的指针
	typedef char* pchar;//到字符的指针
	typedef char** ppchar;//到字符的 指针的指针
	typedef char (*Pchar)[10];//到字符的数组的指针 (指向含有10个元素的字符数组的指针)
	typedef int NUM[3];//含有3个元素的整型数组
	NUM n;
	int i;
	int a[3][3]={6,7,8,4,5,6,8,9,0};
	int b[]={1,2,3,4,5,6,7};
	for(i=0;i<3;i++)
		{n[i]=i;cout<<n[i]<<','<<endl;}
	typedef int (*BER)[3];//到整型的数组的指针 (指向含有3个元素的字符数组的指针)
	BER k;
	int (*j)[3]=a;
	for(k=a;k<a+3;k++,j++)
		cout<<**k<<**j<<endl;
	typedef int* pintt[7];//7个到整数的指针的数组 (一个含有7个整型指针元素的数组)
	pintt f;
	for(i=0;i<7;i++)
		{f[i]=&b[i];cout<<*f[i]<<endl;}

	system("pause");
	return 0;
}