//类型转换!!
//建立整型数据的char[]字符串

#include<iostream>
using namespace std;
char* ITOA(int i,char b[])
{
	char* pb=b;
	int count=0;
	while(i/10>0)          //这里开始转换类型
	{
		*pb++=i%10+'0';   //单个数字加上'0'就是字符
		i/=10;
		count++;
	}
	*pb=i+'0';
	count++;
	char* t=new char[count+1];          //由于类型转换时留下的问题，这里开始首尾倒置数组
	char* pt=t;         //保留new空间首地址，用于返回指针值
	while((pb+1)!=b)
		*t++=*pb--;
	*t='\0';
	return pt;
}

int main(void)
{
	int num;
	char a[10];
	cin>>num;
	cout<<ITOA(num,a)<<endl;

	system("pause");
	return 0;
}

