//分别用char数组和struct结构输出一个关于 每个月的天数 表格

#include<iostream>
using namespace std;

void charmd(void)
{
	char* month[12]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	int day[12]={31,28,31,30,31,30,31,31,30,31,30,31};
	int i;
	for(i=1;i<=47;i++)
		cout<<'=';
	cout<<endl;
	for(i=0;i<12;i++)
		cout<<month[i]<<' ';
	cout<<endl;
	for(i=0;i<12;i++)
		cout<<' '<<day[i]<<' ';
	cout<<endl;
	for(i=1;i<=47;i++)
		cout<<'=';
	cout<<endl;
	return;
}

void structmd(void)
{
	struct MD
	{
		char* month[12];
		int day[12];
	}year={{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"},{31,28,31,30,31,30,31,31,30,31,30,31}};
	int i;
	cout<<"========"<<endl;
	cout<<"Mon Day"<<"\n========"<<endl;
	for(i=0;i<12;i++)
		cout<<year.month[i]<<": "<<year.day[i]<<endl;
	cout<<"========"<<endl;
	return;
}

int main(void)
{
	int a;
	charmd();
	for(a=0;a<50;a++)
		cout<<'*';
	cout<<endl;
	structmd();

	system("pause");
	return 0;
}