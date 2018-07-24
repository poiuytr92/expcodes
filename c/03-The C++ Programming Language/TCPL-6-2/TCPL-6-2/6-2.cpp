//存在括号与不存在括号的各种等价运算（根据运算符的优先级别）

#include<iostream>
using namespace std;

int f(int m,int n)
{
	return m+n;
}

int main(void)
{
	int a1,a2;
	int a=1,b=2,c=3,d=4,x=5,i=6;
	int* p=&b,*q=&d;
	int ab[2][3]={2,5,6,6,9,1};

	a1=b+c*d<<2&8;
	a2=(((b+(c*d))<<2)&8);
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=(a==b||a==c&&c<5);
	a2=((a==b)||((a==c)&&(c<5)));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=a&007!=3;
	a2=a&(007!=3);
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=x!=0;
	a2=(x!=0);
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=(0<=i<7);
	a2=(0<=(i<7));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=f(1,2)+3;
	a2=(f(1,2)+3);
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=-1+b---5;
	b=2;
	a2=((-1+(b--))-5);  // 注意常量不能自增、减
	cout<<"a1="<<a1<<",a2="<<a2<<endl;
	b=2;

	a1=b==c++;
	c=3;
	a2=(b==(c++));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;
	c=3;

	a1=b=c=0;
	a2=(b=(c=0));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;

	a1=(ab[1][2]*=*p?c:*q*2);
	ab[1][2]=1;
	a2=(ab[1][2]*=((*p)?c:((*q)*2)));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;
	ab[1][2]=1;

	a1=(a-b,c=d);
	a2=((a-b),(c=d));
	cout<<"a1="<<a1<<",a2="<<a2<<endl;


	system("pause");
	return 0;
}
